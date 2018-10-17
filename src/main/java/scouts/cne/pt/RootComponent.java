package scouts.cne.pt;

import java.util.Locale;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

@Route( "" )
public class RootComponent extends Div
{
	private static final long serialVersionUID = 7587583974909956721L;
	// private final RouterLink link;

	public RootComponent()
	{
		final Label greeting = new Label( "Hello" );
		final Style grretingStyle = greeting.getElement().getStyle();
		grretingStyle.set( "display", "block" );
		grretingStyle.set( "margin-bottom", "10px" );

		final Button button = new Button( "Switch language to Chinese", event -> getUI().get().setLocale( Locale.CHINESE ) );

		// link = new RouterLink( getTranslation( "root.navigate_to_component" ), ViewComponent.class );

		// final Style linkStyle = link.getElement().getStyle();
		// linkStyle.set( "display", "block" );
		// linkStyle.set( "margin-bottom", "10px" );

		add( greeting, button );

	}
}
