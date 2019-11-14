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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.types.SIIESituacao;
import scouts.cne.pt.ui.components.Badge;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.layout.size.Right;
import scouts.cne.pt.ui.layout.size.Vertical;
import scouts.cne.pt.ui.util.FontSize;
import scouts.cne.pt.ui.util.LineHeight;
import scouts.cne.pt.ui.util.LumoStyles;
import scouts.cne.pt.ui.util.TextColor;
import scouts.cne.pt.ui.util.css.AlignSelf;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.util.css.Overflow;
import scouts.cne.pt.ui.util.css.PointerEvents;
import scouts.cne.pt.ui.util.css.TextOverflow;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-09-05
 *
 */
public class ElementosGrid extends Grid< SIIEElemento >
{
	private static final long							serialVersionUID	= -1960188142116715298L;
	private final ListDataProvider< SIIEElemento >		dataProvider;
	private final TextField								searchNameField		= new TextField();
	private final Grid.Column< SIIEElemento >			situacaoColumn;
	private Registration								resizeListener;
	private Column< SIIEElemento >						mobileColumn;
	private final List< Grid.Column< SIIEElemento > >	desktopColumns		= new ArrayList<>();

	public ElementosGrid( boolean bUseFilterRow, Collection< SIIEElemento > lstService )
	{
		super();
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
			HeaderRow filterRow = appendHeaderRow();
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
		}
		
		desktopColumns.forEach( e -> e.setAutoWidth( true ) );
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

	private Component getSIIESeccao( SIIEElemento siieElemento )
	{
		return siieElemento.getSiglaseccao().getLabel();
	}
	private Component getMobileTemplate( SIIEElemento siieElemento )
	{
		return new SIIEMobileTemplate( siieElemento );
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

	/**
	 * A layout for displaying SIIElemento info in a mobile friendly format.
	 */
	private class SIIEMobileTemplate extends FlexBoxLayout
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 9029448247040068065L;
		private SIIEElemento		siieElemento;

		public SIIEMobileTemplate( SIIEElemento siieElemento )
		{
			this.siieElemento = siieElemento;
			UIUtils.setLineHeight( LineHeight.M, this );
			UIUtils.setPointerEvents( PointerEvents.NONE, this );
			setPadding( Vertical.AUTO );
			setSpacing( Right.L );
			FlexBoxLayout column = new FlexBoxLayout( getNome(), getSeccao() );
			column.setFlexDirection( FlexDirection.COLUMN );
			column.setOverflow( Overflow.HIDDEN );

			add( column );
			setFlexGrow( 1, column );
		}

		private Label getNome()
		{
			Label owner = UIUtils.createLabel( FontSize.M, TextColor.BODY, siieElemento.getNome() );
			UIUtils.setOverflow( Overflow.HIDDEN, owner );
			UIUtils.setTextOverflow( TextOverflow.ELLIPSIS, owner );
			UIUtils.setAlignSelf( AlignSelf.CENTER, owner );
			FlexBoxLayout wrapper = new FlexBoxLayout( owner );
			wrapper.setAlignItems( Alignment.CENTER );
			wrapper.setFlexGrow( 1, owner );
			wrapper.setSpacing( Right.M );
			return owner;
		}

		private Badge getSeccao()
		{
			Badge account = siieElemento.getSiglaseccao().getLabel();
			account.addClassNames( LumoStyles.Margin.Bottom.S );
			UIUtils.setOverflow( Overflow.HIDDEN, account );
			UIUtils.setTextOverflow( TextOverflow.ELLIPSIS, account );
			return account;
		}
	}
}
