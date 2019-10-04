package scouts.cne.pt.ui.components;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
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
import com.google.gdata.data.contacts.UserDefinedField;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.ServiceException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.ImportContactReport;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.utils.ContactUtils;
import scouts.cne.pt.utils.ElementoImport;

/**
 * @author anco62000465 2018-09-17
 *
 */
public class FooterLayout extends MasterVerticalLayout implements HasLogger
{
	private static final long					serialVersionUID	= -6763770502811814642L;
	private final Button						btnAutorizacao;
	private final Button						btImportacao;
	private final Button						btImportacaoVCard;
	private final Button						btnCopyMailingList;
	private final Button						btnEmailer;
	private final Button						btnAuthFile;
	private final GoogleAuthenticationBean		googleAuthentication;
	private final ImportContactsConfigWindow	importContactsConfigWindow;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-09-17
	 * @param button
	 */
	public FooterLayout( GoogleAuthenticationBean googleAuthentication )
	{
		super();
		// "Funcionalidades disponiveis"
		setSizeFull();
		this.googleAuthentication = googleAuthentication;
		importContactsConfigWindow = new ImportContactsConfigWindow();

		btnAutorizacao = new Button( "Autorizar", VaadinIcon.GOOGLE_PLUS_SQUARE.create() );
		final boolean bAlreadyHaveRefreshToken = StringUtils.isNotBlank( googleAuthentication.getRefreshToken() );
		btnAutorizacao.setEnabled( !bAlreadyHaveRefreshToken );
		btImportacao = new Button( "Importação (0)", VaadinIcon.USER_STAR.create() );
		btImportacao.setEnabled( false );
		btImportacao.setVisible( bAlreadyHaveRefreshToken );
		btImportacao.setDisableOnClick( true );
		btImportacao.addClickListener( event ->
		{
			// if ( googleAuthentication.getRefreshToken() != null && elementosLayout.getSelecionados() > 0 )
			// {
			// getUI().get().add( importContactsConfigWindow );
			// }
		} );

		importContactsConfigWindow.addDetachListener( event ->
		{
			if ( !importContactsConfigWindow.isCancel() )
			{
				importProcess();
			}
			btImportacao.setEnabled( true );
		} );

		btImportacaoVCard = new Button( "Download como VCard (0)", VaadinIcon.DOWNLOAD_ALT.create() );
		btImportacaoVCard.setEnabled( false );
		final StreamResource myResource = createVCardFile();
		final FileDownloader fileDownloader = new FileDownloader( myResource );
		// fileDownloader.extend( btImportacaoVCard );
		final Label labelFooter = new Label(
						"<p><strong>Powered by:</strong> Patrulha Digital 122 - <a href=\"mailto:patrulha.digital.122@escutismo.pt?Subject=SIIE%20Contactos%20\" target=\"_top\">patrulha.digital.122@escutismo.pt</a> </p>" );
		final Button btnFaq = new Button( "Ajuda - FAQ", VaadinIcon.QUESTION.create() );
		btnFaq.addClickListener( event ->
		{
			// getUI().addWindow( new FAQWindow( btnFaq.getCaption() ) );
		} );

		btnCopyMailingList = new Button( "Mailing list (0)", VaadinIcon.ENVELOPES.create() );
		btnCopyMailingList.setEnabled( false );
		btnCopyMailingList.addClickListener( event ->
		{
			// getUI().addWindow( new MailingListWindow( elementosLayout.getElementosSelecionados().values() ) );
		} );

		btnEmailer = new Button( "Email's", VaadinIcon.MAILBOX.create() );
		btnEmailer.setEnabled( false );
		btnEmailer.setVisible( bAlreadyHaveRefreshToken );
		btnEmailer.addClickListener( event ->
		{
			// getUI().addWindow( new EmailerWindow( elementosLayout.getElementosSelecionados().values(),
			// googleAuthentication ) );

		} );

		// Ficheiro autorização
		btnAuthFile = new Button( "Ficheiro Autorização - MAF/SIIE", VaadinIcon.DOWNLOAD.create() );
		btnAuthFile.setEnabled( false );
		btnAuthFile.addClickListener( event ->
		{
			// getUI().addWindow( new AutorizationFilesWindow( elementosLayout.getElementosSelecionados().values(),
			// googleAuthentication ) );
		} );

		final HorizontalLayout horizontalLayoutBtn = new HorizontalLayout();
		horizontalLayoutBtn.setWidth( "100%" );
		horizontalLayoutBtn.setDefaultVerticalComponentAlignment( Alignment.CENTER );
		horizontalLayoutBtn.add( btnAutorizacao, btImportacao, btImportacaoVCard, btnCopyMailingList, btnFaq );
		horizontalLayoutBtn.add( btnEmailer );
		horizontalLayoutBtn.add( btnAuthFile );

		final VerticalLayout verticalLayout = new VerticalLayout( horizontalLayoutBtn, labelFooter );
		verticalLayout.setHorizontalComponentAlignment( Alignment.CENTER );
		verticalLayout.setMargin( true );
		add( verticalLayout );
	}

	/**
	 * Getter for btnAutorizacao
	 * 
	 * @author anco62000465 2018-09-26
	 * @return the btnAutorizacao {@link Button}
	 */
	public Button getBtnAutorizacao()
	{
		return btnAutorizacao;
	}

	/**
	 * Getter for btImportacao
	 *
	 * @author anco62000465 2018-09-17
	 * @return the btImportacao {@link Button}
	 */
	public Button getBtImportacao()
	{
		return btImportacao;
	}

	/**
	 * Getter for btImportacaoVCard
	 *
	 * @author anco62000465 2018-09-17
	 * @return the btImportacaoVCard {@link Button}
	 */
	public Button getBtImportacaoVCard()
	{
		return btImportacaoVCard;
	}

	/**
	 * Getter for btnCopyMailingList
	 *
	 * @author anco62000465 2018-09-21
	 * @return the btnCopyMailingList {@link Button}
	 */
	public Button getBtnCopyMailingList()
	{
		return btnCopyMailingList;
	}

	/**
	 * Getter for btnEmailer
	 *
	 * @author anco62000465 2018-09-25
	 * @return the btnEmailer {@link Button}
	 */
	public Button getBtnEmailer()
	{
		return btnEmailer;
	}

	public Button getBtnAuthFile()
	{
		return btnAuthFile;
	}

	/**
	 * The <b>createVCardFile</b> method returns {@link StreamResource}
	 *
	 * @author anco62000465 2018-08-29
	 * @return
	 */
	private StreamResource createVCardFile()
	{
		return new StreamResource( new StreamSource()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = 721545760831041530L;

			@Override
			public InputStream getStream()
			{
				// try
				// {
				// // return new FileInputStream( ContactVCardUtils.getVCardFile(
				// // elementosLayout.getElementosSelecionados().values() ) );
				// }
				// catch ( final IOException e )
				// {
				// getLogger().error( e.getMessage(), e );
				// }
				return null;
			};
		}, "contact.vcf" );
	}

	public void importProcess()
	{
		try
		{
			final Map< String, Elemento > elementosParaImportar = new HashMap<>(); // elementosLayout.getElementosSelecionados();
			if ( elementosParaImportar.isEmpty() )
			{
				return;
			}
			final GoogleCredential credential = googleAuthentication.getGoogleCredentials();
			if ( credential == null )
			{
				showWarning( "Aplicação sem autorização..." );
				return;
			}
			final String applicationName = googleAuthentication.getApplicationName();
			final ContactsService contactsService = new ContactsService( applicationName );
			contactsService.setOAuth2Credentials( credential );
			final URL feedUrl = new URL( "https://www.google.com/m8/feeds/contacts/default/full" );
			ContactFeed resultFeed = contactsService.getFeed( feedUrl, ContactFeed.class );
			// Print the results
			// Add a normal progress bar
			// elementosLayout.addComponent( progressBar );
			// elementosLayout.setComponentAlignment( progressBar, Alignment.MIDDLE_CENTER
			// );
			// showDebugNotification( "Elementos para importar: " +
			// elementosParaImportar.size() );
			final Map< String, ContactEntry > elementosExistentes = new HashMap<>();
			final List< ContactEntry > listContc = new ArrayList<>( resultFeed.getEntries() );
			while ( resultFeed.getNextLink() != null )
			{
				resultFeed = contactsService.getFeed( new URL( resultFeed.getNextLink().getHref() ), ContactFeed.class );
				listContc.addAll( resultFeed.getEntries() );
			}
			// showDebugNotification( "Resultados recebidos: " +
			// resultFeed.getTotalResults() + " / " + listContc.size() );
			final Set< String > listTelefonesExistentes = new TreeSet<>();
			for ( final ContactEntry contactEntry : listContc )
			{
				for ( final PhoneNumber phoneNumber : contactEntry.getPhoneNumbers() )
				{
					String strPhoneNumber = phoneNumber.getPhoneNumber();
					strPhoneNumber = strPhoneNumber.replace( " ", "" );
					if ( "NIN".equals( phoneNumber.getLabel() ) )
					{
						if ( elementosParaImportar.containsKey( strPhoneNumber.trim() ) )
						{
							elementosExistentes.put( strPhoneNumber, contactEntry );
						}
					}
					listTelefonesExistentes.add( strPhoneNumber );
				}
				for ( final UserDefinedField userDefinedField : contactEntry.getUserDefinedFields() )
				{
					if ( StringUtils.equals( userDefinedField.getKey(), "NIN" ) )
					{
						final String strNIN = StringUtils.trimToEmpty( userDefinedField.getValue() );
						if ( elementosParaImportar.containsKey( strNIN ) )
						{
							elementosExistentes.put( strNIN, contactEntry );
						}
					}
				}
			}
			final Map< SECCAO, ContactGroupEntry > processarGrupo = processarGrupo( contactsService, elementosParaImportar );
			final List< ContactFeed > listBatchFeeds = new ArrayList<>();
			ContactFeed batchRequestFeed = new ContactFeed();
			listBatchFeeds.add( batchRequestFeed );
			int i = 0;
			final List< Elemento > values = new ArrayList<>( elementosParaImportar.values() );
			final Map< String, ElementoImport > importReports = new HashMap<>();
			Collections.sort( values );
			for ( final Elemento elemento : values )
			{
				if ( ++i > 98 )
				{
					batchRequestFeed = new ContactFeed();
					listBatchFeeds.add( batchRequestFeed );
					i = 0;
				}
				ElementoImport elementoImport;
				final ContactEntry elementoProcessar = elementosExistentes.get( elemento.getNin() );
				elementoImport = ContactUtils.convertElementoToContactEntry(	elemento,
																				elementoProcessar,
																				listTelefonesExistentes,
																				importContactsConfigWindow.getMapProperties() );
				importReports.put( elementoImport.getImportContactReport().getNin(), elementoImport );
				if ( elementoProcessar != null )
				{
					// Actualizar
					BatchUtils.setBatchId( elementoImport.getContactEntry(), "update" );
					BatchUtils.setBatchOperationType( elementoImport.getContactEntry(), BatchOperationType.UPDATE );
				}
				else
				{
					// Adicionar elemento
					BatchUtils.setBatchId( elementoImport.getContactEntry(), "create" );
					BatchUtils.setBatchOperationType( elementoImport.getContactEntry(), BatchOperationType.INSERT );
				}
				final Map< String, ContactGroupEntry > groupsToDelete = new HashMap<>();
				final ContactGroupEntry myContacts = processarGrupo.get( SECCAO.NONE );
				for ( final Entry< SECCAO, ContactGroupEntry > entry : processarGrupo.entrySet() )
				{
					final SECCAO seccao = entry.getKey();
					final ContactGroupEntry groupEntry = entry.getValue();
					if ( elemento.getCategoria() != seccao && groupEntry.getSystemGroup() == null )
					{
						groupsToDelete.put( groupEntry.getId(), groupEntry );
					}
				}
				final ContactGroupEntry contactGroupEntry = processarGrupo.get( elemento.getCategoria() );
				boolean associarGroup = true;
				for ( final GroupMembershipInfo groupMembershipInfo : elementoImport.getContactEntry().getGroupMembershipInfos() )
				{
					if ( groupsToDelete.containsKey( groupMembershipInfo.getHref() ) )
					{
						groupMembershipInfo.setDeleted( true );
					}
					else if ( groupMembershipInfo.getHref().equals( contactGroupEntry.getId() ) )
					{
						associarGroup = false;
						break;
					}
				}
				if ( associarGroup )
				{
					final GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo();
					groupMembershipInfo.setHref( contactGroupEntry.getId() );
					elementoImport.getContactEntry().getGroupMembershipInfos().add( groupMembershipInfo );
				}
				if ( myContacts != null )
				{
					final GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo();
					groupMembershipInfo.setHref( myContacts.getId() );
					elementoImport.getContactEntry().getGroupMembershipInfos().add( groupMembershipInfo );
				}
				batchRequestFeed.getEntries().add( elementoImport.getContactEntry() );
			}
			final List< ElementoImport > listOk = new ArrayList<>();
			final List< ElementoImport > listCriados = new ArrayList<>();
			final List< ElementoImport > listErro = new ArrayList<>();
			final List< ElementoImport > listNaoModificado = new ArrayList<>();
			for ( final ContactFeed contactFeed : listBatchFeeds )
			{
				// Submit the batch request to the server.
				final ContactFeed responseFeed =
								contactsService.batch( new URL( "https://www.google.com/m8/feeds/contacts/default/full/batch" ), contactFeed );
				// Check the status of each operation.
				for ( final ContactEntry entry : responseFeed.getEntries() )
				{
					// String batchId = BatchUtils.getBatchId( entry );
					final BatchStatus status = BatchUtils.getBatchStatus( entry );
					final ElementoImport e = importReports.get( getNinFromContactEntry( entry.getUserDefinedFields() ) );

					String fullName = "?";
					if ( entry.getName() != null && entry.getName().getFullName() != null )
					{
						fullName = entry.getName().getFullName().getValue();
					}
					else
					{
						fullName = Objects.toString( entry.getId(), "?" );
					}
					switch ( status.getCode() )
					{
						case 200:
							getLogger().error( "Contacto actualizado: {}", fullName );
							listOk.add( e );
							break;
						case 201:
							getLogger().error( "Contacto criados: {}", fullName );
							e.getContactEntry().setId( entry.getId() );
							listCriados.add( e );
							break;
						case 304:
							getLogger().error( "Contacto não actualizados: {}", entry.getName().getFullName() );
							listNaoModificado.add( e );
							break;
						default:
							getLogger().error(	"Erro a processar : {} | {} :: {}",
												entry.getPlainTextContent(),
												status.getCode(),
												status.getReason() );
							final ContainerTag join = TagCreator.p( TagCreator.join(	TagCreator.text( "Código do erro: " ),
																						TagCreator.b( String.valueOf( status.getCode() ) ),
																						TagCreator.text( " | Motivo: " ),
																						TagCreator.b( status.getReason() ) ) );
							final ElementoImport elementoImport = new ElementoImport( entry, null,
											new ImportContactReport( getNinFromContactEntry( entry.getUserDefinedFields() ) ) );
							elementoImport.getImportContactReport().getLstLabels().add( join.render() );
							listErro.add( elementoImport );
							break;
					}
				}
			}
			final Dialog window = new Dialog();
			window.add( new ImportContactsReportLayout( listOk, listCriados, listErro, listNaoModificado ) );
			// Center it in the browser window
			window.setCloseOnOutsideClick( true );
			window.setHeight( "500px" );
			window.setWidth( "1000px" );
			// Open it in the UI
			// getUI().addWindow( window );
		}
		catch ( final Exception e )
		{
			showError( e );
		}
	}

	private String getNinFromContactEntry( List< UserDefinedField > userDefinedFields )
	{
		final Optional< UserDefinedField > findFirst = userDefinedFields.stream().filter( p -> StringUtils.equals( p.getKey(), "NIN" ) ).findFirst();
		if ( findFirst.isPresent() )
		{
			return findFirst.get().getValue();
		}
		else
		{
			return null;
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
	private Map< SECCAO, ContactGroupEntry > processarGrupo( ContactsService contactsService, Map< String, Elemento > elementosParaImportar )
	{
		final Map< String, SECCAO > listSeccaoNecessaris = new HashMap<>();
		for ( final Elemento elemento : elementosParaImportar.values() )
		{
			listSeccaoNecessaris.put( elemento.getCategoria().getNome(), elemento.getCategoria() );
		}
		// Create query and submit a request
		URL feedUrl;
		try
		{
			feedUrl = new URL( "https://www.google.com/m8/feeds/groups/default/full" );
			final Query myQuery = new Query( feedUrl );
			final ContactGroupFeed resultFeed = contactsService.query( myQuery, ContactGroupFeed.class );
			// Print the results
			ContactGroupEntry contactGroupEntry = null;
			final Map< SECCAO, ContactGroupEntry > mapGrupos = new EnumMap<>( SECCAO.class );
			ContactGroupEntry myContacts = null;
			for ( final ContactGroupEntry entry : resultFeed.getEntries() )
			{
				if ( listSeccaoNecessaris.containsKey( entry.getTitle().getPlainText() ) )
				{
					mapGrupos.put( listSeccaoNecessaris.get( entry.getTitle().getPlainText() ), entry );
					listSeccaoNecessaris.remove( entry.getTitle().getPlainText() );
				}
				// Adicionar aos contactos pessoais
				if ( entry.getSystemGroup() != null && entry.getSystemGroup().getId() != null && "Contacts".equals( entry.getSystemGroup().getId() ) )
				{
					myContacts = entry;
				}
			}
			for ( final SECCAO seccao : listSeccaoNecessaris.values() )
			{
				if ( !mapGrupos.containsKey( seccao ) )
				{
					// Cria um novo grupo
					contactGroupEntry = new ContactGroupEntry();
					contactGroupEntry.setTitle( new PlainTextConstruct( seccao.getNome() ) );
					final ExtendedProperty additionalInfo = new ExtendedProperty();
					additionalInfo.setName( "Informações do grupo" );
					additionalInfo.setValue( seccao.getDescricao() );
					contactGroupEntry.addExtendedProperty( additionalInfo );
					// Ask the service to insert the new entry
					final URL postUrl = new URL( "https://www.google.com/m8/feeds/groups/default/full" );
					mapGrupos.put( seccao, contactsService.insert( postUrl, contactGroupEntry ) );
				}
			}
			if ( myContacts != null )
			{
				mapGrupos.put( SECCAO.NONE, myContacts );
			}
			return mapGrupos;
		}
		catch ( IOException | ServiceException e )
		{
			System.err.println( e.getMessage() );
		}
		return null;
	}
}
