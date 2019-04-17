package scouts.cne.pt.component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import scouts.cne.pt.model.ElementoTags;

/**
 * @author anco62000465 2018-09-27
 *
 */
public class ImportContactsConfigWindow extends Dialog
{
	private static final long serialVersionUID = -5129303155232606228L;
	private final VerticalLayout			mainLayout;
	private final Map< ElementoTags, Checkbox >	mapProperties;
	private Boolean								isCancel			= Boolean.TRUE;
	/**
	 * constructor
	 * @author anco62000465 2018-09-27
	 * @param caption
	 */
	public ImportContactsConfigWindow(  )
	{
		super();
		setAriaLabel( "\"Configurações das propriedades a importar\"" );

		setHeight( "400px" );
		setWidth( "1000px" );
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setDefaultHorizontalComponentAlignment( Alignment.START );

		mapProperties = new EnumMap<>( ElementoTags.class );

		Button btnSelectAll = new Button( "Selecionar todos", VaadinIcon.CHECK_SQUARE_O.create() );
		btnSelectAll.addClickListener( event ->
		{
			for ( Entry< ElementoTags, Checkbox > entry : mapProperties.entrySet() )
			{
				entry.getValue().setValue( true );
			}
		} );
		Button btnUnselectAll = new Button( "Retirar todos", VaadinIcon.SQUARE_SHADOW.create() );
		btnUnselectAll.addClickListener( event ->
		{
			for ( Entry< ElementoTags, Checkbox > entry : mapProperties.entrySet() )
			{
				entry.getValue().setValue( false );
			}
		} );
		HorizontalLayout horizontalSelectAllLayout = new HorizontalLayout();
		horizontalSelectAllLayout.setSizeFull();
		horizontalSelectAllLayout.setDefaultVerticalComponentAlignment( Alignment.CENTER );
		horizontalSelectAllLayout.add( btnSelectAll, btnUnselectAll );
		mainLayout.add( horizontalSelectAllLayout );
		for ( ElementoTags component : ElementoTags.getGoogleImportTags() )
		{
			Checkbox checkBox = new Checkbox( component.getTagDescription(), true );

			mapProperties.put( component, checkBox );
			mainLayout.add( checkBox );
		}
		Button btnOk = new Button( "Iniciar", VaadinIcon.OPEN_BOOK.create() );
		btnOk.setDisableOnClick(true);
		btnOk.addClickListener( new ComponentEventListener< ClickEvent< Button > >()
		{
			private static final long serialVersionUID = -2108089736744022194L;

			@Override
			public void onComponentEvent( ClickEvent< Button > event )
			{
				isCancel			= Boolean.FALSE;
				close();
			}
		} );
		Button btnCancel = new Button( "Cancelar", VaadinIcon.CLOSE.create() );
		btnCancel.addClickListener( new ComponentEventListener< ClickEvent< Button > >()
		{
			private static final long serialVersionUID = -8294575140053591855L;

			@Override
			public void onComponentEvent( ClickEvent< Button > event )
			{
				isCancel			= Boolean.TRUE;
				close();
			}
		} );

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		horizontalLayout.setDefaultVerticalComponentAlignment( Alignment.CENTER );
		horizontalLayout.add( btnCancel, btnOk );

		mainLayout.add( horizontalLayout );
		add( mainLayout );
	}

	/**
	 * Getter for mapProperties
	 *
	 * @author anco62000465 2018-09-27
	 * @return the mapProperties {@link Map<ElementoTags,CheckBox>}
	 */
	public Map< ElementoTags, Checkbox > getMapProperties()
	{
		return mapProperties;
	}

	/**
	 * Getter for isCancel
	 *
	 * @author anco62000465 2018-09-27
	 * @return the isCancel {@link Boolean}
	 */
	public Boolean isCancel()
	{
		return isCancel;
	}
}
