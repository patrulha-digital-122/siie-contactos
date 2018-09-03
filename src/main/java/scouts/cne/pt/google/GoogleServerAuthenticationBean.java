package scouts.cne.pt.google;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import scouts.cne.pt.app.HasLogger;

@SpringComponent
@UIScope
public class GoogleServerAuthenticationBean implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;
	private static final List< String >	SCOPES				= Arrays.asList( GmailScopes.GMAIL_SEND );

	public GoogleServerAuthenticationBean()
	{
	}

	public NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException
	{
		NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
		return builder.build();
	}

	public JacksonFactory getJsonfactry()
	{
		return JacksonFactory.getDefaultInstance();
	}

	private GoogleCredential authorizeServer()
	{
		String strGoogleClientSecrets = System.getenv().get( "GOOGLE_SERVER_SECRETS" );
		if ( StringUtils.isNotBlank( strGoogleClientSecrets ) )
		{
			try ( ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( strGoogleClientSecrets.getBytes( "UTF-8" ) ) )
			{
				return GoogleCredential.fromStream( byteArrayInputStream ).createScoped( SCOPES );
			}
			catch ( Exception e )
			{
				printError( e );
			}
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try ( InputStream in = classLoader.getResourceAsStream( "server_secrets.json" ) )
		{
			try
			{
				return GoogleCredential.fromStream( in ).createScoped( SCOPES );
			}
			catch ( IOException e )
			{
				printError( e );
			}
		}
		catch ( IOException e1 )
		{
			printError( e1 );
		}
		return null;
	}

	public Gmail getGmailService() throws GeneralSecurityException, IOException
	{
		GoogleCredential authorizeServer = authorizeServer();
		getLogger().info( "App name: {}", authorizeServer.getServiceAccountProjectId() );
		return new Gmail.Builder( getHttpTransport(), getJsonfactry(), authorizeServer )
						.setApplicationName( authorizeServer.getServiceAccountProjectId() ).build();
	}
}
