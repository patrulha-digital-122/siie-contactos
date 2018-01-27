package scouts.cne.pt;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.Query;
import com.google.gdata.client.Query.CustomParameter;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;
import com.vaadin.annotations.Push;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;

@SpringUI
public class MyUI extends UI
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -8505226283440302479L;
	public final static String			VIEW_NAME			= "main";
	@Autowired
	SIIEService							siieService;
	private TextArea					debugTextArea;
	private Label						debugLabel;
	private VerticalLayout				afterLoginLayout;
	private StringBuilder				sbLog;
	private Grid< Explorador >			gridLobitos;
	private Grid< Explorador >			gridExploradores;
	private Grid< Explorador >			gridPioneiros;
	private Grid< Explorador >			gridCaminheiros;
	private Grid< Explorador >			gridDirigentes;
	@Autowired
	private GoogleAuthenticationBean	googleAuthentication;
	VerticalLayout						rootLayout;
	private BrowserWindowOpener			browserWindowOpener;

	@Override
	protected void init( VaadinRequest vaadinRequest )
	{
		rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		rootLayout.addComponent( getUploadSIIEFileMenu() );
		System.out.println( "New Session: " + getEmbedId() );
		setContent( rootLayout );
		getPage().setTitle( "SIIE - importer" );
		debugLabel = new Label();
		debugLabel.setSizeFull();
		// Button btAuthentication = new Button("Conceder autorização");
		//
		// sbLog = new StringBuilder();
		//
		// try {
		// excelFile = File.createTempFile("stuff", ".xlsx");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// afterLoginLayout = new VerticalLayout();
		// afterLoginLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		// if (VaadinSession.getCurrent().getAttribute("credential") == null) {
		// afterLoginLayout.setVisible(false);
		// }
		// List<String> data = Arrays.asList("Lobitos", "Exploradores", "Pioneiros",
		// "Caminheiros", "Dirigentes");
		// CheckBoxGroup<String> checkBoxGroup = new CheckBoxGroup<>("Selecione as
		// secções que pretende importar", data);
		// Button btImportacao = new Button("Iniciar importação");
		//
		// debugTextArea = new TextArea();
		// debugTextArea.setReadOnly(true);
		// debugTextArea.setValueChangeMode(ValueChangeMode.LAZY);
		// debugTextArea.setWordWrap(true);
		//
		// //afterLoginLayout.addComponents(upload, checkBoxGroup, btImportacao);
		//
		// // login.addClickListener( new LoginClickListener( afterLoginLayout,
		// // name ) );
		// btImportacao.addClickListener(new ImportContacts(excelFile, checkBoxGroup,
		// this));
		//
		// LoginClickListener listener = new LoginClickListener(this);
		// btAuthentication.addClickListener(new ClickListener() {
		//
		// /**
		// *
		// */
		// private static final long serialVersionUID = -290402141178194857L;
		//
		// @Override
		// public void buttonClick(ClickEvent event) {
		// //FirebaseManager.getInstance().addLogMessage("Novo Pedido de autorização");
		// GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl;
		// try {
		// googleAuthorizationCodeRequestUrl =
		// googleAuthentication.getGoogleAuthorizationCodeRequestUrl();
		// getUI().getPage().open(googleAuthorizationCodeRequestUrl.build(), "_blank",
		// true);
		// } catch (GeneralSecurityException | IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// });
		//
		// //rootLayout.addComponents(btAuthentication, afterLoginLayout,
		// debugTextArea);
		//
		// //setContent(rootLayout);
		//
		// // A request handler for generating some content
		// VaadinSession.getCurrent().addRequestHandler(listener);
		// FirebaseManager.getInstance().addLogMessage("App started");
	}

	private Component getUploadSIIEFileMenu()
	{
		FileUploader fileUploader = new FileUploader( this, siieService );
		Upload upload = new Upload( "Upload Ficheiro .xlsx do SIIE", fileUploader );
		upload.addSucceededListener( fileUploader );
		upload.setImmediateMode( true );
		return upload;
	}

	public void receiveGoogleCode( String code )
	{
		try
		{
			GoogleCredential credential = googleAuthentication.getGoogleCredentials( code );
			String applicationName = googleAuthentication.getApplicationName();
			ContactsService contactsService = new ContactsService( applicationName );
			contactsService.setOAuth2Credentials( credential );
			URLConnection feedUrl = new URL( "https://www.google.com/m8/feeds/contacts/default/full" ).openConnection();
			Query myQuery = new Query( feedUrl.getURL() );
			myQuery.addCustomParameter( new CustomParameter( "gd:phoneNumber", "918880044" ) );
			ContactFeed resultFeed;
			resultFeed = contactsService.getFeed( myQuery, ContactFeed.class );
			// Print the results
			String caption = "Olá " + resultFeed.getTitle().getPlainText();
			Notification.show( caption, Type.HUMANIZED_MESSAGE );
			debugLabel.setValue( caption );
			debugLabel.markAsDirty();
			Notification.show( "Contactos encontrados: " + resultFeed.getTotalResults() );
			System.out.println( "Contactos encontrados: " + resultFeed.getTotalResults() );
			System.out.println( gridLobitos.getSelectedItems().size() );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		catch ( ServiceException e )
		{
			e.printStackTrace();
		}
	}

	public void showMenus()
	{
		TabSheet tabsheet = new TabSheet();
		siieService.loadExploradoresSIIE();
		for ( SECCAO seccao : SECCAO.values() )
		{
			// Tab dos Lobitos
			VerticalLayout tabLobitos = new VerticalLayout();
			gridLobitos = new Grid<>( Explorador.class );
			gridLobitos.setSizeFull();
			gridLobitos.setSelectionMode( SelectionMode.MULTI );
			tabLobitos.addComponent( gridLobitos );
			
			// Create a header row to hold column filters
			gridLobitos.prependHeaderRow();

			gridLobitos.setDataProvider( DataProvider.fromStream( siieService.getMapSeccaoElemento().get( seccao ).stream() ) );
			gridLobitos.setHeaderVisible( true );
			tabsheet.addTab( tabLobitos, seccao.getNome() + " - " + siieService.getMapSeccaoElemento().get( seccao ).size() );
		}
		rootLayout.addComponent( tabsheet );
		rootLayout.addComponent( debugLabel );
		Button btAuthentication = new Button( "Conceder autorização" );
		try
		{
			if ( googleAuthentication.getGoogleAcessCode( getCurrent().getId() ) == null )
			{
				GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
								googleAuthentication.getGoogleAuthorizationCodeRequestUrl( getEmbedId() );
				browserWindowOpener = new BrowserWindowOpener( googleAuthorizationCodeRequestUrl.build() );
				browserWindowOpener.setFeatures( "height=600,width=600" );
				browserWindowOpener.extend( btAuthentication );
			}
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}
		debugTextArea = new TextArea();
		debugTextArea.setReadOnly( true );
		debugTextArea.setValueChangeMode( ValueChangeMode.LAZY );
		debugTextArea.setWordWrap( true );
		debugTextArea.setResponsive( true );
		rootLayout.addComponent( btAuthentication );
		rootLayout.addComponent( debugTextArea );
	}

	public void showSecondPhaseOptions()
	{
		afterLoginLayout.setVisible( true );
	}

	public void addDebugInfo( String text )
	{
		access( new Runnable()
		{
			@Override
			public void run()
			{
				sbLog.append( text ).append( "\n" );
				debugTextArea.setValue( sbLog.toString() );
			}
		} );
	}
}
