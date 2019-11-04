package scouts.cne.pt.ui.views.elementos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
	private final Div					content				= new Div();
	private final List< SIIEElemento >	lstElementos		= new ArrayList<>();


	public AniversarioListView()
	{
		setId( VIEW_NAME );

		content.setSizeFull();
		content.addClassNames( BoxShadowBorders.BOTTOM, LumoStyles.Padding.Bottom.L );
		FlexBoxLayout flexBoxLayout = new FlexBoxLayout( content );
		flexBoxLayout.setFlexDirection( FlexDirection.COLUMN );
		flexBoxLayout.setMargin( Horizontal.AUTO );
		flexBoxLayout.setPadding( Horizontal.RESPONSIVE_L, Vertical.L );
		setViewContent( flexBoxLayout );
	}


	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		lstElementos.clear();
		lstElementos.addAll( siieService.getAllElementos().stream().filter( ( p ) ->
		{
			if ( p.getDatanascimento() != null )
			{
				return p.getDatanascimento().getDayOfYear() == LocalDate.now()
								.getDayOfYear();
			}
			return false;
		} ).collect( Collectors.toList() ) );
		if ( lstElementos.isEmpty() )
		{
			content.add( UIUtils.createH3Label( "Hoje ninguém faz anos hoje... tens de esperar por outro dia para uma fatia de bolo :-)" ) );
		}
		else
		{
			for ( SIIEElemento siieElemento : lstElementos )
			{
				HorizontalLayout suffix = new HorizontalLayout( siieElemento.getSiglasituacao().getLable(), siieElemento.getSiglaseccao().getLabel() );
				suffix.setSpacing( true );
				ListItem item = new ListItem( UIUtils.createSIIEAvatar( siieElemento ), siieElemento.getNome(),
								siieElemento.getIdade().intValue() + " anos",
								suffix );
				// Dividers for all but the last item
				item.setDividerVisible( true );
				content.add( item );
			}
		}
	}


}
