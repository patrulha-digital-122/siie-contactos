package scouts.cne.pt.controllers;

import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.UI;

/**
 * @author anco62000465 2018-01-23
 *
 */
// @Route( value = "/Callback" )
@UIScope
public class GoogleCodeController extends UI
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7860796915342581862L;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init( VaadinRequest request )
	{
		request.getParameter( "code" );
		request.getParameter( "state" );

		// final MyUI uiByEmbedId = ( MyUI ) VaadinSession.getCurrent().getUIById( Integer.parseInt( embedId ) );
		// if ( uiByEmbedId != null )
		// {
		// uiByEmbedId.access( () -> uiByEmbedId.receiveGoogleCode( code, embedId ) );
		// }
		// Close the popup
		JavaScript.eval( "close()" );
		// Detach the UI from the session
		getUI().close();
	}
}
