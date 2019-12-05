package scouts.cne.pt.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
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
	static Map< UUID, LinkedList< Consumer< InternalEvent > > >	sessionListeners	= new LinkedHashMap<>();

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

	public static synchronized Registration register( UUID uuid, Consumer< InternalEvent > listener )
	{
		LinkedList< Consumer< InternalEvent > > consumer = sessionListeners.get( uuid );
		if ( consumer == null )
		{
			consumer = new LinkedList<>();
			sessionListeners.put( uuid, consumer );
		}
		consumer.add( listener );
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

	public static synchronized void broadcast( UUID uuid, InternalEvent event )
	{
		LinkedList< Consumer< InternalEvent > > consumers = sessionListeners.get( uuid );
		if ( consumers != null )
		{
			event.finish();
			for ( Consumer< InternalEvent > listener : consumers )
			{
				executor.execute( () -> listener.accept( event ) );
			}
		}
	}
}
