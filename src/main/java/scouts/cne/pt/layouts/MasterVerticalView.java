package scouts.cne.pt.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import scouts.cne.pt.app.HasLogger;

/**
 * @author anco62000465 2018-05-10
 *
 */
public class MasterVerticalView extends VerticalLayout implements HasLogger
{
	private static final long serialVersionUID = -3240012974722197366L;

	public MasterVerticalView()
	{
		setAlignItems( Alignment.CENTER );
		setJustifyContentMode( JustifyContentMode.CENTER );

		setMargin( false );
		setSizeFull();
	}

	public Div createDiv( Component component, String height )
	{
		final Div div = new Div( component );
		div.setWidth( "100%" );
		div.setHeight( height );
		// div.getStyle().set( "overflow", "auto" );

		return div;
	}

	public Div generateDiv()
	{
		final Div div = new Div();
		div.setWidth( "100%" );
		// div.getStyle().set( "overflow", "auto" );

		return div;
	}
}
