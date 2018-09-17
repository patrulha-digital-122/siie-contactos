package scouts.cne.pt;

import java.util.List;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleServerAuthenticationBean;
import scouts.cne.pt.utils.HTMLUtils;

/**
 * @author anco62000465 2018-09-17
 *
 */
public class TestEmail implements HasLogger
{
	private final static Logger logger = LoggerFactory.getLogger( TestEmail.class );
	
	/**
	 * The <b>main</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-09-17
	 * @param args
	 */
	public static void main( String[] args )
	{
		try
		{
			GoogleServerAuthenticationBean googleServerAuthentication = new GoogleServerAuthenticationBean();
			Gmail service = googleServerAuthentication.getGmailService();
			logger.info( service.getServicePath() );
			// Print the labels in the user's account.
			String user = System.getenv().get( "GOOGLE_SERVER_USER_ID" );
			if ( user == null )
			{
				user = "me";
			}
			logger.info( "GOOGLE_SERVER_USER_ID: {}", user );
			ListLabelsResponse listResponse = service.users().labels().list( user ).execute();
			List< com.google.api.services.gmail.model.Label > labels = listResponse.getLabels();
			if ( labels.isEmpty() )
			{
				logger.info( "No labels found." );
			}
			else
			{
				logger.info( "Labels:" );
				for ( com.google.api.services.gmail.model.Label label : labels )
				{
					logger.info( "## {}", label.getName() );
				}
			}
			MimeMessage createEmail =
							HTMLUtils.createEmail( "andre.conrado.0@gmail.com", "patrulha.digital.122@escutismo.pt", "subject Text", "bodyText" );
			Message message = service.users().messages().send( user, HTMLUtils.createMessageWithEmail( createEmail ) ).execute();
			logger.info( "Message id: {}", message.getId() );
			logger.info( message.toPrettyString() );
		}
		catch ( Exception e )
		{
			logger.error( e.getMessage(), e );
		}
	}
}
