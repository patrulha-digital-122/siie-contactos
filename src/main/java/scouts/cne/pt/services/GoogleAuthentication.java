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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.ModifyContactGroupMembersRequest;
import com.google.api.services.people.v1.model.Person;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.gdata.client.contacts.ContactsService;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.google.GoogleAuthInfo;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.utils.GoogleContactUtils;

@Component
@VaadinSessionScope
public class GoogleAuthentication implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;
	public static final List< String >	SCOPES				=
					Arrays.asList( GmailScopes.GMAIL_SEND, PeopleServiceScopes.CONTACTS );
	private static final List< String >	SERVER_SCOPES		= Arrays.asList( GmailScopes.GMAIL_SEND );
	private GoogleAuthInfo				googleAuthInfo		= null;
	public static String				PERSON_FIELDS		=
					"addresses,birthdays,emailAddresses,events,genders,memberships,names,phoneNumbers,relations,userDefined,nicknames";

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

	public void updateElemento( SIIEElemento siieElemento ) throws SIIIEImporterException
	{
		Person googlePerson = siieElemento.getGooglePerson();
		try
		{
			Person personUpdated = getPeopleService().people().updateContact( googlePerson.getResourceName(), googlePerson )
							.setUpdatePersonFields( GoogleAuthentication.PERSON_FIELDS ).execute();
			siieElemento.setGooglePerson( personUpdated );
		}
		catch ( GoogleJsonResponseException e )
		{
			if ( e.getStatusCode() == 400 && e.getDetails().getErrors().get( 0 ).getReason().equals( "failedPrecondition" ) )
			{
				getLogger().warn( "FAILED_PRECONDITION :: " + siieElemento.getNin() );
				try
				{
					googlePerson = getPeopleService().people().get( googlePerson.getResourceName() ).setPersonFields( PERSON_FIELDS ).execute();
					siieElemento.setGooglePerson( googlePerson );
					GoogleContactUtils.updateGoogleFromSIIE( siieElemento );
					Person personUpdated = getPeopleService().people().updateContact( googlePerson.getResourceName(), googlePerson )
									.setUpdatePersonFields( GoogleAuthentication.PERSON_FIELDS ).execute();
					siieElemento.setGooglePerson( personUpdated );
				}
				catch ( Exception e1 )
				{
					throw new SIIIEImporterException( e.getMessage() );
				}
			}
		}
		catch ( Exception e )
		{
			throw new SIIIEImporterException( e.getMessage() );
		}
	}

	/**
	 * The <b>createElemento</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-04
	 * @param siieElemento
	 * @throws SIIIEImporterException
	 */
	public void createElemento( SIIEElemento siieElemento ) throws SIIIEImporterException
	{
		try
		{
			Person person = new Person();
			siieElemento.setGooglePerson( person );
			GoogleContactUtils.updateGoogleFromSIIE( siieElemento );
			person = getPeopleService().people().createContact( person ).execute();
			siieElemento.setGooglePerson( person );
			getLogger().info( "Criado contacto :: " + person.getResourceName() );

			ModifyContactGroupMembersRequest content = new ModifyContactGroupMembersRequest();
			content.setResourceNamesToAdd( Arrays.asList( person.getResourceName() ) );
			getPeopleService().contactGroups().members().modify( "contactGroups/myContacts", content ).execute();
			getLogger().info( "Contacto associado a MyContacts." );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			throw new SIIIEImporterException( e.getMessage() );
		}
	}
}
