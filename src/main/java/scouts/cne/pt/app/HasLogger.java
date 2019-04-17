package scouts.cne.pt.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

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

	default void printError( Exception e )
	{
		getLogger().error( e.getMessage(), e );
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
		Notification notification = new Notification( e, Type.HUMANIZED_MESSAGE );
		notification.setDelayMsec( 10 * 1000 );
		notification.setIcon( VaadinIcons.INFO );
		notification.show( Page.getCurrent() );
	}

	default void showWarning( String e )
	{
		Notification.show( "Warning", e, Type.WARNING_MESSAGE );
	}

	default void showTray( String string )
	{
		Notification notification = new Notification( string, Type.TRAY_NOTIFICATION );
		notification.setDelayMsec( 3 * 1000 );
		notification.setIcon( VaadinIcons.HANDS_UP );
		notification.show( Page.getCurrent() );
	}
}
