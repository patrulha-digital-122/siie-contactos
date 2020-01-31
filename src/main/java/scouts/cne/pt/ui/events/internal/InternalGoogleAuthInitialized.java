package scouts.cne.pt.ui.events.internal;


import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import scouts.cne.pt.ui.components.GoogleSignin;

/**
 * Custom event to capture (internally) google sign in events
 */
@DomEvent( "initialized-changed" )
public class InternalGoogleAuthInitialized extends ComponentEvent<GoogleSignin> {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4582145244046915522L;
	private final String		data;

	public InternalGoogleAuthInitialized( GoogleSignin source, boolean fromClient, @EventData( "event.data" ) String data )
	{
		super( source, fromClient );
		System.out.println( "initialized-changed" );
		this.data = data;
	}

	/**
	 * Getter for data
	 * 
	 * @author 62000465 2019-10-25
	 * @return the data {@link String}
	 */
	public String getData()
	{
		return data;
	}

}
