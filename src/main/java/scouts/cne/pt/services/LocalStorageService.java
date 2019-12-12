package scouts.cne.pt.services;

import java.io.Serializable;
import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.google.GoogleAuthInfo;
import scouts.cne.pt.model.localStorage.AniversariosEventConfigurations;
import scouts.cne.pt.model.localStorage.SIIELocalStorageConfigurations;
import scouts.cne.pt.ui.components.LocalStorage;
import scouts.cne.pt.ui.events.internal.InternalStorageEventReady;
import scouts.cne.pt.utils.Broadcaster;

@Component
@VaadinSessionScope
public class LocalStorageService implements Serializable, HasLogger
{
	/**
	 * 
	 */
	private static final long				serialVersionUID				= 7876456779482647543L;
	private final static String				SIIE_CONFIG						= "siieConfig";
	private final static String				GOOGLE_AUTH						= "google-auth";
	private final static String				ANIVERSARIOS_CONFIG				= "aniversarios-config";
	private LocalStorage					localStorage					= new LocalStorage();
	private SIIELocalStorageConfigurations	siieLocalStorageConfigurations	= new SIIELocalStorageConfigurations();
	private AniversariosEventConfigurations	aniversariosEventConfigurations	= new AniversariosEventConfigurations();
	private GoogleAuthInfo					googleAuthInfo					= new GoogleAuthInfo();

	public LocalStorageService()
	{
		super();
		getLogger().info( "New LocalStorageService :: " + Instant.now() );
	}

	/**
	 * Getter for localStorage
	 * 
	 * @author 62000465 2019-12-12
	 * @return the localStorage {@link LocalStorage}
	 */
	public LocalStorage getLocalStorage()
	{
		return localStorage;
	}

	/**
	 * Setter for localStorage
	 * 
	 * @author 62000465 2019-12-12
	 * @param localStorage the localStorage to set
	 */
	public void setLocalStorage( LocalStorage localStorage )
	{
		this.localStorage = localStorage;
		initLocalStorageListener();
	}

	/**
	 * The <b>initLocalStorageListener</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-12-12
	 */
	private void initLocalStorageListener()
	{
		this.localStorage.addInitListener( ls ->
		{
			InternalStorageEventReady eventReady = new InternalStorageEventReady();
			String strConfig = localStorage.getString( SIIE_CONFIG );
			if ( StringUtils.isNotEmpty( strConfig ) )
			{
				try
				{
					siieLocalStorageConfigurations = new ObjectMapper().readValue( strConfig, SIIELocalStorageConfigurations.class );
				}
				catch ( Exception e )
				{
					printError( e );
				}
			}
			strConfig = localStorage.getString( ANIVERSARIOS_CONFIG );
			if ( StringUtils.isNotEmpty( strConfig ) )
			{
				try
				{
					aniversariosEventConfigurations = new ObjectMapper().readValue( strConfig, AniversariosEventConfigurations.class );
				}
				catch ( Exception e )
				{
					printError( e );
				}
			}
			strConfig = localStorage.getString( GOOGLE_AUTH );
			if ( StringUtils.isNotEmpty( strConfig ) )
			{
				try
				{
					googleAuthInfo = new ObjectMapper().readValue( strConfig, GoogleAuthInfo.class );
				}
				catch ( Exception e )
				{
					printError( e );
				}
			}
			Broadcaster.broadcast( eventReady );
			getLogger().info( "LocalStorageReady :: " + eventReady.getDuration().toString() );
		} );
	}

	/**
	 * The <b>updateValueInLocalStorage</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-12-12
	 * @param siieLocalStorageConfigurations
	 * @throws JsonProcessingException
	 */
	private void updateValueInLocalStorage( String key, Object object )
	{
		try
		{
			localStorage.setValue( key, new ObjectMapper().writeValueAsString( object ) );
		}
		catch ( JsonProcessingException e )
		{
			printError( e );
		}
	}

	/**
	 * Getter for siieLocalStorageConfigurations
	 * 
	 * @author 62000465 2019-12-12
	 * @return the siieLocalStorageConfigurations {@link SIIELocalStorageConfigurations}
	 */
	public SIIELocalStorageConfigurations getSiieLocalStorageConfigurations()
	{
		return siieLocalStorageConfigurations;
	}

	/**
	 * Setter for siieLocalStorageConfigurations
	 * 
	 * @author 62000465 2019-12-12
	 * @param siieLocalStorageConfigurations the siieLocalStorageConfigurations to set
	 */
	public void setSiieLocalStorageConfigurations( SIIELocalStorageConfigurations siieLocalStorageConfigurations )
	{
		this.siieLocalStorageConfigurations = siieLocalStorageConfigurations;
		updateValueInLocalStorage( SIIE_CONFIG, siieLocalStorageConfigurations );
	}

	/**
	 * Getter for aniversariosEventConfigurations
	 * 
	 * @author 62000465 2019-12-12
	 * @return the aniversariosEventConfigurations {@link AniversariosEventConfigurations}
	 */
	public AniversariosEventConfigurations getAniversariosEventConfigurations()
	{
		return aniversariosEventConfigurations;
	}

	/**
	 * Setter for aniversariosEventConfigurations
	 * 
	 * @author 62000465 2019-12-12
	 * @param aniversariosEventConfigurations the aniversariosEventConfigurations to set
	 */
	public void setAniversariosEventConfigurations( AniversariosEventConfigurations aniversariosEventConfigurations )
	{
		this.aniversariosEventConfigurations = aniversariosEventConfigurations;
		updateValueInLocalStorage( ANIVERSARIOS_CONFIG, this.aniversariosEventConfigurations );
	}

	/**
	 * Getter for googleAuthInfo
	 * 
	 * @author 62000465 2019-12-12
	 * @return the googleAuthInfo {@link GoogleAuthInfo}
	 */
	public GoogleAuthInfo getGoogleAuthInfo()
	{
		return googleAuthInfo;
	}

	/**
	 * Setter for googleAuthInfo
	 * 
	 * @author 62000465 2019-12-12
	 * @param googleAuthInfo the googleAuthInfo to set
	 */
	public void setGoogleAuthInfo( GoogleAuthInfo googleAuthInfo )
	{
		this.googleAuthInfo = googleAuthInfo;
		updateValueInLocalStorage( GOOGLE_AUTH, this.googleAuthInfo );
	}
}
