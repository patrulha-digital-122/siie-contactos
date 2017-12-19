package scouts.cne.pt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.Categories;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@SpringUI
public class MyUI extends UI {

	/**
	 *
	 */
	private static final long serialVersionUID = -8505226283440302479L;
	private static Logger logger = LoggerFactory.getLogger(MyUI.class);

	@Autowired
	SIIEService siieService;

	private TextArea debugTextArea;
	private VerticalLayout afterLoginLayout;
	private StringBuilder sbLog;

	@Autowired
	private GoogleAuthenticationBean googleAuthentication;
	VerticalLayout rootLayout;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		rootLayout.addComponent(getUploadSIIEFileMenu());
		setContent(rootLayout);

		// Button btAuthentication = new Button("Conceder autorização");
		//
		// sbLog = new StringBuilder();
		//
		// try {
		// excelFile = File.createTempFile("stuff", ".xlsx");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// afterLoginLayout = new VerticalLayout();
		// afterLoginLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		// if (VaadinSession.getCurrent().getAttribute("credential") == null) {
		// afterLoginLayout.setVisible(false);
		// }
		// List<String> data = Arrays.asList("Lobitos", "Exploradores", "Pioneiros",
		// "Caminheiros", "Dirigentes");
		// CheckBoxGroup<String> checkBoxGroup = new CheckBoxGroup<>("Selecione as
		// secções que pretende importar", data);
		// Button btImportacao = new Button("Iniciar importação");
		//
		// debugTextArea = new TextArea();
		// debugTextArea.setReadOnly(true);
		// debugTextArea.setValueChangeMode(ValueChangeMode.LAZY);
		// debugTextArea.setWordWrap(true);
		//
		// //afterLoginLayout.addComponents(upload, checkBoxGroup, btImportacao);
		//
		// // login.addClickListener( new LoginClickListener( afterLoginLayout,
		// // name ) );
		// btImportacao.addClickListener(new ImportContacts(excelFile, checkBoxGroup,
		// this));
		//
		// LoginClickListener listener = new LoginClickListener(this);
		// btAuthentication.addClickListener(new ClickListener() {
		//
		// /**
		// *
		// */
		// private static final long serialVersionUID = -290402141178194857L;
		//
		// @Override
		// public void buttonClick(ClickEvent event) {
		// //FirebaseManager.getInstance().addLogMessage("Novo Pedido de autorização");
		// GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl;
		// try {
		// googleAuthorizationCodeRequestUrl =
		// googleAuthentication.getGoogleAuthorizationCodeRequestUrl();
		// getUI().getPage().open(googleAuthorizationCodeRequestUrl.build(), "_blank",
		// true);
		// } catch (GeneralSecurityException | IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// });
		//
		// //rootLayout.addComponents(btAuthentication, afterLoginLayout,
		// debugTextArea);
		//
		// //setContent(rootLayout);
		//
		// // A request handler for generating some content
		// VaadinSession.getCurrent().addRequestHandler(listener);

		// FirebaseManager.getInstance().addLogMessage("App started");
	}

	private Component getUploadSIIEFileMenu() {
		FileUploader fileUploader = new FileUploader(this, siieService);
		Upload upload = new Upload("Upload Ficheiro .xlsx do SIIE", fileUploader);
		upload.addSucceededListener(fileUploader);
		upload.setImmediateMode(true);

		return upload;
	}

	public void showMenus() {
		Grid<Explorador> grid = new Grid<>(Explorador.class);
		HashMap<String, Explorador> loadExploradoresSIIE = siieService.loadExploradoresSIIE();
		grid.setItems(loadExploradoresSIIE.values());
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.SINGLE);
		rootLayout.addComponent(new Label("Encontrados:" + loadExploradoresSIIE.size()));
		rootLayout.addComponent(grid);
		
		Button button1 = new Button("Download vCard");
		
		FileDownloader fileDownloader = new FileDownloader( createVCard() );
		fileDownloader.extend( button1 );
		
		rootLayout.addComponent( button1 );
	}

	/**
	 * The <b>createVCard</b> method returns {@link Resource}
	 * <br><br><b>author</b> anco62000465 2017-12-19
	 * @return 
	 */
	private StreamResource createVCard()
	{
		return new StreamResource(new StreamSource() {
            @Override
            public InputStream getStream() {
                String text = "My image";

                Collection<VCard> agrupamentoVCard = new ArrayList<>();
                
                HashMap< String, Explorador > loadExploradoresSIIE = siieService.loadExploradoresSIIE();
                
                Categories exploradores = new Categories();
                exploradores.setGroup( SECCAO.EXPLORADORES.getDescricao() );
				for ( Entry< String, Explorador > entry : loadExploradoresSIIE.entrySet() )
				{
					String key = entry.getKey();
					Explorador explorador = entry.getValue();
					
					if(explorador.getCategoria() == SECCAO.EXPLORADORES) {
						 VCard exploradoreVCard = new VCard();
						 exploradoreVCard.setFormattedName( explorador.getNome() );
						 
						 exploradoreVCard.addCategories( exploradores );
						 
						 agrupamentoVCard.add( exploradoreVCard );
					}
				}
				
				try
				{
					File file = new File("vcards.vcf");
					Ezvcard.write(agrupamentoVCard).go(file);
                    return new FileInputStream( file );
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }, "contacts.vcf");
	}

	public void showSecondPhaseOptions() {
		afterLoginLayout.setVisible(true);
	}

	public void addDebugInfo(String text) {
		access(new Runnable() {

			@Override
			public void run() {
				sbLog.append(text).append("\n");
				debugTextArea.setValue(sbLog.toString());
			}
		});
	}
}
