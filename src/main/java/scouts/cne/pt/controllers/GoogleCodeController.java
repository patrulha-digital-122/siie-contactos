package scouts.cne.pt.controllers;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.UI;
import scouts.cne.pt.MyUI;

/**
 * @author anco62000465 2018-01-23
 *
 */
@SpringUI( path = "/Callback" )
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
		
		MyUI uiByEmbedId = ( MyUI ) VaadinSession.getCurrent().getUIByEmbedId( embedId );
		if ( uiByEmbedId != null )
		{
			uiByEmbedId.access( new Runnable()
			{
				@Override
				public void run()
				{
					uiByEmbedId.receiveGoogleCode( code );
				}
			} );
		}
		// Close the popup
		JavaScript.eval( "close()" );
		// Detach the UI from the session
		getUI().close();
	}
}
