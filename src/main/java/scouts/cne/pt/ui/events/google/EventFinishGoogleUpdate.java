package scouts.cne.pt.ui.events.google;

import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import scouts.cne.pt.ui.events.InternalEvent;

/**
 * @author 62000465 2019-11-22
 *
 */
public class EventFinishGoogleUpdate extends InternalEvent
{
	private String				message;
	private ProgressBarVariant	proBarVariant;

	/**
	 * Getter for message
	 * 
	 * @author 62000465 2019-11-22
	 * @return the message {@link String}
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Setter for message
	 * 
	 * @author 62000465 2019-11-22
	 * @param message the message to set
	 */
	public void setMessage( String message )
	{
		this.message = message;
	}

	/**
	 * Getter for barVariant
	 * 
	 * @author 62000465 2019-11-22
	 * @return the barVariant {@link ProgressBarVariant}
	 */
	public ProgressBarVariant getBarVariant()
	{
		return proBarVariant;
	}

	/**
	 * Setter for barVariant
	 * 
	 * @author 62000465 2019-11-22
	 * @param barVariant the barVariant to set
	 */
	public void setBarVariant( ProgressBarVariant barVariant )
	{
		proBarVariant = barVariant;
	}

	@Override
	public String getDescription()
	{
		return "Finilizado update de dados do SIIE na conta do google";
	}
}
