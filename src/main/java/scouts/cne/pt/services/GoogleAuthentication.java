package scouts.cne.pt.services;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;

@Component
@VaadinSessionScope
public class GoogleAuthentication implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;
	public static final List< String >	SCOPES				=
					Arrays.asList( "https://www.google.com/m8/feeds/", GmailScopes.GMAIL_SEND, PeopleServiceScopes.USERINFO_PROFILE );
	private static final List< String >	SERVER_SCOPES		= Arrays.asList( GmailScopes.GMAIL_SEND );
	private String						accessToken			= null;

	public GoogleAuthentication()
	{
		getLogger().info( "New GoogleAuthentication :: " );
	}

	public String getAccessToken()
	{
		return accessToken;
	}

	public void setAccessToken( String accessToken )
	{
		this.accessToken = accessToken;
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

	public Gmail getGmailService() throws GeneralSecurityException, IOException
	{
		return new Gmail.Builder( getHttpTransport(), getJsonfactry(), getGoogleCredentials() ).setApplicationName( getApplicationName() ).build();
	}

	private HttpCredentialsAdapter getGoogleCredentials()
	{
		OAuth2Credentials auth2Credentials = OAuth2Credentials.create( new AccessToken( accessToken, new Date() ) );
		return new HttpCredentialsAdapter( auth2Credentials );
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

	private String getApplicationName()
	{
		return "cnhefe-122";
	}
}
