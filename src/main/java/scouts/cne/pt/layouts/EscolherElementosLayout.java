package scouts.cne.pt.layouts;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.ProgressBar;
import scouts.cne.pt.MyUI;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class EscolherElementosLayout extends VerticalLayout
{
	/**
	 * 
	 */
	private static final long					serialVersionUID			= 5253307196908771291L;
	private TabSheet							tabsheetContactos;
	private Map< SECCAO, Grid< Explorador > >	mapSelecionados;
	private ProgressBar							progressBar;
	private int									elementosParaActualizar		= 0;
	private int									elementosParaActualizados	= 0;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-01-27
	 * @param siieService
	 */
	public EscolherElementosLayout()
	{
		super();
		setSizeFull();
		setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		mapSelecionados = new EnumMap<>( SECCAO.class );
	}

	/**
	 * Getter for tabsheetContactos
	 * 
	 * @author anco62000465 2018-01-27
	 * @return the tabsheetContactos {@link TabSheet}
	 */
	public TabSheet getTabsheetContactos()
	{
		return tabsheetContactos;
	}

	public Component getLayout( GoogleAuthenticationBean googleAuthentication, SIIEService siieService, String embedId )
	{
		tabsheetContactos = new TabSheet();
		siieService.loadExploradoresSIIE();
		for ( SECCAO seccao : SECCAO.getListaSeccoes() )
		{
			// Tab dos Lobitos
			VerticalLayout tabLobitos = new VerticalLayout();
			Grid< Explorador > grid = new Grid<>();
			grid.setSizeFull();
			grid.removeAllColumns();
			grid.setSelectionMode( SelectionMode.MULTI );
			grid.setItems( siieService.getMapSeccaoElemento().get( seccao ) );
			tabLobitos.addComponent( grid );
			// Create a header row to hold column filters
			grid.prependHeaderRow();
			// grid.setDataProvider( DataProvider.fromStream( siieService.getMapSeccaoElemento().get( seccao
			// ).stream()
			// ) );
			grid.setHeaderVisible( true );
			grid.addColumn( Explorador::getNome ).setCaption( "Nome" );
			grid.addColumn( Explorador::getNin ).setCaption( "NIN" );
			grid.addColumn( Explorador::getEmail ).setCaption( "Email" );
			grid.addColumn( Explorador::getNif ).setCaption( "NIF" );
			tabsheetContactos.addTab( tabLobitos, seccao.getNome() + " - " + siieService.getMapSeccaoElemento().get( seccao ).size() );
			mapSelecionados.put( seccao, grid );
		}
		addComponent( tabsheetContactos );
		// rootLayout.addComponent( debugLabel );
		Button btAuthentication = new Button( "Conceder autorização" );
		try
		{
			GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
							googleAuthentication.getGoogleAuthorizationCodeRequestUrl( embedId );
			BrowserWindowOpener browserWindowOpener = new BrowserWindowOpener( googleAuthorizationCodeRequestUrl.build() );
			browserWindowOpener.setFeatures( "height=600,width=600" );
			browserWindowOpener.extend( btAuthentication );
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}
		addComponent( btAuthentication );
		progressBar = new ProgressBar();
		progressBar.setSizeFull();
		progressBar.setVisible( false );
		addComponent( progressBar );
		return this;
	}

	/**
	 * The <b>getElementosSelecionados</b> method returns {@link Map<String,Explorador>}
	 * 
	 * @author anco62000465 2018-01-27
	 * @return
	 */
	public Map< String, Explorador > getElementosSelecionados()
	{
		Map< String, Explorador > map = new HashMap<>();
		for ( Entry< SECCAO, Grid< scouts.cne.pt.model.Explorador > > entry : mapSelecionados.entrySet() )
		{
			Grid< Explorador > grid = ( Grid< Explorador > ) entry.getValue();
			for ( Explorador explorador : grid.getSelectedItems() )
			{
				System.out.println( "Elemento para adicionar: '" + explorador.getNin() + "'" );
				map.put( explorador.getNin().trim(), explorador );
			}
		}
		elementosParaActualizar = map.size();
		return map;
	}

	public void updateProgressBar()
	{
		progressBar.setVisible( true );
		progressBar.setValue( elementosParaActualizar / ( ++elementosParaActualizar ) );
	}
}
