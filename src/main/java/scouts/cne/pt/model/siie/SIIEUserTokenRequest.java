package scouts.cne.pt.model.siie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author 62000465 2019-02-06
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class SIIEUserTokenRequest
{
	private String			username;
	private String			password;
	private final String	grant_type	= "password";
	
	/**
	 * Getter for grant_type
	 * 
	 * @author 62000465 2019-02-06
	 * @return the grant_type {@link String}
	 */
	public String getGrant_type()
	{
		return grant_type;
	}

	/**
	 * Getter for username
	 * 
	 * @author 62000465 2019-02-06
	 * @return the username {@link String}
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * Setter for username
	 * 
	 * @author 62000465 2019-02-06
	 * @param username the username to set
	 */
	public void setUsername( String username )
	{
		this.username = username;
	}
	
	/**
	 * Getter for password
	 * 
	 * @author 62000465 2019-02-06
	 * @return the password {@link String}
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * Setter for password
	 * 
	 * @author 62000465 2019-02-06
	 * @param password the password to set
	 */
	public void setPassword( String password )
	{
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "SIIEUserTokenRequest [username=" + username + ", password=" + password + ", grant_type=" + grant_type + "]";
	}
}
