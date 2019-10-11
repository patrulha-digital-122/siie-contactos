package scouts.cne.pt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.MaskFormatter;
import org.apache.commons.lang3.StringUtils;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.contacts.Birthday;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.Event;
import com.google.gdata.data.contacts.Gender;
import com.google.gdata.data.contacts.Nickname;
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
import com.google.gdata.data.extensions.When;
import com.vaadin.flow.component.checkbox.Checkbox;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.ElementoTags;
import scouts.cne.pt.model.ImportContactReport;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.model.siie.SIIEElemento;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class ContactUtils
{
	public static ElementoImport convertElementoToContactEntry(	Elemento elemento,
	                                                           	ContactEntry contactEntry,
	                                                           	Set< String > listTelefonesExistentes,
																Map< ElementoTags, Checkbox > mapConfigs )
	{
		ImportContactReport importContactReport = new ImportContactReport( elemento.getNin() );
		if ( contactEntry == null )
		{
			contactEntry = new ContactEntry();
		}
		else
		{
			removeNINandNIF( contactEntry );
		}
		updateUserDefinedField( contactEntry, "NIN", elemento.getNin(), importContactReport );
		updateUserDefinedField( contactEntry, "NIF", elemento.getNif(), importContactReport );
		if ( updatePropertie( mapConfigs, ElementoTags.TOTEM ) )
		{
			updateUserDefinedField( contactEntry, ElementoTags.TOTEM.getTagDescription(), elemento.getTotem(), importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.NOME ) )
		{
			updateNome( contactEntry, elemento, importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.TELEMOVEL ) )
		{
			updatePhoneNumber( contactEntry, "Telemóvel", elemento.getTelemovel(), importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.TELEFONE ) )
		{
			updatePhoneNumber( contactEntry, "Telefone", elemento.getTelefone(), importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.EMAIL ) )
		{
			updateEmail( contactEntry, "Pessoal", elemento.getEmail(), importContactReport );
		}
		if ( elemento.getCategoria().equals( SECCAO.DIRIGENTES ) )
		{
			listTelefonesExistentes.add( elemento.getTelemovel() );
			listTelefonesExistentes.add( elemento.getTelefone() );
		}
		else
		{
			if ( !listTelefonesExistentes.contains( elemento.getTelefoneMae() ) && updatePropertie( mapConfigs, ElementoTags.TELEFONE_MAE ) )
			{
				updatePhoneNumber( contactEntry, "Mãe", elemento.getTelefoneMae(), importContactReport );
				listTelefonesExistentes.add( elemento.getTelefoneMae() );
			}
			if ( !listTelefonesExistentes.contains( elemento.getTelefonePai() ) && updatePropertie( mapConfigs, ElementoTags.TELEFONE_PAI ) )
			{
				updatePhoneNumber( contactEntry, "Pai", elemento.getTelefonePai(), importContactReport );
				listTelefonesExistentes.add( elemento.getTelefonePai() );
			}
			if ( updatePropertie( mapConfigs, ElementoTags.EMAIL_MAE ) )
			{
				updateEmail( contactEntry, "Mãe", elemento.getEmailMae(), importContactReport );
			}
			if ( updatePropertie( mapConfigs, ElementoTags.EMAIL_PAI ) )
			{
				updateEmail( contactEntry, "Pai", elemento.getEmailPai(), importContactReport );
			}
		}
		if ( updatePropertie( mapConfigs, ElementoTags.DATA_NASCIMENTO ) )
		{
			updateAniversario( contactEntry, "Aniversário", elemento.getDataNascimento(), importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.DATA_PROMESSA ) )
		{
			updateEvent( contactEntry, ElementoTags.DATA_PROMESSA.getTagDescription(), elemento.getDataPromessa(), importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.DATA_ADMISSAO ) )
		{
			updateEvent( contactEntry, ElementoTags.DATA_ADMISSAO.getTagDescription(), elemento.getDataAdmissao(), importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.NOME_MAE ) )
		{
			updatePais( contactEntry, "Mãe", elemento.getNomeMae(), importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.NOME_PAI ) )
		{
			updatePais( contactEntry, "Pai", elemento.getNomePai(), importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.MORADA ) )
		{
			updateMorada( contactEntry, "Casa", elemento, importContactReport );
		}
		if ( updatePropertie( mapConfigs, ElementoTags.TOTEM ) )
		{
			updateNickname( contactEntry, ElementoTags.TOTEM.getTagDescription(), elemento, importContactReport );
		}
		contactEntry.setGender( elemento.isMasculino() ? new Gender( Gender.Value.MALE ) : new Gender( Gender.Value.FEMALE ) );
		return new ElementoImport( contactEntry, elemento, importContactReport );
	}

	/**
	 * The <b>updatePropertie</b> method returns {@link boolean}
	 *
	 * @author anco62000465 2018-09-27
	 * @param mapConfigs
	 * @param totem
	 * @return
	 */
	private static boolean updatePropertie( Map< ElementoTags, Checkbox > mapConfigs, ElementoTags tags )
	{
		Checkbox checkBox = mapConfigs.get( tags );
		if ( checkBox != null )
		{
			return checkBox.getValue();
		}
		return false;
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
			if ( !alreadyHave || ( structuredPostalAddress.getCity() == null ) )
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
			if ( !alreadyHave || ( structuredPostalAddress.getFormattedAddress() == null ) )
			{
				FormattedAddress formattedAddress = new FormattedAddress( elemento.getMorada() );
				structuredPostalAddress.setFormattedAddress( formattedAddress );
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
			if ( !alreadyHave || ( structuredPostalAddress.getPostcode() == null ) )
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
		number = convertPhoneNumber( number );
		if ( contactEntry.getPhoneNumbers() != null )
		{
			for ( PhoneNumber phoneNumber : contactEntry.getPhoneNumbers() )
			{
				if ( ( phoneNumber.getLabel() != null ) && phoneNumber.getLabel().equals( lable ) )
				{
					String previousPhoneNumber = convertPhoneNumber( phoneNumber.getPhoneNumber() );
					if ( !StringUtils.equals( number, previousPhoneNumber ) )
					{
						importContactReport.addUpdateField( lable, previousPhoneNumber, number );
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
			FullName fullName = new FullName();
			fullName.setValue( elemento.getNome() );
			name.setFullName( fullName );
			importContactReport.addNewField( "Nome", elemento.getNome() );
			return;
		}
		FullName fullName = name.getFullName();
		if ( fullName == null )
		{
			name.setFullName( new FullName() );
		}
		if ( !StringUtils.equals( elemento.getNome().trim(), name.getFullName().getValue() ) )
		{
			importContactReport.addUpdateField( "Nome", name.getFullName().getValue(), elemento.getNome() );
			name.getFullName().setValue( elemento.getNome() );
		}
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

	private static void updateEvent( ContactEntry contactEntry, String label, Date date, ImportContactReport importContactReport )
	{
		if ( date == null )
		{
			return;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		String format = dateFormat.format( date );
		When when = new When();
		DateTime v = new DateTime( date );
		v.setDateOnly( true );
		when.setStartTime( v );
		List< Event > events = contactEntry.getEvents();
		for ( Event event : events )
		{
			if ( StringUtils.equals( event.getLabel(), label ) )
			{
				if ( ( event.getWhen() != null ) && !event.getWhen().getStartTime().equals( when.getStartTime() ) )
				{
					event.setWhen( when );
					importContactReport.addNewField( label, format );
				}
				return;
			}
		}
		Event event = new Event();
		event.setLabel( label );
		event.setWhen( when );
		contactEntry.getEvents().add( event );
		importContactReport.addNewField( label, format );
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

	private static void updateNickname( ContactEntry contactEntry, String tagDescription, Elemento elemento, ImportContactReport importContactReport )
	{
		if ( StringUtils.isBlank( elemento.getTotem() ) )
		{
			return;
		}
		Nickname nickname = contactEntry.getNickname();
		if ( nickname == null )
		{
			nickname = new Nickname();
			contactEntry.setNickname( nickname );
			FullName fullName = new FullName();
			fullName.setValue( elemento.getNome() );
			importContactReport.addNewField( "Nickname/Totem", elemento.getTotem() );
			return;
		}

		if ( !StringUtils.equalsIgnoreCase( elemento.getTotem().trim(), nickname.getValue() ) )
		{
			importContactReport.addUpdateField( "Nickname/Totem", nickname.getValue(), elemento.getTotem() );
			nickname.setValue( elemento.getTotem() );
		}
	}

	public static String convertPhoneNumber( String phoneNumber )
	{
		if ( StringUtils.isBlank( phoneNumber ) )
		{
			return "";
		}
		phoneNumber = phoneNumber.replace( " ", "" );
		if ( phoneNumber.length() > 9 )
		{
			phoneNumber = phoneNumber.substring( phoneNumber.length() - 9 );
		}
		else if ( phoneNumber.length() < 9 )
		{
			return "";
		}
		String phoneMask = "+351 ### ### ####";
		MaskFormatter maskFormatter;
		try
		{
			maskFormatter = new MaskFormatter( phoneMask );
			maskFormatter.setValueContainsLiteralCharacters( false );
			return maskFormatter.valueToString( phoneNumber );
		}
		catch ( ParseException e )
		{
		}
		return "";
	}

	public static String getMailingListFromElemento( Set< SIIEElemento > siieElementos, boolean usarMailPais, boolean usarNomes )
	{
		List< String > list = new ArrayList<>();
		for ( SIIEElemento siieElemento : siieElementos )
		{
			list.add( getEmail( siieElemento.getEmail(), usarNomes ? siieElemento.getNome() : null ) );
			if ( usarMailPais )
			{
				list.add( getEmail(	siieElemento.getMaeemail(),
									usarNomes && StringUtils.isNotBlank( siieElemento.getMae() ) ? siieElemento.getMae() : null ) );
				list.add( getEmail(	siieElemento.getPaiemail(),
									usarNomes && StringUtils.isNotBlank( siieElemento.getPai() ) ? siieElemento.getPai() : null ) );
			}
		}
		list.removeIf( p -> StringUtils.isBlank( p ) );
		return StringUtils.join( list, ", " );
	}

	private static String getEmail( String email, String nome )
	{
		StringBuilder sb = new StringBuilder();
		if ( StringUtils.isNotEmpty( email ) )
		{
			if ( nome != null )
			{
				sb.append( "\"" );
				sb.append( nome );
				sb.append( "\" " );
			}
			sb.append( "<" );
			sb.append( email );
			sb.append( ">" );
		}
		return sb.toString();
	}
}
