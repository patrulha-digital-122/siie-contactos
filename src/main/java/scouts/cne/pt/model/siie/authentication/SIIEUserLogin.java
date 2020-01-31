package scouts.cne.pt.model.siie.authentication;

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
	@JsonProperty( value = "Message" )
	private String	strMessage;
	@JsonProperty( value = "MessageDetail" )
	private String	strMessageDetail;

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

	/**
	 * Getter for message
	 * 
	 * @author 62000465 2019-10-18
	 * @return the message {@link String}
	 */
	public String getMessage()
	{
		return strMessage;
	}

	/**
	 * Setter for message
	 * @author 62000465 2019-10-18
	 * @param message the message to set
	 */
	public void setMessage( String message )
	{
		strMessage = message;
	}

	/**
	 * Getter for messageDetail
	 * 
	 * @author 62000465 2019-10-18
	 * @return the messageDetail {@link String}
	 */
	public String getMessageDetail()
	{
		return strMessageDetail;
	}

	/**
	 * Setter for messageDetail
	 * 
	 * @author 62000465 2019-10-18
	 * @param messageDetail the messageDetail to set
	 */
	public void setMessageDetail( String messageDetail )
	{
		strMessageDetail = messageDetail;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SIIEUserLogin [strUsername=" );
		builder.append( strUsername );
		builder.append( ", strAcessToken=" );
		builder.append( strAcessToken );
		builder.append( ", strTokenType=" );
		builder.append( strTokenType );
		builder.append( ", iExpiresIn=" );
		builder.append( iExpiresIn );
		builder.append( ", strMessage=" );
		builder.append( strMessage );
		builder.append( ", strMessageDetail=" );
		builder.append( strMessageDetail );
		builder.append( "]" );
		return builder.toString();
	}

}
