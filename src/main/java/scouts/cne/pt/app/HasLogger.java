package scouts.cne.pt.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 *
 */
/**
 * HasLogger is a feature interface that provides Logging capability for anyone implementing it where logger needs to
 * operate in serializable environment without being static.
 * 
 * @author anco62000465 2018-04-19
 */
public interface HasLogger
{
	default Logger getLogger()
	{
		return LoggerFactory.getLogger( getClass() );
	}

	default void showError( Exception e )
	{
		getLogger().error( e.getMessage(), e );
		Notification.show( "Erro", e.getMessage(), Type.ERROR_MESSAGE );
	}

	default void showError( String e )
	{
		getLogger().error( e );
		Notification.show( "Erro", e, Type.ERROR_MESSAGE );
	}

	default void showInfo( String e )
	{
		Notification.show( "Info", e, Type.HUMANIZED_MESSAGE );
	}

	default void showWarning( String e )
	{
		Notification.show( "Warning", e, Type.WARNING_MESSAGE );
	}
}
