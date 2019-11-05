package scouts.cne.pt.ui.components.grids;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.types.SIIESituacao;
import scouts.cne.pt.ui.util.LumoStyles;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-09-05
 *
 */
public class ElementosGrid extends Grid< SIIEElemento >
{
	private static final long serialVersionUID = -1960188142116715298L;

	private final ListDataProvider< SIIEElemento >	dataProvider;
	private final TextField							searchNameField		= new TextField();;


	public ElementosGrid( boolean bUseFilterRow, Collection< SIIEElemento > lstService )
	{
		super();
		setSizeFull();
		Column< SIIEElemento > avatar =
						addComponentColumn( i -> UIUtils.createSIIEAvatar( i ) )
						.setHeader( "Foto" ).setWidth( "30px" ).setSortable( false );
		Grid.Column< SIIEElemento > ninColumn = addColumn( SIIEElemento::getNin ).setHeader( "NIN" ).setSortable( true );
		Grid.Column< SIIEElemento > nomeColumn = addColumn( SIIEElemento::getNome ).setHeader( "Nome" ).setSortable( true );
		Grid.Column< SIIEElemento > seccaoColumn = addColumn( SIIEElemento::getSeccao ).setHeader( "Secção" ).setSortable( true );
		Grid.Column< SIIEElemento > situacaoColumn = addColumn( SIIEElemento::getSiglasituacao ).setHeader( "Situação" ).setSortable( true );
		getColumns().forEach( column -> column.setAutoWidth( true ) );
		avatar.setAutoWidth( false ).setWidth( LumoStyles.IconSize.S );

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
			SituacaoComboBox situacaoComboBox = new SituacaoComboBox();
			situacaoComboBox.setSizeFull();
			situacaoComboBox.addValueChangeListener( event -> dataProvider
							.addFilter( elemento -> situacaoComboBox.isEmpty() ||
								elemento.getSiglasituacao().equals( situacaoComboBox.getValue() ) ) );
			situacaoComboBox.setValue( SIIESituacao.A );
			filterRow.getCell( situacaoColumn ).setComponent( situacaoComboBox );
		}
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
}
