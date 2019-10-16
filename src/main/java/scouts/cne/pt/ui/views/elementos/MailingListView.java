package scouts.cne.pt.ui.views.elementos;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.olli.ClipboardHelper;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.grids.ElementosGrid;
import scouts.cne.pt.ui.views.HasSIIELoginUrl;
import scouts.cne.pt.utils.ContactUtils;
import scouts.cne.pt.utils.UIUtils;

@Route( value = MailingListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( MailingListView.VIEW_DISPLAY_NAME )
public class MailingListView extends HasSIIELoginUrl
{
	private static final long	serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "mailing-list";
	public static final String			VIEW_DISPLAY_NAME	= "Mailing List";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.USERS;
	@Autowired
	private SIIEService					siieService;

	private final List< SIIEElemento >	lstServices			= new ArrayList<>();
	private final ElementosGrid			grid;
	private final TextArea				mailingList			= new TextArea( "Mailing list" );
	private final Checkbox				usarMailsPais		= new Checkbox( "Utilizar emails dos pais", true );
	private final Checkbox				usarNomes			= new Checkbox( "Utilizar nomes", true );
	private final TextField				totalSelecionados	= new TextField( "Total de selecionados" );
	private final ClipboardHelper		clipboard;

	public MailingListView()
	{
		setId( VIEW_NAME );

		grid = new ElementosGrid( true, lstServices );
		grid.setSelectionMode( SelectionMode.MULTI );
		grid.addSelectionListener( e -> updateMailingListTextArea() );
		grid.setMinHeight( "50%" );
		
		Button createButton = UIUtils.createButton( "Copiar texto", ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY );
		createButton.setWidthFull();
		clipboard = new ClipboardHelper( "Copiado",
						createButton );
		
		mailingList.setEnabled( false );
		mailingList.addValueChangeListener( e ->
		{
			clipboard.setContent( e.getValue() );
		} );
		
		createButton.addClickListener( e -> showInfo( "Texto copiado" ) );
		
		usarMailsPais.addValueChangeListener( e -> updateMailingListTextArea() );

		usarNomes.addValueChangeListener( e -> updateMailingListTextArea() );
		
		totalSelecionados.setEnabled( false );
		totalSelecionados.setValue( "0" );
		
		setViewContent( createContent() );
	}

	private Component createContent()
	{
		VerticalLayout content = new VerticalLayout( grid, getOptionComponent() );
		content.setSizeFull();
		
		return content;
	}

	/**
	 * The <b>updateMailingListTextArea</b> method returns {@link Object}
	 * 
	 * @author 62000465 2019-10-11
	 * @return
	 */
	private void updateMailingListTextArea()
	{
		totalSelecionados.setValue( String.valueOf( grid.getSelectedItems().size() ) );
		mailingList.setValue( ContactUtils.getMailingListFromElemento( grid.getSelectedItems(), usarMailsPais.getValue(), usarNomes.getValue() ) );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		lstServices.clear();
		if ( !siieService.isAuthenticated() )
		{
			new Thread( () ->
			{
				if ( authenticate( siieService, attachEvent.getUI() ) )
				{
					attachEvent.getUI().access( () ->
					{
						lstServices.addAll( siieService.getAllElementos() );
						grid.getDataProvider().refreshAll();
						attachEvent.getUI().navigate( VIEW_NAME );
					} );
				}
			} ).start();
		}
		else
		{
			lstServices.addAll( siieService.getAllElementos() );
			grid.getDataProvider().refreshAll();
		}
	}

	public Component getOptionComponent()
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setAlignItems( Alignment.STRETCH );
		verticalLayout.setMargin( false );
		verticalLayout.setPadding( false );
		verticalLayout.setSpacing( false );
		
		FormLayout formLayout = new FormLayout();
		formLayout.add( usarMailsPais, usarNomes, totalSelecionados );
		formLayout.setResponsiveSteps( new ResponsiveStep( "25em", 1 ), new ResponsiveStep( "32em", 2 ), new ResponsiveStep( "40em", 3 ) );
		
		mailingList.setMaxHeight( "100px" );
		verticalLayout.add( formLayout, mailingList, clipboard );
		return verticalLayout;
	}
}
