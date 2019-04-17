package scouts.cne.pt.layouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import com.vaadin.annotations.Push;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.ui.TabSheet;
import scouts.cne.pt.MainView;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.ElementoTags;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;

/**
 * @author anco62000465 2018-01-27
 *
 */
@Push
public class EscolherElementosLayout extends MasterVerticalView implements HasLogger
{
	/**
	 *
	 */
	private static final long						serialVersionUID	= 5253307196908771291L;
	private Tabs									tabsheetContactos;
	private final Map< SECCAO, List< Elemento > >	mapSelecionados;
	private int										iSelecionados		= 0;
	private final MainView								myUI;
	@Value( "classpath:L.jpg" )
	private Resource								resourceLobitos;
	private final Map< SECCAO, Grid< Elemento > >	mapGrid;
	private final Map< SECCAO, Tab >				mapTabs;
	private final SIIEService						siieService;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-01-27
	 * @param string
	 * @param siieService
	 */
	public EscolherElementosLayout( MainView myUI, SIIEService siieService )
	{
		super();
		// "Segundo Passo - Escolher os elementos a importar para os contactos do Google"
		setSizeFull();
		mapSelecionados = new EnumMap<>( SECCAO.class );
		mapGrid = new HashMap<>();
		mapTabs = new HashMap<>();
		for ( final SECCAO component : SECCAO.getListaSeccoes() )
		{
			mapSelecionados.put( component, new ArrayList<>() );
		}
		this.myUI = myUI;
		this.siieService = siieService;
		add( getLayout() );
	}

	/**
	 * Getter for tabsheetContactos
	 *
	 * @author anco62000465 2018-01-27
	 * @return the tabsheetContactos {@link TabSheet}
	 */
	public Tabs getTabsheetContactos()
	{
		return tabsheetContactos;
	}

	private VerticalLayout getLayout()
	{
		final VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setSpacing( false );
		verticalLayout.setDefaultHorizontalComponentAlignment( Alignment.CENTER );
		tabsheetContactos = new Tabs();
		tabsheetContactos.setSizeFull();
		createTable();
		verticalLayout.add( tabsheetContactos );
		return verticalLayout;
	}

	private void createTable()
	{
		tabsheetContactos.removeAll();
		for ( final SECCAO seccao : SECCAO.getListaSeccoes() )
		{
			// Tab dos Lobitos
			final VerticalLayout verticalLayout = new VerticalLayout();
			verticalLayout.setSizeFull();
			final Grid< Elemento > grid = new Grid<>();
			mapGrid.put( seccao, grid );
			grid.setSizeFull();
			grid.setSelectionMode( SelectionMode.MULTI );
			verticalLayout.add( grid );
			grid.addColumn( Elemento::getNome ).setHeader( ElementoTags.NOME.getTagDescription() );
			grid.addColumn( Elemento::getNin ).setHeader( ElementoTags.NIN.getTagDescription() );
			grid.addColumn( Elemento::getEmail ).setHeader( ElementoTags.EMAIL.getTagDescription() );
			grid.addColumn( Elemento::getNif ).setHeader( ElementoTags.NIF.getTagDescription() );
			// final DateRenderer dateRenderer = new DateRenderer( new SimpleDateFormat( "dd/MM/yyyy" ), "" );
			// grid.addColumn( Elemento::getDataNascimento, dateRenderer ).setHeader(
			// ElementoTags.DATA_NASCIMENTO.getTagDescription() );
			//
			// grid.addColumn( Elemento::getDataPromessa, dateRenderer ).setHeader(
			// ElementoTags.DATA_PROMESSA.getTagDescription() ).setHidable( true )
			// .setHidden( true );
			//
			// grid.addColumn( Elemento::getDataAdmissao, dateRenderer ).setHeader(
			// ElementoTags.DATA_ADMISSAO.getTagDescription() ).setHidable( true )
			// .setHidden( true );
			//
			// grid.addColumn( Elemento::getTotem ).setHeader( ElementoTags.TOTEM.getTagDescription() ).setVisible( true
			// ).setHidden( true );
			// grid.addColumn( Elemento::getTelemovel ).setHeader( ElementoTags.TELEMOVEL.getTagDescription()
			// ).setHidable( true ).setHidden( true );
			// grid.addColumn( Elemento::getTelefone ).setHeader( ElementoTags.TELEFONE.getTagDescription()
			// ).setHidable( true ).setHidden( true );
			// grid.addColumn( Elemento::getMorada ).setHeader( ElementoTags.MORADA.getTagDescription() ).setHidable(
			// true ).setHidden( true );
			// grid.addColumn( Elemento::getCodigoPostal ).setHeader( "Código Postal" ).setHidable( true ).setHidden(
			// true );
			// grid.addColumn( Elemento::getNomeMae ).setHeader( ElementoTags.NOME_MAE.getTagDescription() ).setHidable(
			// true ).setHidden( true );
			// grid.addColumn( Elemento::getTelefoneMae ).setHeader( ElementoTags.TELEFONE_MAE.getTagDescription()
			// ).setHidable( true )
			// .setHidden( true );
			// grid.addColumn( Elemento::getEmailMae ).setHeader( ElementoTags.EMAIL_MAE.getTagDescription()
			// ).setHidable( true ).setHidden( true );
			// grid.addColumn( Elemento::getProfissaoMae ).setHeader( ElementoTags.PROFISSAO_MAE.getTagDescription()
			// ).setHidable( true )
			// .setHidden( true );
			// grid.addColumn( Elemento::getNomePai ).setHeader( ElementoTags.NOME_PAI.getTagDescription() ).setHidable(
			// true ).setHidden( true );
			// grid.addColumn( Elemento::getTelefonePai ).setHeader( ElementoTags.TELEFONE_PAI.getTagDescription()
			// ).setHidable( true )
			// .setHidden( true );
			// grid.addColumn( Elemento::getEmailPai ).setHeader( ElementoTags.EMAIL_PAI.getTagDescription()
			// ).setHidable( true ).setHidden( true );
			// grid.addColumn( Elemento::getProfissaoPai ).setHeader( ElementoTags.PROFISSAO_PAI.getTagDescription()
			// ).setHidable( true )
			// .setHidden( true );
			// grid.addColumn( Elemento::getNotas ).setHeader( ElementoTags.NOTAS.getTagDescription() ).setHidable( true
			// ).setHidden( true )
			// .setDescriptionGenerator( new DescriptionGenerator< Elemento >()
			// {
			// private static final long serialVersionUID = -6877934609841332609L;
			//
			// @Override
			// public String apply( Elemento t )
			// {
			// return t.getNotas();
			// }
			// } );
			// grid.addColumn( Elemento::getObservacoes ).setHeader( ElementoTags.OBSERVACOES.getTagDescription()
			// ).setHidable( true ).setHidden( true )
			// .setDescriptionGenerator( new DescriptionGenerator< Elemento >()
			// {
			// private static final long serialVersionUID = 3913814674210101147L;
			//
			// @Override
			// public String apply( Elemento t )
			// {
			// return t.getObservacoes();
			// }
			// } );

			grid.addSelectionListener( new SelectionListener< Grid< Elemento >, Elemento >()
			{
				@Override
				public void selectionChange( SelectionEvent< Grid< Elemento >, Elemento > event )
				{
					mapSelecionados.get( seccao ).clear();
					mapSelecionados.get( seccao ).addAll( event.getAllSelectedItems() );
					int iCount = 0;
					for ( final List< Elemento > list : mapSelecionados.values() )
					{
						iCount += list.size();
					}
					iSelecionados = iCount;
					VaadinSession.getCurrent().access( () -> myUI.updateSelectionados( iSelecionados ) );
				}
			} );
			final String nomeTab = seccao.getNome() + " - 0";
			try
			{
				// final FileResource resource = new FileResource( new File( basepath + "/WEB-INF/images/" +
				// seccao.getId() + ".jpg" ) );
				// mapTabs.put( seccao, tabsheetContactos.getSelectedTab().add( verticalLayout ) );
			}
			catch ( final Exception e )
			{
				getLogger().error( e.getMessage(), e );
				// mapTabs.put( seccao, tabsheetContactos.add( verticalLayout ) );
			}

		}
	}

	public void refreshGrids()
	{
		for ( final Entry< SECCAO, List< Elemento > > entry : siieService.getMapSeccaoElemento().entrySet() )
		{
			final SECCAO seccao = entry.getKey();
			final List< Elemento > items = siieService.getMapSeccaoElemento().get( seccao );
			Collections.sort( items );
			final Grid< Elemento > grid = mapGrid.get( seccao );
			if ( grid != null )
			{
				grid.setItems( items );
				grid.getDataProvider().refreshAll();
				mapTabs.get( seccao ).setLabel( seccao.getNome() + " - " + items.size() );
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
		final Map< String, Elemento > map = new HashMap<>();
		for ( final Entry< SECCAO, List< Elemento > > entry : mapSelecionados.entrySet() )
		{
			final List< Elemento > list = entry.getValue();
			for ( final Elemento elemento : list )
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
