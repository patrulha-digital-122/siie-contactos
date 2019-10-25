package scouts.cne.pt.model.google;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Custom event to capture (internally) google sign in events
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class GoogleAuthInfo
{
	@JsonProperty( "w3" )
	private GoogleAuthProfile	googleProfile;
	@JsonProperty( "Zi" )
	private GoogleAuthAccessInfo	googleAcessInfo;
	
	/**
	 * Getter for googleProfile
	 * 
	 * @author 62000465 2019-10-25
	 * @return the googleProfile {@link GoogleAuthProfile}
	 */
	public GoogleAuthProfile getGoogleProfile()
	{
		return googleProfile;
	}

	/**
	 * Setter for googleProfile
	 * 
	 * @author 62000465 2019-10-25
	 * @param googleProfile the googleProfile to set
	 */
	public void setGoogleProfile( GoogleAuthProfile googleProfile )
	{
		this.googleProfile = googleProfile;
	}

	/**
	 * Getter for googleAcessInfo
	 * 
	 * @author 62000465 2019-10-25
	 * @return the googleAcessInfo {@link GoogleAuthAccessInfo}
	 */
	public GoogleAuthAccessInfo getGoogleAcessInfo()
	{
		return googleAcessInfo;
	}

	/**
	 * Setter for googleAcessInfo
	 * 
	 * @author 62000465 2019-10-25
	 * @param googleAcessInfo the googleAcessInfo to set
	 */
	public void setGoogleAcessInfo( GoogleAuthAccessInfo googleAcessInfo )
	{
		this.googleAcessInfo = googleAcessInfo;
	}



}
