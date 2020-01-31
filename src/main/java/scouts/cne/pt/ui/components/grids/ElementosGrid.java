package scouts.cne.pt.ui.components.grids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.types.SIIESituacao;
import scouts.cne.pt.ui.components.SIIEMobileTemplate;
import scouts.cne.pt.ui.util.FontSize;
import scouts.cne.pt.ui.util.TextColor;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-09-05
 *
 */
public class ElementosGrid extends Grid< SIIEElemento >
{
	/**
	 * 
	 */
	public static final String							ADDITIONAL_INFO_COLUMN	= "additional_info";
	public static final String							AGRUPAMENTO_COLUMN		= "agrupamento";
	private static final long							serialVersionUID	= -1960188142116715298L;
	private final ListDataProvider< SIIEElemento >		dataProvider;
	private final TextField								searchNameField		= new TextField();
	private final Grid.Column< SIIEElemento >			situacaoColumn;
	private Registration								resizeListener;
	private Column< SIIEElemento >						mobileColumn;
	private final List< Grid.Column< SIIEElemento > >	desktopColumns		= new ArrayList<>();
	private final boolean								bUseFilterRow;
	private HeaderRow									filterRow				= null;

	public ElementosGrid( boolean bUseFilterRow, Collection< SIIEElemento > lstService )
	{
		super();
		this.bUseFilterRow = bUseFilterRow;
		setSizeFull();
		// addThemeName( "mobile" );
		// "Mobile" column
		mobileColumn = addColumn( new ComponentRenderer<>( this::getMobileTemplate ) ).setSortable( false ).setHeader( "Nome / Secção" );
		mobileColumn.setVisible( false );

		Column< SIIEElemento > avatar =
						addComponentColumn( i -> UIUtils.createSIIEAvatar( i ) ).setHeader( "Foto" ).setWidth( "30px" ).setSortable( false );
		// "Desktop" columns
		Grid.Column< SIIEElemento > ninColumn = addColumn( SIIEElemento::getNin ).setHeader( "NIN" ).setSortable( true );
		Grid.Column< SIIEElemento > nomeColumn = addColumn( SIIEElemento::getNome ).setHeader( "Nome" ).setSortable( true );
		Grid.Column< SIIEElemento > seccaoColumn = addColumn( new ComponentRenderer<>( t ->
		{
			return t.getSiglaseccao().getLabel();
		} ) ).setHeader( "Secção" ).setSortable( true );
		situacaoColumn = addColumn( new ComponentRenderer<>( t ->
		{
			return t.getSiglasituacao().getLable();
		} ) ).setHeader( "Situação" ).setSortable( true );

		Grid.Column< SIIEElemento > agrupamentoColumn =
						addColumn( SIIEElemento::getAgrupdesc ).setHeader( "Agrupamento" ).setAutoWidth( true ).setKey( AGRUPAMENTO_COLUMN )
										.setSortable( true );
		agrupamentoColumn.setVisible( false );

		desktopColumns.add( avatar );
		desktopColumns.add( ninColumn );
		desktopColumns.add( nomeColumn );
		desktopColumns.add( seccaoColumn );
		desktopColumns.add( situacaoColumn );

		dataProvider = new ListDataProvider<>( lstService );
		setDataProvider( dataProvider );

		if ( bUseFilterRow )
		{
			// Create a header row to hold column filters
			filterRow = appendHeaderRow();
			// First filter
			TextField firstNameField = new TextField();
			firstNameField.addValueChangeListener( event -> dataProvider
							.addFilter( elemento -> StringUtils.containsIgnoreCase( elemento.getNin(), firstNameField.getValue() ) ) );
			firstNameField.setValueChangeMode( ValueChangeMode.EAGER );
			filterRow.getCell( ninColumn ).setComponent( firstNameField );
			firstNameField.setSizeFull();
			firstNameField.setPlaceholder( "Filtrar" );
			// Second filter
			searchNameField.addValueChangeListener( event -> dataProvider
							.addFilter( elemento -> StringUtils.containsIgnoreCase( elemento.getNome(), searchNameField.getValue() ) ) );
			searchNameField.setValueChangeMode( ValueChangeMode.EAGER );
			filterRow.getCell( nomeColumn ).setComponent( searchNameField );
			searchNameField.setSizeFull();
			searchNameField.setPlaceholder( "Filtrar" );
			// Third filter
			SeccaoComboBox comboBox = new SeccaoComboBox();
			comboBox.setSizeFull();
			comboBox.addValueChangeListener( event -> dataProvider
							.addFilter( elemento -> comboBox.isEmpty() || elemento.getSiglaseccao().equals( comboBox.getValue() ) ) );
			filterRow.getCell( seccaoColumn ).setComponent( comboBox );
			// Third filter
			SeccaoComboBox comboMobileBox = new SeccaoComboBox();
			comboMobileBox.setWidthFull();
			comboMobileBox.addValueChangeListener( event -> dataProvider
							.addFilter( elemento -> comboMobileBox.isEmpty() || elemento.getSiglaseccao().equals( comboMobileBox.getValue() ) ) );
			filterRow.getCell( mobileColumn ).setComponent( comboMobileBox );
			// Third filter
			SituacaoComboBox situacaoComboBox = new SituacaoComboBox();
			situacaoComboBox.setSizeFull();
			situacaoComboBox.addValueChangeListener( event -> dataProvider.addFilter( elemento -> situacaoComboBox.isEmpty() ||
				elemento.getSiglasituacao().equals( situacaoComboBox.getValue() ) ) );
			situacaoComboBox.setValue( SIIESituacao.A );
			filterRow.getCell( situacaoColumn ).setComponent( situacaoComboBox );
			// Agrupamento filter
			TextField agrupamentoField = new TextField();
			agrupamentoField.addValueChangeListener( event -> dataProvider
							.addFilter( elemento -> StringUtils.containsIgnoreCase( elemento.getAgrupdesc(), agrupamentoField.getValue() ) ) );
			agrupamentoField.setValueChangeMode( ValueChangeMode.EAGER );
			agrupamentoField.setSizeFull();
			agrupamentoField.setPlaceholder( "Filtrar" );
			filterRow.getCell( agrupamentoColumn ).setComponent( agrupamentoField );
		}
		
		desktopColumns.forEach( e -> e.setAutoWidth( true ) );
	}

	public void useAdditionalInfoColumn()
	{
		Column< SIIEElemento > column = addColumn( new ComponentRenderer<>( this::getAdditionalInfoTemplate ) ).setSortable( false )
						.setHeader( "Informação Adicional" ).setKey( ADDITIONAL_INFO_COLUMN ).setAutoWidth( true );
		desktopColumns.add( column );
	}

	public void useAgrupamentoColumn()
	{

		Column< SIIEElemento > columnByKey = getColumnByKey( AGRUPAMENTO_COLUMN );
		if ( columnByKey != null )
		{
			columnByKey.setVisible( true );
			desktopColumns.add( columnByKey );
		}
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		super.onAttach( attachEvent );
		getUI().ifPresent( ui ->
		{
			Page page = ui.getPage();
			resizeListener = page.addBrowserWindowResizeListener( event -> updateVisibleColumns( event.getWidth() ) );
			page.retrieveExtendedClientDetails( details -> updateVisibleColumns( details.getBodyClientWidth() ) );
		} );
	}

	@Override
	protected void onDetach( DetachEvent detachEvent )
	{
		resizeListener.remove();
		super.onDetach( detachEvent );
	}

	private void updateVisibleColumns( int width )
	{
		boolean mobile = width < UIUtils.MOBILE_BREAKPOINT;
		// "Mobile" column
		if ( mobile )
		{
			dataProvider.clearFilters();
			dataProvider.addFilter( elemento -> elemento.getSiglasituacao().equals( SIIESituacao.A ) );
		}
		mobileColumn.setVisible( mobile );
		// "Desktop" columns
		for ( Column< SIIEElemento > column : desktopColumns )
		{
			column.setVisible( !mobile );
		}
	}

	/**
	 * 
	 * The <b>getMobileTemplate</b> method returns {@link Component}
	 * 
	 * @author 62000465 2019-11-21
	 * @param siieElemento
	 * @return
	 */
	private Component getMobileTemplate( SIIEElemento siieElemento )
	{
		return new SIIEMobileTemplate( siieElemento );
	}

	private Component getAdditionalInfoTemplate( SIIEElemento siieElemento )
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setAlignItems( Alignment.STRETCH );
		siieElemento.getAdditionalInfo().forEach( p -> verticalLayout.add( UIUtils.createLabel( FontSize.S, TextColor.ERROR, p ) ) );
		return verticalLayout;
	}

	/**
	 * Getter for searchNameField
	 * 
	 * @author 62000465 2019-10-18
	 * @return the searchNameField {@link TextField}
	 */
	public TextField getSearchNameField()
	{
		return searchNameField;
	}

	/**
	 * Getter for situacaoColumn
	 * 
	 * @author 62000465 2019-11-06
	 * @return the situacaoColumn {@link Grid.Column<SIIEElemento>}
	 */
	public Grid.Column< SIIEElemento > getSituacaoColumn()
	{
		return situacaoColumn;
	}
}
