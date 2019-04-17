package scouts.cne.pt.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import scouts.cne.pt.app.HasLogger;

/**
 * @author anco62000465 2018-05-10
 *
 */
@UIScope
public class MasterHorizontalView extends HorizontalLayout implements HasLogger
{
	private static final long serialVersionUID = 3289601553743397951L;

	public MasterHorizontalView()
	{
		setAlignItems( Alignment.CENTER );
		setJustifyContentMode( JustifyContentMode.CENTER );

		setMargin( false );
		setPadding( false );
		setSizeFull();
		// getElement().getStyle().set( "overflow", "auto" );
	}

	protected Div createDiv( Component component, String width )
	{
		final Div div = new Div( component );
		div.setWidth( width );
		div.setHeight( "100%" );

		return div;
	}
}
