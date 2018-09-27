package scouts.cne.pt.component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import scouts.cne.pt.model.ElementoTags;

/**
 * @author anco62000465 2018-09-27
 *
 */
public class ImportContactsConfigWindow extends Window
{
	private static final long serialVersionUID = -5129303155232606228L;
	private final VerticalLayout			mainLayout;
	private final Map< ElementoTags, CheckBox >	mapProperties;
	private Boolean								isCancel			= Boolean.TRUE;
	/**
	 * constructor
	 * @author anco62000465 2018-09-27
	 * @param caption
	 */
	public ImportContactsConfigWindow(  )
	{
		super( "Configurações das propriedades a importar" );

		center();
		setResizable( true );
		setHeight( "400px" );
		setWidth( "1000px" );
		setModal( true );
		setClosable( false );
		mainLayout = new VerticalLayout();
		mainLayout.setMargin( new MarginInfo( true, true, false, true ) );
		mainLayout.setSizeFull();
		mainLayout.setDefaultComponentAlignment( Alignment.MIDDLE_LEFT );

		mapProperties = new EnumMap<>( ElementoTags.class );

		Button btnSelectAll = new Button( "Selecionar todos", VaadinIcons.CHECK_SQUARE_O );
		btnSelectAll.addClickListener( new ClickListener()
		{
			@Override
			public void buttonClick( ClickEvent event )
			{
				for ( Entry< ElementoTags, CheckBox > entry : mapProperties.entrySet() )
				{
					entry.getValue().setValue( true );
				}
			}
		} );
		Button btnUnselectAll = new Button( "Retirar todos", VaadinIcons.SQUARE_SHADOW );
		btnUnselectAll.addClickListener( new ClickListener()
		{
			@Override
			public void buttonClick( ClickEvent event )
			{
				for ( Entry< ElementoTags, CheckBox > entry : mapProperties.entrySet() )
				{
					entry.getValue().setValue( false );
				}
			}
		} );
		HorizontalLayout horizontalSelectAllLayout = new HorizontalLayout();
		horizontalSelectAllLayout.setSizeFull();
		horizontalSelectAllLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		horizontalSelectAllLayout.addComponents( btnSelectAll, btnUnselectAll );
		mainLayout.addComponent( horizontalSelectAllLayout );
		for ( ElementoTags component : ElementoTags.getGoogleImportTags() )
		{
			CheckBox checkBox = new CheckBox( component.getTagDescription(), true );

			mapProperties.put( component, checkBox );
			mainLayout.addComponent( checkBox );
		}
		Button btnOk = new Button( "Iniciar", VaadinIcons.OPEN_BOOK );
		btnOk.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -2108089736744022194L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				isCancel			= Boolean.FALSE;
				close();
			}
		} );
		Button btnCancel = new Button( "Cancelar", VaadinIcons.CLOSE );
		btnCancel.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -8294575140053591855L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				isCancel			= Boolean.TRUE;
				close();
			}
		} );

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		horizontalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		horizontalLayout.addComponents( btnCancel, btnOk );

		mainLayout.addComponent( horizontalLayout );
		setContent( new Panel( mainLayout ) );
	}

	/**
	 * Getter for mapProperties
	 *
	 * @author anco62000465 2018-09-27
	 * @return the mapProperties {@link Map<ElementoTags,CheckBox>}
	 */
	public Map< ElementoTags, CheckBox > getMapProperties()
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
