package scouts.cne.pt.ui.util;

public enum TextColor {

	HEADER("var(--lumo-header-text-color)"),
	BODY("var(--lumo-body-text-color)"),
	SECONDARY("var(--lumo-secondary-text-color)"),
	TERTIARY("var(--lumo-tertiary-text-color)"),
	DISABLED("var(--lumo-disabled-text-color)"),
	PRIMARY("var(--lumo-primary-text-color)"),
	PRIMARY_CONTRAST("var(--lumo-primary-contrast-color)"),
	ERROR("var(--lumo-error-text-color)"),
	ERROR_CONTRAST("var(--lumo-error-contrast-color)"),
	SUCCESS("var(--lumo-success-text-color)"),
	SUCCESS_CONTRAST( "var(--lumo-success-contrast-color)" ),
	LOBITOS( "hsl(60, 100%, 70%)" ),
	EXPLORADORES( "hsl(140, 100%, 30%)" ),
	PIONEIROS( "hsl(225, 100%, 40%)" ),
	CAMINHERIOS( "hsl(0, 100%, 40%)" ),
	DIRIGENTES( "hsl(300, 100%, 25%)" ),
	AUXILIAR( "hsl(0, 0%, 60%)" ),
	WHITE( "hsl(0, 0%, 100%)" ),
	BLACK( "hsl(0, 0%, 0%)" ),
	LARANJA( "hsl(20, 100%, 60%)" ),
	CINZENTO( "hsl(0, 0%, 80%)" );

	private String value;

	TextColor(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
