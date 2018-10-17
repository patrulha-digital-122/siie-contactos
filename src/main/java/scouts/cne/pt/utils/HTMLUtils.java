package scouts.cne.pt.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import j2html.tags.UnescapedText;
import scouts.cne.pt.model.ElementoTags;

public class HTMLUtils
{

	public static String	strHTMLHelpFicheiroSIIE	=
					"<p>Para fazer o download do ficheiro .xls do SIIE deve aceder &agrave;s listagens completas - <a href=\"https://siie.escutismo.pt/elementos/List?xml=elementos/elementos/dados-completos\" target=\"_blank\" rel=\"noopener\">LINK</a> - e fazer o download carregando no bot&atilde;o que est&aacute; no canto superior esquerdo.</p>";
	private static Logger	logger					= LoggerFactory.getLogger( HTMLUtils.class );

	public static MimeMessage createEmail(	List< InternetAddress > listTo,
											List< InternetAddress > listCc,
											List< InternetAddress > listBcc,
											InternetAddress fromInternetAddress,
											String subject,
											String bodyText,
											File attachFile )
		throws MessagingException
	{
		final Properties props = new Properties();
		final Session session = Session.getDefaultInstance( props, null );
		final MimeMessage message = new MimeMessage( session );
		message.setFrom( fromInternetAddress );
		if ( listTo != null )
		{
			listTo.forEach( p ->
			{
				try
				{
					message.addRecipient( javax.mail.Message.RecipientType.TO, p );
				}
				catch ( final MessagingException e )
				{
					logger.error( e.getMessage(), e );
				}
			} );
		}
		if ( listCc != null )
		{
			listCc.forEach( p ->
			{
				try
				{
					message.addRecipient( javax.mail.Message.RecipientType.CC, p );
				}
				catch ( final MessagingException e )
				{
					logger.error( e.getMessage(), e );
				}
			} );
		}
		if ( listBcc != null )
		{
			listBcc.forEach( p ->
			{
				try
				{
					message.addRecipient( javax.mail.Message.RecipientType.BCC, p );
				}
				catch ( final MessagingException e )
				{
					logger.error( e.getMessage(), e );
				}
			} );
		}
		message.setSubject( subject );

		final Multipart multipart = new MimeMultipart();

		final MimeBodyPart textBodyPart = new MimeBodyPart();
		textBodyPart.setContent( bodyText, "text/html; charset=utf-8" );
		multipart.addBodyPart( textBodyPart ); // add the text part

		if ( attachFile != null )
		{

			final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
			final DataSource source = new FileDataSource( attachFile ); //
			attachmentBodyPart.setDataHandler( new DataHandler( source ) );
			attachmentBodyPart.setFileName( attachFile.getName() ); //

			multipart.addBodyPart( attachmentBodyPart ); // add the attachement part
		}

		message.setContent( multipart );

		return message;
	}

	public static Message createMessageWithEmail( MimeMessage emailContent ) throws MessagingException, IOException
	{
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		emailContent.writeTo( buffer );
		final byte[] bytes = buffer.toByteArray();
		final String encodedEmail = Base64.encodeBase64URLSafeString( bytes );
		final Message message = new Message();
		message.setRaw( encodedEmail );
		return message;
	}

	public static String getDefaultEmail()
	{
		final UnescapedText linhaOla = TagCreator.join(	TagCreator.text( "Olá " ),
														TagCreator.text( ElementoTags.NOME.getTagReplace() ),
														TagCreator.text( "," ),
														TagCreator.br() );
		final UnescapedText linhaInicio = TagCreator.join(	TagCreator
						.text( "Com o inicio do ano escutista é altura de confirmar se os dados pessoais que temos estão actualizados. Por isso pedimos que nos informe se algum destes dados está incompleto/desactualizado:" ),
															TagCreator.br() );
		final UnescapedText each = TagCreator.join( TagCreator
						.ul( TagCreator.each(	ElementoTags.getGoogleImportTags(),
												e -> TagCreator.li( TagCreator.join(	TagCreator.b( TagCreator.text( e.getTagDescription() + ": " ) ),
																						TagCreator.text( e.getTagReplace() ) ) ) ) ) );
		final UnescapedText linhaUnchange =
						TagCreator.join( TagCreator.text( "Aproveitamos a partilhamos alguns dados sobre a tua vida escutista:" ), TagCreator.br() );
		final UnescapedText eachUnchange = TagCreator.join( TagCreator
						.ul( TagCreator.each(	ElementoTags.getUnchagedTags(),
												e -> TagCreator.li( TagCreator.join(	TagCreator.b( TagCreator.text( e.getTagDescription() + ": " ) ),
																						TagCreator.text( e.getTagReplace() ) ) ) ) ) );
		final UnescapedText linhaDespedida = TagCreator
						.join( TagCreator.text( "Obrigado. " ), TagCreator.br(), TagCreator.br(), TagCreator.text( "Canhotas," ), TagCreator.br() );
		final ContainerTag p = TagCreator.p( linhaOla, linhaInicio, each, linhaUnchange, eachUnchange, linhaDespedida );
		return p.render();
	}
}
