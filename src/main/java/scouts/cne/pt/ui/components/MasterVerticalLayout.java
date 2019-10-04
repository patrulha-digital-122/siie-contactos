package scouts.cne.pt.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.ui.views.HasNotifications;

/**
 * @author anco62000465 2018-05-10
 *
 */
public class MasterVerticalLayout extends VerticalLayout implements HasLogger, HasNotifications
{
	private static final long serialVersionUID = -3240012974722197366L;

	public MasterVerticalLayout()
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
