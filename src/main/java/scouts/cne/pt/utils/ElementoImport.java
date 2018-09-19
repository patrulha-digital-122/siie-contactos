package scouts.cne.pt.utils;

import com.google.gdata.data.contacts.ContactEntry;
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
		// sb.append( String.format( "<p><strong>Google contacts:</strong> <a href=\"%s\"
		// target=\"_blank\">LINK</a></p>",
		// getContactEntry().getSelfLink().getHref() ) );
		getImportContactReport().getLstLabels().forEach( l -> sb.append( l ) );
		return sb.toString();
	}
}
