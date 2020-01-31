package scouts.cne.pt.ui.views.elementos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.types.SIIESeccao;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.grids.ElementosGrid;
import scouts.cne.pt.ui.events.google.FinishSIIEUpdate;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Vertical;
import scouts.cne.pt.ui.util.BoxShadowBorders;
import scouts.cne.pt.ui.util.LumoStyles;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.views.HasSIIELoginUrl;
import scouts.cne.pt.utils.Broadcaster;

@Route( value = DiagnosticoListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( DiagnosticoListView.VIEW_DISPLAY_NAME )
public class DiagnosticoListView extends HasSIIELoginUrl
{
	private static final long			serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "diagnostico";
	public static final String			VIEW_DISPLAY_NAME	= "Diagnóstico";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.DOCTOR;
	@Autowired
	private SIIEService					siieService;
	private final Div					content				= new Div();
	private final List< SIIEElemento >	lstElementos		= new ArrayList<>();
	private final Accordion				accordion			= new Accordion();
	private UI							ui;
	private ProgressBar					progressBar			= new ProgressBar( 0, 6, 0 );


	public DiagnosticoListView()
	{
		setId( VIEW_NAME );
		content.addClassNames( BoxShadowBorders.BOTTOM, LumoStyles.Padding.Bottom.L );
		content.add( accordion );
		progressBar.setWidthFull();

		FlexBoxLayout flexBoxLayout = new FlexBoxLayout( progressBar, content );
		flexBoxLayout.setFlexDirection( FlexDirection.COLUMN );
		flexBoxLayout.setMargin( Horizontal.AUTO );
		flexBoxLayout.setPadding( Horizontal.RESPONSIVE_L, Vertical.L );
		flexBoxLayout.setSizeFull();
		setViewContent( flexBoxLayout );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );

		ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register( newMessage ->
		{
			if ( newMessage instanceof FinishSIIEUpdate )
			{
				FinishSIIEUpdate finishSIIEUpdate = ( FinishSIIEUpdate ) newMessage;
				getLogger().info( "Dados do SIIE actualizados em :: " + finishSIIEUpdate.getDuration().toString() );
			}
			ui.access( () -> updateContent() );
		} );
		updateContent();
	}

	/**
	 * The <b>updateContent</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-21
	 */
	private void updateContent()
	{
		content.removeAll();
		progressBar.setValue( 0 );
		new Thread( () ->
		{
			lstElementos.clear();
			lstElementos.addAll( siieService.getElementosActivos() );
			ui.access( () ->
			{
				progressBar.setValue( 1 );
				lstElementos.forEach( p -> p.getAdditionalInfo().clear() );
				checkEmails();
			} );
			ui.access( () ->
			{
				progressBar.setValue( 2 );
				lstElementos.forEach( p -> p.getAdditionalInfo().clear() );
				checkTelefone();
			} );
			ui.access( () ->
			{
				progressBar.setValue( 5 );
				lstElementos.forEach( p -> p.getAdditionalInfo().clear() );
				checkDadosPessoais();
				accordion.close();
				progressBar.setValue( 6 );
			} );
		} ).start();
	}

	/**
	 * The <b>checkEmails</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-07
	 */
	private void checkEmails()
	{
		List< SIIEElemento > collect = lstElementos.stream().filter( ( p ) ->
		{
			boolean isOk = true;
			if ( StringUtils.isAllBlank( p.getEmail(), p.getPaiemail(), p.getMaeemail() ) )
			{
				p.getAdditionalInfo().add( "Nenhum email definido." );
				return true;
			}
			if ( p.getSiglaseccao().equals( SIIESeccao.D ) && StringUtils.isBlank( p.getEmail() ) )
			{
				p.getAdditionalInfo().add( "Dirigente sem email pessoal definido." );
				isOk = false;
			}
			if ( StringUtils.isNotBlank( p.getEmail() ) && !isValidEmailAddress( p.getEmail() ) )
			{
				p.getAdditionalInfo().add( "Email pessoal incorrecto :: " + p.getEmail() );
				isOk = false;
			}

			if ( !p.getSiglaseccao().equals( SIIESeccao.D ) )
			{
				if ( StringUtils.isNotBlank( p.getMaeemail() ) && !isValidEmailAddress( p.getMaeemail() ) )
				{
					p.getAdditionalInfo().add( "Email Mãe incorrecto :: " + p.getMaeemail() );
					isOk = false;
				}
				if ( StringUtils.isNotBlank( p.getPaiemail() ) && !isValidEmailAddress( p.getPaiemail() ) )
				{
					p.getAdditionalInfo().add( "Email Pai incorrecto :: " + p.getPaiemail() );
					isOk = false;
				}
			}
			return !isOk;
		} ).collect( Collectors.toList() );

		if ( !collect.isEmpty() )
		{
			createGrid( "Elementos com problemas no email:", collect );
		}
	}

	private void checkTelefone()
	{
		List< SIIEElemento > collect = lstElementos.stream().filter( ( p ) ->
		{
			boolean isOk = true;
			if ( StringUtils.isAllBlank( p.getTelefone(), p.getTelemovel(), p.getPaitelefone(), p.getMaetelefone() ) )
			{
				p.getAdditionalInfo().add( "Nenhum telefone/telemóvel definido." );
				return true;
			}
			if ( p.getSiglaseccao().equals( SIIESeccao.D ) && StringUtils.isAllBlank( p.getTelefone(), p.getTelemovel() ) )
			{
				p.getAdditionalInfo().add( "Dirigente sem telefone/telemóvel pessoal definido." );
				isOk = false;
			}
			if ( !p.getSiglaseccao().equals( SIIESeccao.D ) )
			{
				if ( StringUtils.isBlank( p.getMaetelefone() ) && StringUtils.isBlank( p.getMaetelefone() ) )
				{
					p.getAdditionalInfo().add( "Não existe telefone/telemóvel dos pais." );
					isOk = false;
				}
			}
			return !isOk;
		} ).collect( Collectors.toList() );

		if ( !collect.isEmpty() )
		{
			createGrid( "Elementos com problemas no telefone/telemóvel:", collect );
		}

	}


	private void checkDadosPessoais()
	{
		List< SIIEElemento > collect = lstElementos.stream().filter( ( p ) ->
		{
			boolean isOk = true;
			if ( StringUtils.isBlank( p.getSns() ) )
			{
				p.getAdditionalInfo().add( "Falta número do Cartão de Saúde." );
				isOk = false;
			}
			if ( StringUtils.isBlank( p.getCc() ) )
			{
				p.getAdditionalInfo().add( "Falta número do Cartão do Cidadão." );
				isOk = false;
			}
			if ( StringUtils.isBlank( p.getNif() ) )
			{
				p.getAdditionalInfo().add( "Falta NIF!" );
				isOk = false;
			}
			if ( !validaNif( p.getNif() ) )
			{
				p.getAdditionalInfo().add( "NIF inválido! :: " + p.getNif() );
				isOk = false;
			}
			return !isOk;
		} ).collect( Collectors.toList() );

		if ( !collect.isEmpty() )
		{
			createGrid( "Elementos com problemas nos dados pessoais:", collect );
		}
	}

	/**
	 * The <b>createGrid</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-21
	 * @param collect
	 */
	private void createGrid( String strText, List< SIIEElemento > collect )
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin( false );
		verticalLayout.setPadding( false );
		verticalLayout.setSizeFull();

		List< SIIEElemento > list = new ArrayList< SIIEElemento >();
		for ( SIIEElemento siieElemento : collect )
		{
			SIIEElemento siieElemento2 = new SIIEElemento();
			siieElemento2.merge( siieElemento );
			siieElemento2.getAdditionalInfo().addAll( siieElemento.getAdditionalInfo() );
			list.add( siieElemento2 );
		}
		ElementosGrid grid = new ElementosGrid( true, list );
		grid.removeColumn( grid.getSituacaoColumn() );
		grid.useAdditionalInfoColumn();
		grid.setSizeUndefined();

		verticalLayout.add( grid );

		accordion.add( strText, new Div( grid ) ).addThemeVariants( DetailsVariant.FILLED );

		content.add( accordion );
	}

	/*
	 * As regras para a validação do NIF são:
	 * 
	 * Tem de ter 9 dígitos; O primeiro dígito tem de ser 1, 2, 5, 6, 8 ou 9; (Esta é a informação que circula na maior
	 * parte dos fóruns da internet, mas a realidade é que o 3 está reservado para uso de particulares assim que os
	 * começados por 2 se esgotarem e o 4 e 7 são utilizados em casos especiais, pelo que, por omissão, a nossa função
	 * ignora esta validação) O dígito de controlo (último digíto do NIF) é obtido da seguinte forma: 9*d1 + 8*d2 + 7*d3
	 * + 6*d4 + 5*d5 + 4*d6 + 3*d7 + 2*d8 + 1*d9 (em que d1 a d9 são os 9 dígitos do NIF); Esta soma tem de ser múltiplo
	 * de 11 (quando divídida por 11 dar 0); Subtraír o resto da divisão da soma por 11 a 11; Se o resultado for 10, é
	 * assumído o algarismo 0; [in webdados]
	 */
	private boolean validaNif( String nif )
	{
		try
		{
			final int max = 9;
			if ( !nif.matches( "[0-9]+" ) || nif.length() != max )
			{
				return false;
			}
			int checkSum = 0;
			// calcula a soma de controlo
			for ( int i = 0; i < max - 1; i++ )
			{
				checkSum += ( nif.charAt( i ) - '0' ) * ( max - i );
			}
			int checkDigit = 11 - ( checkSum % 11 );
			if ( checkDigit >= 10 )
			{
				checkDigit = 0;
			}
			return checkDigit == nif.charAt( max - 1 ) - '0';
		}
		catch ( Exception e )
		{
			return false;
		}
		finally
		{
		}
	}

	private boolean isValidEmailAddress( String email )
	{
		boolean result = true;
		try
		{
			InternetAddress emailAddr = new InternetAddress( email );
			emailAddr.validate();
		}
		catch ( Exception ex )
		{
			result = false;
		}
		return result;
	}
}
