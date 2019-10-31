package scouts.cne.pt.services;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.gdata.client.contacts.ContactsService;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.google.GoogleAuthInfo;

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
	private GoogleAuthInfo				googleAuthInfo		= null;
	public static String				PERSON_FIELDS		=
					"addresses,birthdays,emailAddresses,events,genders,imClients,memberships,names,phoneNumbers,relations,userDefined";

	public GoogleAuthentication()
	{
		getLogger().info( "New GoogleAuthentication :: " );
	}

	public String getClientId()
	{
		return StringUtils.trimToEmpty( System.getenv().get( "GOOGLE_CLIENT_ID" ) );
	}

	/**
	 * Getter for googleAuthInfo
	 * 
	 * @author 62000465 2019-10-28
	 * @return the googleAuthInfo {@link GoogleAuthInfo}
	 */
	public GoogleAuthInfo getGoogleAuthInfo()
	{
		return googleAuthInfo;
	}


	/**
	 * Setter for googleAuthInfo
	 * 
	 * @author 62000465 2019-10-28
	 * @param googleAuthInfo the googleAuthInfo to set
	 */
	public void setGoogleAuthInfo( GoogleAuthInfo googleAuthInfo )
	{
		this.googleAuthInfo = googleAuthInfo;
	}

	public NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException
	{
		final NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
		if ( StringUtils.equals( "Y", System.getenv().get( "USE_RPOXY" ) ) )
		{
			Proxy proxy = new Proxy( Type.HTTP, new InetSocketAddress( "localhost", 808 ) );
			builder.setProxy( proxy );
		}
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
		OAuth2Credentials auth2Credentials = OAuth2Credentials.create( new AccessToken( googleAuthInfo.getGoogleAcessInfo().getAccess_token(),
						googleAuthInfo.getGoogleAcessInfo().getExpiresAt() ) );
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
	
	public ContactsService getContactService() throws GeneralSecurityException, IOException
	{
		ContactsService contactsService = new ContactsService( getApplicationName() );
		Credential credential = new Credential.Builder( BearerToken.authorizationHeaderAccessMethod() ).setTransport( getHttpTransport() )
						.setJsonFactory( getJsonfactry() ).build();
		credential.setAccessToken( googleAuthInfo.getGoogleAcessInfo().getAccess_token() );
		contactsService.setOAuth2Credentials( credential );
		return contactsService;
	}

	private String getApplicationName()
	{
		return "cnhefe-122";
	}
}
