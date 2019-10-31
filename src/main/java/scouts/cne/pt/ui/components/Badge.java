package scouts.cne.pt.ui.components;

import static scouts.cne.pt.ui.util.css.lumo.BadgeShape.PILL;
import java.util.StringJoiner;
import com.vaadin.flow.component.html.Span;
import scouts.cne.pt.ui.util.TextColor;
import scouts.cne.pt.ui.util.css.lumo.BadgeColor;
import scouts.cne.pt.ui.util.css.lumo.BadgeShape;
import scouts.cne.pt.ui.util.css.lumo.BadgeSize;
import scouts.cne.pt.utils.UIUtils;

public class Badge extends Span
{
	public Badge( String text )
	{
		this( text, BadgeColor.NORMAL );
	}

	public Badge( String text, BadgeColor color )
	{
		super( text );
		UIUtils.setTheme( color.getThemeName(), this );
	}

	public Badge( String text, TextColor backgroundColor, TextColor textColor )
	{
		super( text );
		UIUtils.setTheme( BadgeColor.NORMAL.getThemeName(), this );
		UIUtils.setBackgroundColor( backgroundColor.getValue(), this );
		UIUtils.setTextColor( textColor, this );
	}

	public Badge( String text, BadgeColor color, BadgeSize size, BadgeShape shape )
	{
		super( text );
		StringJoiner joiner = new StringJoiner( " " );
		joiner.add( color.getThemeName() );
		if ( shape.equals( PILL ) )
		{
			joiner.add( shape.getThemeName() );
		}
		if ( size.equals( BadgeSize.S ) )
		{
			joiner.add( size.getThemeName() );
		}
		UIUtils.setTheme( joiner.toString(), this );
	}
}
