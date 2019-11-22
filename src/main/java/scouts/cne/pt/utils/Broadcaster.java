package scouts.cne.pt.utils;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import com.vaadin.flow.shared.Registration;
import scouts.cne.pt.ui.events.InternalEvent;

/**
 * @author 62000465 2019-11-21
 *
 */
public class Broadcaster
{
	static Executor							executor	= Executors.newSingleThreadExecutor();
	static LinkedList< Consumer< InternalEvent > >	listeners	= new LinkedList<>();

	public static synchronized Registration register( Consumer< InternalEvent > listener )
	{
		listeners.add( listener );
		return () ->
		{
			synchronized ( Broadcaster.class )
			{
				listeners.remove( listener );
			}
		};
	}

	public static synchronized void broadcast( InternalEvent event )
	{
		event.finish();
		for ( Consumer< InternalEvent > listener : listeners )
		{
			executor.execute( () -> listener.accept( event ) );
		}
	}
}
