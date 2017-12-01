package scouts.cne.pt;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBoxGroup;

import scouts.cne.pt.google.GoogleAuthenticationBean;


public class ImportContacts implements ClickListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 5047491211673390251L;
	private File excelFile;
	private CheckBoxGroup<String> seccoes;
	private static MyUI debugTextArea;

	@Autowired
	private GoogleAuthenticationBean googleAuthentication;

	public ImportContacts(File excelFile, CheckBoxGroup<String> checkBoxGroup, MyUI vaadinUI) {
		super();
		this.excelFile = excelFile;
		this.seccoes = checkBoxGroup;
		this.debugTextArea = vaadinUI;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		String accessToken = (String) VaadinSession.getCurrent().getAttribute("credential");

		try {
			Credential credential = GoogleCredential.getApplicationDefault(googleAuthentication.getHttpTransport(), googleAuthentication.getJsonfactry());
			credential.setAccessToken(accessToken);

			credential.refreshToken();
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy", 8080));

			ContactsService contactsService = new ContactsService(googleAuthentication.getApplicationName());
			contactsService.setOAuth2Credentials(credential);
			Authenticator.setDefault(new Authenticator() {
				@Override
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("62000465", "Thales_1".toCharArray());
				}
			});
			System.setProperty("http.proxyUser", "62000465");
			System.setProperty("http.proxyPassword", "Thales_1");

			// Request the feed
			URLConnection feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full")
					.openConnection(proxy);
			Query myQuery = new Query(feedUrl.getURL());
			myQuery.setMaxResults(2);

			ContactFeed resultFeed;
			resultFeed = contactsService.getFeed(myQuery, ContactFeed.class);
			// Print the results
			System.out.println(resultFeed.getTitle().getPlainText());

		} catch (IOException | ServiceException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
