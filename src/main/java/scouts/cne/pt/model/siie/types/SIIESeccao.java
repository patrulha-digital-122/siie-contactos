package scouts.cne.pt.model.siie.types;

import scouts.cne.pt.ui.components.Badge;
import scouts.cne.pt.ui.util.TextColor;

public enum SIIESeccao
{
	D( "Dirigentes", TextColor.DIRIGENTES, TextColor.WHITE ),
	L( "Lobitos", TextColor.LOBITOS, TextColor.WHITE ),
	E( "Exploradores / Moços", TextColor.EXPLORADORES, TextColor.WHITE ),
	P( "Pioneiros / Marinheiros", TextColor.PIONEIROS, TextColor.WHITE ),
	C( "Caminheiros / Companheiros", TextColor.CAMINHERIOS, TextColor.WHITE ),
	A( "Sem secção atribuida", TextColor.DIRIGENTES, TextColor.WHITE );
	private final String	nome;
	private final TextColor	textColor;
	private final TextColor	backgroundColor;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-31
	 * @param nome
	 * @param color
	 * @param backgroundColor
	 */
	private SIIESeccao( String nome, TextColor backgroundColor, TextColor color )
	{
		this.nome = nome;
		this.textColor = color;
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the nome
	 */
	public String getNome()
	{
		return nome;
	}

	

	/**
	 * Getter for textColor
	 * 
	 * @author 62000465 2019-10-31
	 * @return the textColor {@link TextColor}
	 */
	public TextColor getTextColor()
	{
		return textColor;
	}

	/**
	 * Getter for backgroundColor
	 * 
	 * @author 62000465 2019-10-31
	 * @return the backgroundColor {@link TextColor}
	 */
	public TextColor getBackgroundColor()
	{
		return backgroundColor;
	}

	public Badge getLable()
	{
		return new Badge( getNome(), getBackgroundColor(), getTextColor() );
	}
}
