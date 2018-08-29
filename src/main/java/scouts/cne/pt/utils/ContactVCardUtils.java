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
import scouts.cne.pt.model.Explorador;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class ContactVCardUtils
{
	public static File getVCardFile( Collection< Explorador > collection ) throws IOException
	{
		File file = File.createTempFile( "list", ".vcard" );
		List< VCard > listVcard = new ArrayList<>();
		for ( Explorador explorador : collection )
		{
			VCard vCard = new VCard();
			vCard.setFormattedName( explorador.getNome() );
			updateEmail( vCard, "Pessoal", explorador.getEmail() );
			updateEmail( vCard, "Mãe", explorador.getEmailMae() );
			updateEmail( vCard, "Pai", explorador.getEmailPai() );
			updateTelefone( vCard, "Telemóvel", explorador.getTelemovel() );
			updateTelefone( vCard, "Casa", explorador.getTelefone() );
			updateTelefone( vCard, "Telefone Mãe", explorador.getTelefoneMae() );
			updateTelefone( vCard, "Telefone Pai", explorador.getTelefonePai() );
			updateDataNascimento( vCard, explorador.getDataNascimento() );
			vCard.setGender( explorador.isMasculino() ? Gender.male() : Gender.female() );
			updateMorada( vCard, explorador );
			updatePais( vCard, "Mãe", explorador.getNomeMae() );
			updatePais( vCard, "Pai", explorador.getNomePai() );
			vCard.addLanguage( "pt-PT" );
			vCard.setCategories( explorador.getCategoria().getNome() );
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
	 * @param explorador
	 */
	private static void updateMorada( VCard vCard, Explorador explorador )
	{
		if ( StringUtils.isNotBlank( explorador.getMorada() ) )
		{
			Address adr = new Address();
			adr.setStreetAddress( explorador.getMorada() );
			adr.setLocality( explorador.getLocalidade() );
			adr.setPostalCode( explorador.getCodigoPostal() );
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
