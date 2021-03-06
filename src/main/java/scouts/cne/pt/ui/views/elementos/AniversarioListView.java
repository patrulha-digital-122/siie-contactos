package scouts.cne.pt.ui.views.elementos;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.model.localStorage.AniversariosEventConfigurations;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.LocalStorageService;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.grids.ElementosGrid;
import scouts.cne.pt.ui.events.google.FinishSIIEUpdate;
import scouts.cne.pt.ui.events.internal.InternalStorageEventReady;
import scouts.cne.pt.ui.views.HasSIIELoginUrl;
import scouts.cne.pt.utils.Broadcaster;
import scouts.cne.pt.utils.UIUtils;

@Route( value = AniversarioListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( AniversarioListView.VIEW_DISPLAY_NAME )
public class AniversarioListView extends HasSIIELoginUrl
{
	private static final long	serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "aniversario-list";
	public static final String			VIEW_DISPLAY_NAME	= "Aniversários";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.MEGAFONE;
	@Autowired
	private SIIEService					siieService;
	@Autowired
	private LocalStorageService				localStorageService;
	private final VerticalLayout			content									= new VerticalLayout();
	private final List< SIIEElemento >	lstElementos		= new ArrayList<>();
	private final Checkbox					chkReceberNotifications					= new Checkbox( "Receber notificações" );
	private final Checkbox					chkReceberNotificationsElementosActivos	=
					new Checkbox( "Receber notificações somente de elementos activos" );
	private ElementosGrid				elementosGrid;
	private AniversariosEventConfigurations	aniversariosEventConfigurations	= new AniversariosEventConfigurations();
	private UI								ui;

	public AniversarioListView()
	{
		setId( VIEW_NAME );

		content.setSizeFull();
		content.setAlignItems( Alignment.STRETCH );

		elementosGrid = new ElementosGrid( true, lstElementos );
		elementosGrid.useAdditionalInfoColumn();

		chkReceberNotifications.addValueChangeListener( e ->
		{
			aniversariosEventConfigurations.setReceberNotificacoes( e.getValue() );
			localStorageService.setAniversariosEventConfigurations( aniversariosEventConfigurations );
		} );
		chkReceberNotificationsElementosActivos.addValueChangeListener( e ->
		{
			aniversariosEventConfigurations.setElementosActivos( e.getValue() );
			localStorageService.setAniversariosEventConfigurations( aniversariosEventConfigurations );
		} );


		setViewContent( getOptionComponent(), content );
	}


	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		this.ui = attachEvent.getUI();

		broadcasterRegistration = Broadcaster.register( newMessage ->
		{
			if ( newMessage instanceof FinishSIIEUpdate )
			{
				FinishSIIEUpdate finishSIIEUpdate = ( FinishSIIEUpdate ) newMessage;
				getLogger().info( "Dados do SIIE actualizados em :: " + finishSIIEUpdate.getDuration().toString() );
				ui.access( () -> updateContent() );
			}
			else if ( newMessage instanceof InternalStorageEventReady )
			{
				try
				{
					aniversariosEventConfigurations = localStorageService.getAniversariosEventConfigurations();
					ui.access( () ->
					{
						chkReceberNotifications.setValue( aniversariosEventConfigurations.isReceberNotificacoes() );
						chkReceberNotificationsElementosActivos.setValue( aniversariosEventConfigurations.isElementosActivos() );
					} );
				}
				catch ( Exception e )
				{
					printError( e );
				}
			}
		} );

		updateContent();

		chkReceberNotifications.setValue( localStorageService.getAniversariosEventConfigurations().isReceberNotificacoes() );
		chkReceberNotificationsElementosActivos.setValue( localStorageService.getAniversariosEventConfigurations().isElementosActivos() );
	}

	public Component getOptionComponent()
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setAlignItems( Alignment.STRETCH );

		FormLayout formLayout = new FormLayout();
		formLayout.add( chkReceberNotifications, chkReceberNotificationsElementosActivos );
		// formLayout.setResponsiveSteps( new ResponsiveStep( "25em", 1 ),
		// new ResponsiveStep( "32em", 2 ),
		// new ResponsiveStep( "40em", 3 ),
		// new ResponsiveStep( "42em", 4 ) );
		verticalLayout.add( formLayout );
		return verticalLayout;
	}

	/**
	 * The <b>updateContent</b> method returns {@link Object}
	 * 
	 * @author 62000465 2019-11-22
	 * @return
	 */
	private void updateContent()
	{
		content.removeAll();
		lstElementos.clear();
		LocalDate today = LocalDate.now( ZoneId.ofOffset( "UTC", ZoneOffset.ofHours( 1 ) ) );
		lstElementos.addAll( siieService.getAllElementos().stream().filter( ( p ) ->
		{
			if ( p.getDatanascimento() != null )
			{
				return p.getDatanascimento().getDayOfMonth() == today.getDayOfMonth() &&
					p.getDatanascimento().getMonthValue() == today.getMonthValue();
			}
			return false;
		} ).collect( Collectors.toList() ) );

		if ( lstElementos.isEmpty() )
		{
			content.add( UIUtils.createH3Label( "Hoje ninguém faz anos... tens de esperar por outro dia para uma fatia de bolo :-)" ) );
		}
		else
		{
			content.add( elementosGrid );
			lstElementos.forEach( p ->
			{
				p.getAdditionalInfo().clear();
				p.getAdditionalInfo().add( p.getIdade().intValue() + " anos" );
			} );
			elementosGrid.getDataProvider().refreshAll();
		}
		

	}


}
