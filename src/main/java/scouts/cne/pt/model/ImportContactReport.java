package scouts.cne.pt.model;

import java.util.LinkedList;
import java.util.List;
import j2html.TagCreator;
import j2html.tags.ContainerTag;

/**
 * @author anco62000465 2018-09-19
 *
 */
public class ImportContactReport
{
	private final List< String >	lstLabels;
	private final String			nin;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-09-19
	 */
	public ImportContactReport( String nin )
	{
		super();
		lstLabels = new LinkedList<>();
		this.nin = nin;
	}

	/**
	 * @return the nin
	 */
	public String getNin()
	{
		return nin;
	}

	public void addNewField( String field, String strNewValue )
	{
		ContainerTag p = TagCreator.p( TagCreator.join(	TagCreator.text( "Adicionado " ),
														TagCreator.text( field ),
														TagCreator.text( ": " ),
														TagCreator.strong( strNewValue ) ) );
		lstLabels.add( p.render() );
	}

	public void addUpdateField( String field, String strOldValue, String strNewValue )
	{
		ContainerTag p = TagCreator.p( TagCreator.join(	TagCreator.text( "Alterado " ),
														TagCreator.text( field ),
														TagCreator.text( " de " ),
														TagCreator.strong( strOldValue ),
														TagCreator.text( " para " ),
														TagCreator.strong( strNewValue ) ) );
		lstLabels.add( p.render() );
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
