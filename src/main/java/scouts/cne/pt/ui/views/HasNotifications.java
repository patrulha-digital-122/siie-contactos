package scouts.cne.pt.ui.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Interface for views showing notifications to users
 *
 */
public interface HasNotifications extends HasElement
{
	default void showNotification( String message, boolean useCloseButton )
	{
		Dialog dialog = new Dialog();
		dialog.setCloseOnEsc( !useCloseButton );
		dialog.setCloseOnOutsideClick( !useCloseButton );
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setAlignItems( Alignment.CENTER );
		Label messageLabel = new Label( message );
		Button close = new Button( "Fechar", event -> dialog.close() );
		verticalLayout.add( messageLabel );
		if ( useCloseButton )
		{
			verticalLayout.add( close );
		}
		dialog.add( verticalLayout );
		dialog.open();
		
	}
}
