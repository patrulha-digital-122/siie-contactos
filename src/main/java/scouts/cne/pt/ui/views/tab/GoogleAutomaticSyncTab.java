package scouts.cne.pt.ui.views.tab;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.api.services.people.v1.model.ContactGroup;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.types.SIIESeccao;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.GoogleContactGroupsService;
import scouts.cne.pt.services.GoogleContactsService;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.Badge;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.grids.SeccaoComboBox;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Vertical;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-10-30
 *
 */
@Route( value = GoogleAutomaticSyncTab.VIEW_NAME, layout = MainLayout.class )
@PageTitle( GoogleAutomaticSyncTab.VIEW_TITLE )
public class GoogleAutomaticSyncTab extends FlexBoxLayout implements HasLogger
{
	/**
	 * 
	 */
	private static final long			serialVersionUID	= -7135758008679193088L;
	public static final String			VIEW_NAME			= "google-auto-sync-tab";
	public static final String			VIEW_TITLE			= "Google :: Sincronização Automática";
	@Autowired
	private SIIEService					siieService;
	@Autowired
	private GoogleAuthentication		googleAuthentication;
	@Autowired
	private GoogleContactGroupsService	googleContactGroupsService;
	@Autowired
	private GoogleContactsService		googleContactsService;
	private final ProgressBar			progressBar			= new ProgressBar();
	private final TextField				progressText		= new TextField();
	private final SeccaoComboBox		seccaoComboBox		= new SeccaoComboBox();
	private final Div					content				= new Div();
	private final VerticalLayout		createSuperAutoContent;
	private Button						buttonSuperAuto;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-30
	 * @param components
	 */
	public GoogleAutomaticSyncTab()
	{
		super();
		setFlexDirection( FlexDirection.COLUMN );
		setMargin( Horizontal.AUTO );
		setPadding( Horizontal.RESPONSIVE_L, Vertical.L );
		setSizeFull();
		progressBar.setWidthFull();
		progressText.setWidthFull();
		progressText.setEnabled( false );
		seccaoComboBox.setWidthFull();
		seccaoComboBox.setLabel( "Por favor escolha uma secção:" );
		content.setSizeFull();
		add( seccaoComboBox );
		add( progressBar );
		add( progressText );
		add( UIUtils.createH4Label( "A sincronização pode demorar alguns minutos (depende do número de elementos na seccao). Por favor utilize esta funcionalidade com moderação - por exemplo não sincronizar a mesma secção de novo :-) - pois existe algumas quotas de utilização do serviço do Google que não podemos ultrapassar." ) );
		add( content );
		createSuperAutoContent = createSuperAutoContent();
		// add( createSuperAutoContent );
		setAlignSelf( Alignment.BASELINE, createSuperAutoContent );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		if ( googleAuthentication.getGoogleAuthInfo() != null )
		{
			seccaoComboBox.addValueChangeListener( e ->
			{
				content.removeAll();
				if ( !seccaoComboBox.isEmpty() )
				{
					content.add( createSeccaoContent( e.getValue() ) );
				}
			} );
			createSuperAutoContent.setVisible( true );
		}
		else
		{
			seccaoComboBox.setEnabled( false );
			createSuperAutoContent.setVisible( false );
		}
	}

	/**
	 * The <b>addContent</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 */
	private VerticalLayout createSeccaoContent( SIIESeccao siieSeccao )
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		Badge label = siieSeccao.getLabel();
		label.setWidthFull();
		verticalLayout.add( label );
		verticalLayout.add( UIUtils.createH5Label( "Ao carregar neste botão iremos criar/actualizar todos os '" + siieSeccao.getNome() +
			"' na tua conta google e associar ao respectivo grupo." ) );
		Button button = UIUtils.createSuccessPrimaryButton( "Actualizar", VaadinIcon.UPLOAD_ALT );
		button.setWidthFull();
		button.setDisableOnClick( true );
		button.addClickListener( e ->
		{
			new Thread( () ->
			{
				try
				{
					e.getSource().getUI().get().access( () ->
					{
						buttonSuperAuto.setEnabled( false );
						progressBar.addThemeVariants( ProgressBarVariant.LUMO_CONTRAST );
						progressBar.setMax( 3 );
						progressBar.setValue( 0 );
						progressText.setValue( "A confirmar se o grupo existe" );
					} );
					updateGroup( siieSeccao );
					e.getSource().getUI().get().access( () ->
					{
						progressBar.setValue( 1 );
					} );
					List< SIIEElemento > updateCreateElementos = updateCreateElementos( siieSeccao, e.getSource().getUI().get() );
					e.getSource().getUI().get().access( () ->
					{
						progressBar.setValue( 2 );
					} );
					syncGroupElemento( siieSeccao, updateCreateElementos );
				}
				catch ( SIIIEImporterException | GeneralSecurityException | IOException e1 )
				{
					e.getSource().getUI().get().access( () ->
					{
						progressBar.addThemeVariants( ProgressBarVariant.LUMO_ERROR );
					} );
					showError( e1 );
				}
				e.getSource().getUI().get().access( () ->
				{
					progressText.setValue( "Sincronização completa!" );
					progressBar.setValue( 3 );
					progressBar.addThemeVariants( ProgressBarVariant.LUMO_SUCCESS );
					button.setEnabled( true );
					buttonSuperAuto.setEnabled( true );
				} );
			} ).start();
		} );
		verticalLayout.add( button );
		add( verticalLayout );

		return verticalLayout;
	}

	private VerticalLayout createSuperAutoContent()
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.add( UIUtils.createH4Label( "Modo Super automático" ) );
		verticalLayout.add( UIUtils
						.createH5Label( "Ao carregar neste botão iremos criar todos os grupos necessários, criar/actualizar todos os elementos e associar os elementos aos respectivos grupos." ) );
		buttonSuperAuto = UIUtils.createSuccessPrimaryButton( "Actualizar tudo!", VaadinIcon.UPLOAD_ALT );
		buttonSuperAuto.setWidthFull();
		buttonSuperAuto.setDisableOnClick( true );
		buttonSuperAuto.addClickListener( e ->
		{
			try
			{
				progressBar.addThemeVariants( ProgressBarVariant.LUMO_CONTRAST );
				progressBar.setValue( 0 );
				updateGroups();
				progressBar.setValue( 1 );
				List< SIIEElemento > updateCreateElementos = updateCreateElementos( SIIESeccao.D, e.getSource().getUI().get() );
				syncGroupElemento( SIIESeccao.D, updateCreateElementos );
				progressBar.setValue( 2 );
			}
			catch ( SIIIEImporterException | GeneralSecurityException | IOException e1 )
			{
				progressBar.addThemeVariants( ProgressBarVariant.LUMO_ERROR );
				showError( e1 );
			}
			progressBar.setValue( 3 );
			progressBar.addThemeVariants( ProgressBarVariant.LUMO_SUCCESS );
			buttonSuperAuto.setEnabled( true );
		} );
		verticalLayout.add( buttonSuperAuto );

		return verticalLayout;
	}

	/**
	 * The <b>syncGroupElemento</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 * @param siieSeccao
	 * @param updateCreateElementos
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws SIIIEImporterException
	 */
	private void syncGroupElemento( SIIESeccao siieSeccao, List< SIIEElemento > updateCreateElementos )
		throws SIIIEImporterException, GeneralSecurityException, IOException
	{
		ContactGroup contactGroup = googleContactGroupsService.getListGroups().get( siieSeccao );
		if ( contactGroup != null )
		{
			googleContactGroupsService.updateGroup( googleAuthentication.getPeopleService(), contactGroup, updateCreateElementos );
		}
	}

	/**
	 * The <b>updateCreateElementos</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 * @param ui
	 * @param d
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws SIIIEImporterException
	 */
	private List< SIIEElemento > updateCreateElementos( SIIESeccao siieSeccao, UI ui )
		throws SIIIEImporterException, GeneralSecurityException, IOException
	{
		List< SIIEElemento > elementosActivosBySeccao = siieService.getElementosActivosBySeccao( siieSeccao );
		int iCount = 0;
		for ( SIIEElemento siieElemento : elementosActivosBySeccao )
		{
			++iCount;
			String strIndex = String.format( "%d / %d - ", iCount, elementosActivosBySeccao.size() );
			if ( siieElemento.getGooglePerson() == null )
			{
				ui.access( () ->
				{
					progressText.setValue( strIndex + "A criar o elemento :: " + siieElemento.getNome() );
				} );
				googleContactsService.createElemento( googleAuthentication.getPeopleService(), siieElemento, true );
			}
			else
			{
				ui.access( () ->
				{
					progressText.setValue( strIndex + "A actualizar o elemento :: " + siieElemento.getNome() );
				} );
				googleContactsService.updateElemento( googleAuthentication.getPeopleService(), siieElemento, true );
			}
		}
		return elementosActivosBySeccao;
	}

	/**
	 * The <b>updateGroups</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 */
	private void updateGroups()
	{
		for ( SIIESeccao siieSeccao : SIIESeccao.values() )
		{
			updateGroup( siieSeccao );
		}
	}

	private void updateGroup( SIIESeccao siieSeccao )
	{
		ContactGroup contactGroup = googleContactGroupsService.getListGroups().get( siieSeccao );
		if ( contactGroup == null )
		{
			try
			{
				googleContactGroupsService.createGroup( googleAuthentication.getPeopleService(), siieSeccao );
			}
			catch ( Exception e )
			{
				showError( e );
				return;
			}
		}
	}
}
