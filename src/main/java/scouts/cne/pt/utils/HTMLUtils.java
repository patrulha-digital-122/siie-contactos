package scouts.cne.pt.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;

public class HTMLUtils {

	public static String strHTMLHelpFicheiroSIIE = "<p>Para fazer o download do ficheiro .xls do SIIE deve aceder &agrave;s listagens completas - <a href=\"https://siie.escutismo.pt/elementos/List?xml=elementos/elementos/dados-completos\" target=\"_blank\" rel=\"noopener\">LINK</a> - e fazer o download carregando no bot&atilde;o que est&aacute; no canto superior esquerdo.</p>";

	public static MimeMessage createEmail(String to, String from, String subject, String bodyText)
			throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);

		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
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
}
