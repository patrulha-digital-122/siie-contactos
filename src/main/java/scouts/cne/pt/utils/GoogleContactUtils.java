package scouts.cne.pt.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.google.api.services.people.v1.model.Address;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
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
			updateAddresses( siieElemento );
			updateBirthdays( siieElemento );
		}
	}

	private static void updateNome( SIIEElemento siieElemento )
	{
		Person googlePerson = siieElemento.getGooglePerson();
		String[] lstNames = siieElemento.getNome().split( " " );
		String firstName = lstNames[ 0 ];
		String lastName = lstNames[ lstNames.length - 1 ];
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
			return;
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
				if ( address.getFormattedType().equals( "Casa" ) )
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
			addressToUpdate.setType( "home" );
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
				return;
			}
			for ( Birthday birthday : birthdays )
			{
				birthday.getDate().setDay( siieElemento.getDatanascimento().getDayOfMonth() );
				birthday.getDate().setMonth( siieElemento.getDatanascimento().getMonthValue() );
				birthday.getDate().setYear( siieElemento.getDatanascimento().getYear() );
			}
		}
	}
}
