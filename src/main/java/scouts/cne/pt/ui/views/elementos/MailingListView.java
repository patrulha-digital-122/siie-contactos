package scouts.cne.pt.ui.views.elementos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.olli.ClipboardHelper;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.grids.ElementosGrid;
import scouts.cne.pt.ui.events.google.FinishSIIEUpdate;
import scouts.cne.pt.ui.views.HasSIIELoginUrl;
import scouts.cne.pt.utils.Broadcaster;
import scouts.cne.pt.utils.ContactUtils;
import scouts.cne.pt.utils.UIUtils;

@Route( value = MailingListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( MailingListView.VIEW_DISPLAY_NAME )
@PreserveOnRefresh
public class MailingListView extends HasSIIELoginUrl
{
	private static final long	serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "mailing-list";
	public static final String			VIEW_DISPLAY_NAME	= "Listas de e-mails";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.USERS;
	@Autowired
	private SIIEService					siieService;

	private final List< SIIEElemento >	lstElementos		= new ArrayList<>();
	private final ElementosGrid			grid;
	private final TextArea				mailingList			= new TextArea( "Mailing list" );
	private final Checkbox				usarMailsPais		= new Checkbox( "Utilizar emails dos pais", true );
	private final Checkbox				usarNomes			= new Checkbox( "Utilizar nomes", true );
	private final TextField				totalSelecionados	= new TextField( "Total de selecionados" );
	private final TextField				totalEmails			= new TextField( "Total de endereços de email" );
	private final ClipboardHelper		clipboard;
	private final Button				refresh				= UIUtils.createPrimaryButton( "Actualizar dados do SIIE", VaadinIcon.REFRESH );
	private String						nomeToSearch		= "";
	private UI							ui;

	public MailingListView()
	{
		setId( VIEW_NAME );
		grid = new ElementosGrid( true, lstElementos );
		grid.setSelectionMode( SelectionMode.MULTI );
		grid.addSelectionListener( e -> updateMailingListTextArea() );
		grid.setMinHeight( "50%" );
		
		refresh.setWidthFull();
		refresh.setDisableOnClick( true );
		Button createButton = UIUtils.createButton( "Copiar texto", ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY );
		createButton.setSizeFull();
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
		
		totalEmails.setEnabled( false );
		totalEmails.setValue( "0" );
		setViewContent( createContent() );
	}

	private Component createContent()
	{
		VerticalLayout content = new VerticalLayout( getContentBeforeGrid(), grid, getOptionComponent() );
		content.setSizeFull();
		return content;
	}

	/**
	 * The <b>getContentBeforeGrid</b> method returns {@link Component}
	 * 
	 * @author 62000465 2019-11-14
	 * @return
	 */
	private Component getContentBeforeGrid()
	{
		Label helpLabel = UIUtils
						.createH5Label( "Aqui podes criar uma mailing list com os dados obtidos do SIIE. Seleciona os elementos para quem pretendes enviar o e-mail (podes utilizar os filtros para simplificar a pesquisa) e depois clica no botão lá de baixo para obteres a mailing list completa." );
		helpLabel.setWidthFull();
		VerticalLayout contentBeforeGrid = new VerticalLayout( helpLabel, refresh );
		contentBeforeGrid.setMargin( false );
		contentBeforeGrid.setPadding( false );
		contentBeforeGrid.setSpacing( false );
		contentBeforeGrid.setWidthFull();
		
		return contentBeforeGrid;
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
		List< String > list = ContactUtils.getMailingListFromElemento( grid.getSelectedItems(), usarMailsPais.getValue(), usarNomes.getValue() );
		totalEmails.setValue( String.valueOf( list.size() ) );
		mailingList.setValue( StringUtils.join( list, ", " ) );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register( newMessage ->
		{
			if ( newMessage instanceof FinishSIIEUpdate )
			{
				FinishSIIEUpdate finishSIIEUpdate = ( FinishSIIEUpdate ) newMessage;
				getLogger().info( "Dados do SIIE actualizados em :: " + finishSIIEUpdate.getDuration().toString() );
			}
			updateContent();
		} );

		updateContent();
	}

	/**
	 * The <b>updateContent</b> method returns {@link Object}
	 * 
	 * @author 62000465 2019-11-22
	 * @param attachEvent
	 * @return
	 */
	private void updateContent()
	{
		new Thread( () ->
		{
			lstElementos.clear();
			lstElementos.addAll( siieService.getAllElementos() );
			ui.access( () ->
			{
				Optional< SIIEElemento > elementoByNIN = siieService.getElementoByNIN( siieService.getUserNIN() );
				if ( elementoByNIN.isPresent() )
				{
					grid.useAgrupamentoColumn();
				}
				grid.getDataProvider().refreshAll();
				grid.getSearchNameField().setValue( nomeToSearch );
				refresh.setEnabled( siieService.isAuthenticated() );
				refresh.addClickListener( e ->
				{
					try
					{
						siieService.updateFullSIIE();
					}
					catch ( SIIIEImporterException e1 )
					{
						showError( e1 );
					}
				} );
			} );
		} ).start();
	}


	public Component getOptionComponent()
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setAlignItems( Alignment.STRETCH );
		verticalLayout.setMargin( false );
		verticalLayout.setPadding( false );
		verticalLayout.setSpacing( false );
		
		FormLayout formLayout = new FormLayout();
		formLayout.add( usarMailsPais, usarNomes, totalSelecionados, totalEmails );
		// formLayout.setResponsiveSteps( new ResponsiveStep( "25em", 1 ),
		// new ResponsiveStep( "32em", 2 ),
		// new ResponsiveStep( "40em", 3 ),
		// new ResponsiveStep( "42em", 4 ) );
		
		mailingList.setMaxHeight( "100px" );
		verticalLayout.add( formLayout, mailingList, clipboard );
		return verticalLayout;
	}

	@Override
	public void setParameter( BeforeEvent event, @OptionalParameter String parameter )
	{
		super.setParameter( event, parameter );
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map< String, List< String > > parametersMap = queryParameters.getParameters();
		nomeToSearch = parametersMap.getOrDefault( "nome", Arrays.asList( "" ) ).get( 0 );
		grid.getSearchNameField().setValue( nomeToSearch );
	}
}
