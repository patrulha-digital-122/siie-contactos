package scouts.cne.pt.model.google;

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
	private Date				expires_in;

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

	public Date getExpires_in()
	{
		return expires_in;
	}

	public void setExpires_in( Date expires_in )
	{
		this.expires_in = expires_in;
	}
}
