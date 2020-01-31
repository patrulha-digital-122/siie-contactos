package scouts.cne.pt.utils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import com.google.api.services.people.v1.model.Address;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Event;
import com.google.api.services.people.v1.model.FieldMetadata;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Nickname;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.google.api.services.people.v1.model.Relation;
import com.google.api.services.people.v1.model.Source;
import com.google.api.services.people.v1.model.UserDefined;
import scouts.cne.pt.model.siie.SIIEElemento;

/**
 * @author 62000465 2019-10-31
 *
 */
public class GoogleContactUtils
{
	public static List< String > listDiferencas( SIIEElemento siieElemento )
	{
		List< String > list = new ArrayList< String >();
		return list;
	}

	public static void updateGoogleFromSIIE( SIIEElemento siieElemento )
	{
		if ( siieElemento.getGooglePerson() != null )
		{
			updateNome( siieElemento );
			updateEmail( siieElemento );
			updateAddresses( siieElemento );
			updateBirthdays( siieElemento );
			updateUserDefined( siieElemento );
			updateRelations( siieElemento );
			updatePhoneNumbers( siieElemento );
			updateEvents( siieElemento );
			updateTotem( siieElemento );
			updateGender( siieElemento );
		}
	}

	private static void updateNome( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		String[] lstNames = siieElemento.getNome().split( " " );
		String firstName = lstNames[ 0 ];
		String lastName = "";
		if ( lstNames.length > 1 )
		{
			lastName = lstNames[ lstNames.length - 1 ];
		}
		StringBuilder middleName = new StringBuilder();
		for ( int i = 1; i < lstNames.length - 1; i++ )
		{
			if ( i > 1 )
			{
				middleName.append( " " );
			}
			middleName.append( lstNames[ i ] );
		}
		List< Name > names = googlePerson.getNames();
		if ( names == null )
		{
			names = new ArrayList<>();
			googlePerson.setNames( names );
		}
		if ( names.isEmpty() )
		{
			Name name = new Name();
			name.setDisplayName( siieElemento.getNome() );
			names.add( name );
		}

		for ( Name name : names )
		{
			name.setDisplayName( siieElemento.getNome() );
			name.setFamilyName( lastName );
			name.setMiddleName( middleName.toString() );
			name.setGivenName( firstName );
			name.setDisplayNameLastFirst( lastName + ", " + firstName );
		}
	}

	private static void updateEmail( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		if ( StringUtils.isNotBlank( siieElemento.getEmail() ) )
		{
			List< EmailAddress > emailAddresses = googlePerson.getEmailAddresses();
			if ( emailAddresses == null )
			{
				emailAddresses = new ArrayList<>();
				googlePerson.setEmailAddresses( emailAddresses );
			}

			boolean bPrimaryExists = false;
			for ( EmailAddress emailAddress : emailAddresses )
			{
				if ( StringUtils.equals( emailAddress.getType(), "Pessoal" ) )
				{
					// update value
					emailAddress.setValue( siieElemento.getEmail() );
					return;
				}
				if ( emailAddress.getMetadata() != null && BooleanUtils.isTrue( emailAddress.getMetadata().getPrimary() ) )
				{
					bPrimaryExists = true;
				}
			}
			// add email
			EmailAddress emailAddress = new EmailAddress();
			emailAddress.setType( "Pessoal" );
			emailAddress.setValue( siieElemento.getEmail() );
			if ( !bPrimaryExists )
			{
				FieldMetadata fieldMetadata = new FieldMetadata();
				fieldMetadata.setPrimary( true );
				Source source = new Source();
				source.setType( "SOURCE_TYPE_UNSPECIFIED" );
				// fieldMetadata.setSource( source );
				emailAddress.setMetadata( fieldMetadata );
			}
			emailAddresses.add( emailAddress );
		}
	}
	private static void updateAddresses( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		if ( StringUtils.isNotBlank( siieElemento.getMorada() ) )
		{
			List< Address > addresses = googlePerson.getAddresses();
			if ( addresses == null )
			{
				addresses = new ArrayList<>();
				googlePerson.setAddresses( addresses );
			}
			Address addressToUpdate = null;
			for ( Address address : addresses )
			{
				if ( StringUtils.equals( address.getFormattedType(), "Casa" ) )
				{
					addressToUpdate = address;
					break;
				}
			}
			if ( addressToUpdate == null )
			{
				addressToUpdate = new Address();
				addresses.add( addressToUpdate );
			}

			addressToUpdate.setFormattedValue( siieElemento.getMorada() );
			addressToUpdate.setType( "Casa" );
			addressToUpdate.setFormattedType( "Casa" );
			addressToUpdate.setCity( siieElemento.getLocalidade() );
			StringBuilder sbPostal = new StringBuilder( siieElemento.getCp1() );
			if ( StringUtils.isNotBlank( siieElemento.getCp2() ) )
			{
				sbPostal.append( "-" );
				sbPostal.append( siieElemento.getCp2() );
			}
			addressToUpdate.setPostalCode( sbPostal.toString() );
			addressToUpdate.setCountry( "Portugal" );
			addressToUpdate.setCountryCode( "PT" );
		}
	}

	private static void updateBirthdays( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		if ( siieElemento.getDatanascimento() != null )
		{
			List< Birthday > birthdays = googlePerson.getBirthdays();
			if ( birthdays == null )
			{
				birthdays = new ArrayList<>();
				googlePerson.setBirthdays( birthdays );
			}
			if ( birthdays.isEmpty() )
			{
				Birthday birthday = new Birthday();
				birthday.setDate( new Date() );
				birthdays.add( birthday );
			}
			for ( Birthday birthday : birthdays )
			{
				birthday.getDate().setDay( siieElemento.getDatanascimento().getDayOfMonth() );
				birthday.getDate().setMonth( siieElemento.getDatanascimento().getMonthValue() );
				birthday.getDate().setYear( siieElemento.getDatanascimento().getYear() );
			}
		}
	}

	private static void updateUserDefined( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		List< UserDefined > userDefineds = googlePerson.getUserDefined();
		if ( userDefineds == null )
		{
			userDefineds = new ArrayList<>();
			googlePerson.setUserDefined( userDefineds );
		}
		boolean createNIN = true;
		boolean createNIF = true;
		boolean createCC = true;
		for ( UserDefined userDefined : userDefineds )
		{
			switch ( userDefined.getKey() )
			{
				case "NIN":
					userDefined.setValue( StringUtils.trimToEmpty( siieElemento.getNin() ) );
					createNIN = false;
					break;
				case "NIF":
					userDefined.setValue( StringUtils.trimToEmpty( siieElemento.getNif() ) );
					createNIF = false;
					break;
				case "CC":
					userDefined.setValue( StringUtils.trimToEmpty( siieElemento.getCc() ) );
					createCC = false;
					break;
				default:
					break;
			}
		}
		if ( createNIN && StringUtils.isNotBlank( siieElemento.getNin() ) )
		{
			UserDefined userDefined = new UserDefined();
			userDefined.setKey( "NIN" );
			userDefined.setValue( siieElemento.getNin() );
			userDefineds.add( userDefined );
		}
		if ( createNIF && StringUtils.isNotBlank( siieElemento.getNif() ) )
		{
			UserDefined userDefined = new UserDefined();
			userDefined.setKey( "NIF" );
			userDefined.setValue( siieElemento.getNif() );
			userDefineds.add( userDefined );
		}
		if ( createCC && StringUtils.isNotBlank( siieElemento.getCc() ) )
		{
			UserDefined userDefined = new UserDefined();
			userDefined.setKey( "CC" );
			userDefined.setValue( siieElemento.getCc() );
			userDefineds.add( userDefined );
		}
	}

	/**
	 * The <b>updateRelations</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 * @param siieElemento
	 */
	private static void updateRelations( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		List< Relation > relations = googlePerson.getRelations();
		if ( relations == null )
		{
			relations = new ArrayList<>();
			googlePerson.setRelations( relations );
		}

		if ( StringUtils.isNotBlank( siieElemento.getPai() ) )
		{
			Optional< Relation > findFirst = relations.stream().filter( p -> StringUtils.equals( p.getFormattedType(), "Pai" ) ).findFirst();
			if ( findFirst.isPresent() )
			{
				findFirst.get().setPerson( siieElemento.getPai() );
			}
			else
			{
				Relation relation = new Relation();
				relation.setType( "Pai" );
				relation.setFormattedType( "Pai" );
				relation.setPerson( siieElemento.getPai() );
				relations.add( relation );
			}
		}
		if ( StringUtils.isNotBlank( siieElemento.getMae() ) )
		{
			Optional< Relation > findFirst = relations.stream().filter( p -> StringUtils.equals( p.getFormattedType(), "Mãe" ) ).findFirst();
			if ( findFirst.isPresent() )
			{
				findFirst.get().setPerson( siieElemento.getMae() );
			}
			else
			{
				Relation relation = new Relation();
				relation.setType( "Mãe" );
				relation.setFormattedType( "Mãe" );
				relation.setPerson( siieElemento.getMae() );
				relations.add( relation );
			}
		}
	}

	/**
	 * The <b>updatePhoneNumbers</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 * @param siieElemento
	 * @param string
	 */
	private static void updatePhoneNumbers( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		List< PhoneNumber > phoneNumbers = googlePerson.getPhoneNumbers();
		if ( phoneNumbers == null )
		{
			phoneNumbers = new ArrayList<>();
			googlePerson.setPhoneNumbers( phoneNumbers );
		}

		boolean createTelemovel = true;
		boolean createTelefone = true;

		for ( PhoneNumber phoneNumber : phoneNumbers )
		{
			if ( StringUtils.equals( phoneNumber.getType(), "mobile" ) && StringUtils.isNotBlank( siieElemento.getTelemovel() ) )
			{
				String convertPhoneNumber = ContactUtils.convertPhoneNumber( siieElemento.getTelemovel() );
				phoneNumber.setValue( convertPhoneNumber );
				phoneNumber.setCanonicalForm( convertPhoneNumber.replace( " ", "" ) );
				createTelemovel = false;
			}
			else if ( StringUtils.equals( phoneNumber.getType(), "home" ) && StringUtils.isNotBlank( siieElemento.getTelefone() ) )
			{
				String convertPhoneNumber = ContactUtils.convertPhoneNumber( siieElemento.getTelefone() );
				phoneNumber.setValue( convertPhoneNumber );
				phoneNumber.setCanonicalForm( convertPhoneNumber.replace( " ", "" ) );
				createTelefone = false;
			}
		}
		if ( createTelemovel )
		{
			PhoneNumber phoneNumber = new PhoneNumber();
			phoneNumber.setType( "mobile" );
			String convertPhoneNumber = ContactUtils.convertPhoneNumber( siieElemento.getTelemovel() );
			phoneNumber.setValue( convertPhoneNumber );
			phoneNumber.setCanonicalForm( convertPhoneNumber.replace( " ", "" ) );
			phoneNumbers.add( phoneNumber );
		}
		if ( createTelefone )
		{
			PhoneNumber phoneNumber = new PhoneNumber();
			phoneNumber.setType( "home" );
			String convertPhoneNumber = ContactUtils.convertPhoneNumber( siieElemento.getTelefone() );
			phoneNumber.setValue( convertPhoneNumber );
			phoneNumber.setCanonicalForm( convertPhoneNumber.replace( " ", "" ) );
			phoneNumbers.add( phoneNumber );
		}
	}

	private static void updateEvents( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		List< Event > events = googlePerson.getEvents();
		if ( events == null )
		{
			events = new ArrayList<>();
			googlePerson.setEvents( events );
		}
		processEvent( siieElemento.getDatapromessa(), events, "Data da Promessa" );
		processEvent( siieElemento.getDataadmissao(), events, "Data da Admissão" );
	}

	private static void updateTotem( SIIEElemento siieElemento )
	{
		if ( StringUtils.isNotBlank( siieElemento.getTotem() ) )
		{
			Person googlePerson = siieElemento.getGooglePerson();
			List< Nickname > nicknames = googlePerson.getNicknames();
			if ( nicknames == null )
			{
				nicknames = new ArrayList<>();
				googlePerson.setNicknames( nicknames );
			}
			Optional< Nickname > findFirst = nicknames.stream().filter( p -> StringUtils.equals( p.getType(), "DEFAULT" ) ).findFirst();
			if ( findFirst.isPresent() )
			{
				findFirst.get().setValue( siieElemento.getTotem() );
			}
			else
			{
				Nickname nickname = new Nickname();
				nickname.setType( "DEFAULT" );
				nickname.setValue( siieElemento.getTotem() );
				nicknames.add( nickname );
			}
		}
	}

	private static void updateGender( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		List< Gender > genders = googlePerson.getGenders();
		if ( genders == null )
		{
			genders = new ArrayList<>();
			googlePerson.setGenders( genders );
		}
		
		for ( Gender gender : genders )
		{
			if ( gender.getMetadata().getPrimary() )
			{
				gender.setValue( siieElemento.getSiglasexo().getGoogleValue() );
				gender.setFormattedValue( siieElemento.getSiglasexo().getNome() );
				return;
			}
		}
		
		Gender gender = new Gender();
		gender.setValue( siieElemento.getSiglasexo().getGoogleValue() );
		gender.setFormattedValue( siieElemento.getSiglasexo().getNome() );
	}

	/**
	 * The <b>processEvent</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 * @param siieElemento
	 * @param events
	 * @param strEventType
	 */
	private static void processEvent( ZonedDateTime zonedDateTime, List< Event > events, String strEventType )
	{
		if ( zonedDateTime != null )
		{
			Optional< Event > findFirst = events.stream().filter( p -> StringUtils.equals( p.getFormattedType(), strEventType ) ).findFirst();
			if ( findFirst.isPresent() )
			{
				findFirst.get().getDate().setDay( zonedDateTime.getDayOfMonth() );
				findFirst.get().getDate().setMonth( zonedDateTime.getMonthValue() );
				findFirst.get().getDate().setYear( zonedDateTime.getYear() );
			}
			else
			{
				Event event = new Event();
				event.setType( strEventType );
				event.setFormattedType( strEventType );
				Date date = new Date();
				date.setDay( zonedDateTime.getDayOfMonth() );
				date.setMonth( zonedDateTime.getMonthValue() );
				date.setYear( zonedDateTime.getYear() );
				event.setDate( date );
				events.add( event );
			}
		}
	}

	public static void updateDadosPais( SIIEElemento siieElemento, Set< String > totalEmails, Set< String > totalPhones )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		// Emails
		List< EmailAddress > emailAddresses = googlePerson.getEmailAddresses();
		if ( emailAddresses == null )
		{
			emailAddresses = new ArrayList<>();
			googlePerson.setEmailAddresses( emailAddresses );
		}
		EmailAddress emailMae = null;
		EmailAddress emailPai = null;

		for ( EmailAddress emailAddress : emailAddresses )
		{
			if ( StringUtils.equals( emailAddress.getFormattedType(), "Mãe" ) )
			{
				emailMae = emailAddress;
			}
			else if ( StringUtils.equals( emailAddress.getFormattedType(), "Pai" ) )
			{
				emailPai = emailAddress;
			}
		}

		if ( StringUtils.isNotBlank( siieElemento.getMaeemail() ) && !totalEmails.contains( siieElemento.getMaeemail() ) )
		{
			if ( emailMae == null )
			{
				emailMae = new EmailAddress();
				emailMae.setType( "Mãe" );
				emailMae.setFormattedType( "Mãe" );
				emailAddresses.add( emailMae );
			}
			emailMae.setValue( siieElemento.getMaeemail() );
		}
		if ( StringUtils.isNotBlank( siieElemento.getPaiemail() ) && !totalEmails.contains( siieElemento.getPaiemail() ) )
		{
			if ( emailPai == null )
			{
				emailPai = new EmailAddress();
				emailPai.setType( "Pai" );
				emailPai.setFormattedType( "Pai" );
				emailAddresses.add( emailPai );
			}
			emailPai.setValue( siieElemento.getPaiemail() );
		}
		// Telefones
		List< PhoneNumber > phoneNumbers = googlePerson.getPhoneNumbers();
		if ( phoneNumbers == null )
		{
			phoneNumbers = new ArrayList<>();
			googlePerson.setPhoneNumbers( phoneNumbers );
		}
		PhoneNumber numeroMae = null;
		PhoneNumber numeroPai = null;
		for ( PhoneNumber phoneNumber : phoneNumbers )
		{
			if ( StringUtils.equals( phoneNumber.getFormattedType(), "Mãe" ) )
			{
				numeroMae = phoneNumber;
			}
			else if ( StringUtils.equals( phoneNumber.getFormattedType(), "Pai" ) )
			{
				numeroPai = phoneNumber;
			}
		}
		String strNumeroMae = ContactUtils.convertPhoneNumber( siieElemento.getMaetelefone() );
		if ( StringUtils.isNotBlank( strNumeroMae ) && !totalPhones.contains( strNumeroMae ) )
		{
			if ( numeroMae == null )
			{
				numeroMae = new PhoneNumber();
				numeroMae.setType( "Mãe" );
				numeroMae.setFormattedType( "Mãe" );
				phoneNumbers.add( numeroMae );
			}
			numeroMae.setValue( strNumeroMae );
			numeroMae.setCanonicalForm( strNumeroMae.replace( " ", "" ) );
		}
		String strNumeroPai = ContactUtils.convertPhoneNumber( siieElemento.getPaitelefone() );
		if ( StringUtils.isNotBlank( strNumeroPai ) && !totalPhones.contains( strNumeroPai ) )
		{
			if ( numeroPai == null )
			{
				numeroPai = new PhoneNumber();
				numeroPai.setType( "Pai" );
				numeroPai.setFormattedType( "Pai" );
				phoneNumbers.add( numeroPai );
			}
			numeroPai.setValue( strNumeroPai );
			numeroPai.setCanonicalForm( strNumeroPai.replace( " ", "" ) );
		}
	}
}
