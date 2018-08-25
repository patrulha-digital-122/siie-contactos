package scouts.cne.pt;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.ServiceException;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.Responsive;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import scouts.cne.pt.google.FirebaseManager;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.layouts.EscolherElementosLayout;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.utils.ContactUtils;
import scouts.cne.pt.utils.HTMLUtils;

@SpringUI
@Push
@Title("SIIE - importer")
public class MyUI extends UI {
	/**
	 *
	 */
	private static final long serialVersionUID = -8505226283440302479L;
	private static final transient Logger logger = Logger.getLogger(MyUI.class.getName());
	VaadinResponse currentResponse;
	private Button btImportacao;
	private Button btAuthentication;
	public static String AUTH_COOKIE_CODE = "CODE";
	private GoogleCredential credential = null;
	private BrowserWindowOpener browserWindowOpener;

	public static Logger getLogger() {
		return MyUI.logger;
	}

	@Autowired
	SIIEService siieService;
	@Autowired
	private GoogleAuthenticationBean googleAuthentication;
	private EscolherElementosLayout elementosLayout;

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		getLogger().info("EmbedId " + getEmbedId());
		FirebaseManager.getInstance();

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setSizeFull();
		mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		setContent(mainLayout);
		setTheme("mytheme");

		Responsive.makeResponsive(mainLayout);

		getLogger().info("New Session: " + getEmbedId());
		// setContent( uploadFileLayout.getLayout( this, siieService ) );
		elementosLayout = new EscolherElementosLayout(getEmbedId());
		VerticalLayout layout = elementosLayout.getLayout(siieService, getEmbedId());
		FileUploader fileUploader = new FileUploader(siieService);
		Upload upload = new Upload("Upload Ficheiro .xlsx do SIIE", fileUploader);

		upload.setDescription(HTMLUtils.strHTMLHelpFicheiroSIIE, ContentMode.HTML);
		upload.addSucceededListener(event -> {

			siieService.setFile(fileUploader.getFile());
			siieService.loadExploradoresSIIE();

			elementosLayout.prencherTabela(siieService);
		});
		upload.setImmediateMode(true);

		btAuthentication = new Button("Dar permissão");
		btAuthentication.setEnabled(true);

		btImportacao = new Button("Iniciar Importação (0)");
		btImportacao.setEnabled(false);

		try {
			GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl = googleAuthentication
					.getGoogleAuthorizationCodeRequestUrl(getEmbedId());
			browserWindowOpener = new BrowserWindowOpener(googleAuthorizationCodeRequestUrl.build());
			browserWindowOpener.setFeatures("height=600,width=600");
			browserWindowOpener.extend(btImportacao);

		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}

		btImportacao.addClickListener(event -> {

			if ((googleAuthentication.getRefreshToken() != null) && (elementosLayout.getSelecionados() > 0)) {
				importProcess();
			}
		});

		mainLayout.addComponent(upload);
		mainLayout.setExpandRatio(upload, 1);
		mainLayout.addComponent(layout);
		mainLayout.setExpandRatio(layout, 10);
		// mainLayout.addComponent(btAuthentication);
		// mainLayout.setExpandRatio(btAuthentication, 1);
		HorizontalLayout verticalLayout = new HorizontalLayout(btImportacao);
		verticalLayout.setSizeFull();
		verticalLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		mainLayout.addComponent(btImportacao);
		mainLayout.setExpandRatio(btImportacao, 1);

		VaadinService.getCurrent().setSystemMessagesProvider(new SystemMessagesProvider() {
			private static final long serialVersionUID = -9118140641761605204L;

			@Override
			public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
				CustomizedSystemMessages messages = new CustomizedSystemMessages();
				messages.setCommunicationErrorCaption("Comm Err");
				messages.setCommunicationErrorMessage("This is bad.");
				messages.setCommunicationErrorNotificationEnabled(true);
				messages.setCommunicationErrorURL("http://vaadin.com/");
				return messages;
			}
		});

		// FirebaseManager.getInstance().addLogMessage("App started");
	}

	public void receiveGoogleCode(String code, String embedId) {

		getLogger().info("Received code: " + code + " | embedId: " + embedId);
		googleAuthentication.addSession(code);
		browserWindowOpener.remove();

		if (elementosLayout.getSelecionados() > 0) {
			importProcess();
		}

		push();
	}

	public void showMenus() {
		setContent(elementosLayout.getLayout(siieService, getEmbedId()));

	}

	public void importProcess() {
		try {
			Map<String, Explorador> elementosParaImportar = elementosLayout.getElementosSelecionados();
			if (elementosParaImportar.isEmpty()) {
				return;
			}
			if (credential == null) {
				credential = googleAuthentication.getGoogleCredentials(getUIId());
			}
			String applicationName = googleAuthentication.getApplicationName();

			ContactsService contactsService = new ContactsService(applicationName);
			contactsService.setOAuth2Credentials(credential);
			URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
			ContactFeed resultFeed = contactsService.getFeed(feedUrl, ContactFeed.class);
			// Print the results
			// Add a normal progress bar
			// elementosLayout.addComponent( progressBar );
			// elementosLayout.setComponentAlignment( progressBar, Alignment.MIDDLE_CENTER
			// );
			// showDebugNotification( "Elementos para importar: " +
			// elementosParaImportar.size() );
			Map<String, ContactEntry> elementosExistentes = new HashMap<>();
			List<ContactEntry> listContc = new ArrayList<>(resultFeed.getEntries());
			while (resultFeed.getNextLink() != null) {
				resultFeed = contactsService.getFeed(new URL(resultFeed.getNextLink().getHref()), ContactFeed.class);
				listContc.addAll(resultFeed.getEntries());
			}
			// showDebugNotification( "Resultados recebidos: " +
			// resultFeed.getTotalResults() + " / " + listContc.size() );
			Set<String> listTelefonesExistentes = new TreeSet<>();
			for (ContactEntry contactEntry : listContc) {
				for (PhoneNumber phoneNumber : contactEntry.getPhoneNumbers()) {
					String strPhoneNumber = phoneNumber.getPhoneNumber();
					strPhoneNumber = strPhoneNumber.replace(" ", "");
					if ("NIN".equals(phoneNumber.getLabel())) {
						if (elementosParaImportar.containsKey(strPhoneNumber.trim())) {
							elementosExistentes.put(strPhoneNumber, contactEntry);
						}
					}

					listTelefonesExistentes.add(strPhoneNumber);
				}
			}
			Map<SECCAO, ContactGroupEntry> processarGrupo = processarGrupo(contactsService, elementosParaImportar);
			List<ContactFeed> listBatchFeeds = new ArrayList<>();
			ContactFeed batchRequestFeed = new ContactFeed();
			listBatchFeeds.add(batchRequestFeed);
			int i = 0;
			List<Explorador> values = new ArrayList<>(elementosParaImportar.values());
			Collections.sort(values);
			for (Explorador explorador : values) {
				if (++i > 98) {
					batchRequestFeed = new ContactFeed();
					listBatchFeeds.add(batchRequestFeed);
					i = 0;
				}
				ContactEntry contEntry;
				ContactEntry elementoProcessar = elementosExistentes.get(explorador.getNin());
				contEntry = ContactUtils.convertElementoToContactEntry(explorador,
						elementoProcessar, listTelefonesExistentes);
				if (elementoProcessar != null) {
					// Actualizar
					System.out.println("Actualizar: " + explorador.getNome());
					BatchUtils.setBatchId(contEntry, "update");
					BatchUtils.setBatchOperationType(contEntry, BatchOperationType.UPDATE);
				} else {
					// Adicionar elemento
					System.out.println("Adicionar: " + explorador.getNome());
					BatchUtils.setBatchId(contEntry, "create");
					BatchUtils.setBatchOperationType(contEntry, BatchOperationType.INSERT);
				}
				Map<String, ContactGroupEntry> groupsToDelete = new HashMap<>();
				ContactGroupEntry myContacts = processarGrupo.get(SECCAO.NONE);
				for (Entry<SECCAO, ContactGroupEntry> entry : processarGrupo.entrySet()) {
					SECCAO seccao = entry.getKey();
					ContactGroupEntry groupEntry = entry.getValue();
					if ((explorador.getCategoria() != seccao) && (groupEntry.getSystemGroup() == null)) {
						groupsToDelete.put(groupEntry.getId(), groupEntry);
					}
				}
				ContactGroupEntry contactGroupEntry = processarGrupo.get(explorador.getCategoria());
				boolean associarGroup = true;
				for (GroupMembershipInfo groupMembershipInfo : contEntry.getGroupMembershipInfos()) {
					if (groupsToDelete.containsKey(groupMembershipInfo.getHref())) {
						groupMembershipInfo.setDeleted(true);
					} else if (groupMembershipInfo.getHref().equals(contactGroupEntry.getId())) {
						associarGroup = false;
						break;
					}
				}
				if (associarGroup) {
					GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo();
					groupMembershipInfo.setHref(contactGroupEntry.getId());
					contEntry.getGroupMembershipInfos().add(groupMembershipInfo);
				}
				if (myContacts != null) {
					GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo();
					groupMembershipInfo.setHref(myContacts.getId());
					contEntry.getGroupMembershipInfos().add(groupMembershipInfo);
				}
				batchRequestFeed.getEntries().add(contEntry);
			}
			List<ContactEntry> listOk = new ArrayList<>();
			List<ContactEntry> listCriados = new ArrayList<>();
			List<ContactEntry> listErro = new ArrayList<>();
			List<ContactEntry> listNaoModificado = new ArrayList<>();
			for (ContactFeed contactFeed : listBatchFeeds) {
				// Submit the batch request to the server.
				ContactFeed responseFeed = contactsService
						.batch(new URL("https://www.google.com/m8/feeds/contacts/default/full/batch"), contactFeed);
				// Check the status of each operation.
				for (ContactEntry entry : responseFeed.getEntries()) {
					// String batchId = BatchUtils.getBatchId( entry );
					BatchStatus status = BatchUtils.getBatchStatus(entry);
					switch (status.getCode()) {
					case 200:
						listOk.add(entry);
						break;
					case 201:
						listCriados.add(entry);
						break;
					case 304:
						listNaoModificado.add(entry);
						break;
					default:
						System.out.println("Erro a processar :" + entry.getPlainTextContent() + " | " + status.getCode()
						+ ": " + status.getReason());
						listErro.add(entry);
						break;
					}
				}
			}
			VerticalLayout verticalLayout = new VerticalLayout();
			Label labelOk = new Label("Contactos actualizados com sucesso");
			Label labelOkValue = new Label("<center><b>" + listOk.size() + "</b></center>", ContentMode.HTML);
			Label labelNovos = new Label("Contactos criados com sucesso");
			Label labelNovosValue = new Label("<center><b>" + listCriados.size() + "</b></center>", ContentMode.HTML);
			Label labelErro = new Label("Contactos não importados");
			Label labelErroValue = new Label(
					"<font color=\"red\"><center><b>" + listErro.size() + "</b></center></font>", ContentMode.HTML);
			verticalLayout.addComponent(labelOk);
			verticalLayout.addComponent(labelOkValue);
			verticalLayout.addComponent(labelNovos);
			verticalLayout.addComponent(labelNovosValue);
			verticalLayout.addComponent(labelErro);
			verticalLayout.addComponent(labelErroValue);
			for (ContactEntry contactEntry : listErro) {
				verticalLayout.addComponent(new Label(contactEntry.getName().getFullName().getValue()));
			}
			Window subWindow = new Window("Resultado");
			subWindow.setContent(verticalLayout);
			// Center it in the browser window
			subWindow.center();
			// Open it in the UI
			getUI().addWindow(subWindow);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The <b>processarGrupo</b> method returns {@link void}
	 *
	 * @author anco62000465 2018-01-27
	 * @param contactsService
	 * @param elementosParaImportar
	 * @param contEntry
	 * @param seccao
	 * @return
	 */
	private Map<SECCAO, ContactGroupEntry> processarGrupo(ContactsService contactsService,
			Map<String, Explorador> elementosParaImportar) {
		Map<String, SECCAO> listSeccaoNecessaris = new HashMap<>();
		for (Explorador explorador : elementosParaImportar.values()) {
			listSeccaoNecessaris.put(explorador.getCategoria().getNome(), explorador.getCategoria());
		}
		// Create query and submit a request
		URL feedUrl;
		try {
			feedUrl = new URL("https://www.google.com/m8/feeds/groups/default/full");
			Query myQuery = new Query(feedUrl);
			ContactGroupFeed resultFeed = contactsService.query(myQuery, ContactGroupFeed.class);
			// Print the results
			ContactGroupEntry contactGroupEntry = null;
			Map<SECCAO, ContactGroupEntry> mapGrupos = new EnumMap<>(SECCAO.class);
			ContactGroupEntry myContacts = null;
			for (ContactGroupEntry entry : resultFeed.getEntries()) {
				if (listSeccaoNecessaris.containsKey(entry.getTitle().getPlainText())) {
					mapGrupos.put(listSeccaoNecessaris.get(entry.getTitle().getPlainText()), entry);
					listSeccaoNecessaris.remove(entry.getTitle().getPlainText());
				}
				// Adicionar aos contactos pessoais
				if ((entry.getSystemGroup() != null) && (entry.getSystemGroup().getId() != null)
						&& "Contacts".equals(entry.getSystemGroup().getId())) {
					myContacts = entry;
				}
			}
			for (SECCAO seccao : listSeccaoNecessaris.values()) {
				if (!mapGrupos.containsKey(seccao)) {
					// Cria um novo grupo
					contactGroupEntry = new ContactGroupEntry();
					contactGroupEntry.setTitle(new PlainTextConstruct(seccao.getNome()));
					ExtendedProperty additionalInfo = new ExtendedProperty();
					additionalInfo.setName("Informações do grupo");
					additionalInfo.setValue(seccao.getDescricao());
					contactGroupEntry.addExtendedProperty(additionalInfo);
					// Ask the service to insert the new entry
					URL postUrl = new URL("https://www.google.com/m8/feeds/groups/default/full");
					mapGrupos.put(seccao, contactsService.insert(postUrl, contactGroupEntry));
				}
			}
			if (myContacts != null) {
				mapGrupos.put(SECCAO.NONE, myContacts);
			}
			return mapGrupos;
		} catch (IOException | ServiceException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public void updateSelectionados(int iSelecionados) {
		btImportacao.setCaption(String.format("Iniciar Importação (%d)", iSelecionados));
		btImportacao.setEnabled( iSelecionados > 0);
	}
}
