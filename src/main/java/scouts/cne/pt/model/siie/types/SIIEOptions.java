package scouts.cne.pt.model.siie.types;

/**
 * @author 62000465 2019-10-04
 *
 */
public enum SIIEOptions
{
	DADOS_COMPLETOS( "Dados Completos", "Dados completos", "https://siie.escutismo.pt/elementos/list?xml=elementos/elementos/dados-completos" ),
	DADOS_SAUDE( "Dados Saúde", "Dados Saúde", "https://siie.escutismo.pt/elementos/List?xml=elementos/dadossaude/dados-saude" ),
	NOITES_CAMPO( "Noites de campo", "Dados completos", "https://siie.escutismo.pt/elementos/List?xml=elementos/actividades/noites-campo" );
	private final String	name;
	private final String	descricao;
	private final String	url;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-04
	 * @param name
	 * @param descricao
	 * @param url
	 */
	private SIIEOptions( String name, String descricao, String url )
	{
		this.name = name;
		this.descricao = descricao;
		this.url = url;
	}

	/**
	 * Getter for name
	 * 
	 * @author 62000465 2019-10-04
	 * @return the name {@link String}
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Getter for descricao
	 * 
	 * @author 62000465 2019-10-04
	 * @return the descricao {@link String}
	 */
	public String getDescricao()
	{
		return descricao;
	}

	/**
	 * Getter for url
	 * 
	 * @author 62000465 2019-10-04
	 * @return the url {@link String}
	 */
	public String getUrl()
	{
		return url;
	}
}
