package scouts.cne.pt;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.leif.headertags.Meta;
import org.vaadin.leif.headertags.MetaTags;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.Responsive;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.google.GoogleServerAuthenticationBean;
import scouts.cne.pt.layouts.EscolherElementosLayout;
import scouts.cne.pt.layouts.ImportarLayout;
import scouts.cne.pt.layouts.UploadFileLayout;
import scouts.cne.pt.services.SIIEService;

@SpringUI
@Push
@Title( "SIIE - importer" )
// <meta name="google-site-verification" content="FOqGrvVOczGenSzPckQdRiNI8Qv_RJWd8PteDcezCKk" />
@MetaTags(
{ @Meta( name = "google-site-verification", content = "FOqGrvVOczGenSzPckQdRiNI8Qv_RJWd8PteDcezCKk" ) } )
public class MyUI extends UI implements HasLogger
{
	/**
	 *
	 */
	private static final long				serialVersionUID	= -8505226283440302479L;
	VaadinResponse							currentResponse;
	public static String					AUTH_COOKIE_CODE	= "CODE";
	private BrowserWindowOpener				browserWindowOpener;
	@Autowired
	SIIEService								siieService;
	@Autowired
	private GoogleAuthenticationBean		googleAuthentication;
	@Autowired
	private GoogleServerAuthenticationBean	googleServerAuthentication;
	private EscolherElementosLayout			elementosLayout;
	private ImportarLayout					importarLayout;

	@Override
	protected void init( VaadinRequest vaadinRequest )
	{
		getLogger().info( "EmbedId " + getEmbedId() );
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing( true );
		mainLayout.setMargin( new MarginInfo( false, true, false, true ) );
		mainLayout.setSizeFull();
		mainLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		setContent( mainLayout );
		setTheme( "mytheme" );
		Responsive.makeResponsive( mainLayout );
		elementosLayout = new EscolherElementosLayout( getEmbedId(), siieService );
		importarLayout = new ImportarLayout( elementosLayout, googleAuthentication );
		try
		{
			GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
							googleAuthentication.getGoogleAuthorizationCodeRequestUrl( getEmbedId() );
			browserWindowOpener = new BrowserWindowOpener( googleAuthorizationCodeRequestUrl.build() );
			browserWindowOpener.setFeatures( "height=600,width=600" );
			browserWindowOpener.extend( importarLayout.getBtImportacao() );
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}

		UploadFileLayout uploadFileLayout = new UploadFileLayout( siieService, elementosLayout );
		mainLayout.addComponent( uploadFileLayout );
		mainLayout.setExpandRatio( uploadFileLayout, 1 );

		mainLayout.addComponent( elementosLayout );
		mainLayout.setExpandRatio( elementosLayout, 4 );

		mainLayout.addComponent( importarLayout );
		mainLayout.setExpandRatio( importarLayout, 1 );

		//
		VaadinService.getCurrent().setSystemMessagesProvider( new SystemMessagesProvider()
		{
			private static final long serialVersionUID = -9118140641761605204L;

			@Override
			public SystemMessages getSystemMessages( SystemMessagesInfo systemMessagesInfo )
			{
				CustomizedSystemMessages messages = new CustomizedSystemMessages();
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
				StringBuilder cause = new StringBuilder();
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
				Window window = new Window( "Erro" );
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

	public void receiveGoogleCode( String code, String embedId )
	{
		getLogger().info( "Received code: " + code + " | embedId: " + embedId );
		googleAuthentication.addSession( code );
		browserWindowOpener.remove();
		push();
		if ( elementosLayout.getSelecionados() > 0 )
		{
			importarLayout.importProcess();
		}
	}

	public void updateSelectionados( int iSelecionados )
	{
		importarLayout.getBtImportacao().setCaption( String.format( "Iniciar Importação (%d)", iSelecionados ) );
		importarLayout.getBtImportacao().setEnabled( iSelecionados > 0 );
		importarLayout.getBtImportacaoVCard().setCaption( String.format( "Download como VCard (%d)", iSelecionados ) );
		importarLayout.getBtImportacaoVCard().setEnabled( iSelecionados > 0 );
	}
}
