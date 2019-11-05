package scouts.cne.pt.ui.views.tab;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.grids.ElementosGrid;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Vertical;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.utils.GoogleContactUtils;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-10-30
 *
 */
@Route( value = GoogleSyncTab.VIEW_NAME, layout = MainLayout.class )
@PageTitle( GoogleSyncTab.VIEW_TITLE )
public class GoogleSyncTab extends FlexBoxLayout implements HasLogger
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
	private final ElementosGrid				grid;
	private final List< SIIEElemento >	lstElementos			= new ArrayList<>();

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-30
	 * @param components
	 */
	public GoogleSyncTab()
	{
		super();
		setFlexDirection( FlexDirection.COLUMN );
		setMargin( Horizontal.AUTO );
		setPadding( Horizontal.RESPONSIVE_L, Vertical.L );
		setSizeFull();
		
		grid = new ElementosGrid( true, lstElementos );
		grid.setSelectionMode( SelectionMode.NONE );
		grid.addComponentColumn( i ->
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
					grid.getDataProvider().refreshItem( i );
					button.setEnabled( true );
				} );
			}
			button.setEnabled( googleAuthentication.getGoogleAuthInfo() != null );
			button.setDisableOnClick( true );
			return button;
		} ).setHeader( "Acção" ).setSortable( false );
		add( grid );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		lstElementos.clear();
		lstElementos.addAll( siieService.getElementosActivos().stream().collect( Collectors.toList() ) );
		
		grid.getDataProvider().refreshAll();
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
				googleAuthentication.updateElemento( siieElemento );
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
				googleAuthentication.createElemento( siieElemento );
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
