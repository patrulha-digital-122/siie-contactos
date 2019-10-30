package scouts.cne.pt.model.google;

import java.math.BigInteger;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Custom event to capture (internally) google sign in events
 */
@JsonPropertyOrder( "Zi" )
@JsonIgnoreProperties( ignoreUnknown = true )
public class GoogleAuthAccessInfo
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4582145244046915522L;
	private String				access_token;
	private String				login_hint;
	private String				scope;
	private String				id_token;
	private String				token_type;
	private Integer				expires_in;
	private BigInteger			expires_at;

	public String getAccess_token()
	{
		return access_token;
	}

	public void setAccess_token( String access_token )
	{
		this.access_token = access_token;
	}

	public String getLogin_hint()
	{
		return login_hint;
	}

	public void setLogin_hint( String login_hint )
	{
		this.login_hint = login_hint;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope( String scope )
	{
		this.scope = scope;
	}

	public String getId_token()
	{
		return id_token;
	}

	public void setId_token( String id_token )
	{
		this.id_token = id_token;
	}

	public String getToken_type()
	{
		return token_type;
	}

	public void setToken_type( String token_type )
	{
		this.token_type = token_type;
	}

	/**
	 * Getter for expires_in
	 * 
	 * @author 62000465 2019-10-28
	 * @return the expires_in {@link Integer}
	 */
	public Integer getExpires_in()
	{
		return expires_in;
	}

	/**
	 * Setter for expires_in
	 * 
	 * @author 62000465 2019-10-28
	 * @param expires_in the expires_in to set
	 */
	public void setExpires_in( Integer expires_in )
	{
		this.expires_in = expires_in;
	}

	/**
	 * Getter for expires_at
	 * 
	 * @author 62000465 2019-10-28
	 * @return the expires_at {@link Date}
	 */
	public BigInteger getExpires_at()
	{
		return expires_at;
	}

	public Date getExpiresAt()
	{
		return new Date( expires_at.longValue() );
	}

	/**
	 * Setter for expires_at
	 * 
	 * @author 62000465 2019-10-28
	 * @param expires_at the expires_at to set
	 */
	public void setExpires_at( BigInteger expires_at )
	{
		this.expires_at = expires_at;
	}
}
