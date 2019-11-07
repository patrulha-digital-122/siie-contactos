package scouts.cne.pt.ui.views.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.github.juchar.colorpicker.ColorPickerFieldRaw;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.CodigoTTF;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.util.TextColor;
import scouts.cne.pt.ui.util.css.TextAlign;
import scouts.cne.pt.ui.views.ViewFrame;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-11-06
 *
 */
@PageTitle( CodificadorView.VIEW_DISPLAY_NAME )
@Route( value = CodificadorView.VIEW_NAME, layout = MainLayout.class )
@PreserveOnRefresh
public class CodificadorView extends ViewFrame implements HasLogger
{
	private static final long			serialVersionUID	= -8783462098490997667L;
	public static final String			VIEW_NAME			= "codificador";
	public static final String			VIEW_DISPLAY_NAME	= "Codificador";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.CODE;
	//
	private final TextArea				textArea;
	private final Label					textAreaCodificado;
	private final ComboBox< CodigoTTF >	cbCodigos;
	private final Anchor				anchorDownload;

	public CodificadorView()
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setAlignItems( Alignment.CENTER );
		verticalLayout.setJustifyContentMode( JustifyContentMode.CENTER );
		verticalLayout.setSpacing( false );
		verticalLayout.setSizeFull();
		//
		textArea = new TextArea( "Texto a codificar" );
		textArea.setClassName( "desc", true );
		textArea.setSizeFull();
		textArea.setValueChangeMode( ValueChangeMode.EAGER );
		textArea.addValueChangeListener( e -> codificarTexto() );
		//
		List< CodigoTTF > lstCodigos = new ArrayList<>();
		lstCodigos.add( new CodigoTTF( "Morse", "morse", UIUtils.FONTS_PATH + "morse.ttf" ) );
		lstCodigos.add( new CodigoTTF( "Angular", "angular", UIUtils.FONTS_PATH + "angular.ttf" ) );
		lstCodigos.add( new CodigoTTF( "Homografo", "homografo", UIUtils.FONTS_PATH + "homografo.ttf" ) );
		lstCodigos.add( new CodigoTTF( "LGP", "LGP", UIUtils.FONTS_PATH + "LGP.ttf" ) );
		lstCodigos.add( new CodigoTTF( "Chinês", "chines", UIUtils.FONTS_PATH + "chines.ttf" ) );
		//
		cbCodigos = new ComboBox<>();
		cbCodigos.setWidthFull();
		cbCodigos.setItemLabelGenerator( e -> e.getName() );
		cbCodigos.setItems( lstCodigos );
		cbCodigos.setValue( lstCodigos.get( 0 ) );
		//
		textAreaCodificado = new Label();
		// textAreaCodificado.addThemeVariants( TextAreaVariant.LUMO_ALIGN_CENTER );
		textAreaCodificado.getStyle().set( "font-size", "1.75rem" );
		textAreaCodificado.getStyle().set( "white-space", "pre-wrap" );
		textAreaCodificado.getStyle().set( "border-radius", "5px" );
		textAreaCodificado.getStyle().set( "border-style", "dashed" );
		textAreaCodificado.getStyle().set( "border-width", "thin" );
		textAreaCodificado.getStyle().set( "border-color", "black" );
		textAreaCodificado.setClassName( lstCodigos.get( 0 ).getCSSUrl() );
		UIUtils.setTextAlign( TextAlign.CENTER, textAreaCodificado );
		//
		anchorDownload = new Anchor( lstCodigos.get( 0 ).getLink(), "" );
		anchorDownload.getElement().setAttribute( "download", true );
		anchorDownload.setWidthFull();
		Button button = UIUtils.createPrimaryButton( "Download do ficheiro .ttf", VaadinIcon.DOWNLOAD );
		button.setWidthFull();
		anchorDownload.add( button );
		cbCodigos.addValueChangeListener( event ->
		{
			UIUtils.setFontFamily( event.getValue(), textAreaCodificado );
			codificarTexto();
			anchorDownload.setHref( event.getValue().getLink() );
		} );
		//
		textAreaCodificado.setSizeFull();
		verticalLayout.add( textArea );
		verticalLayout.add( getOptionsLayout() );
		// verticalLayout.add( cbCodigos );
		verticalLayout.add( textAreaCodificado );
		verticalLayout.add( anchorDownload );
		//
		verticalLayout.setFlexGrow( 4, textArea );
		verticalLayout.setFlexGrow( 4, textAreaCodificado );
		setViewContent( verticalLayout );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		UIUtils.setFontFamily( cbCodigos.getValue(), textAreaCodificado );
	}

	protected void codificarTexto()
	{
		textAreaCodificado.setText( StringUtils.stripAccents( textArea.getValue() ) );
	}

	private Component getOptionsLayout()
	{

		Button btnIncreaseText = UIUtils.createSmallButton( VaadinIcon.PLUS );
		btnIncreaseText.setWidthFull();
		btnIncreaseText.addClickListener( e -> textAreaCodificado.getStyle().set( "font-size", processTextSize( true ) ) );

		Button btnDecreaseText = UIUtils.createSmallButton( VaadinIcon.MINUS );
		btnDecreaseText.setWidthFull();
		btnDecreaseText.addClickListener( e -> textAreaCodificado.getStyle().set( "font-size", processTextSize( false ) ) );

		ColorPickerFieldRaw colorPickerText = getColorPick( "hsl(0, 0%, 0%)" );
		colorPickerText.addValueChangeListener( event ->
		{
			UIUtils.setTextColor( event.getValue(), textAreaCodificado );
		} );
		ColorPickerFieldRaw colorPickerBackground = getColorPick( TextColor.WHITE.getValue() );
		colorPickerBackground.addValueChangeListener( event ->
		{
			UIUtils.setBackgroundColor( event.getValue(), textAreaCodificado );
		} );
		// horizontalLayout.add( cbCodigos, btnIncreaseText, btnDecreaseText, colorPickerText, colorPickerBackground );
		FormLayout flexBoxLayout = new FormLayout();
		// flexBoxLayout.setSpacing( Uniform.AUTO );
		flexBoxLayout.setWidthFull();

		FormItem addFormItem = flexBoxLayout.addFormItem( cbCodigos, "Escolher Código" );

		HorizontalLayout buttonLayout = new HorizontalLayout( btnIncreaseText, btnDecreaseText );
		buttonLayout.setSpacing( true );
		buttonLayout.setMargin( false );
		buttonLayout.setPadding( false );

		FormItem addFormItemText = flexBoxLayout.addFormItem( buttonLayout, "Tamanho texto" );
		flexBoxLayout.addFormItem( colorPickerText, "Cor do texto" );
		flexBoxLayout.addFormItem( colorPickerBackground, "Cor do fundo" );

		flexBoxLayout.setColspan( addFormItem, 4 );
		flexBoxLayout.setColspan( addFormItemText, 4 );
		return flexBoxLayout;
	}

	private ColorPickerFieldRaw getColorPick( String initValue )
	{
		ColorPickerFieldRaw colorPickerField = new ColorPickerFieldRaw();
		colorPickerField.setWidthFull();
		colorPickerField.setValue( initValue );
		colorPickerField.setPinnedPalettes( true );
		colorPickerField.getTextField().getStyle().set( "padding-top", "0px" );

		colorPickerField.setHexEnabled( false );
		colorPickerField.setRgbEnabled( false );
		colorPickerField.setHslEnabled( false );
		colorPickerField.getI18n().setCancel( "Cancelar" );
		colorPickerField.getI18n().setSelect( "Selecionar" );
		colorPickerField.setHoverIcon( VaadinIcon.FILL.create() );

		colorPickerField.setChangeFormatButtonVisible( false );
		return colorPickerField;
	}

	private String processTextSize( boolean bEncrease )
	{
		String strOriginalSize = textAreaCodificado.getStyle().get( "font-size" );
		if ( StringUtils.isBlank( strOriginalSize ) )
		{
			return "1.75rem";
		}
		strOriginalSize = strOriginalSize.replace( "rem", "" );
		BigDecimal bigDecimal = new BigDecimal( strOriginalSize );
		BigDecimal valueToChange = BigDecimal.valueOf( 0.25 );
		if ( bEncrease )
		{
			bigDecimal = bigDecimal.add( valueToChange );
		}
		else
		{
			bigDecimal = bigDecimal.subtract( valueToChange );
		}
		return bigDecimal.toString() + "rem";
	}
}
