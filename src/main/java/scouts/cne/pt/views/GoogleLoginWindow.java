package scouts.cne.pt.views;

import com.vaadin.ui.Window;

public class GoogleLoginWindow extends Window {


	/**
	 *
	 */
	private static final long serialVersionUID = -3269588975459926811L;

	@Override
	public void attach() {
		super.attach();

		setModal(true);
		setWidth("300px");
		setHeight("200px");

	}

}
