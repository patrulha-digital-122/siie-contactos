package scouts.cne.pt.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ModifyContactGroupMembersRequest;
import com.google.api.services.people.v1.model.Person;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.utils.ContactUtils;
import scouts.cne.pt.utils.GoogleContactUtils;

@Component
@VaadinSessionScope
public class GoogleContactsService implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4266591353450666223L;
	private final Set< String >	lstTotalEmails		= new HashSet<>();
	private final Set< String >	lstTotalPhones		= new HashSet<>();

	/**
	 * 
	 * The <b>updateEmailAndPhoneList</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-06
	 * @param person
	 */
	public void updateEmailAndPhoneList( Person person )
	{
		if ( person.getEmailAddresses() != null )
		{
			person.getEmailAddresses().forEach( p -> lstTotalEmails.add( StringUtils.trimToEmpty( p.getValue() ) ) );
		}
		if ( person.getPhoneNumbers() != null )
		{
			person.getPhoneNumbers().forEach( p -> lstTotalPhones.add( ContactUtils.convertPhoneNumber( p.getValue() ) ) );
		}
	}

	public void updateElemento( PeopleService peopleService, SIIEElemento siieElemento ) throws SIIIEImporterException
	{
		Person googlePerson = siieElemento.getGooglePerson();
		
		GoogleContactUtils.updateGoogleFromSIIE( siieElemento );
		GoogleContactUtils.updateDadosPais( siieElemento, lstTotalEmails, lstTotalPhones );
		
		try
		{
			Person personUpdated = peopleService.people().updateContact( googlePerson.getResourceName(), googlePerson )
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
					googlePerson = peopleService.people().get( googlePerson.getResourceName() ).setPersonFields( GoogleAuthentication.PERSON_FIELDS )
									.execute();
					siieElemento.setGooglePerson( googlePerson );
					GoogleContactUtils.updateGoogleFromSIIE( siieElemento );
					Person personUpdated = peopleService.people().updateContact( googlePerson.getResourceName(), googlePerson )
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
	public void createElemento( PeopleService peopleService, SIIEElemento siieElemento ) throws SIIIEImporterException
	{
		try
		{
			Person person = new Person();
			siieElemento.setGooglePerson( person );

			GoogleContactUtils.updateGoogleFromSIIE( siieElemento );
			GoogleContactUtils.updateDadosPais( siieElemento, lstTotalEmails, lstTotalPhones );

			person = peopleService.people().createContact( person ).execute();
			siieElemento.setGooglePerson( person );

			getLogger().info( "Criado contacto :: " + person.getResourceName() );

			ModifyContactGroupMembersRequest content = new ModifyContactGroupMembersRequest();
			content.setResourceNamesToAdd( Arrays.asList( person.getResourceName() ) );
			peopleService.contactGroups().members().modify( "contactGroups/myContacts", content ).execute();

			getLogger().info( "Contacto associado a MyContacts." );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			throw new SIIIEImporterException( e.getMessage() );
		}
	}
}
