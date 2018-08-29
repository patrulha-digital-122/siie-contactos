package scouts.cne.pt.layouts;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import com.vaadin.annotations.Push;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import scouts.cne.pt.MyUI;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;

/**
 * @author anco62000465 2018-01-27
 *
 */
@Push
public class EscolherElementosLayout extends VerticalLayout implements HasLogger
{
	/**
	 *
	 */
	private static final long					serialVersionUID	= 5253307196908771291L;
	private TabSheet							tabsheetContactos;
	private Map< SECCAO, List< Explorador > >	mapSelecionados;
	private int									iSelecionados		= 0;
	private String								embedId;
	@Value( "classpath:L.jpg" )
	private Resource							resourceLobitos;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-01-27
	 * @param string
	 * @param siieService
	 */
	public EscolherElementosLayout( String embedId )
	{
		super();
		setSizeFull();
		setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		mapSelecionados = new EnumMap<>( SECCAO.class );
		for ( SECCAO component : SECCAO.getListaSeccoes() )
		{
			mapSelecionados.put( component, new ArrayList<>() );
		}
		this.embedId = embedId;
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

	public VerticalLayout getLayout( SIIEService siieService, String embedId )
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setSpacing( false );
		verticalLayout.setMargin( new MarginInfo( false, true, false, true ) );
		verticalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		tabsheetContactos = new TabSheet();
		tabsheetContactos.setSizeFull();
		prencherTabela( siieService );
		verticalLayout.addComponent( tabsheetContactos );
		verticalLayout.setExpandRatio( tabsheetContactos, 8 );
		Label label = new Label( "Configurações avançadas" );
		VerticalLayout opcoesExtraLayout = new VerticalLayout();
		List< String > data = Arrays.asList( "Importar dados dos pais em separado", "Importar dados dos pais em conjunto" );
		CheckBoxGroup< String > chkBImportarPais = new CheckBoxGroup<>( "Opções:", data );
		opcoesExtraLayout.addComponent( chkBImportarPais );
		// verticalLayout.addComponent( label );
		// verticalLayout.addComponent( opcoesExtraLayout );
		// verticalLayout.setExpandRatio( opcoesExtraLayout, 1 );
		addComponent( verticalLayout );
		return verticalLayout;
	}

	public void prencherTabela(SIIEService siieService) {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		tabsheetContactos.removeAllComponents();

		for (SECCAO seccao : SECCAO.getListaSeccoes()) {
			// Tab dos Lobitos
			VerticalLayout tabLobitos = new VerticalLayout();
			tabLobitos.setSizeFull();
			Grid<Explorador> grid = new Grid<>();
			grid.setSizeFull();
			grid.removeAllColumns();
			grid.setSelectionMode(SelectionMode.MULTI);
			grid.setItems(siieService.getMapSeccaoElemento().get(seccao));
			tabLobitos.addComponent(grid);
			grid.addColumn(Explorador::getNome).setCaption("Nome");
			grid.addColumn(Explorador::getNin).setCaption("NIN");
			grid.addColumn(Explorador::getEmail).setCaption("Email");
			grid.addColumn(Explorador::getNif).setCaption("NIF");
			grid.addSelectionListener(new SelectionListener<Explorador>() {
				@Override
				public void selectionChange(SelectionEvent<Explorador> event) {
					mapSelecionados.get(seccao).clear();
					mapSelecionados.get(seccao).addAll(event.getAllSelectedItems());
					int iCount = 0;
					for (List<Explorador> list : mapSelecionados.values()) {
						iCount += list.size();
					}
					iSelecionados = iCount;

					MyUI uiByEmbedId = ( MyUI ) VaadinSession.getCurrent().getUIByEmbedId( embedId );
					if ( uiByEmbedId != null )
					{
						uiByEmbedId.access( new Runnable()
						{
							@Override
							public void run()
							{
								uiByEmbedId.updateSelectionados(iSelecionados);
							}
						} );
					}
				}
			});
			String nomeTab = seccao.getNome() + " - " + siieService.getMapSeccaoElemento().get(seccao).size();
			try
			{
				FileResource resource = new FileResource( new File( basepath + "/WEB-INF/images/" + seccao.getId() + ".jpg" ) );
				tabsheetContactos.addTab( tabLobitos, nomeTab, resource );
			}
			catch ( Exception e )
			{
				getLogger().error( e.getMessage(), e );
				tabsheetContactos.addTab( tabLobitos, nomeTab );
			}
		}
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
		for ( Entry< SECCAO, List< Explorador > > entry : mapSelecionados.entrySet() )
		{
			List< Explorador > list = entry.getValue();
			for ( Explorador explorador : list )
			{
				map.put( explorador.getNin().trim(), explorador );
			}
		}
		return map;
	}

	private void showDebugNotification( String message )
	{
		System.out.println( message );
		// Notification notification = new Notification( message, Type.TRAY_NOTIFICATION
		// );
		// notification.setDelayMsec( 1000 );
		// notification.setPosition( Position.TOP_RIGHT );
		Notification.show( message, Type.TRAY_NOTIFICATION );
	}

	/**
	 * @return the iSelecionados
	 */
	public int getSelecionados()
	{
		return iSelecionados;
	}
}
