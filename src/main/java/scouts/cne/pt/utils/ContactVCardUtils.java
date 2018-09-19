package scouts.cne.pt.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.AddressType;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Email;
import ezvcard.property.Gender;
import ezvcard.property.Revision;
import ezvcard.property.Telephone;
import scouts.cne.pt.model.Elemento;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class ContactVCardUtils
{
	public static File getVCardFile( Collection< Elemento > collection ) throws IOException
	{
		File file = File.createTempFile( "list", ".vcard" );
		List< VCard > listVcard = new ArrayList<>();
		for ( Elemento elemento : collection )
		{
			VCard vCard = new VCard();
			vCard.setFormattedName( elemento.getNome() );
			updateEmail( vCard, "Pessoal", elemento.getEmail() );
			updateEmail( vCard, "Mãe", elemento.getEmailMae() );
			updateEmail( vCard, "Pai", elemento.getEmailPai() );
			updateTelefone( vCard, "Telemóvel", elemento.getTelemovel() );
			updateTelefone( vCard, "Casa", elemento.getTelefone() );
			updateTelefone( vCard, "Telefone Mãe", elemento.getTelefoneMae() );
			updateTelefone( vCard, "Telefone Pai", elemento.getTelefonePai() );
			updateDataNascimento( vCard, elemento.getDataNascimento() );
			vCard.setGender( elemento.isMasculino() ? Gender.male() : Gender.female() );
			updateMorada( vCard, elemento );
			updatePais( vCard, "Mãe", elemento.getNomeMae() );
			updatePais( vCard, "Pai", elemento.getNomePai() );
			vCard.addLanguage( "pt-PT" );
			vCard.setCategories( elemento.getCategoria().getNome() );
			vCard.setRevision( Revision.now() );
			listVcard.add( vCard );
		}
		try ( OutputStreamWriter outputStreamWriter = new OutputStreamWriter( new FileOutputStream( file ), "windows-1252" ) )
		{
			Ezvcard.write( listVcard ).version( VCardVersion.V3_0 ).go( outputStreamWriter );
		}
		return file;
	}

	/**
	 * The <b>updateMorada</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-08-29
	 * @param vCard
	 * @param elemento
	 */
	private static void updateMorada( VCard vCard, Elemento elemento )
	{
		if ( StringUtils.isNotBlank( elemento.getMorada() ) )
		{
			Address adr = new Address();
			adr.setStreetAddress( elemento.getMorada() );
			adr.setLocality( elemento.getLocalidade() );
			adr.setPostalCode( elemento.getCodigoPostal() );
			adr.setCountry( "PT" );
			adr.getTypes().add( AddressType.HOME );
			vCard.addAddress( adr );
		}
	}

	/**
	 * The <b>updatePais</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-08-29
	 * @param vCard
	 * @param string
	 * @param nomePai
	 */
	private static void updatePais( VCard vCard, String string, String strNome )
	{
		if ( StringUtils.isNotBlank( strNome ) )
		{
		}
	}

	/**
	 * The <b>updateEmail</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-08-29
	 * @param vCard
	 * @param string
	 * @param strEmail
	 */
	private static void updateEmail( VCard vCard, String string, String strEmail )
	{
		if ( StringUtils.isNotBlank( strEmail ) )
		{
			Email email = new Email( strEmail );
			email.setParameter( string, strEmail );
			vCard.addEmail( email );
		}
	}

	/**
	 * 
	 * The <b>updateTelefone</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-08-29
	 * @param vCard
	 * @param string
	 * @param strTelefone
	 */
	private static void updateTelefone( VCard vCard, String string, String strTelefone )
	{
		if ( StringUtils.isNotBlank( strTelefone ) )
		{
			Telephone telephone = new Telephone( strTelefone );
			telephone.setParameter( string, string );
			vCard.addTelephoneNumber( telephone );
		}
	}

	/**
	 * The <b>updateDataNascimento</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-08-29
	 * @param vCard
	 * @param dataNascimento
	 */
	private static void updateDataNascimento( VCard vCard, Date dataNascimento )
	{
		if ( dataNascimento != null )
		{
			Birthday birthday = new Birthday( dataNascimento );
			vCard.setBirthday( birthday );
		}
	}
}
