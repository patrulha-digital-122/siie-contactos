package scouts.cne.pt.ui.events.google;

import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.ui.events.InternalEvent;

/**
 * @author 62000465 2019-11-22
 *
 */
public class EventUpdateSIIElemento extends InternalEvent
{
	private int				actual	= 0;
	private int				total	= 0;
	private SIIEElemento	siieElemento;
	private boolean			isCreate	= true;

	/**
	 * Getter for actual
	 * 
	 * @author 62000465 2019-11-22
	 * @return the actual {@link int}
	 */
	public int getActual()
	{
		return actual;
	}

	/**
	 * Setter for actual
	 * 
	 * @author 62000465 2019-11-22
	 * @param actual the actual to set
	 */
	public void setActual( int actual )
	{
		this.actual = actual;
	}

	/**
	 * Getter for total
	 * 
	 * @author 62000465 2019-11-22
	 * @return the total {@link int}
	 */
	public int getTotal()
	{
		return total;
	}

	/**
	 * Setter for total
	 * 
	 * @author 62000465 2019-11-22
	 * @param total the total to set
	 */
	public void setTotal( int total )
	{
		this.total = total;
	}

	/**
	 * Getter for isCreate
	 * 
	 * @author 62000465 2019-11-22
	 * @return the isCreate {@link boolean}
	 */
	public boolean isCreate()
	{
		return isCreate;
	}

	/**
	 * Setter for isCreate
	 * 
	 * @author 62000465 2019-11-22
	 * @param isCreate the isCreate to set
	 */
	public void setCreate( boolean isCreate )
	{
		this.isCreate = isCreate;
	}

	/**
	 * Getter for siieElemento
	 * 
	 * @author 62000465 2019-11-22
	 * @return the siieElemento {@link SIIEElemento}
	 */
	public SIIEElemento getSiieElemento()
	{
		return siieElemento;
	}

	/**
	 * Setter for siieElemento
	 * 
	 * @author 62000465 2019-11-22
	 * @param siieElemento the siieElemento to set
	 */
	public void setSiieElemento( SIIEElemento siieElemento )
	{
		this.siieElemento = siieElemento;
	}

	@Override
	public String getDescription()
	{
		return "Update SIIE Elemento";
	}
}
