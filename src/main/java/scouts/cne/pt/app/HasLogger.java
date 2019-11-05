package scouts.cne.pt.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

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
		Notification notification = new Notification();
		notification.addThemeVariants( NotificationVariant.LUMO_ERROR );
		notification.setText( e.getMessage() );
		notification.setDuration( 3000 );
		notification.setPosition( Position.MIDDLE );
		notification.open();
	}

	default void showError( String errorMessage )
	{
		getLogger().error( errorMessage );
		
		Notification notification = new Notification();
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		notification.setText( errorMessage );
		notification.setDuration( 3000 );
		notification.setPosition( Position.MIDDLE );

		notification.open();
	}

	default void showInfo( String message )
	{
		getLogger().info( message );
		Notification notification = new Notification();
		notification.setText( message );
		notification.setDuration( 3000 );
		notification.open();
	}

	default void showWarning( String message )
	{
		getLogger().warn( message );
		Notification notification = new Notification();
		notification.addThemeVariants( NotificationVariant.LUMO_PRIMARY );
		notification.setText( message );
		notification.setDuration( 3000 );
		notification.setPosition( Position.MIDDLE );

		notification.open();
	}
}
