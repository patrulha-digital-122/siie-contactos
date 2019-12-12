package scouts.cne.pt.ui.events.internal;
import scouts.cne.pt.ui.events.InternalEvent;
public class InternalStorageEventReady extends InternalEvent
{
	String strDescription = "Storage Service Ready";

	@Override
	public String getDescription()
	{
		return strDescription;
	}

	/**
	 * Setter for description
	 * 
	 * @author 62000465 2019-12-05
	 * @param description the description to set
	 */
	public void setDescription( String description )
	{
		strDescription = description;
	}
}
