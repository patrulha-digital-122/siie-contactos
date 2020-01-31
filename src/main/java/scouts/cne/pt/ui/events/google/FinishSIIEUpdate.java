package scouts.cne.pt.ui.events.google;

import scouts.cne.pt.ui.events.InternalEvent;

/**
 * @author 62000465 2019-11-22
 *
 */
public class FinishSIIEUpdate extends InternalEvent
{
	String strDescription = "Finilizado update de dados do SIIE";

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
