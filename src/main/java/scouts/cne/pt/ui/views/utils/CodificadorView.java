package scouts.cne.pt.ui.views.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.CodigoTTF;
import scouts.cne.pt.ui.MainLayout;
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
	private final TextArea				textAreaCodificado;
	private final ComboBox< CodigoTTF >	cbCodigos;
	private final Anchor				anchorDownload;

	public CodificadorView()
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setAlignItems( Alignment.CENTER );
		verticalLayout.setJustifyContentMode( JustifyContentMode.CENTER );
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
		lstCodigos.add( new CodigoTTF( "Homografo", "homografo",
						UIUtils.FONTS_PATH + "homografo.ttf" ) );
		lstCodigos.add( new CodigoTTF( "LGP", "LGP", UIUtils.FONTS_PATH + "LGP.ttf" ) );
		lstCodigos.add( new CodigoTTF( "Chinês", "chines", UIUtils.FONTS_PATH + "chines.ttf" ) );
		//
		cbCodigos = new ComboBox<>( "Escolher o codigo" );
		cbCodigos.setWidthFull();
		cbCodigos.setItemLabelGenerator( e -> e.getName() );
		cbCodigos.setItems( lstCodigos );
		cbCodigos.setValue( lstCodigos.get( 0 ) );
		//
		textAreaCodificado = new TextArea();
		textAreaCodificado.addThemeVariants( TextAreaVariant.LUMO_ALIGN_CENTER );
		textAreaCodificado.getStyle().set( "font-size", "1.75rem" );
		textAreaCodificado.setClassName( lstCodigos.get( 0 ).getCSSUrl() );
		textAreaCodificado.setEnabled( false );
		//
		anchorDownload = new Anchor( lstCodigos.get( 0 ).getLink(), "" );
		anchorDownload.getElement().setAttribute( "download", true );
		anchorDownload.setWidthFull();
		Button button = UIUtils.createPrimaryButton( "Ficheiro .ttf", VaadinIcon.DOWNLOAD );
		button.setWidthFull();
		anchorDownload.add( button );
		cbCodigos.addValueChangeListener( event ->
		{
			UIUtils.setFontFamily( event.getValue(), textAreaCodificado );
			codificarTexto();
			anchorDownload.setHref( event.getValue().getLink() );
		} );
		HorizontalLayout optionsLayout = getOptionsLayout();
		//
		textAreaCodificado.setSizeFull();
		verticalLayout.add( textArea );
		verticalLayout.add( cbCodigos );
		verticalLayout.add( textAreaCodificado );
		verticalLayout.add( optionsLayout );
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
		textAreaCodificado.setValue( StringUtils.stripAccents( textArea.getValue() ) );
	}

	private HorizontalLayout getOptionsLayout()
	{
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing( true );
		horizontalLayout.setMargin( false );
		horizontalLayout.setPadding( true );
		horizontalLayout.setWidthFull();
		horizontalLayout.setAlignItems( Alignment.CENTER );
		horizontalLayout.setJustifyContentMode( JustifyContentMode.CENTER );
		// horizontalLayout.setSizeFull();
		Button btnIncreaseText = new Button( "Texto", VaadinIcon.PLUS.create() );
		btnIncreaseText.addClickListener( e -> textAreaCodificado.getStyle().set( "font-size", processTextSize( true ) ) );
		btnIncreaseText.setSizeFull();
		Button btnDecreaseText = new Button( "Texto", VaadinIcon.MINUS.create() );
		btnDecreaseText.addClickListener( e -> textAreaCodificado.getStyle().set( "font-size", processTextSize( false ) ) );
		btnDecreaseText.setSizeFull();
		horizontalLayout.add( cbCodigos, btnIncreaseText, btnDecreaseText, anchorDownload );
		return horizontalLayout;
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
