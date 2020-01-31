package scouts.cne.pt.model.siie.types;

import com.vaadin.flow.component.icon.VaadinIcon;

public enum SIIESexo
{
	M( "Masculino", "male", VaadinIcon.MALE ),
	F( "Feminino", "female", VaadinIcon.FEMALE );

	private final String		nome;
	private final String		googleValue;
	private final VaadinIcon	icon;


	/**
	 * constructor
	 * 
	 * @author 62000465 2019-11-05
	 * @param nome
	 * @param googleValue
	 * @param icon
	 */
	private SIIESexo( String nome, String googleValue, VaadinIcon icon )
	{
		this.nome = nome;
		this.googleValue = googleValue;
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

	/**
	 * Getter for googleValue
	 * 
	 * @author 62000465 2019-11-05
	 * @return the googleValue {@link String}
	 */
	public String getGoogleValue()
	{
		return googleValue;
	}
}
