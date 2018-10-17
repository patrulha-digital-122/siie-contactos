package scouts.cne.pt;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.leif.headertags.Meta;
import org.vaadin.leif.headertags.MetaTags;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Person;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.Responsive;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.google.GoogleServerAuthenticationBean;
import scouts.cne.pt.layouts.EscolherElementosLayout;
import scouts.cne.pt.layouts.FooterLayout;
import scouts.cne.pt.layouts.UploadFileLayout;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.services.SIIEService;

@Route( "inicio" )
@UIScope
@Push
@Title( "SIIE - importer" )
@PreserveOnRefresh
@MetaTags(
{ @Meta( name = "google-site-verification", content = "FOqGrvVOczGenSzPckQdRiNI8Qv_RJWd8PteDcezCKk" ) } )
public class MyUI extends VerticalLayout implements HasLogger, HasUrlParameter< String >
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
	@Autowired
	SIIEService								siieService;
	@Autowired
	private GoogleAuthenticationBean		googleAuthentication;
	@Autowired
	private GoogleServerAuthenticationBean	googleServerAuthentication;
	private EscolherElementosLayout			elementosLayout;
	private FooterLayout					importarLayout;
	private Button							btnUpdate;
	private String							siieLocalFile;
	private String							siieGDriveFile;

	public MyUI()
	{

		setSpacing( false );
		setMargin( new MarginInfo( false, true, false, true ) );
		setSizeFull();
		setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		Responsive.makeResponsive( this );

		elementosLayout = new EscolherElementosLayout( this, siieService );
		importarLayout = new FooterLayout( elementosLayout, googleAuthentication );
		try
		{
			final GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
							googleAuthentication.getGoogleAuthorizationCodeRequestUrl( com.vaadin.flow.component.UI.getCurrent().getUIId() );
			browserWindowOpener = new BrowserWindowOpener( googleAuthorizationCodeRequestUrl.build() );
			browserWindowOpener.setFeatures( "height=600,width=600" );
			browserWindowOpener.extend( importarLayout.getBtnAutorizacao() );
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}
		// process parameters
		if ( siieLocalFile != null )
		{
			VaadinSession.getCurrent().access( () ->
			{
				siieService.setFile( new File( siieLocalFile ) );
				try
				{
					siieService.loadExploradoresSIIE();
					elementosLayout.refreshGrids();
				}
				catch ( final Exception e )
				{
					showError( e );
				}
			} );
		}
		if ( siieGDriveFile != null )
		{
			loadContacts( siieGDriveFile );
		}

		btnUpdate = new Button( "Actualizar tabela", VaadinIcons.REFRESH );

		if ( siieLocalFile == null && siieGDriveFile == null )
		{
			final UploadFileLayout uploadFileLayout = new UploadFileLayout( siieService, elementosLayout );
			final VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel( uploadFileLayout, elementosLayout );
			verticalSplitPanel.setSplitPosition( 20 );

			final VerticalSplitPanel verticalSplitPanel2 = new VerticalSplitPanel( verticalSplitPanel, importarLayout );
			verticalSplitPanel2.setSplitPosition( 85 );

			addComponent( verticalSplitPanel2 );
		}
		else
		{

			final VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel( elementosLayout, importarLayout );
			verticalSplitPanel.setSplitPosition( 85 );
			if ( siieGDriveFile != null )
			{
				btnUpdate.setWidth( "100%" );
				btnUpdate.addClickListener( event -> loadContacts( siieGDriveFile ) );
				btnUpdate.setDisableOnClick( true );
				addComponent( btnUpdate );
				setExpandRatio( btnUpdate, 1 );
			}
			addComponent( verticalSplitPanel );
			setExpandRatio( verticalSplitPanel, 10 );
		}

		//
		VaadinService.getCurrent().setSystemMessagesProvider( new SystemMessagesProvider()
		{
			private static final long serialVersionUID = -9118140641761605204L;

			@Override
			public SystemMessages getSystemMessages( SystemMessagesInfo systemMessagesInfo )
			{
				final CustomizedSystemMessages messages = new CustomizedSystemMessages();
				messages.setCommunicationErrorCaption( "Comm Err" );
				messages.setCommunicationErrorMessage( "This is bad." );
				messages.setCommunicationErrorNotificationEnabled( true );
				messages.setCommunicationErrorURL( "http://vaadin.com/" );
				return messages;
			}
		} );
		// Configure the error handler for the UI
		UI.getCurrent().setErrorHandler( new DefaultErrorHandler()
		{
			@Override
			public void error( com.vaadin.server.ErrorEvent event )
			{
				// Find the final cause
				final StringBuilder cause = new StringBuilder();
				for ( Throwable t = event.getThrowable(); t != null; t = t.getCause() )
				{
					if ( t.getCause() == null )
					{
						cause.append( "<b>" );
						cause.append( t.getClass().getName() );
						cause.append( "</b>: " );
						cause.append( t.getMessage() );
					}
					t.printStackTrace();
				}
				// Display the error message in a custom fashion
				final Window window = new Window( "Erro" );
				window.center();
				window.setResizable( true );
				window.setModal( true );
				window.setHeight( "600px" );
				window.setWidth( "600px" );
				window.setContent( new VerticalLayout( new Label( cause.toString(), ContentMode.HTML ) ) );
				getUI().addWindow( window );
			}
		} );
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
		importarLayout.getBtImportacao().setCaption( String.format( "Iniciar Importação (%d)", iSelecionados ) );
		importarLayout.getBtImportacao().setEnabled( iSelecionados > 0 );
		importarLayout.getBtImportacaoVCard().setCaption( String.format( "Download como VCard (%d)", iSelecionados ) );
		importarLayout.getBtImportacaoVCard().setEnabled( iSelecionados > 0 );
		importarLayout.getBtnCopyMailingList().setCaption( String.format( "Mailing list (%d)", iSelecionados ) );
		importarLayout.getBtnCopyMailingList().setEnabled( iSelecionados > 0 );
		importarLayout.getBtnEmailer().setEnabled( iSelecionados > 0 );
		importarLayout.getBtnAuthFile().setEnabled( iSelecionados > 0 );
	}

	@Override
	public void setParameter( BeforeEvent event, String parameter )
	{
		if ( parameter != null )
		{
			if ( parameter.startsWith( parameterSIIE_FILE ) )
			{
				siieLocalFile = parameter.replace( parameterSIIE_FILE, "" );
			}
			else if ( parameter.startsWith( parameterSHEET_ID ) )
			{
				siieGDriveFile = parameter.replace( parameterSHEET_ID, "" );
			}
		}
	}
}
