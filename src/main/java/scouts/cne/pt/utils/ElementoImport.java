package scouts.cne.pt.utils;

import com.google.gdata.data.contacts.ContactEntry;
import j2html.TagCreator;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.ImportContactReport;

/**
 * @author anco62000465 2018-09-19
 *
 */
public class ElementoImport
{
	private final ContactEntry			contactEntry;
	private final Elemento				elemento;
	private final ImportContactReport	importContactReport;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-09-19
	 * @param contactEntry
	 * @param elemento
	 */
	public ElementoImport( ContactEntry contactEntry, Elemento elemento, ImportContactReport importContactReport )
	{
		super();
		this.contactEntry = contactEntry;
		this.elemento = elemento;
		this.importContactReport = importContactReport;
	}

	/**
	 * Getter for contactEntry
	 *
	 * @author anco62000465 2018-09-19
	 * @return the contactEntry {@link ContactEntry}
	 */
	public ContactEntry getContactEntry()
	{
		return contactEntry;
	}

	/**
	 * Getter for elemento
	 *
	 * @author anco62000465 2018-09-19
	 * @return the elemento {@link Elemento}
	 */
	public Elemento getElemento()
	{
		return elemento;
	}

	/**
	 * Getter for importContactReport
	 *
	 * @author anco62000465 2018-09-19
	 * @return the importContactReport {@link ImportContactReport}
	 */
	public ImportContactReport getImportContactReport()
	{
		return importContactReport;
	}

	/**
	 * The <b>getHTMLImportContactReport</b> method returns {@link String}
	 *
	 * @author anco62000465 2018-09-19
	 * @return
	 */
	public String getHTMLImportContactReport()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( getHTMLGoogleContactLink() );
		getImportContactReport().getLstLabels().forEach( l -> sb.append( l ) );
		return sb.toString();
	}

	public String getHTMLGoogleContactLink()
	{
		String id = getContactEntry().getId();
		String[] split = id.split( "base/" );
		StringBuilder sb = new StringBuilder();
		if ( split.length > 1 )
		{
			sb.append( TagCreator
							.p( TagCreator.join(	TagCreator.text( "Link para Google Contacts: " ),
													TagCreator.a( "versão antiga" )
																	.withHref( String.format(	"https://www.google.com/contacts/?cplus=0#contact/%s",
																								split[ 1 ] ) )
																	.withTarget( "_blank" ),
													TagCreator.text( " | " ),
													TagCreator.a( "versão nova" )
																	.withHref( String.format( "https://contacts.google.com/contact/%s", split[ 1 ] ) )
																	.withTarget( "_blank" ) ) )
							.render() );
		}
		return sb.toString();
	}
}
