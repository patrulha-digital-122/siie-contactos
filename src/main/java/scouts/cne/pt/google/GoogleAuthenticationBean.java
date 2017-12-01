package scouts.cne.pt.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class GoogleAuthenticationBean implements Serializable  {

	/**
	 *
	 */
	private static final long serialVersionUID = -4266591353450666223L;
	private static final List<String> SCOPES = Arrays.asList("https://www.google.com/m8/feeds/");



	public GoogleAuthenticationBean() {
	}

	public GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow() throws GeneralSecurityException, IOException {
		// Build flow and trigger user authorization request.
		return new GoogleAuthorizationCodeFlow.Builder(getHttpTransport(), getJsonfactry(),
				getGoogleClientSecrets(), SCOPES)
				.setAccessType("online")
				.setApprovalPrompt("auto").build();
		//					// LocalServerReceiver receiver = new
		//					// LocalServerReceiver.Builder().setHost(
		//					// "localhost").setPort(9000).build();
		//
		//
		//					GoogleAuthorizationCodeRequestUrl newAuthorizationUrl = flow.newAuthorizationUrl()
		//							.setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0));
		//					ui.getUI().getPage().open(newAuthorizationUrl.build(), "_blank", true);
		//
		//
		//				} catch (IOException e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
	}

	public NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
		NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
		builder.trustCertificates(GoogleUtils.getCertificateTrustStore());
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy", 8080));
		Authenticator.setDefault(
				new Authenticator() {
					@Override
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								"62000465", "Thales_1".toCharArray());
					}
				}
				);
		System.setProperty("http.proxyUser", "62000465");
		System.setProperty("http.proxyPassword", "Thales_1");
		builder.setProxy(proxy);
		return builder.build();
	}

	public JacksonFactory getJsonfactry() {
		return JacksonFactory.getDefaultInstance();
	}

	public GoogleClientSecrets getGoogleClientSecrets() throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream("client_secrets.json");
		return GoogleClientSecrets.load(getJsonfactry(), new InputStreamReader(in));

	}

	public GoogleAuthorizationCodeRequestUrl getGoogleAuthorizationCodeRequestUrl() throws GeneralSecurityException, IOException {
		// TODO Auto-generated method stub
		return getGoogleAuthorizationCodeFlow().newAuthorizationUrl()
				.setRedirectUri(getGoogleClientSecrets().getDetails().getRedirectUris().get(1));
	}

	public String getApplicationName() throws IOException {
		return (String) getGoogleClientSecrets().getWeb().get("project_id");
	}
}
