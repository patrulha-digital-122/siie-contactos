package scouts.cne.pt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import scouts.cne.pt.model.siie.CookieRestTemplate;
import scouts.cne.pt.model.siie.SIIEElementos;
import scouts.cne.pt.model.siie.SIIEUserLogin;
import scouts.cne.pt.model.siie.SIIEUserTokenRequest;

public class SIIEWebApiTest
{

	public static void main( String[] args )
	{
		try
		{
			
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			Proxy proxy = new Proxy( Type.HTTP, new InetSocketAddress( "localhost", 808 ) );
			requestFactory.setProxy( proxy );
			URI uriLogin = new URI( "https://siie.escutismo.pt/api/logintoken" );
			URI uri = new URI(
							"https://siie.escutismo.pt/elementos/list?xml=elementos/elementos/dados-completos" );
			
			CookieRestTemplate restTemplate = new CookieRestTemplate( requestFactory );

			SIIEUserTokenRequest tokenRequest = new SIIEUserTokenRequest();
			tokenRequest.setUsername( "" );
			tokenRequest.setPassword( "" );

			ResponseEntity< SIIEUserLogin > postForEntity =
							restTemplate.exchange( uriLogin, HttpMethod.POST, new HttpEntity<>( tokenRequest ), SIIEUserLogin.class );

			String strAcessToken = postForEntity.getBody().getAcessToken();
			List< String > orDefault = postForEntity.getHeaders().getOrDefault( "xSIIE", Arrays.asList() );
			List< String > lstOriginalCookies = postForEntity.getHeaders().get( HttpHeaders.SET_COOKIE );
			
			
			if ( !orDefault.isEmpty() )
			 {
				String strOrginalXSIIE = orDefault.get( 0 );

				HttpHeaders headers = new HttpHeaders();
				headers.add( "Authorization", "Bearer " + strAcessToken );
				headers.add( "xSIIE", strOrginalXSIIE );
				for ( String string : lstOriginalCookies )
				{
					headers.add( HttpHeaders.COOKIE, string );
				}

				Map< String, String > mapRequest = new LinkedHashMap<>();
				mapRequest.put( "Dados completos", "https://siie.escutismo.pt/elementos/list?xml=elementos/elementos/dados-completos" );
				mapRequest.put( "Dados saude", "https://siie.escutismo.pt/elementos/List?xml=elementos/dadossaude/dados-saude" );
				mapRequest.put( "Noites de campo", "https://siie.escutismo.pt/elementos/List?xml=elementos/actividades/noites-campo" );
				
				for ( Entry< String, String > entry : mapRequest.entrySet() )
				{
					String info = entry.getKey();
					String url = entry.getValue();
					ResponseEntity< String > forEntity =
									restTemplate.exchange( new URI( url ), HttpMethod.GET, new HttpEntity( null, headers ), String.class );
					if ( forEntity.hasBody() )
					{
						String strWSApi = StringUtils.substringBetween( forEntity.getBody(), "wsapi: \"", "\"," );
						URI uriElementos = new URI( "https://siie.escutismo.pt" + strWSApi +
							"&%7B%22take%22%3A2%2C%22skip%22%3A0%2C%22page%22%3A1%2C%22pageSize%22%3A12%2C%22sort%22%3A%5B%5D%7D" );
						restTemplate.setAcessToken( strAcessToken );
						restTemplate.setCookies( lstOriginalCookies );
						ResponseEntity< SIIEElementos > elementosFor = restTemplate.getForEntity( uriElementos, SIIEElementos.class );
						System.out.println( info + " :: " + elementosFor.getBody().getCount() );
						System.out.println( elementosFor.getBody().getCount() );
					}
				}
			}
			
			

		}
		catch ( final Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String readStream( InputStream in )
	{
		StringBuilder sb = new StringBuilder();
		try ( BufferedReader reader = new BufferedReader( new InputStreamReader( in ) ); )
		{
			String nextLine = "";
			String newLine = System.getProperty( "line.separator" );
			while ( ( nextLine = reader.readLine() ) != null )
			{
				sb.append( nextLine + newLine );
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return sb.toString();
	}

}
