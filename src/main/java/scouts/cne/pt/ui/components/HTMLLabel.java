package scouts.cne.pt.ui.components;

import com.vaadin.flow.component.html.Label;

/**
 * @author 62000465 2019-04-24
 *
 */
public class HTMLLabel extends Label
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6088932333111231966L;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-04-24
	 * @param text
	 */
	public HTMLLabel( String text )
	{
		super();
		this.setText( text );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.flow.component.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText( String text )
	{
		getElement().setProperty( "innerHTML", text );
	}
}
