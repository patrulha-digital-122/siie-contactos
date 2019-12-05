package scouts.cne.pt.ui.views.tab;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.types.SIIESeccao;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.GoogleContactGroupsService;
import scouts.cne.pt.services.GoogleContactsService;
import scouts.cne.pt.services.GoogleSyncContactsService;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.Badge;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.grids.SeccaoComboBox;
import scouts.cne.pt.ui.events.google.EventFinishGoogleUpdate;
import scouts.cne.pt.ui.events.google.EventStartGoogleUpdate;
import scouts.cne.pt.ui.events.google.EventUpdateSIIElemento;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Uniform;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.utils.Broadcaster;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-10-30
 *
 */
@Route( value = GoogleAutomaticSyncTab.VIEW_NAME, layout = MainLayout.class )
@PageTitle( GoogleAutomaticSyncTab.VIEW_TITLE )
@PreserveOnRefresh
public class GoogleAutomaticSyncTab extends VerticalLayout implements HasLogger
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
	@Autowired
	private GoogleSyncContactsService	googleSyncContactsService;
	private final ProgressBar			progressBar			= new ProgressBar();
	private final TextField				progressText		= new TextField();
	private final SeccaoComboBox		seccaoComboBox		= new SeccaoComboBox();
	private final Div					content				= new Div();
	private final VerticalLayout		createSuperAutoContent;
	private Button						buttonSuperAuto;
	private Button						buttonSeccaoUpdateAuto	= UIUtils.createSuccessPrimaryButton( "Actualizar", VaadinIcon.UPLOAD_ALT );
	private UI							ui;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-30
	 * @param components
	 */
	public GoogleAutomaticSyncTab()
	{
		super();
		setSizeFull();
		progressBar.setWidthFull();
		progressText.setWidthFull();
		progressText.setEnabled( false );
		seccaoComboBox.setWidthFull();
		seccaoComboBox.setLabel( "Por favor escolha uma secção:" );
		seccaoComboBox.addValueChangeListener( e ->
		{
			content.removeAll();
			if ( !seccaoComboBox.isEmpty() )
			{
				content.add( createSeccaoContent( e.getValue() ) );
			}
		} );
		content.setSizeFull();
		createSuperAutoContent = createSuperAutoContent();
		// add( createSuperAutoContent );
		// setAlignSelf( Alignment.BASELINE, createSuperAutoContent );
		add( createContent() );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		ui = attachEvent.getUI();
		if ( googleAuthentication.getGoogleAuthInfo() != null )
		{
			createSuperAutoContent.setVisible( true );
		}
		else
		{
			createSuperAutoContent.setVisible( false );
		}
		if ( googleSyncContactsService.getSiieSeccaoInUpdate() != null )
		{
			seccaoComboBox.setEnabled( false );
			seccaoComboBox.setValue( googleSyncContactsService.getSiieSeccaoInUpdate() );
			buttonSeccaoUpdateAuto.setEnabled( false );
			progressText.setValue( "Já existe um update em andamento.." );
		}

		register();
	}

	/**
	 * The <b>register</b> method returns {@link void}
	 * @author 62000465 2019-11-22 
	 */
	private void register()
	{
		Broadcaster.register( googleSyncContactsService.getUuid(), newMessage ->
		{
			if ( ui == null )
			{
				return;
			}
			if ( newMessage instanceof EventUpdateSIIElemento )
			{
				EventUpdateSIIElemento updateSIIElemento = ( EventUpdateSIIElemento ) newMessage;
				String strIndex = String.format(	"%d / %d - A %s o elemento :: %s",
													updateSIIElemento.getActual(),
													updateSIIElemento.getTotal(),
													updateSIIElemento.isCreate() ? "criar" : "actualizar",
													updateSIIElemento.getSiieElemento().getNome() );
				ui.access( () ->
				{
					progressBar.setValue( updateSIIElemento.getActual() );
					progressText.setValue( strIndex );
				} );
			}
			else if ( newMessage instanceof EventFinishGoogleUpdate )
			{
				EventFinishGoogleUpdate event = ( EventFinishGoogleUpdate ) newMessage;
				ui.access( () ->
				{
					progressText.setValue( event.getMessage() + " :: " + event.getDuration() );
					progressBar.addThemeVariants( event.getBarVariant() );
					progressBar.setValue( progressBar.getMax() );
					buttonSeccaoUpdateAuto.setEnabled( true );
					buttonSuperAuto.setEnabled( true );
					seccaoComboBox.setEnabled( true );
				} );
			}
			else if ( newMessage instanceof EventStartGoogleUpdate )
			{
				EventStartGoogleUpdate event = ( EventStartGoogleUpdate ) newMessage;
				ui.access( () ->
				{
					progressText.setValue( event.getMessage() );
					
					progressBar.addThemeVariants( event.getBarVariant() );
					progressBar.setMax( event.getTotal() + 3 );
					progressBar.setValue( 0 );

					buttonSeccaoUpdateAuto.setEnabled( false );
					buttonSuperAuto.setEnabled( false );
					
					seccaoComboBox.setEnabled( false );
					if ( googleSyncContactsService.getSiieSeccaoInUpdate() != null )
					{
						seccaoComboBox.setValue( googleSyncContactsService.getSiieSeccaoInUpdate() );
					}
				} );
			}
		} );
	}

	private Component createContent()
	{
		Label labelHelp = UIUtils
						.createH4Label( "A sincronização pode demorar alguns minutos (depende do número de elementos na seccao). Por favor utilize esta funcionalidade com moderação - por exemplo não sincronizar a mesma secção de novo :-) - pois existe algumas quotas de utilização do serviço do Google que não podemos ultrapassar." );
		FlexBoxLayout flexBoxLayout = new FlexBoxLayout( progressBar, progressText, labelHelp, seccaoComboBox, content );
		flexBoxLayout.setAlignItems( Alignment.CENTER );
		flexBoxLayout.setFlexDirection( FlexDirection.COLUMN );
		flexBoxLayout.setMargin( Horizontal.AUTO );
		flexBoxLayout.setPadding( Uniform.RESPONSIVE_L );
		return flexBoxLayout;
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

		buttonSeccaoUpdateAuto.setWidthFull();
		buttonSeccaoUpdateAuto.setDisableOnClick( true );
		buttonSeccaoUpdateAuto.addClickListener( e ->
		{
			new Thread( () ->
			{
				googleSyncContactsService
								.startAutoUpdate( siieSeccao, siieService, googleAuthentication, googleContactsService, googleContactGroupsService );
			} ).start();
		} );
		verticalLayout.add( buttonSeccaoUpdateAuto );
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
				// updateGroups();
				progressBar.setValue( 1 );
				// List< SIIEElemento > updateCreateElementos = updateCreateElementos( SIIESeccao.D );
				// syncGroupElemento( SIIESeccao.D, updateCreateElementos );
				progressBar.setValue( 2 );
			}
			catch ( Exception e1 )
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


}
