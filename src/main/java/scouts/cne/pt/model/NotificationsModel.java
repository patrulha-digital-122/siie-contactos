package scouts.cne.pt.model;

import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author 62000465 2019-12-12
 *
 */
public class NotificationsModel
{
	public static final String	JS_FUNCTION	= "navigator.serviceWorker.ready.then(function(registration) { registration.showNotification($0, $1)});";
	// Visual Options
	private String			body;
	private String			icon;
	private String			image;
	private String			badge;
	private List< Integer >	vibrate;
	private String			sound;
	// Behavioral Options
	private String			tag;
	private String			data;
	private Boolean			requireInteraction;
	private Boolean			renotify;
	private Boolean			silent;
	// Both visual & behavioral options
	private List< String >	actions;
	// Information Option. No visual affect.
	private Long			timestamp;

	/**
	 * Getter for body
	 * 
	 * @author 62000465 2019-12-12
	 * @return the body {@link String}
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * Setter for body
	 * 
	 * @author 62000465 2019-12-12
	 * @param body the body to set
	 */
	public void setBody( String body )
	{
		this.body = body;
	}

	/**
	 * Getter for icon
	 * 
	 * @author 62000465 2019-12-12
	 * @return the icon {@link String}
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Setter for icon
	 * 
	 * @author 62000465 2019-12-12
	 * @param icon the icon to set
	 */
	public void setIcon( String icon )
	{
		this.icon = icon;
	}

	/**
	 * Getter for image
	 * 
	 * @author 62000465 2019-12-12
	 * @return the image {@link String}
	 */
	public String getImage()
	{
		return image;
	}

	/**
	 * Setter for image
	 * 
	 * @author 62000465 2019-12-12
	 * @param image the image to set
	 */
	public void setImage( String image )
	{
		this.image = image;
	}

	/**
	 * Getter for badge
	 * 
	 * @author 62000465 2019-12-12
	 * @return the badge {@link String}
	 */
	public String getBadge()
	{
		return badge;
	}

	/**
	 * Setter for badge
	 * 
	 * @author 62000465 2019-12-12
	 * @param badge the badge to set
	 */
	public void setBadge( String badge )
	{
		this.badge = badge;
	}

	/**
	 * Getter for vibrate
	 * 
	 * @author 62000465 2019-12-12
	 * @return the vibrate {@link List<Integer>}
	 */
	public List< Integer > getVibrate()
	{
		return vibrate;
	}

	/**
	 * Setter for vibrate
	 * 
	 * @author 62000465 2019-12-12
	 * @param vibrate the vibrate to set
	 */
	public void setVibrate( List< Integer > vibrate )
	{
		this.vibrate = vibrate;
	}

	/**
	 * Getter for sound
	 * 
	 * @author 62000465 2019-12-12
	 * @return the sound {@link String}
	 */
	public String getSound()
	{
		return sound;
	}

	/**
	 * Setter for sound
	 * 
	 * @author 62000465 2019-12-12
	 * @param sound the sound to set
	 */
	public void setSound( String sound )
	{
		this.sound = sound;
	}

	/**
	 * Getter for tag
	 * 
	 * @author 62000465 2019-12-12
	 * @return the tag {@link String}
	 */
	public String getTag()
	{
		return tag;
	}

	/**
	 * Setter for tag
	 * 
	 * @author 62000465 2019-12-12
	 * @param tag the tag to set
	 */
	public void setTag( String tag )
	{
		this.tag = tag;
	}

	/**
	 * Getter for data
	 * 
	 * @author 62000465 2019-12-12
	 * @return the data {@link String}
	 */
	public String getData()
	{
		return data;
	}

	/**
	 * Setter for data
	 * 
	 * @author 62000465 2019-12-12
	 * @param data the data to set
	 */
	public void setData( String data )
	{
		this.data = data;
	}

	/**
	 * Getter for requireInteraction
	 * 
	 * @author 62000465 2019-12-12
	 * @return the requireInteraction {@link Boolean}
	 */
	public Boolean getRequireInteraction()
	{
		return requireInteraction;
	}

	/**
	 * Setter for requireInteraction
	 * 
	 * @author 62000465 2019-12-12
	 * @param requireInteraction the requireInteraction to set
	 */
	public void setRequireInteraction( Boolean requireInteraction )
	{
		this.requireInteraction = requireInteraction;
	}

	/**
	 * Getter for renotify
	 * 
	 * @author 62000465 2019-12-12
	 * @return the renotify {@link Boolean}
	 */
	public Boolean getRenotify()
	{
		return renotify;
	}

	/**
	 * Setter for renotify
	 * 
	 * @author 62000465 2019-12-12
	 * @param renotify the renotify to set
	 */
	public void setRenotify( Boolean renotify )
	{
		this.renotify = renotify;
	}

	/**
	 * Getter for silent
	 * 
	 * @author 62000465 2019-12-12
	 * @return the silent {@link Boolean}
	 */
	public Boolean getSilent()
	{
		return silent;
	}

	/**
	 * Setter for silent
	 * 
	 * @author 62000465 2019-12-12
	 * @param silent the silent to set
	 */
	public void setSilent( Boolean silent )
	{
		this.silent = silent;
	}

	/**
	 * Getter for actions
	 * 
	 * @author 62000465 2019-12-12
	 * @return the actions {@link List<String>}
	 */
	public List< String > getActions()
	{
		return actions;
	}

	/**
	 * Setter for actions
	 * 
	 * @author 62000465 2019-12-12
	 * @param actions the actions to set
	 */
	public void setActions( List< String > actions )
	{
		this.actions = actions;
	}

	/**
	 * Getter for timestamp
	 * 
	 * @author 62000465 2019-12-12
	 * @return the timestamp {@link Long}
	 */
	public Long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * Setter for timestamp
	 * 
	 * @author 62000465 2019-12-12
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp( Long timestamp )
	{
		this.timestamp = timestamp;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "NotificationsModel [" );
		if ( body != null )
		{
			builder.append( "body=" );
			builder.append( body );
			builder.append( ", " );
		}
		if ( icon != null )
		{
			builder.append( "icon=" );
			builder.append( icon );
			builder.append( ", " );
		}
		if ( image != null )
		{
			builder.append( "image=" );
			builder.append( image );
			builder.append( ", " );
		}
		if ( badge != null )
		{
			builder.append( "badge=" );
			builder.append( badge );
			builder.append( ", " );
		}
		if ( vibrate != null )
		{
			builder.append( "vibrate=" );
			builder.append( vibrate );
			builder.append( ", " );
		}
		if ( sound != null )
		{
			builder.append( "sound=" );
			builder.append( sound );
			builder.append( ", " );
		}
		if ( tag != null )
		{
			builder.append( "tag=" );
			builder.append( tag );
			builder.append( ", " );
		}
		if ( data != null )
		{
			builder.append( "data=" );
			builder.append( data );
			builder.append( ", " );
		}
		if ( requireInteraction != null )
		{
			builder.append( "requireInteraction=" );
			builder.append( requireInteraction );
			builder.append( ", " );
		}
		if ( renotify != null )
		{
			builder.append( "renotify=" );
			builder.append( renotify );
			builder.append( ", " );
		}
		if ( silent != null )
		{
			builder.append( "silent=" );
			builder.append( silent );
			builder.append( ", " );
		}
		if ( actions != null )
		{
			builder.append( "actions=" );
			builder.append( actions );
			builder.append( ", " );
		}
		if ( timestamp != null )
		{
			builder.append( "timestamp=" );
			builder.append( timestamp );
		}
		builder.append( "]" );
		return builder.toString();
	}

	public String toJson()
	{
		try
		{
			return new ObjectMapper().writeValueAsString( this );
		}
		catch ( JsonProcessingException e )
		{
			e.printStackTrace();
		}
		return "";
	}
}
