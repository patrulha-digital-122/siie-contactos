package scouts.cne.pt.model.siie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 62000465 2019-02-06
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class SIIEUserLogin
{
	@JsonProperty( value = "username" )
	private String	strUsername;
	@JsonProperty( value = "access_token" )
	private String	strAcessToken;
	@JsonProperty( value = "token_type" )
	private String	strTokenType;
	@JsonProperty( value = "expires_in" )
	private Integer	iExpiresIn;

	/**
	 * Getter for username
	 * 
	 * @author 62000465 2019-02-06
	 * @return the username {@link String}
	 */
	public String getUsername()
	{
		return strUsername;
	}

	/**
	 * Setter for username
	 * 
	 * @author 62000465 2019-02-06
	 * @param username the username to set
	 */
	public void setUsername( String username )
	{
		strUsername = username;
	}

	/**
	 * Getter for acessToken
	 * 
	 * @author 62000465 2019-02-06
	 * @return the acessToken {@link String}
	 */
	public String getAcessToken()
	{
		return strAcessToken;
	}

	/**
	 * Setter for acessToken
	 * 
	 * @author 62000465 2019-02-06
	 * @param acessToken the acessToken to set
	 */
	public void setAcessToken( String acessToken )
	{
		strAcessToken = acessToken;
	}

	/**
	 * Getter for tokenType
	 * 
	 * @author 62000465 2019-02-06
	 * @return the tokenType {@link String}
	 */
	public String getTokenType()
	{
		return strTokenType;
	}

	/**
	 * Setter for tokenType
	 * 
	 * @author 62000465 2019-02-06
	 * @param tokenType the tokenType to set
	 */
	public void setTokenType( String tokenType )
	{
		strTokenType = tokenType;
	}

	/**
	 * Getter for expiresIn
	 * 
	 * @author 62000465 2019-02-06
	 * @return the expiresIn {@link Integer}
	 */
	public Integer getExpiresIn()
	{
		return iExpiresIn;
	}

	/**
	 * Setter for expiresIn
	 * 
	 * @author 62000465 2019-02-06
	 * @param expiresIn the expiresIn to set
	 */
	public void setExpiresIn( Integer expiresIn )
	{
		iExpiresIn = expiresIn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "SIIEUserLogin [strUsername=" + strUsername + ", strAcessToken=" + strAcessToken + ", strTokenType=" + strTokenType + ", iExpiresIn=" +
			iExpiresIn + "]";
	}

}
