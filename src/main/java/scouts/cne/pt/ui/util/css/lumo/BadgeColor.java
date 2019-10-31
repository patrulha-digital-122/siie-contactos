package scouts.cne.pt.ui.util.css.lumo;

public enum BadgeColor
{
	NORMAL( "badge" ),
	NORMAL_PRIMARY( "badge primary" ),
	SUCCESS( "badge success" ),
	SUCCESS_PRIMARY( "badge success primary" ),
	ERROR( "badge error" ),
	ERROR_PRIMARY( "badge error primary" ),
	CONTRAST( "badge contrast" ),
	CONTRAST_PRIMARY( "badge contrast primary" ),
	LOBITOS( "badge lobitos" ),
	EXPLORADORES( "badge hsl(140, 100%, 30%)" ),
	PIONEIROS( "badge hsl(225, 100%, 40%)" ),
	CAMINHERIOS( "badge hsl(0, 100%, 40%)" ),
	DIRIGENTES( "badge hsl(300, 100%, 25%)" );
	private String style;

	BadgeColor( String style )
	{
		this.style = style;
	}

	public String getThemeName()
	{
		return style;
	}
}
