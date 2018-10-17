package scouts.cne.pt.google;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import scouts.cne.pt.app.HasLogger;

@Service
public class GoogleServerAuthenticationBean implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;
	private static final List< String >	SCOPES				= Arrays.asList( GmailScopes.GMAIL_SEND, SheetsScopes.SPREADSHEETS_READONLY );

	public GoogleServerAuthenticationBean()
	{
	}

	public NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException
	{
		final NetHttpTransport.Builder builder = new NetHttpTransport.Builder().trustCertificates( GoogleUtils.getCertificateTrustStore() );
		return builder.build();
	}

	public JacksonFactory getJsonfactry()
	{
		return new JacksonFactory();
	}

	private GoogleCredential createCredentialUsingServerToken() throws IOException, GeneralSecurityException
	{
		// Use the email address when granting the service account access to supported Google APIs
		final String serviceAccountUserEmail = "gmail-server@siie-importer-server.iam.gserviceaccount.com";

		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		final InputStream inputStream = classLoader.getResourceAsStream( "siie-importer-server.p12" );
		final File f = File.createTempFile( "temp", ".p12" );
		try ( FileOutputStream fileOutputStream = new FileOutputStream( f ) )
		{
			IOUtils.copy( inputStream, fileOutputStream );
		}
		catch ( final Exception e )
		{
			printError( e );
		}
		getLogger().info( "P12File path: {} - {}", f.getAbsolutePath(), f.getTotalSpace() );
		final GoogleCredential credential = new GoogleCredential.Builder().setTransport( getHttpTransport() ).setJsonFactory( getJsonfactry() )
						.setServiceAccountId( serviceAccountUserEmail ) // requesting the token
						.setServiceAccountPrivateKeyFromP12File( f ).setServiceAccountScopes( SCOPES ) // see
																										// https://developers.google.com/gmail/api/auth/scopes
						.setServiceAccountUser( serviceAccountUserEmail ).build();
		getLogger().info( "refresh token? {}", credential.refreshToken() );
		return credential;

	}

	private GoogleCredential authorizeServer( String fileName )
	{
		final String strGoogleClientSecrets = System.getenv().get( "GOOGLE_SERVER_SECRETS" );
		if ( StringUtils.isNotBlank( strGoogleClientSecrets ) )
		{
			try ( ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( strGoogleClientSecrets.getBytes( "UTF-8" ) ) )
			{
				return GoogleCredential.fromStream( byteArrayInputStream ).createScoped( SCOPES );
			}
			catch ( final Exception e )
			{
				printError( e );
			}
		}
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try ( InputStream in = classLoader.getResourceAsStream( fileName ) )
		{
			try
			{
				return GoogleCredential.fromStream( in ).createScoped( SCOPES );
			}
			catch ( final IOException e )
			{
				printError( e );
			}
		}
		catch ( final IOException e1 )
		{
			printError( e1 );
		}
		return null;
	}

	public Gmail getGmailService() throws GeneralSecurityException, IOException
	{
		final GoogleCredential authorizeServer = authorizeServer( "server_secrets.json" );
		getLogger().info( "App name: {}", authorizeServer.getServiceAccountProjectId() );
		// if ( authorizeServer.refreshToken() )
		// {
		// GoogleCredential googleCredential = new GoogleCredential().setAccessToken( authorizeServer.getAccessToken()
		// );
		// getLogger().info( "GoogleCredential refresh token: {}", authorizeServer.getRefreshToken() );
		// getLogger().info( "GoogleCredential access token: {}", authorizeServer.getAccessToken() );
		// return new Gmail.Builder( getHttpTransport(), getJsonfactry(), googleCredential ).setApplicationName(
		// "siie-importer-server" ).build();
		// }
		return new Gmail.Builder( getHttpTransport(), getJsonfactry(), authorizeServer ).setApplicationName( "siie-importer-server" ).build();
	}

	public Sheets getSheetsService() throws GeneralSecurityException, IOException
	{
		final GoogleCredential authorizeServer = authorizeServer( "server_secrets.json" );
		getLogger().info( "App name: {}", authorizeServer.getServiceAccountProjectId() );
		return new Sheets.Builder( getHttpTransport(), getJsonfactry(), authorizeServer )
						.setApplicationName( authorizeServer.getServiceAccountProjectId() ).build();
	}
}
