package scouts.cne.pt.model.siie.types;

import scouts.cne.pt.ui.components.Badge;
import scouts.cne.pt.ui.util.TextColor;

/**
 * @author 62000465 2019-10-04
 *
 */
public enum SIIESituacao
{
	A( "Activo", TextColor.SUCCESS, TextColor.WHITE ),
	E( "Exonerado (disciplina)", TextColor.SECONDARY, TextColor.WHITE ),
	F( "Falecido", TextColor.SECONDARY, TextColor.WHITE ),
	I( "Inactivo", TextColor.ERROR, TextColor.WHITE ),
	S( "Suspenso (administrativo)", TextColor.SECONDARY, TextColor.WHITE ),
	T( "Transferido", TextColor.SECONDARY, TextColor.WHITE ),
	V( "Ativo (Formação)", TextColor.SUCCESS, TextColor.WHITE ),;
	private final String	nome;
	private final TextColor	textColor;
	private final TextColor	backgroundColor;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-31
	 * @param name
	 * @param textColor
	 * @param backgroundColor
	 */
	private SIIESituacao( String name, TextColor textColor, TextColor backgroundColor )
	{
		this.nome = name;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Getter for nome
	 * 
	 * @author 62000465 2019-10-31
	 * @return the nome {@link String}
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
