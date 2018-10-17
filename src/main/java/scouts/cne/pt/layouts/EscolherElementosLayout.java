package scouts.cne.pt.layouts;

import java.io.File;
import java.text.SimpleDateFormat;
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
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.DescriptionGenerator;
import com.vaadin.ui.renderers.DateRenderer;
import scouts.cne.pt.MyUI;
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
public class EscolherElementosLayout extends Panel implements HasLogger
{
	/**
	 *
	 */
	private static final long						serialVersionUID	= 5253307196908771291L;
	private TabSheet								tabsheetContactos;
	private final Map< SECCAO, List< Elemento > >	mapSelecionados;
	private int										iSelecionados		= 0;
	private final MyUI								myUI;
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
	public EscolherElementosLayout( MyUI myUI, SIIEService siieService )
	{
		super( "Segundo Passo - Escolher os elementos a importar para os contactos do Google" );
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
		setContent( getLayout() );
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

	private VerticalLayout getLayout()
	{
		final VerticalLayout verticalLayout = new VerticalLayout();
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
		final String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		tabsheetContactos.removeAllComponents();
		for ( final SECCAO seccao : SECCAO.getListaSeccoes() )
		{
			// Tab dos Lobitos
			final VerticalLayout verticalLayout = new VerticalLayout();
			verticalLayout.setSizeFull();
			final Grid< Elemento > grid = new Grid<>();
			mapGrid.put( seccao, grid );
			grid.setSizeFull();
			grid.setSelectionMode( SelectionMode.MULTI );
			verticalLayout.addComponent( grid );
			grid.addColumn( Elemento::getNome ).setCaption( ElementoTags.NOME.getTagDescription() );
			grid.addColumn( Elemento::getNin ).setCaption( ElementoTags.NIN.getTagDescription() );
			grid.addColumn( Elemento::getEmail ).setCaption( ElementoTags.EMAIL.getTagDescription() );
			grid.addColumn( Elemento::getNif ).setCaption( ElementoTags.NIF.getTagDescription() );
			final DateRenderer dateRenderer = new DateRenderer( new SimpleDateFormat( "dd/MM/yyyy" ), "" );
			grid.addColumn( Elemento::getDataNascimento, dateRenderer ).setCaption( ElementoTags.DATA_NASCIMENTO.getTagDescription() );

			grid.addColumn( Elemento::getDataPromessa, dateRenderer ).setCaption( ElementoTags.DATA_PROMESSA.getTagDescription() ).setHidable( true )
							.setHidden( true );

			grid.addColumn( Elemento::getDataAdmissao, dateRenderer ).setCaption( ElementoTags.DATA_ADMISSAO.getTagDescription() ).setHidable( true )
							.setHidden( true );

			grid.addColumn( Elemento::getTotem ).setCaption( ElementoTags.TOTEM.getTagDescription() ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getTelemovel ).setCaption( ElementoTags.TELEMOVEL.getTagDescription() ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getTelefone ).setCaption( ElementoTags.TELEFONE.getTagDescription() ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getMorada ).setCaption( ElementoTags.MORADA.getTagDescription() ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getCodigoPostal ).setCaption( "Código Postal" ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getNomeMae ).setCaption( ElementoTags.NOME_MAE.getTagDescription() ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getTelefoneMae ).setCaption( ElementoTags.TELEFONE_MAE.getTagDescription() ).setHidable( true )
							.setHidden( true );
			grid.addColumn( Elemento::getEmailMae ).setCaption( ElementoTags.EMAIL_MAE.getTagDescription() ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getProfissaoMae ).setCaption( ElementoTags.PROFISSAO_MAE.getTagDescription() ).setHidable( true )
							.setHidden( true );
			grid.addColumn( Elemento::getNomePai ).setCaption( ElementoTags.NOME_PAI.getTagDescription() ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getTelefonePai ).setCaption( ElementoTags.TELEFONE_PAI.getTagDescription() ).setHidable( true )
							.setHidden( true );
			grid.addColumn( Elemento::getEmailPai ).setCaption( ElementoTags.EMAIL_PAI.getTagDescription() ).setHidable( true ).setHidden( true );
			grid.addColumn( Elemento::getProfissaoPai ).setCaption( ElementoTags.PROFISSAO_PAI.getTagDescription() ).setHidable( true )
							.setHidden( true );
			grid.addColumn( Elemento::getNotas ).setCaption( ElementoTags.NOTAS.getTagDescription() ).setHidable( true ).setHidden( true )
							.setDescriptionGenerator( new DescriptionGenerator< Elemento >()
							{
								private static final long serialVersionUID = -6877934609841332609L;

								@Override
								public String apply( Elemento t )
								{
									return t.getNotas();
								}
							} );
			grid.addColumn( Elemento::getObservacoes ).setCaption( ElementoTags.OBSERVACOES.getTagDescription() ).setHidable( true ).setHidden( true )
							.setDescriptionGenerator( new DescriptionGenerator< Elemento >()
							{
								private static final long serialVersionUID = 3913814674210101147L;

								@Override
								public String apply( Elemento t )
								{
									return t.getObservacoes();
								}
							} );

			grid.addSelectionListener( new SelectionListener< Elemento >()
			{
				private static final long serialVersionUID = 1626537027266542111L;

				@Override
				public void selectionChange( SelectionEvent< Elemento > event )
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
				final FileResource resource = new FileResource( new File( basepath + "/WEB-INF/images/" + seccao.getId() + ".jpg" ) );
				mapTabs.put( seccao, tabsheetContactos.addTab( verticalLayout, nomeTab, resource ) );
			}
			catch ( final Exception e )
			{
				getLogger().error( e.getMessage(), e );
				mapTabs.put( seccao, tabsheetContactos.addTab( verticalLayout, nomeTab ) );
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
