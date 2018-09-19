package scouts.cne.pt.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author anco62000465 2018-09-19
 *
 */
public class ImportContactReport
{
	private final List< String > lstLabels;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-19
	 */
	public ImportContactReport()
	{
		super();
		lstLabels = new LinkedList<>();
	}

	public void addNewField( String field, String strNewValue )
	{
		lstLabels.add( String.format( "<p> Adicionado %s:<strong>%s</strong></p>", field, strNewValue ) );
	}

	public void addUpdateField( String field, String strOldValue, String strNewValue )
	{
		lstLabels.add( String.format( "<p> Alteardo %s de <strong>%s</strong> para <strong>%s</strong> </p>", field, strNewValue, strOldValue ) );
	}

	/**
	 * Getter for lstLabels
	 * 
	 * @author anco62000465 2018-09-19
	 * @return the lstLabels {@link List<String>}
	 */
	public List< String > getLstLabels()
	{
		return lstLabels;
	}
}
