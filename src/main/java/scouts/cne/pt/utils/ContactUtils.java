package scouts.cne.pt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import com.google.gdata.data.contacts.Birthday;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.Gender;
import com.google.gdata.data.contacts.Relation;
import com.google.gdata.data.contacts.UserDefinedField;
import com.google.gdata.data.extensions.City;
import com.google.gdata.data.extensions.Country;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.FormattedAddress;
import com.google.gdata.data.extensions.FullName;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.PostCode;
import com.google.gdata.data.extensions.Region;
import com.google.gdata.data.extensions.Street;
import com.google.gdata.data.extensions.StructuredPostalAddress;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.SECCAO;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class ContactUtils
{
	public static ContactEntry convertElementoToContactEntry(	Elemento elemento,
																ContactEntry contactEntry,
																Set< String > listTelefonesExistentes )
	{
		if ( contactEntry == null )
		{
			contactEntry = new ContactEntry();
		}
		else
		{
			removeNINandNIF( contactEntry );
		}
		updateUserDefinedField( contactEntry, "NIN", elemento.getNin() );
		updateNome( contactEntry, elemento );
		updatePhoneNumber( contactEntry, "Telemóvel", elemento.getTelemovel() );
		updatePhoneNumber( contactEntry, "Telefone", elemento.getTelefone() );
		if ( elemento.getCategoria().equals( SECCAO.DIRIGENTES ) )
		{
			listTelefonesExistentes.add( elemento.getTelemovel() );
			listTelefonesExistentes.add( elemento.getTelefone() );
		}
		else
		{
			if ( !listTelefonesExistentes.contains( elemento.getTelefoneMae() ) )
			{
				updatePhoneNumber( contactEntry, "Mãe", elemento.getTelefoneMae() );
				listTelefonesExistentes.add( elemento.getTelefoneMae() );
			}
			if ( !listTelefonesExistentes.contains( elemento.getTelefonePai() ) )
			{
				updatePhoneNumber( contactEntry, "Pai", elemento.getTelefonePai() );
				listTelefonesExistentes.add( elemento.getTelefonePai() );
			}
			updateEmail( contactEntry, "Mãe", elemento.getEmailMae() );
			updateEmail( contactEntry, "Pai", elemento.getEmailPai() );
			updateEmail( contactEntry, "Principal", elemento.getEmailPrincipalGoogle() );
		}
		updateEmail( contactEntry, "Pessoal", elemento.getEmail() );
		updateUserDefinedField( contactEntry, "NIF", elemento.getNif() );
		updateAniversario( contactEntry, "Aniversário", elemento.getDataNascimento() );
		updatePais( contactEntry, "Mãe", elemento.getNomeMae() );
		updatePais( contactEntry, "Pai", elemento.getNomePai() );
		updateMorada( contactEntry, "Casa", elemento );
		contactEntry.setGender( elemento.isMasculino() ? new Gender( Gender.Value.MALE ) : new Gender( Gender.Value.FEMALE ) );
		return contactEntry;
	}

	/**
	 * The <b>updateMorada</b> method returns {@link void}
	 *
	 * @author anco62000465 2018-01-27
	 * @param contactEntry
	 * @param string
	 * @param elemento
	 */
	private static void updateMorada( ContactEntry contactEntry, String label, Elemento elemento )
	{
		if ( elemento.getMorada().isEmpty() )
		{
			return;
		}
		StructuredPostalAddress structuredPostalAddress = null;
		for ( StructuredPostalAddress postalAddress : contactEntry.getStructuredPostalAddresses() )
		{
			if ( StringUtils.equals( postalAddress.getLabel(), label ) )
			{
				structuredPostalAddress = postalAddress;
				break;
			}
		}
		if ( structuredPostalAddress == null )
		{
			structuredPostalAddress = new StructuredPostalAddress();
			structuredPostalAddress.setLabel( label );
			contactEntry.getStructuredPostalAddresses().add( structuredPostalAddress );
		}
		structuredPostalAddress.setCountry( new Country( "PT", "Portugal" ) );
		if ( !elemento.getLocalidade().isEmpty() )
		{
			structuredPostalAddress.setCity( new City( elemento.getLocalidade() ) );
		}
		if ( !elemento.getMorada().isEmpty() )
		{
			structuredPostalAddress.setFormattedAddress( new FormattedAddress( elemento.getMorada() ) );
		}
		if ( !elemento.getCodigoPostal().isEmpty() )
		{
			structuredPostalAddress.setPostcode( new PostCode( elemento.getCodigoPostal() ) );
		}
		if ( !elemento.getMorada().isEmpty() )
		{
			structuredPostalAddress.setStreet( new Street( elemento.getMorada() ) );
		}
		if ( "Torres Vedras".equalsIgnoreCase( elemento.getLocalidade() ) )
		{
			structuredPostalAddress.setRegion( new Region( "Lisboa" ) );
		}
	}

	private static void updateUserDefinedField( ContactEntry contactEntry, String lable, String number )
	{
		if ( StringUtils.isNotBlank( number ) )
		{
			if ( contactEntry.getUserDefinedFields() != null )
			{
				for ( UserDefinedField userDefinedField : contactEntry.getUserDefinedFields() )
				{
					if ( StringUtils.equals( userDefinedField.getKey(), lable ) )
					{
						userDefinedField.setValue( number );
						return;
					}
				}
			}
			UserDefinedField userDefinedField = new UserDefinedField( lable, number );
			contactEntry.getUserDefinedFields().add( userDefinedField );
		}
	}

	private static void updatePhoneNumber( ContactEntry contactEntry, String lable, String number )
	{
		if ( ( number == null ) || number.isEmpty() )
		{
			return;
		}
		
		if ( !number.startsWith( "+351" ) )
		{
			number = "+351" + number;
		}
		
		if ( contactEntry.getPhoneNumbers() != null )
		{
			for ( PhoneNumber phoneNumber : contactEntry.getPhoneNumbers() )
			{
				if ( ( phoneNumber.getLabel() != null ) && phoneNumber.getLabel().equals( lable ) )
				{
					phoneNumber.setPhoneNumber( number );
					return;
				}
			}
		}
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.setLabel( lable );
		phoneNumber.setPrimary( lable.equals( "NIN" ) );
		phoneNumber.setPhoneNumber( number );
		contactEntry.getPhoneNumbers().add( phoneNumber );
	}

	private static void removeNINandNIF( ContactEntry contactEntry )
	{
		for ( Iterator< PhoneNumber > iterator = contactEntry.getPhoneNumbers().iterator(); iterator.hasNext(); )
		{
			PhoneNumber phoneNumber = iterator.next();
			if ( StringUtils.equals( phoneNumber.getLabel(), "NIN" ) || StringUtils.equals( phoneNumber.getLabel(), "NIF" ) )
			{
				iterator.remove();
			}
		}
	}

	private static void updateNome( ContactEntry contactEntry, Elemento elemento )
	{
		Name name = contactEntry.getName();
		if ( name == null )
		{
			name = new Name();
			contactEntry.setName( name );
		}
		FullName fullName = new FullName();
		fullName.setValue( elemento.getNome() );
		name.setFullName( fullName );
	}

	private static void updateEmail( ContactEntry contactEntry, String lable, String strEmail )
	{
		if ( ( strEmail == null ) || strEmail.isEmpty() )
		{
			return;
		}
		boolean existPrincipal = false;
		for ( Email e : contactEntry.getEmailAddresses() )
		{
			if ( ( e.getLabel() != null ) && e.getLabel().equals( lable ) )
			{
				e.setAddress( strEmail );
				return;
			}
			if ( !existPrincipal && e.getPrimary() )
			{
				existPrincipal = true;
			}
		}
		Email email = new Email();
		email.setLabel( lable );
		email.setPrimary( !existPrincipal );
		email.setAddress( strEmail );
		contactEntry.getEmailAddresses().add( email );
	}

	private static void updateAniversario( ContactEntry contactEntry, String label, Date date )
	{
		if ( date == null )
		{
			return;
		}
		Birthday birthday = contactEntry.getBirthday();
		if ( birthday == null )
		{
			birthday = new Birthday();
			contactEntry.setBirthday( birthday );
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		String format = dateFormat.format( date );
		birthday.setWhen( format );
	}

	private static void updatePais( ContactEntry contactEntry, String lable, String name )
	{
		if ( contactEntry.getRelations() != null )
		{
			for ( Relation relation : contactEntry.getRelations() )
			{
				if ( relation.hasLabel() && relation.getLabel().equals( lable ) )
				{
					relation.setValue( name );
					return;
				}
			}
		}
		Relation relation = new Relation();
		relation.setLabel( lable );
		relation.setValue( name );
		contactEntry.getRelations().add( relation );
	}
}
