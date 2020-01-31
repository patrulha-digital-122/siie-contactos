package scouts.cne.pt.ui.views.tab;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.GoogleContactsService;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.grids.ElementosGrid;
import scouts.cne.pt.utils.GoogleContactUtils;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-10-30
 *
 */
@Route( value = GoogleSyncTab.VIEW_NAME, layout = MainLayout.class )
@PageTitle( GoogleSyncTab.VIEW_TITLE )
public class GoogleSyncTab extends ElementosGrid implements HasLogger
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -7135758008679193088L;
	public static final String		VIEW_NAME			= "google-sync-tab";
	public static final String			VIEW_TITLE			= "Google :: Sincronização Manual";
	@Autowired
	private SIIEService				siieService;
	@Autowired
	private GoogleAuthentication	googleAuthentication;
	@Autowired
	private GoogleContactsService		googleContactsService;
	private final static List< SIIEElemento >	lstElementos		= new ArrayList<>();

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-30
	 * @param components
	 */
	public GoogleSyncTab()
	{
		super( true, lstElementos );
		
		setSelectionMode( SelectionMode.NONE );
		removeColumn( getSituacaoColumn() );
		addComponentColumn( i ->
		{
			Button button;
			if ( i.getGooglePerson() != null )
			{
				button = UIUtils.createPrimaryButton( "Actualizar" );
				button.addClickListener( e ->
				{
					updateContact( i.getNin() );
					button.setEnabled( true );
				} );
			}
			else
			{
				button = UIUtils.createSuccessPrimaryButton( "Criar" );
				button.addClickListener( e ->
				{
					createContact( i.getNin() );
					getDataProvider().refreshItem( i );
					button.setEnabled( true );
				} );
			}
			button.setEnabled( googleAuthentication.getGoogleAuthInfo() != null );
			button.setDisableOnClick( true );
			return button;
		} ).setHeader( "Acção" ).setSortable( false );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		lstElementos.clear();
		lstElementos.addAll( siieService.getElementosActivos().stream().collect( Collectors.toList() ) );
		
		Optional< SIIEElemento > elementoByNIN = siieService.getElementoByNIN( siieService.getUserNIN() );
		if ( elementoByNIN.isPresent() && !StringUtils.endsWith( elementoByNIN.get().getElemnucleo(), "00" ) )
		{
			useAgrupamentoColumn();
		}
		getDataProvider().refreshAll();
		recalculateColumnWidths();
	}

	/**
	 * The <b>updateContact</b> method returns {@link Object}
	 * 
	 * @author 62000465 2019-10-30
	 * @param strNin
	 * @return
	 */
	private void updateContact( String strNin )
	{
		try
		{
			Optional< SIIEElemento > siieOptional = siieService.getElementoByNIN( strNin );
			if ( siieOptional.isPresent() )
			{
				SIIEElemento siieElemento = siieOptional.get();
				GoogleContactUtils.updateGoogleFromSIIE( siieElemento );
				googleContactsService.updateElemento( googleAuthentication.getPeopleService(), siieElemento );
				showInfo( siieElemento.getNome() + " actualizado!" );
			}
			else
			{
				showWarning( "Elemento não encontrado :: 1110" );
			}
		}
		catch ( SIIIEImporterException e )
		{
			showError( e );
		}
	}

	private void createContact( String strNin )
	{
		try
		{
			Optional< SIIEElemento > siieOptional = siieService.getElementoByNIN( strNin );
			if ( siieOptional.isPresent() )
			{
				SIIEElemento siieElemento = siieOptional.get();
				googleContactsService.createElemento( googleAuthentication.getPeopleService(), siieElemento );
				showInfo( siieElemento.getNome() + " criado com sucesso!" );
			}
			else
			{
				showWarning( "Não foi possível criar o contacto :: 1110" );
			}
		}
		catch ( SIIIEImporterException e )
		{
			showError( e );
		}
	}
}
