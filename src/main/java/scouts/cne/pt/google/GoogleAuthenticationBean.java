package scouts.cne.pt.google;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import scouts.cne.pt.app.HasLogger;

@SpringComponent
@UIScope
public class GoogleAuthenticationBean implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;
	private static final List< String >	SCOPES				= Arrays.asList( "https://www.google.com/m8/feeds/" );
	private static final List< String >	SERVER_SCOPES		= Arrays.asList( "https://www.googleapis.com/auth/plus.me" );
	private String						refreshToken		= null;

	public GoogleAuthenticationBean()
	{
	}

	public void addSession( String code )
	{
		refreshToken = code;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken()
	{
		return refreshToken;
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

	private GoogleClientSecrets getGoogleClientSecrets() throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream( "client_secrets.json" );
		GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets();
		String strGoogleClientSecrets = System.getenv().get( "GOOGLE_CLIENT_SECRETS" );
		if ( StringUtils.isNotBlank( strGoogleClientSecrets ) )
		{
			try ( StringReader stringReader = new StringReader( strGoogleClientSecrets ) )
			{
				return GoogleClientSecrets.load( getJsonfactry(), stringReader );
			}
			catch ( Exception e )
			{
				printError( e );
			}
		}
		try ( InputStreamReader inputStreamReader = new InputStreamReader( in ) )
		{
			googleClientSecrets = GoogleClientSecrets.load( getJsonfactry(), inputStreamReader );
		}
		catch ( Exception e )
		{
			printError( e );
		}
		return googleClientSecrets;
	}

	public GoogleAuthorizationCodeRequestUrl getGoogleAuthorizationCodeRequestUrl( String sessionId ) throws GeneralSecurityException, IOException
	{
		// TODO Auto-generated method stub
		return getGoogleAuthorizationCodeFlow().newAuthorizationUrl().setRedirectUri( getRedicetUrl() ).setState( sessionId );
	}

	private String getRedicetUrl() throws IOException
	{
		String urlRedirect = System.getenv().get( "GOOGLE_REDIRECT_URL" );
		if ( urlRedirect == null )
		{
			urlRedirect = getGoogleClientSecrets().getDetails().getRedirectUris().get( 0 );
		}
		return urlRedirect;
	}

	public String getApplicationName() throws IOException
	{
		return ( String ) getGoogleClientSecrets().getWeb().get( "project_id" );
	}

	public String getGoogleClientId() throws IOException
	{
		return getGoogleClientSecrets().getDetails().getClientId();
	}

	/**
	 * The <b>getGoogleCredentials</b> method returns {@link GoogleCredential}
	 *
	 * @author anco62000465 2018-01-26
	 * @param uiId
	 * @param code
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public GoogleCredential getGoogleCredentials( Integer uiId ) throws IOException
	{
		GoogleClientSecrets googleClientSecrets = getGoogleClientSecrets();
		GoogleCredential credential = null;
		try
		{
			TokenResponse response = new AuthorizationCodeTokenRequest( getHttpTransport(), getJsonfactry(),
							new GenericUrl( googleClientSecrets.getDetails().getTokenUri() ), refreshToken )
											.setClientAuthentication( new ClientParametersAuthentication(
															googleClientSecrets.getDetails().getClientId(),
															googleClientSecrets.getDetails().getClientSecret() ) )
											.setRedirectUri( getRedicetUrl() ).execute();
			refreshToken = response.getRefreshToken();
			// FirebaseManager.getInstance().addCode(uiId, googleClientSecrets.getDetails().getClientId(),
			// refreshToken);
			credential = new GoogleCredential().setAccessToken( response.getAccessToken() );
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}
		return credential;
	}

	private Credential authorizeServer()
	{
		String strGoogleClientSecrets = System.getenv().get( "GOOGLE_SERVER_SECRETS" );

		if ( StringUtils.isNotBlank( strGoogleClientSecrets ) )
		{
			try ( ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( strGoogleClientSecrets.getBytes( "UTF-8" ) ) )
			{
				return GoogleCredential.fromStream( byteArrayInputStream ).createScoped( SERVER_SCOPES );
			}
			catch ( Exception e )
			{
				printError( e );
			}
		}
		return null;
	}

	public Gmail getGmailService() throws GeneralSecurityException, IOException
	{
		return new Gmail.Builder( getHttpTransport(), getJsonfactry(), authorizeServer() ).setApplicationName( getApplicationName() ).build();
	}
}
