package scouts.cne.pt;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Person;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AbstractAppRouterLayout;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.app.view.MailingListView;
import scouts.cne.pt.app.view.SIIELoginView;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.layouts.EscolherElementosLayout;
import scouts.cne.pt.layouts.FooterLayout;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.services.SIIEService;

@Route( "" )
@Viewport( "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes" )
@PWA(	name = "CNhEfe",
		shortName = "CNhEfe",
		startPath = SIIELoginView.VIEW_NAME,
		backgroundColor = "#227aef",
		themeColor = "#227aef",
		offlinePath = "offline-page.html",
		offlineResources =
		{ "images/offline-login-banner.png" },
		enableInstallPrompt = true,
		display = "standalone",
		description = "Esta app permite codificar texto utilizando varias tipos de fontes" )
public class MainView extends AbstractAppRouterLayout implements HasLogger, HasUrlParameter< String >, BeforeEnterObserver
{
	/**
	 *
	 */
	private static final long				serialVersionUID	= -8505226283440302479L;
	VaadinResponse							currentResponse;
	public static String					AUTH_COOKIE_CODE	= "CODE";
	public static String					parameterSIIE_FILE	= "siieFile=";
	public static String					parameterSHEET_ID	= "sheetId=";
	private BrowserWindowOpener				browserWindowOpener;
	private final SIIEService				siieService;
	private final GoogleAuthenticationBean	googleAuthentication;
	private EscolherElementosLayout			elementosLayout;
	private FooterLayout					importarLayout;
	private Button							btnUpdate;
	private String							siieLocalFile;

	public MainView( @Autowired GoogleAuthenticationBean googleAuthentication, @Autowired SIIEService siieService )
	{
		this.googleAuthentication = googleAuthentication;
		this.siieService = siieService;
		
		getLogger().info( "Logged in -> " + siieService.isLogged() );
		// setSpacing( false );
		// // setMargin( new MarginInfo( false, true, false, true ) );
		// setSizeFull();
		// setAlignItems( Alignment.CENTER );
		// // setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		//
		// elementosLayout = new EscolherElementosLayout( this, siieService );
		// importarLayout = new FooterLayout( elementosLayout, googleAuthentication );
		// try
		// {
		// final GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
		// googleAuthentication.getGoogleAuthorizationCodeRequestUrl(
		// com.vaadin.flow.component.UI.getCurrent().getUIId() );
		// browserWindowOpener = new BrowserWindowOpener( googleAuthorizationCodeRequestUrl.build() );
		// browserWindowOpener.setFeatures( "height=600,width=600" );
		// // browserWindowOpener.extend( importarLayout.getBtnAutorizacao() );
		// }
		// catch ( GeneralSecurityException | IOException e )
		// {
		// e.printStackTrace();
		// }
		// // process parameters
		// if ( siieLocalFile != null )
		// {
		// VaadinSession.getCurrent().access( () ->
		// {
		// siieService.setFile( new File( siieLocalFile ) );
		// try
		// {
		// siieService.loadExploradoresSIIE();
		// elementosLayout.refreshGrids();
		// }
		// catch ( final Exception e )
		// {
		// showError( e );
		// }
		// } );
		// }
		// if ( siieGDriveFile != null )
		// {
		// loadContacts( siieGDriveFile );
		// }
		//
		// btnUpdate = new Button( "Actualizar tabela", VaadinIcon.REFRESH.create() );
		//
		// if ( siieLocalFile == null && siieGDriveFile == null )
		// {
		// final UploadFileLayout uploadFileLayout = new UploadFileLayout( siieService, elementosLayout );
		// final SplitLayout verticalSplitPanel = new SplitLayout( uploadFileLayout, elementosLayout );
		// verticalSplitPanel.setOrientation( Orientation.VERTICAL );
		// verticalSplitPanel.setSplitterPosition( 20 );
		// verticalSplitPanel.setSizeFull();
		//
		// final SplitLayout verticalSplitPanel2 = new SplitLayout( verticalSplitPanel, importarLayout );
		// verticalSplitPanel2.setOrientation( Orientation.VERTICAL );
		// verticalSplitPanel2.setSplitterPosition( 85 );
		// verticalSplitPanel2.setSizeFull();
		//
		// add( verticalSplitPanel2 );
		// }
		// else
		// {
		//
		// final SplitLayout verticalSplitPanel = new SplitLayout( elementosLayout, importarLayout );
		// verticalSplitPanel.setOrientation( Orientation.HORIZONTAL );
		// verticalSplitPanel.setSplitterPosition( 85 );
		// if ( siieGDriveFile != null )
		// {
		// btnUpdate.setWidth( "100%" );
		// btnUpdate.addClickListener( event -> loadContacts( siieGDriveFile ) );
		// btnUpdate.setDisableOnClick( true );
		// add( btnUpdate );
		// }
		// add( verticalSplitPanel );
		// }
	}

	private void loadContacts( String siieGDriveFile )
	{
		VaadinSession.getCurrent().access( () ->
		{
			try
			{
				siieService.loadElementosGDrive( siieGDriveFile );
			}
			catch ( final SIIIEImporterException e )
			{
				showError( e );
			}
			elementosLayout.refreshGrids();
			btnUpdate.setEnabled( true );
		} );
	}

	public void receiveGoogleCode( String code, String embedId )
	{
		getLogger().info( "Received code: " + code + " | embedId: " + embedId );
		googleAuthentication.addSession( code );
		browserWindowOpener.remove();
		importarLayout.getBtnAutorizacao().setVisible( false );
		importarLayout.getBtImportacao().setVisible( true );
		importarLayout.getBtnEmailer().setVisible( true );
		VaadinSession.getCurrent().access( () ->
		{
			PeopleService peopleService;
			try
			{
				peopleService = googleAuthentication.getPeopleService();
				final Person person = peopleService.people().get( "people/me" ).setPersonFields( "names,emailAddresses" ).execute();
				final List< EmailAddress > emailAddresses = person.getEmailAddresses();
				for ( final EmailAddress emailAddress : emailAddresses )
				{
					if ( emailAddress.getMetadata() != null && emailAddress.getMetadata().getPrimary() != null &&
						emailAddress.getMetadata().getPrimary() )
					{
						googleAuthentication.setUserEmail( emailAddress.getValue() );
						if ( !person.getNames().isEmpty() )
						{
							googleAuthentication.setUserFullName( person.getNames().get( 0 ).getDisplayName() );
						}
						break;
					}
				}
				getLogger().info( "Hello '{}' with email '{}'.", googleAuthentication.getUserFullName(), googleAuthentication.getUserEmail() );
				showTray( "Olá " + googleAuthentication.getUserFullName() );
			}
			catch ( final Exception e )
			{
				e.printStackTrace();
			}
		} );
	}

	public void updateSelectionados( int iSelecionados )
	{
		importarLayout.getBtImportacao().setText( String.format( "Iniciar Importação (%d)", iSelecionados ) );
		importarLayout.getBtImportacao().setEnabled( iSelecionados > 0 );
		importarLayout.getBtImportacaoVCard().setText( String.format( "Download como VCard (%d)", iSelecionados ) );
		importarLayout.getBtImportacaoVCard().setEnabled( iSelecionados > 0 );
		importarLayout.getBtnCopyMailingList().setText( String.format( "Mailing list (%d)", iSelecionados ) );
		importarLayout.getBtnCopyMailingList().setEnabled( iSelecionados > 0 );
		importarLayout.getBtnEmailer().setEnabled( iSelecionados > 0 );
		importarLayout.getBtnAuthFile().setEnabled( iSelecionados > 0 );
	}

	@Override
	public void setParameter( BeforeEvent event, @OptionalParameter String parameter )
	{
		if ( parameter != null )
		{
			if ( parameter.startsWith( parameterSIIE_FILE ) )
			{
				siieLocalFile = parameter.replace( parameterSIIE_FILE, "" );
			}
			else if ( parameter.startsWith( parameterSHEET_ID ) )
			{
				// siieGDriveFile = parameter.replace( parameterSHEET_ID, "" );
			}
		}
		getLogger().info( "siieLocalFile :: " + siieLocalFile );

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.flow.component.applayout.AbstractAppRouterLayout#configure(com.vaadin.flow.component.applayout.
	 * AppLayout, com.vaadin.flow.component.applayout.AppLayoutMenu)
	 */
	@Override
	protected void configure( AppLayout appLayout, AppLayoutMenu appLayoutMenu )
	{
		final Label branding = new Label();
		branding.setText( UI.getCurrent().getTranslation( "application.id" ) );
		appLayout.setBranding( branding );
		// Master Data
		appLayoutMenu.addMenuItem( new AppLayoutMenuItem( SIIELoginView.VIEW_ICON, UI.getCurrent().getTranslation( SIIELoginView.VIEW_DISPLAY_NAME ),
						SIIELoginView.VIEW_NAME ) );
		AppLayoutMenuItem item = new AppLayoutMenuItem( MailingListView.VIEW_ICON,
						UI.getCurrent().getTranslation( MailingListView.VIEW_DISPLAY_NAME ), MailingListView.VIEW_NAME );
		item.setEnabled( false );
		appLayoutMenu.addMenuItem( item );
		getElement().addEventListener( "search-focus", e ->
		{
			appLayout.getElement().getClassList().add( "hide-navbar" );
		} );
		getElement().addEventListener( "search-blur", e ->
		{
			appLayout.getElement().getClassList().remove( "hide-navbar" );
		} );

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.flow.router.internal.BeforeEnterHandler#beforeEnter(com.vaadin.flow.router.BeforeEnterEvent)
	 */
	@Override
	public void beforeEnter( BeforeEnterEvent event )
	{
		getLogger().info( "Logged in -> " + siieService.isLogged() );
		if ( siieService.isLogged() )
		{
			getLogger().info( "Redirect to login" );
			event.rerouteTo( SIIELoginView.class );
		}
		else
		{
			event.rerouteTo( MailingListView.class );
		}
	}

	@Override
	public void showRouterLayoutContent( HasElement content )
	{
		super.showRouterLayoutContent( content );
		// if ( content instanceof HasConfirmation )
		// {
		// ( ( HasConfirmation ) content ).setConfirmDialog( this.confirmDialog );
		// }
	}
}
