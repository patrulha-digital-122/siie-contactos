package scouts.cne.pt.ui.components;

import com.vaadin.flow.component.html.Label;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.ui.layout.size.Right;
import scouts.cne.pt.ui.layout.size.Vertical;
import scouts.cne.pt.ui.util.FontSize;
import scouts.cne.pt.ui.util.LineHeight;
import scouts.cne.pt.ui.util.LumoStyles;
import scouts.cne.pt.ui.util.TextColor;
import scouts.cne.pt.ui.util.css.AlignSelf;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.util.css.Overflow;
import scouts.cne.pt.ui.util.css.PointerEvents;
import scouts.cne.pt.ui.util.css.TextOverflow;
import scouts.cne.pt.utils.UIUtils;

/**
 * A layout for displaying SIIElemento info in a mobile friendly format.
 */
public class SIIEMobileTemplate extends FlexBoxLayout
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5908459051268225499L;
	private SIIEElemento		siieElemento;

	public SIIEMobileTemplate( SIIEElemento siieElemento )
	{
		this.siieElemento = siieElemento;
		UIUtils.setLineHeight( LineHeight.M, this );
		UIUtils.setPointerEvents( PointerEvents.NONE, this );
		setPadding( Vertical.AUTO );
		setSpacing( Right.L );
		FlexBoxLayout column = new FlexBoxLayout( getNome(), getSeccao() );
		column.setFlexDirection( FlexDirection.COLUMN );
		column.setOverflow( Overflow.HIDDEN );
		if ( siieElemento.getAdditionalInfo().size() > 0 )
		{
			siieElemento.getAdditionalInfo().forEach( p -> column.add( UIUtils.createLabel( FontSize.S, TextColor.ERROR, p ) ) );
		}
		add( column );
		setFlexGrow( 1, column );
	}

	private Label getNome()
	{
		Label owner = UIUtils.createLabel( FontSize.M, TextColor.BODY, siieElemento.getNome() );
		UIUtils.setOverflow( Overflow.HIDDEN, owner );
		UIUtils.setTextOverflow( TextOverflow.ELLIPSIS, owner );
		UIUtils.setAlignSelf( AlignSelf.START, owner );
		FlexBoxLayout wrapper = new FlexBoxLayout( owner );
		wrapper.setAlignItems( Alignment.CENTER );
		wrapper.setFlexGrow( 1, owner );
		wrapper.setSpacing( Right.M );
		return owner;
	}

	private Badge getSeccao()
	{
		Badge account = siieElemento.getSiglaseccao().getLabel();
		account.addClassNames( LumoStyles.Margin.Bottom.S );
		UIUtils.setOverflow( Overflow.HIDDEN, account );
		UIUtils.setTextOverflow( TextOverflow.ELLIPSIS, account );
		return account;
	}
}
