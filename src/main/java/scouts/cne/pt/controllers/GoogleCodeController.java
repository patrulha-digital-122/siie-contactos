package scouts.cne.pt.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.ui.views.ViewFrame;
import scouts.cne.pt.ui.views.elementos.ImportContactsListView;

/**
 * @author anco62000465 2018-01-23
 *
 */
@Route( value = "/Callback" )
public class GoogleCodeController extends ViewFrame implements HasUrlParameter< String >, HasLogger
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7860796915342581862L;
	@Autowired
	private GoogleAuthenticationBean	googleAuthenticationBean;


	@Override
	public void setParameter( BeforeEvent event, @OptionalParameter String parameter )
	{
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map< String, List< String > > parametersMap = queryParameters.getParameters();
		String code = parametersMap.getOrDefault( "code", Arrays.asList( "" ) ).get( 0 );
		String state = parametersMap.getOrDefault( "state", Arrays.asList( "" ) ).get( 0 );
		getLogger().info( "Received code: " + code + " | state: " + state );
		googleAuthenticationBean.addSession( code );
		UI.getCurrent().navigate( ImportContactsListView.class );
	}
}
