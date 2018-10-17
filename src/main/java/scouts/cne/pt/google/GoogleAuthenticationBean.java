package scouts.cne.pt.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import scouts.cne.pt.app.HasLogger;

@Service
public class GoogleAuthenticationBean implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;
	private static final List< String >	SCOPES				=
					Arrays.asList( "https://www.google.com/m8/feeds/", GmailScopes.GMAIL_SEND, PeopleServiceScopes.USERINFO_PROFILE );
	private static final List< String >	SERVER_SCOPES		= Arrays.asList( GmailScopes.GMAIL_SEND );
	private String						refreshToken		= null;
	private String						userFullName		= "";
	private String						userEmail			= "";
	private GoogleCredential			googleCredential;

	public GoogleAuthenticationBean()
	{
		getLogger().info( "New GoogleAuthenticationBean :: " );
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
		final NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
		return builder.build();
	}

	public JacksonFactory getJsonfactry()
	{
		return JacksonFactory.getDefaultInstance();
	}

	private GoogleClientSecrets getGoogleClientSecrets() throws IOException
	{
		GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets();
		final String strGoogleClientSecrets = System.getenv().get( "GOOGLE_CLIENT_SECRETS" );
		if ( StringUtils.isNotBlank( strGoogleClientSecrets ) )
		{
			try ( StringReader stringReader = new StringReader( strGoogleClientSecrets ) )
			{
				return GoogleClientSecrets.load( getJsonfactry(), stringReader );
			}
			catch ( final Exception e )
			{
				printError( e );
			}
		}
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final InputStream in = classLoader.getResourceAsStream( "client_secrets.json" );
		try ( InputStreamReader inputStreamReader = new InputStreamReader( in ) )
		{
			googleClientSecrets = GoogleClientSecrets.load( getJsonfactry(), inputStreamReader );
		}
		catch ( final Exception e )
		{
			printError( e );
		}
		return googleClientSecrets;
	}

	public GoogleAuthorizationCodeRequestUrl getGoogleAuthorizationCodeRequestUrl( int i ) throws GeneralSecurityException, IOException
	{
		return getGoogleAuthorizationCodeFlow().newAuthorizationUrl().setRedirectUri( getRedicetUrl() ).setState( String.valueOf( i ) );
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
	public GoogleCredential getGoogleCredentials() throws IOException
	{
		if ( googleCredential == null )
		{
			final GoogleClientSecrets googleClientSecrets = getGoogleClientSecrets();
			try
			{
				final TokenResponse response = new AuthorizationCodeTokenRequest( getHttpTransport(), getJsonfactry(),
								new GenericUrl( googleClientSecrets.getDetails().getTokenUri() ), refreshToken )
												.setClientAuthentication( new ClientParametersAuthentication(
																googleClientSecrets.getDetails().getClientId(),
																googleClientSecrets.getDetails().getClientSecret() ) )
												.setRedirectUri( getRedicetUrl() ).execute();
				refreshToken = response.getRefreshToken();
				// FirebaseManager.getInstance().addCode(uiId, googleClientSecrets.getDetails().getClientId(),
				// refreshToken);
				googleCredential = new GoogleCredential().setAccessToken( response.getAccessToken() );
			}
			catch ( GeneralSecurityException | IOException e )
			{
				e.printStackTrace();
			}
		}
		return googleCredential;
	}

	public Gmail getGmailService() throws GeneralSecurityException, IOException
	{
		return new Gmail.Builder( getHttpTransport(), getJsonfactry(), getGoogleCredentials() ).setApplicationName( getApplicationName() ).build();
	}

	/**
	 * 
	 * The <b>getPeopleService</b> method returns {@link PeopleService}
	 * 
	 * @author anco62000465 2018-09-26
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public PeopleService getPeopleService() throws GeneralSecurityException, IOException
	{
		return new PeopleService.Builder( getHttpTransport(), getJsonfactry(), getGoogleCredentials() ).setApplicationName( getApplicationName() )
						.build();
	}

	/**
	 * Getter for userFullName
	 * 
	 * @author anco62000465 2018-09-26
	 * @return the userFullName {@link String}
	 */
	public String getUserFullName()
	{
		return userFullName;
	}

	/**
	 * Setter for userFullName
	 * 
	 * @author anco62000465 2018-09-26
	 * @param userFullName the userFullName to set
	 */
	public void setUserFullName( String userFullName )
	{
		this.userFullName = userFullName;
	}

	/**
	 * Getter for userEmail
	 * 
	 * @author anco62000465 2018-09-26
	 * @return the userEmail {@link String}
	 */
	public String getUserEmail()
	{
		return userEmail;
	}

	/**
	 * Setter for userEmail
	 * 
	 * @author anco62000465 2018-09-26
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail( String userEmail )
	{
		this.userEmail = userEmail;
	}
}
