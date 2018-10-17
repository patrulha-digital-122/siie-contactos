package scouts.cne.pt;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route( "test" )
@PageTitle( "RootComponent" )
public class RootComponent extends VerticalLayout
{

	public RootComponent()
	{
		setSizeFull();
		final Label c = new Label( "test" );
		add( c );
	}

}
