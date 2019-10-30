package scouts.cne.pt.model.siie.types;

import com.vaadin.flow.component.html.Label;
import scouts.cne.pt.ui.util.FontSize;
import scouts.cne.pt.ui.util.TextColor;
import scouts.cne.pt.utils.UIUtils;

public enum SIIESeccao
{
	D( "Dirigentes", TextColor.DIRIGENTES ),
	L( "Lobitos", TextColor.LOBITOS ),
	E( "Exploradores / Moços", TextColor.EXPLORADORES ),
	P( "Pioneiros / Marinheiros", TextColor.PIONEIROS ),
	C( "Caminheiros / Companheiros", TextColor.CAMINHERIOS ),
	A( "Sem secção atribuida", TextColor.DIRIGENTES );
	private String nome;
	private TextColor color;

	private SIIESeccao( String nome, TextColor color )
	{
		this.nome = nome;
		this.color = color;
	}

	/**
	 * @return the nome
	 */
	public String getNome()
	{
		return nome;
	}
	
	/**
	 * Getter for color
	 * 
	 * @author 62000465 2019-10-30
	 * @return the color {@link TextColor}
	 */
	public TextColor getColor()
	{
		return color;
	}

	/**
	 * Setter for color
	 * 
	 * @author 62000465 2019-10-30
	 * @param color the color to set
	 */
	public void setColor( TextColor color )
	{
		this.color = color;
	}

	public Label getLable() {
		return UIUtils.createLabel( FontSize.M, getColor(), getNome() );
	}
}
