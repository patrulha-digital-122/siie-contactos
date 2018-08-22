package scouts.cne.pt.layouts;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import scouts.cne.pt.MyUI;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.services.SIIEService;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class UploadFileLayout extends VerticalLayout {
	/**
	 *
	 */
	private static final long serialVersionUID = 5253307196908771291L;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-01-27
	 * @param siieService
	 */
	public UploadFileLayout() {
		super();
		setSizeFull();
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
	}

	public Component getLayout(MyUI ui, SIIEService siieService) {
		FileUploader fileUploader = new FileUploader(siieService);
		Upload upload = new Upload("Upload Ficheiro .xlsx do SIIE", fileUploader);
		// upload.addSucceededListener(fileUploader);
		upload.setImmediateMode(true);

		ExternalResource resource = new ExternalResource("https://siie.escutismo.pt/Content/images/SIIEv3.png");
		// addComponent( new Image(null,
		// resource ));
		addComponent(upload);

		TextArea textArea = new TextArea();
		textArea.setValue("Escolher o menu \"Elementos\"\n"
				+ "\tAbrir o painel do lado direito e escolher: Listagens -> Dados Completos\n"
				+ "\tUtilizar o botão que está por cima da tabela, do lado esquerdo (um icone de uma folha com um X por cima\n");
		PopupView popupView = new PopupView("Como obter o ficheiro do SIIE?", textArea);
		popupView.setHideOnMouseOut(false);

		addComponent(popupView);
		return this;
	}
}
