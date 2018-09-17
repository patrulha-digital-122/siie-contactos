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
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class ContactUtils
{
	public static ContactEntry convertElementoToContactEntry(	Explorador explorador,
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
		updateUserDefinedField( contactEntry, "NIN", explorador.getNin() );
		updateNome( contactEntry, explorador );
		updatePhoneNumber( contactEntry, "Telemóvel", explorador.getTelemovel() );
		updatePhoneNumber( contactEntry, "Telefone", explorador.getTelefone() );
		if ( explorador.getCategoria().equals( SECCAO.DIRIGENTES ) )
		{
			listTelefonesExistentes.add( explorador.getTelemovel() );
			listTelefonesExistentes.add( explorador.getTelefone() );
		}
		else
		{
			if ( !listTelefonesExistentes.contains( explorador.getTelefoneMae() ) )
			{
				updatePhoneNumber( contactEntry, "Mãe", explorador.getTelefoneMae() );
				listTelefonesExistentes.add( explorador.getTelefoneMae() );
			}
			if ( !listTelefonesExistentes.contains( explorador.getTelefonePai() ) )
			{
				updatePhoneNumber( contactEntry, "Pai", explorador.getTelefonePai() );
				listTelefonesExistentes.add( explorador.getTelefonePai() );
			}
			updateEmail( contactEntry, "Mãe", explorador.getEmailMae() );
			updateEmail( contactEntry, "Pai", explorador.getEmailPai() );
			updateEmail( contactEntry, "Principal", explorador.getEmailPrincipalGoogle() );
		}
		updateEmail( contactEntry, "Pessoal", explorador.getEmail() );
		updateUserDefinedField( contactEntry, "NIF", explorador.getNif() );
		updateAniversario( contactEntry, "Aniversário", explorador.getDataNascimento() );
		updatePais( contactEntry, "Mãe", explorador.getNomeMae() );
		updatePais( contactEntry, "Pai", explorador.getNomePai() );
		updateMorada( contactEntry, "Casa", explorador );
		contactEntry.setGender( explorador.isMasculino() ? new Gender( Gender.Value.MALE ) : new Gender( Gender.Value.FEMALE ) );
		return contactEntry;
	}

	/**
	 * The <b>updateMorada</b> method returns {@link void}
	 *
	 * @author anco62000465 2018-01-27
	 * @param contactEntry
	 * @param string
	 * @param explorador
	 */
	private static void updateMorada( ContactEntry contactEntry, String label, Explorador explorador )
	{
		if ( explorador.getMorada().isEmpty() )
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
		if ( !explorador.getLocalidade().isEmpty() )
		{
			structuredPostalAddress.setCity( new City( explorador.getLocalidade() ) );
		}
		if ( !explorador.getMorada().isEmpty() )
		{
			structuredPostalAddress.setFormattedAddress( new FormattedAddress( explorador.getMorada() ) );
		}
		if ( !explorador.getCodigoPostal().isEmpty() )
		{
			structuredPostalAddress.setPostcode( new PostCode( explorador.getCodigoPostal() ) );
		}
		if ( !explorador.getMorada().isEmpty() )
		{
			structuredPostalAddress.setStreet( new Street( explorador.getMorada() ) );
		}
		if ( "Torres Vedras".equalsIgnoreCase( explorador.getLocalidade() ) )
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

	private static void updateNome( ContactEntry contactEntry, Explorador explorador )
	{
		Name name = contactEntry.getName();
		if ( name == null )
		{
			name = new Name();
			contactEntry.setName( name );
		}
		FullName fullName = new FullName();
		fullName.setValue( explorador.getNome() );
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
