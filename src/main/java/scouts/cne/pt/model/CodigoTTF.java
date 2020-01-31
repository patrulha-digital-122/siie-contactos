package scouts.cne.pt.model;

/**
 * @author 62000465 2019-02-01
 *
 */
public class CodigoTTF
{
	private final String	strName;
	private final String	strCSSUrl;
	private final String	strLink;


	/**
	 * constructor
	 * 
	 * @author 62000465 2019-02-01
	 * @param name
	 * @param url
	 * @param link
	 */
	public CodigoTTF( String name, String CSSUrl, String link )
	{
		super();
		strName = name;
		strCSSUrl = CSSUrl;
		strLink = link;
	}

	/**
	 * Getter for name
	 * 
	 * @author 62000465 2019-02-01
	 * @return the name {@link String}
	 */
	public String getName()
	{
		return strName;
	}

	/**
	 * Getter for url
	 * 
	 * @author 62000465 2019-02-01
	 * @return the url {@link String}
	 */
	public String getCSSUrl()
	{
		return strCSSUrl;
	}

	/**
	 * Getter for link
	 * 
	 * @author 62000465 2019-02-01
	 * @return the link {@link String}
	 */
	public String getLink()
	{
		return strLink;
	}
}
