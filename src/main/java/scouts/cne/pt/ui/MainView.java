package scouts.cne.pt.ui;

import java.util.UUID;

public class MainView
{
	/**
	 *
	 */
	private static final long				serialVersionUID	= -8505226283440302479L;
	private String							uid					= UUID.randomUUID().toString();

	public MainView()
	{
		

		// getLogger().info( "UI id -> " + uid );
		// setSpacing( false );
		// // setMargin( new MarginInfo( false, true, false, true ) );
		// setSizeFull();
		// setAlignItems( Alignment.CENTER );
		// // setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		//
		// elementosLayout = new EscolherElementosLayout( this, siieService );
		// importarLayout = new FooterLayout( elementosLayout, googleAuthentication );
		// try
		// {
		// final GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
		// googleAuthentication.getGoogleAuthorizationCodeRequestUrl(
		// com.vaadin.flow.component.UI.getCurrent().getUIId() );
		// browserWindowOpener = new BrowserWindowOpener( googleAuthorizationCodeRequestUrl.build() );
		// browserWindowOpener.setFeatures( "height=600,width=600" );
		// // browserWindowOpener.extend( importarLayout.getBtnAutorizacao() );
		// }
		// catch ( GeneralSecurityException | IOException e )
		// {
		// e.printStackTrace();
		// }
		// // process parameters
		// if ( siieLocalFile != null )
		// {
		// VaadinSession.getCurrent().access( () ->
		// {
		// siieService.setFile( new File( siieLocalFile ) );
		// try
		// {
		// siieService.loadExploradoresSIIE();
		// elementosLayout.refreshGrids();
		// }
		// catch ( final Exception e )
		// {
		// showError( e );
		// }
		// } );
		// }
		// if ( siieGDriveFile != null )
		// {
		// loadContacts( siieGDriveFile );
		// }
		//
		// btnUpdate = new Button( "Actualizar tabela", VaadinIcon.REFRESH.create() );
		//
		// if ( siieLocalFile == null && siieGDriveFile == null )
		// {
		// final UploadFileLayout uploadFileLayout = new UploadFileLayout( siieService, elementosLayout );
		// final SplitLayout verticalSplitPanel = new SplitLayout( uploadFileLayout, elementosLayout );
		// verticalSplitPanel.setOrientation( Orientation.VERTICAL );
		// verticalSplitPanel.setSplitterPosition( 20 );
		// verticalSplitPanel.setSizeFull();
		//
		// final SplitLayout verticalSplitPanel2 = new SplitLayout( verticalSplitPanel, importarLayout );
		// verticalSplitPanel2.setOrientation( Orientation.VERTICAL );
		// verticalSplitPanel2.setSplitterPosition( 85 );
		// verticalSplitPanel2.setSizeFull();
		//
		// add( verticalSplitPanel2 );
		// }
		// else
		// {
		//
		// final SplitLayout verticalSplitPanel = new SplitLayout( elementosLayout, importarLayout );
		// verticalSplitPanel.setOrientation( Orientation.HORIZONTAL );
		// verticalSplitPanel.setSplitterPosition( 85 );
		// if ( siieGDriveFile != null )
		// {
		// btnUpdate.setWidth( "100%" );
		// btnUpdate.addClickListener( event -> loadContacts( siieGDriveFile ) );
		// btnUpdate.setDisableOnClick( true );
		// add( btnUpdate );
		// }
		// add( verticalSplitPanel );
		// }
	}

	// private void loadContacts( String siieGDriveFile )
	// {
	// VaadinSession.getCurrent().access( () ->
	// {
	// try
	// {
	// siieService.loadElementosGDrive( siieGDriveFile );
	// }
	// catch ( final SIIIEImporterException e )
	// {
	// showError( e );
	// }
	// btnUpdate.setEnabled( true );
	// } );
	// }
	//
	// public void receiveGoogleCode( String code, String embedId )
	// {
	// getLogger().info( "Received code: " + code + " | embedId: " + embedId );
	// googleAuthentication.addSession( code );
	// browserWindowOpener.remove();
	// importarLayout.getBtnAutorizacao().setVisible( false );
	// importarLayout.getBtImportacao().setVisible( true );
	// importarLayout.getBtnEmailer().setVisible( true );
	// VaadinSession.getCurrent().access( () ->
	// {
	// PeopleService peopleService;
	// try
	// {
	// peopleService = googleAuthentication.getPeopleService();
	// final Person person = peopleService.people().get( "people/me" ).setPersonFields( "names,emailAddresses"
	// ).execute();
	// final List< EmailAddress > emailAddresses = person.getEmailAddresses();
	// for ( final EmailAddress emailAddress : emailAddresses )
	// {
	// if ( emailAddress.getMetadata() != null && emailAddress.getMetadata().getPrimary() != null &&
	// emailAddress.getMetadata().getPrimary() )
	// {
	// googleAuthentication.setUserEmail( emailAddress.getValue() );
	// if ( !person.getNames().isEmpty() )
	// {
	// googleAuthentication.setUserFullName( person.getNames().get( 0 ).getDisplayName() );
	// }
	// break;
	// }
	// }
	// getLogger().info( "Hello '{}' with email '{}'.", googleAuthentication.getUserFullName(),
	// googleAuthentication.getUserEmail() );
	// showTray( "Olá " + googleAuthentication.getUserFullName() );
	// }
	// catch ( final Exception e )
	// {
	// e.printStackTrace();
	// }
	// } );
	// }
	//
	// public void updateSelectionados( int iSelecionados )
	// {
	// importarLayout.getBtImportacao().setText( String.format( "Iniciar Importação (%d)", iSelecionados ) );
	// importarLayout.getBtImportacao().setEnabled( iSelecionados > 0 );
	// importarLayout.getBtImportacaoVCard().setText( String.format( "Download como VCard (%d)", iSelecionados ) );
	// importarLayout.getBtImportacaoVCard().setEnabled( iSelecionados > 0 );
	// importarLayout.getBtnCopyMailingList().setText( String.format( "Mailing list (%d)", iSelecionados ) );
	// importarLayout.getBtnCopyMailingList().setEnabled( iSelecionados > 0 );
	// importarLayout.getBtnEmailer().setEnabled( iSelecionados > 0 );
	// importarLayout.getBtnAuthFile().setEnabled( iSelecionados > 0 );
	// }
	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.flow.component.applayout.AbstractAppRouterLayout#configure(com.vaadin.flow.component.applayout.
	 * AppLayout, com.vaadin.flow.component.applayout.AppLayoutMenu)
	 */
	// @Override
	// protected void configure( AppLayout appLayout, AppLayoutMenu appLayoutMenu )
	// {
	// final Label branding = new Label();
	// branding.setText( UI.getCurrent().getTranslation( "application.id" ) );
	// appLayout.setBranding( branding );
	// // Master Data
	// setMenuItem( appLayoutMenu,
	// new AppLayoutMenuItem( LoadingDataView.VIEW_ICON, UI.getCurrent().getTranslation(
	// LoadingDataView.VIEW_DISPLAY_NAME ),
	// LoadingDataView.VIEW_NAME ) );
	// setMenuItem( appLayoutMenu,
	// new AppLayoutMenuItem( EscolherElementosView.VIEW_ICON,
	// UI.getCurrent().getTranslation( EscolherElementosView.VIEW_DISPLAY_NAME ),
	// EscolherElementosView.VIEW_NAME ) );
	// getElement().addEventListener( "search-focus", e ->
	// {
	// appLayout.getElement().getClassList().add( "hide-navbar" );
	// } );
	// getElement().addEventListener( "search-blur", e ->
	// {
	// appLayout.getElement().getClassList().remove( "hide-navbar" );
	// } );
	//
	// }
	//
	// private void setMenuItem( AppLayoutMenu menu, AppLayoutMenuItem menuItem )
	// {
	// menuItem.getElement().setAttribute( "theme", "icon-on-top" );
	// menu.addMenuItem( menuItem );
	// }


}
