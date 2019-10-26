package scouts.cne.pt;

import javax.mail.internet.MimeMessage;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleServerAuthenticationBean;
import scouts.cne.pt.utils.HTMLUtils;

public class GoogleServerCredentialTest implements HasLogger {

	public static void main(String[] args) {
		try {
			GoogleServerAuthenticationBean googleServerAuthentication = new GoogleServerAuthenticationBean();
			Gmail service = googleServerAuthentication.getGmailService();
			System.out.println( service.getServicePath() );
			// Print the labels in the user's account.
			String user = System.getenv().get( "GOOGLE_SERVER_USER_ID" );
			if ( user == null )
			{
				user = "me";
			}
			System.out.println( "GOOGLE_SERVER_USER_ID: "+ user );

			MimeMessage createEmail;
			createEmail = HTMLUtils.createEmail("andre.conrado.0@gmail.com", "patrulha.digital.122@escutismo.pt", "subject Text", "bodyText");
			Message message = service.users().messages().send( user, HTMLUtils.createMessageWithEmail( createEmail ) ).execute();

			System.out.println( "Message id: "+ message.getId() );
			System.out.println( message.toPrettyString() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
