package scouts.cne.pt.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import j2html.tags.UnescapedText;
import scouts.cne.pt.model.ElementoTags;

public class HTMLUtils {

	public static String strHTMLHelpFicheiroSIIE = "<p>Para fazer o download do ficheiro .xls do SIIE deve aceder &agrave;s listagens completas - <a href=\"https://siie.escutismo.pt/elementos/List?xml=elementos/elementos/dados-completos\" target=\"_blank\" rel=\"noopener\">LINK</a> - e fazer o download carregando no bot&atilde;o que est&aacute; no canto superior esquerdo.</p>";
	private static Logger	logger					= LoggerFactory.getLogger( HTMLUtils.class );

	public static MimeMessage createEmail(	List< InternetAddress > listTo,
											List< InternetAddress > listCc,
											List< InternetAddress > listBcc,
											InternetAddress fromInternetAddress,
											String subject,
											String bodyText )
		throws MessagingException
	{
		Properties props = new Properties();
		Session session = Session.getDefaultInstance( props, null );
		MimeMessage email = new MimeMessage( session );
		email.setFrom( fromInternetAddress );
		if ( listTo != null )
		{
			listTo.forEach( p ->
			{
				try
				{
					email.addRecipient( javax.mail.Message.RecipientType.TO, p );
				}
				catch ( MessagingException e )
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
					email.addRecipient( javax.mail.Message.RecipientType.CC, p );
				}
				catch ( MessagingException e )
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
					email.addRecipient( javax.mail.Message.RecipientType.BCC, p );
				}
				catch ( MessagingException e )
				{
					logger.error( e.getMessage(), e );
				}
			} );
		}
		email.setSubject( subject );
		email.setContent( bodyText, "text/html; charset=utf-8" );
		return email;
	}

	public static Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		emailContent.writeTo(buffer);
		byte[] bytes = buffer.toByteArray();
		String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
		Message message = new Message();
		message.setRaw(encodedEmail);
		return message;
	}

	public static String getDefaultEmail()
	{
		UnescapedText linhaOla = TagCreator.join(	TagCreator.text( "Olá " ),
													TagCreator.text( ElementoTags.NOME.getTagReplace() ),
													TagCreator.text( "," ),
													TagCreator.br() );
		UnescapedText linhaInicio = TagCreator.join(	TagCreator.text( "Com o inicio do ano escutista é altura de confirmar se os dados pessoais que temos estão actualizados. Por isso pedimos que nos informe se algum destes dados está incompleto/desactualizado:" ),
		                                         	TagCreator.br() );
		UnescapedText each = TagCreator.join( TagCreator
						.ul( TagCreator.each(	Arrays.asList( ElementoTags.values() ),
												e -> TagCreator.li( TagCreator.join(	TagCreator.b( TagCreator.text( e.getTagDescription() + ": " ) ),
																						TagCreator.text( e.getTagReplace() ) ) ) ) ) );
		UnescapedText linhaDespedida = TagCreator.join(	TagCreator.text( "Obrigado. " ),
													TagCreator.br(),
														TagCreator.br(),
													TagCreator.text( "Canhotas," ),
													TagCreator.br() );
		ContainerTag p = TagCreator.p( linhaOla, linhaInicio, each, linhaDespedida );
		return p.render();
	}
}
