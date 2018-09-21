package scouts.cne.pt.layouts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.DateRenderer;
import scouts.cne.pt.MyUI;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;

/**
 * @author anco62000465 2018-01-27
 *
 */
@Push
public class EscolherElementosLayout extends Panel implements HasLogger
{
	/**
	 *
	 */
	private static final long				serialVersionUID	= 5253307196908771291L;
	private TabSheet						tabsheetContactos;
	private Map< SECCAO, List< Elemento > >	mapSelecionados;
	private int								iSelecionados		= 0;
	private String							embedId;
	@Value( "classpath:L.jpg" )
	private Resource						resourceLobitos;
	private Map< SECCAO, Grid< Elemento > >	mapGrid;
	private Map< SECCAO, Tab >				mapTabs;
	private SIIEService						siieService;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-01-27
	 * @param string
	 * @param siieService
	 */
	public EscolherElementosLayout( String embedId, SIIEService siieService )
	{
		super( "Segundo Passo - Escolher os elementos a importar para os contactos do Google" );
		setSizeFull();
		mapSelecionados = new EnumMap<>( SECCAO.class );
		mapGrid = new HashMap<>();
		mapTabs = new HashMap<>();
		for ( SECCAO component : SECCAO.getListaSeccoes() )
		{
			mapSelecionados.put( component, new ArrayList<>() );
		}
		this.embedId = embedId;
		this.siieService = siieService;
		setContent( getLayout( embedId ) );
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

	private VerticalLayout getLayout( String embedId )
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setSpacing( false );
		verticalLayout.setMargin( new MarginInfo( false, true, false, true ) );
		verticalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		tabsheetContactos = new TabSheet();
		tabsheetContactos.setSizeFull();
		createTable();
		verticalLayout.addComponent( tabsheetContactos );
		return verticalLayout;
	}

	private void createTable()
	{
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		tabsheetContactos.removeAllComponents();
		for ( SECCAO seccao : SECCAO.getListaSeccoes() )
		{
			// Tab dos Lobitos
			VerticalLayout verticalLayout = new VerticalLayout();
			verticalLayout.setSizeFull();
			Grid< Elemento > grid = new Grid<>();
			mapGrid.put( seccao, grid );
			grid.setSizeFull();
			grid.removeAllColumns();
			grid.setSelectionMode( SelectionMode.MULTI );
			verticalLayout.addComponent( grid );
			grid.addColumn( Elemento::getNome ).setCaption( "Nome" );
			grid.addColumn( Elemento::getNin ).setCaption( "NIN" );
			grid.addColumn( Elemento::getEmail ).setCaption( "Email" );
			grid.addColumn( Elemento::getNif ).setCaption( "NIF" );
			Column< Elemento, Date > dataNascimentoColumn = grid.addColumn( Elemento::getDataNascimento ).setCaption( "Data Nascimento" );
			dataNascimentoColumn.setRenderer( new DateRenderer( new SimpleDateFormat( "dd/MM/yyyy" ), "" ) );
			grid.addSelectionListener( new SelectionListener< Elemento >()
			{
				private static final long serialVersionUID = 1626537027266542111L;

				@Override
				public void selectionChange( SelectionEvent< Elemento > event )
				{
					mapSelecionados.get( seccao ).clear();
					mapSelecionados.get( seccao ).addAll( event.getAllSelectedItems() );
					int iCount = 0;
					for ( List< Elemento > list : mapSelecionados.values() )
					{
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
								uiByEmbedId.updateSelectionados( iSelecionados );
							}
						} );
					}
				}
			} );
			String nomeTab = seccao.getNome() + " - 0";
			try
			{
				FileResource resource = new FileResource( new File( basepath + "/WEB-INF/images/" + seccao.getId() + ".jpg" ) );
				mapTabs.put( seccao, tabsheetContactos.addTab( verticalLayout, nomeTab, resource ) );
			}
			catch ( Exception e )
			{
				getLogger().error( e.getMessage(), e );
				mapTabs.put( seccao, tabsheetContactos.addTab( verticalLayout, nomeTab ) );
			}

		}
	}

	public void refreshGrids()
	{
		for ( Entry< SECCAO, List< Elemento > > entry : siieService.getMapSeccaoElemento().entrySet() )
		{
			SECCAO seccao = entry.getKey();
			List< Elemento > items = siieService.getMapSeccaoElemento().get( seccao );
			Grid< Elemento > grid = mapGrid.get( seccao );
			if ( grid != null )
			{
				grid.setItems( items );
				grid.getDataProvider().refreshAll();
				mapTabs.get( seccao ).setCaption( seccao.getNome() + " - " + items.size() );
			}
		}
	}

	/**
	 * The <b>getElementosSelecionados</b> method returns {@link Map<String,Explorador>}
	 *
	 * @author anco62000465 2018-01-27
	 * @return
	 */
	public Map< String, Elemento > getElementosSelecionados()
	{
		Map< String, Elemento > map = new HashMap<>();
		for ( Entry< SECCAO, List< Elemento > > entry : mapSelecionados.entrySet() )
		{
			List< Elemento > list = entry.getValue();
			for ( Elemento elemento : list )
			{
				map.put( elemento.getNin().trim(), elemento );
			}
		}
		return map;
	}

	/**
	 * @return the iSelecionados
	 */
	public int getSelecionados()
	{
		return iSelecionados;
	}
}
