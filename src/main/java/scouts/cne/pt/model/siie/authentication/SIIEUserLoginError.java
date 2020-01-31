package scouts.cne.pt.model.siie.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 62000465 2019-02-06
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class SIIEUserLoginError
{
	@JsonProperty( value = "Message" )
	private String	strMessage;
	@JsonProperty( value = "MessageDetail" )
	private String	strMessageDetail;

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
	 * 
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
		builder.append( "SIIEUserLoginError [strMessage=" );
		builder.append( strMessage );
		builder.append( ", strMessageDetail=" );
		builder.append( strMessageDetail );
		builder.append( "]" );
		return builder.toString();
	}
}
