package scouts.cne.pt.model.siie.types;

import com.vaadin.flow.component.icon.VaadinIcon;

public enum SIIESexo
{
	M( "Masculino", VaadinIcon.MALE ),
	F( "Feminino", VaadinIcon.FEMALE );

	private final String		nome;
	private final VaadinIcon	icon;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-11
	 * @param nome
	 * @param icon
	 */
	private SIIESexo( String nome, VaadinIcon icon )
	{
		this.nome = nome;
		this.icon = icon;
	}

	/**
	 * Getter for nome
	 * 
	 * @author 62000465 2019-10-11
	 * @return the nome {@link String}
	 */
	public String getNome()
	{
		return nome;
	}

	/**
	 * Getter for icon
	 * 
	 * @author 62000465 2019-10-11
	 * @return the icon {@link VaadinIcon}
	 */
	public VaadinIcon getIcon()
	{
		return icon;
	}
}
