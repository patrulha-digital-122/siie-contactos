package scouts.cne.pt.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.Lumo;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.LocalStorage;
import scouts.cne.pt.ui.components.navigation.bar.AppBar;
import scouts.cne.pt.ui.components.navigation.bar.TabBar;
import scouts.cne.pt.ui.components.navigation.drawer.NaviDrawer;
import scouts.cne.pt.ui.components.navigation.drawer.NaviItem;
import scouts.cne.pt.ui.components.navigation.drawer.NaviMenu;
import scouts.cne.pt.ui.events.internal.AniversariosEventConfigurations;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.util.css.Overflow;
import scouts.cne.pt.ui.views.Home;
import scouts.cne.pt.ui.views.admin.GoogleAdminView;
import scouts.cne.pt.ui.views.admin.SIIELoginView;
import scouts.cne.pt.ui.views.elementos.AniversarioListView;
import scouts.cne.pt.ui.views.elementos.DiagnosticoListView;
import scouts.cne.pt.ui.views.elementos.ImportContactsListView;
import scouts.cne.pt.ui.views.elementos.MailingListView;
import scouts.cne.pt.ui.views.utils.CodificadorView;
import scouts.cne.pt.utils.UIUtils;

@CssImport( value = "./styles/components/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme" )
@CssImport( value = "./styles/components/floating-action-button.css", themeFor = "vaadin-button" )
@CssImport( value = "./styles/components/grid.css", themeFor = "vaadin-grid" )
@CssImport( "./styles/lumo/border-radius.css" )
@CssImport( "./styles/lumo/icon-size.css" )
@CssImport( "./styles/lumo/margin.css" )
@CssImport( "./styles/lumo/padding.css" )
@CssImport( "./styles/lumo/shadow.css" )
@CssImport( "./styles/lumo/spacing.css" )
@CssImport( "./styles/lumo/typography.css" )
@CssImport( "./styles/misc/box-shadow-borders.css" )
@CssImport( value = "./styles/styles.css", include = "lumo-badge" )
@Meta( name = "google-site-verification", content = "FOqGrvVOczGenSzPckQdRiNI8Qv_RJWd8PteDcezCKk" )
@JsModule( "@vaadin/vaadin-lumo-styles/badge" )
@PWA(	name = "CNhEfe",
		shortName = "CNhEfe",
		startPath = Home.VIEW_NAME,
		backgroundColor = "#227aef",
		themeColor = "#227aef",
		offlinePath = "offline-page.html",
		offlineResources =
		{ "images/offline-login-banner.webp", "images/logo.webp" },
		enableInstallPrompt = true,
		display = "standalone",
		iconPath = "images/avatar.jpg",
		description = "Esta app permite simplificar a vida dos dirigentes" )
@Viewport( "width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes" )
@PreserveOnRefresh
@Push
public class MainLayout extends FlexBoxLayout implements RouterLayout, PageConfigurator, AfterNavigationObserver, HasLogger
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 308418390376170981L;
	private static final Logger		log					= LoggerFactory.getLogger( MainLayout.class );
	private static final String		CLASS_NAME			= "root";
	@Autowired
	private SIIEService				siieService;
	@Autowired
	private GoogleAuthentication	googleAuthentication;
	private Div						appHeaderOuter;
	private FlexBoxLayout			row;
	private NaviDrawer				naviDrawer;
	private FlexBoxLayout			column;
	private Div						appHeaderInner;
	private FlexBoxLayout			viewContainer;
	private Div						appFooterInner;
	private Div						appFooterOuter;
	private TabBar					tabBar;
	private boolean					navigationTabs		= false;
	private AppBar					appBar;
	private final LocalStorage		localStorage		= new LocalStorage();
	protected Registration			broadcasterRegistration;

	public MainLayout()
	{
		VaadinSession.getCurrent().setErrorHandler( ( ErrorHandler ) errorEvent ->
		{
			getLogger().error( "Uncaught UI exception", errorEvent.getThrowable() );
			Notification.show( "Pedimos desculpa mas houve um erro." );
		} );
		addClassName( CLASS_NAME );
		setFlexDirection( FlexDirection.COLUMN );
		setSizeFull();
		// Initialise the UI building blocks
		initStructure();
		// Populate the navigation drawer
		initNaviItems();
		// Configure the headers and footers (optional)
		initHeadersAndFooters();
		add( localStorage );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );

		initTestData( attachEvent );
	}

	/**
	 * Initialise the required components and containers.
	 */
	private void initStructure()
	{
		naviDrawer = new NaviDrawer();
		viewContainer = new FlexBoxLayout();
		viewContainer.addClassName( CLASS_NAME + "__view-container" );
		viewContainer.setOverflow( Overflow.HIDDEN );
		column = new FlexBoxLayout( viewContainer );
		column.addClassName( CLASS_NAME + "__column" );
		column.setFlexDirection( FlexDirection.COLUMN );
		column.setFlexGrow( 1, viewContainer );
		column.setOverflow( Overflow.HIDDEN );
		row = new FlexBoxLayout( naviDrawer, column );
		row.addClassName( CLASS_NAME + "__row" );
		row.setFlexGrow( 1, column );
		row.setOverflow( Overflow.HIDDEN );
		add( row );
		setFlexGrow( 1, row );
	}

	/**
	 * Initialise the navigation items.
	 */
	private void initNaviItems()
	{
		NaviMenu menu = naviDrawer.getMenu();
		menu.addNaviItem( Home.VIEW_ICON, Home.VIEW_DISPLAY_NAME, Home.class );
		NaviItem elementos = menu.addNaviItem( VaadinIcon.USERS, "Elementos", null );
		menu.addNaviItem( elementos, MailingListView.VIEW_DISPLAY_NAME, MailingListView.class );
		menu.addNaviItem( elementos, AniversarioListView.VIEW_DISPLAY_NAME, AniversarioListView.class );
		menu.addNaviItem( elementos, ImportContactsListView.VIEW_DISPLAY_NAME, ImportContactsListView.class );
		NaviItem utils = menu.addNaviItem( VaadinIcon.TOOLS, "Ferramentas", null );
		menu.addNaviItem( utils, DiagnosticoListView.VIEW_DISPLAY_NAME, DiagnosticoListView.class );
		menu.addNaviItem( utils, CodificadorView.VIEW_DISPLAY_NAME, CodificadorView.class );
		NaviItem admin = menu.addNaviItem( VaadinIcon.COGS, "Administração", null );
		menu.addNaviItem( admin, SIIELoginView.VIEW_DISPLAY_NAME, SIIELoginView.class );
		menu.addNaviItem( admin, GoogleAdminView.VIEW_DISPLAY_NAME, GoogleAdminView.class );
		// menu.addNaviItem( personnel, "Managers", Managers.class );
	}

	/**
	 * Configure the app's inner and outer headers and footers.
	 */
	private void initHeadersAndFooters()
	{
		// setAppHeaderOuter();
		// setAppFooterInner( UIUtils.createH6Label( "powered by: patrulha.digital.122@escutismo.pt" ) );
		// setAppFooterOuter();
		// Default inner header setup:
		// - When using tabbed navigation the view title, user avatar and main menu button will appear in the TabBar.
		// - When tabbed navigation is turned off they appear in the AppBar.
		appBar = new AppBar( "" );
		// Tabbed navigation
		if ( navigationTabs )
		{
			tabBar = new TabBar();
			UIUtils.setTheme( Lumo.DARK, tabBar );
			// Shift-click to add a new tab
			for ( NaviItem item : naviDrawer.getMenu().getNaviItems() )
			{
				item.addClickListener( e ->
				{
					if ( e.getButton() == 0 && e.isShiftKey() )
					{
						tabBar.setSelectedTab( tabBar.addClosableTab( item.getText(), item.getNavigationTarget() ) );
					}
				} );
			}
			appBar.getAvatar().setVisible( false );
			setAppHeaderInner( tabBar, appBar );
			// Default navigation
		}
		else
		{
			UIUtils.setTheme( Lumo.DARK, appBar );
			setAppHeaderInner( appBar );
		}
	}

	private void setAppHeaderOuter( Component... components )
	{
		if ( appHeaderOuter == null )
		{
			appHeaderOuter = new Div();
			appHeaderOuter.addClassName( "app-header-outer" );
			getElement().insertChild( 0, appHeaderOuter.getElement() );
		}
		appHeaderOuter.removeAll();
		appHeaderOuter.add( components );
	}

	private void setAppHeaderInner( Component... components )
	{
		if ( appHeaderInner == null )
		{
			appHeaderInner = new Div();
			appHeaderInner.addClassName( "app-header-inner" );
			column.getElement().insertChild( 0, appHeaderInner.getElement() );
		}
		appHeaderInner.removeAll();
		appHeaderInner.add( components );
	}

	private void setAppFooterInner( Component... components )
	{
		if ( appFooterInner == null )
		{
			appFooterInner = new Div();
			appFooterInner.addClassName( "app-footer-inner" );
			column.getElement().insertChild( column.getElement().getChildCount(), appFooterInner.getElement() );
		}
		appFooterInner.removeAll();
		appFooterInner.add( components );
	}

	private void setAppFooterOuter( Component... components )
	{
		if ( appFooterOuter == null )
		{
			appFooterOuter = new Div();
			appFooterOuter.addClassName( "app-footer-outer" );
			getElement().insertChild( getElement().getChildCount(), appFooterOuter.getElement() );
		}
		appFooterOuter.removeAll();
		appFooterOuter.add( components );
	}

	/**
	 * The <b>initTestData</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-10-31
	 * @param attachEvent
	 */
	private void initTestData( AttachEvent attachEvent )
	{
		localStorage.addInitListener( ls ->
		{
			String siieUser = localStorage.getString( LocalStorage.SIIE_USERNAME );
			String siiePassword = localStorage.getString( LocalStorage.SIIE_PASSWORD );
			if ( StringUtils.isNoneEmpty( siieUser, siiePassword ) )
			{
				try
				{
					if ( !siieService.isAuthenticated() )
					{
						getLogger().info( "SIIE auto-login START :: localStorage" );
						siieService.authenticateSIIE( siieUser, siiePassword );
						siieService.updateFullSIIE();
						getLogger().info( "SIIE auto-login END :: localStorage" );
						showInfo( "Bem vindo " + siieService.getElementoByNIN( siieUser ).get().getNome() +
							". Dados do SIIE actualizados com sucesso!" );
						String string = localStorage.getString( LocalStorage.ANIVERSARIOS_CONFIG );
						if ( StringUtils.isNotEmpty( string ) )
						{
							try
							{
								AniversariosEventConfigurations aniversariosEventConfigurations =
												new ObjectMapper().readValue( string, AniversariosEventConfigurations.class );
								if ( aniversariosEventConfigurations.isReceberNotificacoes() )
								{
									attachEvent.getUI().accessSynchronously( () ->
									{
										// VaadinService.getCurrent().
										attachEvent.getUI().getPage().executeJs( "Notification.requestPermission();" );
									} );
									List< SIIEElemento > allElementos =
													aniversariosEventConfigurations.isElementosActivos() ? siieService.getElementosActivos()
																	: siieService.getAllElementos();
									List< SIIEElemento > lstElementos = allElementos.stream().filter( ( p ) ->
									{
										if ( p.getDatanascimento() != null )
										{
											return p.getDatanascimento().getDayOfYear() == LocalDate.now().getDayOfYear();
										}
										return false;
									} ).collect( Collectors.toList() );

									if ( !lstElementos.isEmpty() && getUI().isPresent() )
									{
										if ( lstElementos.size() > 3 )
										{
											String message = "Hoje " + lstElementos.size() + " fazem anos.";
											getUI().get().accessSynchronously( () ->
											{
												String strScript =
																"navigator.serviceWorker.ready.then(function(registration) { registration.showNotification('Aniversários', { body: '" +
																	message +
																	"', icon: '../images/logo.webp', vibrate: [500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500] }); });";
												// VaadinService.getCurrent().
												getUI().get().getPage().executeJs( strScript );
											} );
										}
										else
										{
											lstElementos.forEach( p ->
											{
												String message = p.getNome() + " faz hoje " + p.getIdade().intValue() + " anos.";
												getUI().get().accessSynchronously( () ->
												{
													String strScript =
																	"navigator.serviceWorker.ready.then(function(registration) { registration.showNotification('Aniversários', { body: '" +
																		message +
																		"', icon: '../images/logo.webp', vibrate: [500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500] }); });";
													// VaadinService.getCurrent().
													getUI().get().getPage().executeJs( strScript );
												} );
											} );
										}
									}
								}
							}
							catch ( Exception e )
							{
								printError( e );
							}
						}

					}
				}
				catch ( Exception e )
				{
					attachEvent.getUI().access( () -> showError( e ) );
				}
			}

		} );
	}

	@Override
	public void configurePage( InitialPageSettings settings )
	{
		settings.addMetaTag( "apple-mobile-web-app-capable", "yes" );
		settings.addMetaTag( "apple-mobile-web-app-status-bar-style", "black" );
		settings.addFavIcon( "icon", "frontend/images/favicons/favicon.ico", "256x256" );
	}

	@Override
	public void showRouterLayoutContent( HasElement content )
	{
		this.viewContainer.getElement().appendChild( content.getElement() );
	}

	public NaviDrawer getNaviDrawer()
	{
		return naviDrawer;
	}

	public static MainLayout get()
	{
		return ( MainLayout ) UI.getCurrent().getChildren().filter( component -> component.getClass() == MainLayout.class ).findFirst().get();
	}

	/**
	 * Getter for localStorage
	 * 
	 * @author 62000465 2019-12-11
	 * @return the localStorage {@link LocalStorage}
	 */
	public LocalStorage getLocalStorage()
	{
		return localStorage;
	}

	public AppBar getAppBar()
	{
		return appBar;
	}

	@Override
	public void afterNavigation( AfterNavigationEvent event )
	{
		if ( navigationTabs )
		{
			afterNavigationWithTabs( event );
		}
		else
		{
			afterNavigationWithoutTabs( event );
		}
	}

	private void afterNavigationWithTabs( AfterNavigationEvent e )
	{
		NaviItem active = getActiveItem( e );
		if ( active == null )
		{
			if ( tabBar.getTabCount() == 0 )
			{
				tabBar.addClosableTab( "", Home.class );
			}
		}
		else
		{
			if ( tabBar.getTabCount() > 0 )
			{
				tabBar.updateSelectedTab( active.getText(), active.getNavigationTarget() );
			}
			else
			{
				tabBar.addClosableTab( active.getText(), active.getNavigationTarget() );
			}
		}
		appBar.getMenuIcon().setVisible( false );
	}

	private NaviItem getActiveItem( AfterNavigationEvent e )
	{
		for ( NaviItem item : naviDrawer.getMenu().getNaviItems() )
		{
			if ( item.isHighlighted( e ) )
			{
				return item;
			}
		}
		return null;
	}

	private void afterNavigationWithoutTabs( AfterNavigationEvent e )
	{
		NaviItem active = getActiveItem( e );
		if ( active != null )
		{
			getAppBar().setTitle( active.getText() );
		}
	}
}
