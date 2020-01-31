package scouts.cne.pt.model.siie;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author 62000465 2019-02-27
 *
 */
public class CookieRestTemplate extends RestTemplate
{
	private String			strAcessToken;
	private List< String >	lstCookies	= new ArrayList<>();

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-02-27
	 * @param requestFactory
	 */
	public CookieRestTemplate( SimpleClientHttpRequestFactory requestFactory )
	{
		super();
		setRequestFactory( requestFactory );
	}

	@Override
	protected ClientHttpRequest createRequest( URI url, HttpMethod method ) throws IOException
	{
		ClientHttpRequest request = super.createRequest( url, method );
		if ( StringUtils.isNotBlank( strAcessToken ) )
		{
			request.getHeaders().setBearerAuth( strAcessToken );
			request.getHeaders().add( HttpHeaders.COOKIE, "Bearer " + strAcessToken );
		}
		else
		{
			request.getHeaders().setBearerAuth( "" );
			request.getHeaders().add( HttpHeaders.COOKIE, "" );
		}

		request.getHeaders().clear();
		for ( String string : lstCookies )
		{
			request.getHeaders().add( HttpHeaders.COOKIE, string );
		}

		return request;
	}

	/**
	 * Getter for acessToken
	 * 
	 * @author 62000465 2019-02-28
	 * @return the acessToken {@link String}
	 */
	public String getAcessToken()
	{
		return strAcessToken;
	}

	/**
	 * Setter for acessToken
	 * 
	 * @author 62000465 2019-02-28
	 * @param acessToken the acessToken to set
	 */
	public void setAcessToken( String acessToken )
	{
		strAcessToken = acessToken;
	}

	/**
	 * Getter for cookies
	 * 
	 * @author 62000465 2019-02-28
	 * @return the cookies {@link List<String>}
	 */
	public List< String > getCookies()
	{
		return lstCookies;
	}

	/**
	 * Setter for cookies
	 * 
	 * @author 62000465 2019-02-28
	 * @param cookies the cookies to set
	 */
	public void setCookies( List< String > cookies )
	{
		lstCookies = cookies;
	}
}
