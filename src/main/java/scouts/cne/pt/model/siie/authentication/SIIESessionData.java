package scouts.cne.pt.model.siie.authentication;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpHeaders;

/**
 * @author 62000465 2019-10-04
 *
 */
public class SIIESessionData
{
	private final Instant	instant;
	private String			strAcessToken;
	private String				strOriginalXSIIE;
	private List< String >	lstOriginalCookies;
	private final HttpHeaders	headers	= new HttpHeaders();

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-04
	 * @param instant
	 */
	public SIIESessionData( Instant instant )
	{
		super();
		this.instant = instant;
	}

	/**
	 * Getter for instant
	 * 
	 * @author 62000465 2019-10-04
	 * @return the instant {@link Instant}
	 */
	public Instant getInstant()
	{
		return instant;
	}

	/**
	 * Getter for acessToken
	 * 
	 * @author 62000465 2019-10-04
	 * @return the acessToken {@link String}
	 */
	public String getAcessToken()
	{
		return strAcessToken;
	}

	/**
	 * Setter for acessToken
	 * 
	 * @author 62000465 2019-10-04
	 * @param acessToken the acessToken to set
	 */
	public void setAcessToken( String acessToken )
	{
		strAcessToken = acessToken;
		headers.add( "Authorization", "Bearer " + strAcessToken );
	}

	/**
	 * Getter for orginalXSIIE
	 * 
	 * @author 62000465 2019-10-04
	 * @return the orginalXSIIE {@link String}
	 */
	public String getOriginalXSIIE()
	{
		return strOriginalXSIIE;
	}

	/**
	 * Setter for orginalXSIIE
	 * 
	 * @author 62000465 2019-10-04
	 * @param orginalXSIIE the orginalXSIIE to set
	 */
	public void setOriginalXSIIE( String orginalXSIIE )
	{
		strOriginalXSIIE = orginalXSIIE;
		headers.add( "xSIIE", strOriginalXSIIE );
	}

	/**
	 * Getter for originalCookies
	 * 
	 * @author 62000465 2019-10-04
	 * @return the originalCookies {@link List<String>}
	 */
	public List< String > getOriginalCookies()
	{
		return lstOriginalCookies;
	}

	/**
	 * Setter for originalCookies
	 * 
	 * @author 62000465 2019-10-04
	 * @param originalCookies the originalCookies to set
	 */
	public void setOriginalCookies( List< String > originalCookies )
	{
		lstOriginalCookies = originalCookies;
		lstOriginalCookies.forEach( cookie -> headers.add( HttpHeaders.COOKIE, cookie ) );
	}

	/**
	 * Getter for headers
	 * 
	 * @author 62000465 2019-10-04
	 * @return the headers {@link HttpHeaders}
	 */
	public HttpHeaders getHeaders()
	{
		return headers;
	}
}
