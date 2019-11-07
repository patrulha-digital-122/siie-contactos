package scouts.cne.pt.ui.views.elementos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.ListItem;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Vertical;
import scouts.cne.pt.ui.util.BoxShadowBorders;
import scouts.cne.pt.ui.util.LumoStyles;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.views.HasSIIELoginUrl;
import scouts.cne.pt.utils.UIUtils;

@Route( value = DiagnosticoListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( DiagnosticoListView.VIEW_DISPLAY_NAME )
public class DiagnosticoListView extends HasSIIELoginUrl
{
	private static final long			serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "diagnostico";
	public static final String			VIEW_DISPLAY_NAME	= "Diagnóstico";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.DOCTOR;
	@Autowired
	private SIIEService					siieService;
	private final Div					content				= new Div();
	private final List< SIIEElemento >	lstElementos		= new ArrayList<>();
	private final Accordion				accordion			= new Accordion();
	private UI							ui;
	private ProgressBar					progressBar			= new ProgressBar( 0, 4, 0 );

	public DiagnosticoListView()
	{
		setId( VIEW_NAME );
		content.setSizeFull();
		content.addClassNames( BoxShadowBorders.BOTTOM, LumoStyles.Padding.Bottom.L );
		content.add( accordion );
		progressBar.setWidthFull();

		FlexBoxLayout flexBoxLayout = new FlexBoxLayout( progressBar, content );
		flexBoxLayout.setFlexDirection( FlexDirection.COLUMN );
		flexBoxLayout.setMargin( Horizontal.AUTO );
		flexBoxLayout.setPadding( Horizontal.RESPONSIVE_L, Vertical.L );
		setViewContent( flexBoxLayout );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		ui = attachEvent.getUI();
		content.removeAll();
		progressBar.setValue( 0 );
		new Thread( () ->
		{
			lstElementos.clear();
			lstElementos.addAll( siieService.getElementosActivos() );
			ui.access( () ->
			{
				progressBar.setValue( 1 );
				checkEmails();
			} );
			ui.access( () ->
			{
				progressBar.setValue( 2 );
				checkTelefone();
			} );
			ui.access( () ->
			{
				progressBar.setValue( 3 );
				checkCC();
				accordion.close();
				progressBar.setValue( 4 );
			} );
		} ).start();
	}

	/**
	 * The <b>checkEmails</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-07
	 */
	private void checkEmails()
	{
		List< SIIEElemento > collect = lstElementos.stream().filter( ( p ) ->
		{
			return StringUtils.isAllBlank( p.getEmail(), p.getPaiemail(), p.getMaeemail() );
		} ).collect( Collectors.toList() );
		if ( !collect.isEmpty() )
		{
			Div verticalLayout = new Div();
			for ( SIIEElemento siieElemento : collect )
			{
				HorizontalLayout suffix =
								new HorizontalLayout( siieElemento.getSiglasituacao().getLable(), siieElemento.getSiglaseccao().getLabel() );
				suffix.setSpacing( true );
				ListItem item = new ListItem( UIUtils.createSIIEAvatar( siieElemento ), siieElemento.getNome(), suffix );
				// Dividers for all but the last item
				item.setDividerVisible( true );
				verticalLayout.add( item );
			}
			accordion.add( "Elementos sem qualquer email definido no SIIE", verticalLayout );
			content.add( accordion );
		}
	}

	private void checkTelefone()
	{
		List< SIIEElemento > collect = lstElementos.stream().filter( ( p ) ->
		{
			return StringUtils.isAllBlank( p.getTelefone(), p.getTelemovel(), p.getPaitelefone(), p.getMaetelefone() );
		} ).collect( Collectors.toList() );
		if ( !collect.isEmpty() )
		{
			Div verticalLayout = new Div();
			for ( SIIEElemento siieElemento : collect )
			{
				HorizontalLayout suffix =
								new HorizontalLayout( siieElemento.getSiglasituacao().getLable(), siieElemento.getSiglaseccao().getLabel() );
				suffix.setSpacing( true );
				ListItem item = new ListItem( UIUtils.createSIIEAvatar( siieElemento ), siieElemento.getNome(), suffix );
				// Dividers for all but the last item
				item.setDividerVisible( true );
				verticalLayout.add( item );
			}
			accordion.add( "Elementos sem qualquer telefone/telemóvel definido no SIIE", verticalLayout );
			content.add( accordion );
		}
	}

	private void checkCC()
	{
		List< SIIEElemento > collect = lstElementos.stream().filter( ( p ) ->
		{
			return StringUtils.isAllBlank( p.getCc() );
		} ).sorted( new Comparator< SIIEElemento >()
		{
			@Override
			public int compare( SIIEElemento o1, SIIEElemento o2 )
			{
				return StringUtils.compare( o1.getNome(), o2.getNome() );
			}
		} ).collect( Collectors.toList() );
		if ( !collect.isEmpty() )
		{
			Div verticalLayout = new Div();
			for ( SIIEElemento siieElemento : collect )
			{
				HorizontalLayout suffix =
								new HorizontalLayout( siieElemento.getSiglasituacao().getLable(), siieElemento.getSiglaseccao().getLabel() );
				suffix.setSpacing( true );
				ListItem item = new ListItem( UIUtils.createSIIEAvatar( siieElemento ), siieElemento.getNome(), suffix );
				// Dividers for all but the last item
				item.setDividerVisible( true );
				verticalLayout.add( item );
			}
			accordion.add( "Elementos sem o número do Cartão do Cidadão definido no SIIE", verticalLayout );
			content.add( accordion );
		}
	}
}
