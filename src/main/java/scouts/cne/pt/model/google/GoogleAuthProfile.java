package scouts.cne.pt.model.google;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Custom event to capture (internally) google sign in events
 */
@JsonPropertyOrder( "w3" )
@JsonIgnoreProperties( ignoreUnknown = true )
public class GoogleAuthProfile
{
	@JsonProperty( "Bd" )
	private String	nome;
	@JsonProperty( "dL" )
	private String	urlImage;
	@JsonProperty( "Bu" )
	private String	email;

	/**
	 * Getter for nome
	 * 
	 * @author 62000465 2019-10-25
	 * @return the nome {@link String}
	 */
	public String getNome()
	{
		return nome;
	}

	/**
	 * Setter for nome
	 * 
	 * @author 62000465 2019-10-25
	 * @param nome the nome to set
	 */
	public void setNome( String nome )
	{
		this.nome = nome;
	}

	/**
	 * Getter for urlImage
	 * 
	 * @author 62000465 2019-10-25
	 * @return the urlImage {@link String}
	 */
	public String getUrlImage()
	{
		return urlImage;
	}

	/**
	 * Setter for urlImage
	 * 
	 * @author 62000465 2019-10-25
	 * @param urlImage the urlImage to set
	 */
	public void setUrlImage( String urlImage )
	{
		this.urlImage = urlImage;
	}

	/**
	 * Getter for email
	 * 
	 * @author 62000465 2019-10-25
	 * @return the email {@link String}
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * Setter for email
	 * 
	 * @author 62000465 2019-10-25
	 * @param email the email to set
	 */
	public void setEmail( String email )
	{
		this.email = email;
	}
}
