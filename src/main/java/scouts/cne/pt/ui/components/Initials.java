package scouts.cne.pt.ui.components;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import scouts.cne.pt.ui.util.FontSize;
import scouts.cne.pt.ui.util.FontWeight;
import scouts.cne.pt.ui.util.LumoStyles;
import scouts.cne.pt.ui.util.css.BorderRadius;
import scouts.cne.pt.utils.UIUtils;

public class Initials extends FlexBoxLayout {

	private String CLASS_NAME = "initials";

	public Initials(String initials) {
		setAlignItems(FlexComponent.Alignment.CENTER);
		setBackgroundColor(LumoStyles.Color.Contrast._10);
		setBorderRadius(BorderRadius.L);
		setClassName(CLASS_NAME);
		UIUtils.setFontSize(FontSize.S, this);
		UIUtils.setFontWeight(FontWeight._600, this);
		setHeight(LumoStyles.Size.M);
		setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		setWidth(LumoStyles.Size.M);

		add(initials);
	}

}
