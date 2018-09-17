package scouts.cne.pt.controllers;

import com.vaadin.flow.router.Route;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.UI;

/**
 * @author anco62000465 2018-01-23
 *
 */
@Route( "Callback" )
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
		String code = request.getParameter( "code" );
		String embedId = request.getParameter( "state" );

		// VerticalLayout uiByEmbedId = ( VerticalLayout ) VaadinSession.getCurrent().getUIByEmbedId( embedId );
		// if ( uiByEmbedId != null )
		// {
		// uiByEmbedId.access( new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// uiByEmbedId.receiveGoogleCode( code, embedId );
		// }
		// } );
		// }
		// Close the popup
		JavaScript.eval( "close()" );
		// Detach the UI from the session
		getUI().close();
	}
}
