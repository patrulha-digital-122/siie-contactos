package scouts.cne.pt.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class GoogleAuthenticationBean implements Serializable
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;
	private static final List< String >	SCOPES				= Arrays.asList( "https://www.google.com/m8/feeds/" );
	private Map< String, String >		mapUserGoogleKey	= new HashMap<>();

	public GoogleAuthenticationBean()
	{
	}

	public void addSession( String code )
	{
		String sessionId = VaadinSession.getCurrent().getPushId();
		if ( sessionId == null )
		{
			return;
		}
		System.out.println( "Added sessionId: " + sessionId + " -> " + code );
		mapUserGoogleKey.put( sessionId, code );
	}

	public String getGoogleAcessCode( String sessionId )
	{
		return mapUserGoogleKey.get( sessionId );
	}

	public GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow() throws GeneralSecurityException, IOException
	{
		// Build flow and trigger user authorization request.
		return new GoogleAuthorizationCodeFlow.Builder( getHttpTransport(), getJsonfactry(), getGoogleClientSecrets(), SCOPES )
						.setAccessType( "offline" ).setApprovalPrompt( "force" ).build();
	}

	public NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException
	{
		NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
		return builder.build();
	}

	public JacksonFactory getJsonfactry()
	{
		return JacksonFactory.getDefaultInstance();
	}

	public GoogleClientSecrets getGoogleClientSecrets() throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream( "client_secrets.json" );
		return GoogleClientSecrets.load( getJsonfactry(), new InputStreamReader( in ) );
	}

	public GoogleAuthorizationCodeRequestUrl getGoogleAuthorizationCodeRequestUrl( String sessionId ) throws GeneralSecurityException, IOException
	{
		// TODO Auto-generated method stub
		String urlRedirect = getGoogleClientSecrets().getDetails().getRedirectUris().get( 0 );
		return getGoogleAuthorizationCodeFlow().newAuthorizationUrl().setRedirectUri( urlRedirect ).setState( sessionId );
	}

	public String getApplicationName() throws IOException
	{
		return ( String ) getGoogleClientSecrets().getWeb().get( "project_id" );
	}

	/**
	 * The <b>getGoogleCredentials</b> method returns {@link GoogleCredential}
	 * 
	 * @author anco62000465 2018-01-26
	 * @param code
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public GoogleCredential getGoogleCredentials( String code ) throws IOException
	{
		GoogleClientSecrets googleClientSecrets = getGoogleClientSecrets();
		String urlRedirect = googleClientSecrets.getDetails().getRedirectUris().get( 0 );
		GoogleCredential credential = null;
		try
		{
			TokenResponse response = new AuthorizationCodeTokenRequest( getHttpTransport(), getJsonfactry(),
							new GenericUrl( googleClientSecrets.getDetails().getTokenUri() ), code )
											.setClientAuthentication( new ClientParametersAuthentication(
															googleClientSecrets.getDetails().getClientId(),
															googleClientSecrets.getDetails().getClientSecret() ) )
											.setRedirectUri( urlRedirect ).execute();
			
			credential = new GoogleCredential().setAccessToken( response.getAccessToken() );
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}
		
		return credential;
	}
}
