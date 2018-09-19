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
import com.google.gdata.data.extensions.StructuredPostalAddress;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.ImportContactReport;
import scouts.cne.pt.model.SECCAO;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class ContactUtils
{
	public static ElementoImport convertElementoToContactEntry(	Elemento elemento,
																ContactEntry contactEntry,
																Set< String > listTelefonesExistentes )
	{
		ImportContactReport importContactReport = new ImportContactReport();
		if ( contactEntry == null )
		{
			contactEntry = new ContactEntry();
		}
		else
		{
			removeNINandNIF( contactEntry );
		}
		updateUserDefinedField( contactEntry, "NIN", elemento.getNin(), importContactReport );
		updateNome( contactEntry, elemento, importContactReport );
		updatePhoneNumber( contactEntry, "Telemóvel", elemento.getTelemovel(), importContactReport );
		updatePhoneNumber( contactEntry, "Telefone", elemento.getTelefone(), importContactReport );
		if ( elemento.getCategoria().equals( SECCAO.DIRIGENTES ) )
		{
			listTelefonesExistentes.add( elemento.getTelemovel() );
			listTelefonesExistentes.add( elemento.getTelefone() );
		}
		else
		{
			if ( !listTelefonesExistentes.contains( elemento.getTelefoneMae() ) )
			{
				updatePhoneNumber( contactEntry, "Mãe", elemento.getTelefoneMae(), importContactReport );
				listTelefonesExistentes.add( elemento.getTelefoneMae() );
			}
			if ( !listTelefonesExistentes.contains( elemento.getTelefonePai() ) )
			{
				updatePhoneNumber( contactEntry, "Pai", elemento.getTelefonePai(), importContactReport );
				listTelefonesExistentes.add( elemento.getTelefonePai() );
			}
			updateEmail( contactEntry, "Mãe", elemento.getEmailMae(), importContactReport );
			updateEmail( contactEntry, "Pai", elemento.getEmailPai(), importContactReport );
			updateEmail( contactEntry, "Principal", elemento.getEmailPrincipalGoogle(), importContactReport );
		}
		updateEmail( contactEntry, "Pessoal", elemento.getEmail(), importContactReport );
		updateUserDefinedField( contactEntry, "NIF", elemento.getNif(), importContactReport );
		updateAniversario( contactEntry, "Aniversário", elemento.getDataNascimento(), importContactReport );
		updatePais( contactEntry, "Mãe", elemento.getNomeMae(), importContactReport );
		updatePais( contactEntry, "Pai", elemento.getNomePai(), importContactReport );
		updateMorada( contactEntry, "Casa", elemento, importContactReport );
		contactEntry.setGender( elemento.isMasculino() ? new Gender( Gender.Value.MALE ) : new Gender( Gender.Value.FEMALE ) );
		return new ElementoImport( contactEntry, elemento, importContactReport );
	}

	/**
	 * The <b>updateMorada</b> method returns {@link void}
	 *
	 * @author anco62000465 2018-01-27
	 * @param contactEntry
	 * @param string
	 * @param elemento
	 * @param importContactReport
	 */
	private static void updateMorada( ContactEntry contactEntry, String label, Elemento elemento, ImportContactReport importContactReport )
	{
		if ( StringUtils.isBlank( elemento.getMorada() ) )
		{
			return;
		}
		boolean alreadyHave = true;
		StructuredPostalAddress structuredPostalAddress = null;
		for ( StructuredPostalAddress postalAddress : contactEntry.getStructuredPostalAddresses() )
		{
			if ( StringUtils.equalsIgnoreCase( postalAddress.getLabel(), label ) )
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
			alreadyHave = false;
			structuredPostalAddress.setCountry( new Country( "PT", "Portugal" ) );
			if ( StringUtils.equalsIgnoreCase( "Torres Vedras", elemento.getLocalidade() ) )
			{
				structuredPostalAddress.setRegion( new Region( "Lisboa" ) );
			}
		}
		// Localidade
		if ( StringUtils.isNotBlank( elemento.getLocalidade() ) )
		{
			if ( !alreadyHave || structuredPostalAddress.getCity() == null )
			{
				structuredPostalAddress.setCity( new City( elemento.getLocalidade() ) );
				importContactReport.addNewField( "Cidade", elemento.getLocalidade() );
			}
			else if ( !StringUtils.equalsIgnoreCase( structuredPostalAddress.getCity().getValue(), elemento.getLocalidade() ) )
			{
				importContactReport.addUpdateField( "Localidade", structuredPostalAddress.getCity().getValue(), elemento.getLocalidade() );
				structuredPostalAddress.getCity().setValue( elemento.getLocalidade() );
			}
		}
		// Morada
		if ( StringUtils.isNotBlank( elemento.getMorada() ) )
		{
			if ( !alreadyHave || structuredPostalAddress.getFormattedAddress() == null )
			{
				structuredPostalAddress.setFormattedAddress( new FormattedAddress( elemento.getMorada() ) );
				importContactReport.addNewField( "Morada", elemento.getMorada() );
			}
			else if ( !StringUtils.equalsIgnoreCase( structuredPostalAddress.getFormattedAddress().getValue(), elemento.getMorada() ) )
			{
				importContactReport.addUpdateField( "Morada", structuredPostalAddress.getFormattedAddress().getValue(), elemento.getMorada() );
				structuredPostalAddress.getFormattedAddress().setValue( elemento.getMorada() );
			}
		}
		// Codigo postal
		if ( StringUtils.isNotBlank( elemento.getCodigoPostal() ) )
		{
			if ( !alreadyHave || structuredPostalAddress.getPostcode() == null )
			{
				structuredPostalAddress.setPostcode( new PostCode( elemento.getCodigoPostal() ) );
				importContactReport.addNewField( "Código Postal", elemento.getCodigoPostal() );
			}
			else if ( !StringUtils.equalsIgnoreCase( structuredPostalAddress.getPostcode().getValue(), elemento.getCodigoPostal() ) )
			{
				importContactReport.addUpdateField( "Código Postal", structuredPostalAddress.getPostcode().getValue(), elemento.getCodigoPostal() );
				structuredPostalAddress.getPostcode().setValue( elemento.getCodigoPostal() );
			}
		}
	}

	private static void updateUserDefinedField( ContactEntry contactEntry, String lable, String number, ImportContactReport importContactReport )
	{
		if ( StringUtils.isNotBlank( number ) )
		{
			if ( contactEntry.getUserDefinedFields() != null )
			{
				for ( UserDefinedField userDefinedField : contactEntry.getUserDefinedFields() )
				{
					if ( StringUtils.equals( userDefinedField.getKey(), lable ) )
					{
						if ( !StringUtils.equals( number, userDefinedField.getValue() ) )
						{
							importContactReport.addUpdateField( lable, userDefinedField.getValue(), number );
							userDefinedField.setValue( number );
						}
						return;
					}
				}
			}
			UserDefinedField userDefinedField = new UserDefinedField( lable, number );
			contactEntry.getUserDefinedFields().add( userDefinedField );
		}
	}

	private static void updatePhoneNumber( ContactEntry contactEntry, String lable, String number, ImportContactReport importContactReport )
	{
		if ( ( number == null ) || number.isEmpty() )
		{
			return;
		}
		
		if ( !number.startsWith( "+351" ) )
		{
			number = "+351 " + number;
		}
		
		if ( contactEntry.getPhoneNumbers() != null )
		{
			for ( PhoneNumber phoneNumber : contactEntry.getPhoneNumbers() )
			{
				if ( ( phoneNumber.getLabel() != null ) && phoneNumber.getLabel().equals( lable ) )
				{
					if ( !StringUtils.equals( number, phoneNumber.getPhoneNumber() ) )
					{
						importContactReport.addUpdateField( lable, phoneNumber.getPhoneNumber(), number );
						phoneNumber.setPhoneNumber( number );
					}
					return;
				}
			}
		}
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.setLabel( lable );
		phoneNumber.setPrimary( lable.equals( "Telemóvel" ) );
		phoneNumber.setPhoneNumber( number );
		contactEntry.getPhoneNumbers().add( phoneNumber );
		importContactReport.addNewField( "Número" + lable, number );
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

	private static void updateNome( ContactEntry contactEntry, Elemento elemento, ImportContactReport importContactReport )
	{
		Name name = contactEntry.getName();
		if ( name == null )
		{
			name = new Name();
			contactEntry.setName( name );
			importContactReport.addNewField( "Nome", elemento.getNome() );
		}
		FullName fullName = new FullName();
		fullName.setValue( elemento.getNome() );
		name.setFullName( fullName );
	}

	private static void updateEmail( ContactEntry contactEntry, String lable, String strEmail, ImportContactReport importContactReport )
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
				if ( !StringUtils.equals( strEmail, e.getAddress() ) )
				{
					importContactReport.addUpdateField( "email " + lable, e.getAddress(), strEmail );
					e.setAddress( strEmail );
				}
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
		importContactReport.addNewField( "email " + lable, strEmail );
	}

	private static void updateAniversario( ContactEntry contactEntry, String label, Date date, ImportContactReport importContactReport )
	{
		if ( date == null )
		{
			return;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		String format = dateFormat.format( date );
		Birthday birthday = contactEntry.getBirthday();
		if ( birthday == null )
		{
			birthday = new Birthday();
			contactEntry.setBirthday( birthday );
			birthday.setWhen( format );
			importContactReport.addNewField( label, format );
		}
		else if ( !StringUtils.equals( birthday.getWhen(), format ) )
		{
			importContactReport.addUpdateField( label, birthday.getWhen(), format );
			birthday.setWhen( format );
		}
	}

	private static void updatePais( ContactEntry contactEntry, String lable, String name, ImportContactReport importContactReport )
	{
		if ( StringUtils.isBlank( name ) )
		{
			return;
		}
		if ( contactEntry.getRelations() != null )
		{
			for ( Relation relation : contactEntry.getRelations() )
			{
				if ( relation.hasLabel() && relation.getLabel().equals( lable ) )
				{
					if ( !StringUtils.equals( relation.getValue(), name ) )
					{
						importContactReport.addUpdateField( "Nome " + lable, relation.getValue(), name );
						relation.setValue( name );
					}
					return;
				}
			}
		}
		Relation relation = new Relation();
		relation.setLabel( lable );
		relation.setValue( name );
		contactEntry.getRelations().add( relation );
		importContactReport.addNewField( "Nome " + lable, name );
	}
}
