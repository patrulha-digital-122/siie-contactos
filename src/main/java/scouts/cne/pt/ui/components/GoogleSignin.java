package scouts.cne.pt.ui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.shared.Registration;
import scouts.cne.pt.model.google.GoogleAuthInfo;
import scouts.cne.pt.ui.events.internal.InternalSignInEvent;
import scouts.cne.pt.ui.events.internal.InternalSignOutEvent;

/**
 * @author 62000465 2019-10-23
 *
 */
@Tag( "google-signin" )
@NpmPackage( value = "@google-web-components/google-signin", version = "3.0.1" )
@JsModule( "@google-web-components/google-signin/google-signin.js" )
public class GoogleSignin extends Component
{
	/**
	 * 
	 */
	private static final long					serialVersionUID	= 3917985177542119581L;
	private Brand								brand;
	private Width								width;
	private Height								height;
	private Theme								theme;
	private boolean								autoLogout			= true;
	private List< Consumer< GoogleAuthInfo > >	loginListeners;
	private List< Runnable >					logoutListeners;

	public enum Brand
	{
		GOOGLE( "google" ),
		GOOGLEPLUS( "google-plus" );
		private String value;

		Brand( String value )
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public enum Width
	{
		WIDE( "wide" ),
		ICON( "inconOnly" );
		private String value;

		Width( String value )
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public enum Height
	{
		TALL( "tall" ),
		STANDARD( "standard" ),
		SHORT( "short" );
		private String value;

		Height( String value )
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public enum Theme
	{
		DARK( "dark" ),
		LIGHT( "light" );
		private String value;

		Theme( String value )
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public GoogleSignin()
	{
		// init listeners
		this.loginListeners = new ArrayList<>();
		this.logoutListeners = new ArrayList<>();
		setWidth( Width.WIDE );
		setHeight( Height.STANDARD );
		setBrand( Brand.GOOGLE );
		setTheme( Theme.DARK );
		// add base-listener for sign-in events
		addListener( InternalSignInEvent.class, this::handleLogin );
		addListener( InternalSignOutEvent.class, this::handleLogout );
	}

	public void setClientId( String clientId )
	{
		// Set default values
		getElement().setProperty( "clientId", clientId );
	}

	private void handleLogin( InternalSignInEvent event )
	{
		PendingJavaScriptResult callJsFunction =
						getElement().executeJs( "return gapi.auth2.getAuthInstance()['currentUser'].get()" );
		callJsFunction.then( e ->
		{
			try
			{
				final ObjectMapper mapper = new ObjectMapper();
				final GoogleAuthInfo googleAuthInfo = mapper.readValue( e.toJson(), GoogleAuthInfo.class );
				if ( autoLogout )
				{
					logout();
				}
				fireEvent( googleAuthInfo );
			}
			catch ( Exception ex )
			{
				ex.printStackTrace();
			}
		} );
	}

	private void fireEvent( GoogleAuthInfo googleAuthInfo )
	{
		loginListeners.forEach( consumer -> consumer.accept( googleAuthInfo ) );
	}

	private void handleLogout( InternalSignOutEvent event )
	{
		// omit logout events, if auto-logout is on
		if ( !autoLogout )
		{
			logoutListeners.forEach( Runnable::run );
		}
	}

	/**
	 * Add listener for login action
	 *
	 * @param consumer that consumes the {@link UserLoginEvent} which has all user info
	 * @return hook for removing the listener
	 */
	public Registration addLoginListener( Consumer< GoogleAuthInfo > consumer )
	{
		loginListeners.add( consumer );
		return () -> loginListeners.remove( consumer );
	}

	/**
	 * Add listener for logout action NOTE: logout listeners are NOT informed of logout action, if isAutoLogout is true
	 *
	 * @param action Action to perform if logout is triggered
	 * @return hook for removing the listener
	 */
	public Registration addLogoutListener( Runnable action )
	{
		logoutListeners.add( action );
		return () -> logoutListeners.remove( action );
	}

	/**
	 * Logout current google login
	 */
	public void logout()
	{
		getElement().callJsFunction( "signOut" );
	}

	/**
	 * 
	 * The <b>login</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-10-31
	 */
	public void login()
	{
		getElement().callJsFunction( "signIn" );
		getElement().executeJs( "signIn" );
	}

	/**
	 * Tells if auto-logout is active:
	 *
	 * if auto-logout is on then the google login is closed after successful login.
	 *
	 * @return
	 */
	public boolean isAutoLogout()
	{
		return autoLogout;
	}

	public void setAutoLogout( boolean autoLogout )
	{
		this.autoLogout = autoLogout;
	}

	/**
	 * The brand being used for logo and styling.
	 *
	 * @default 'google'
	 */
	public Brand getBrand()
	{
		return brand;
	}

	public void setBrand( Brand brand )
	{
		this.brand = brand;
		getElement().setProperty( "brand", brand.getValue() );
	}

	/**
	 * The width to use for the button.
	 *
	 * Available options: iconOnly, standard, wide.
	 */
	public Width getWidth()
	{
		return width;
	}

	public void setWidth( Width width )
	{
		this.width = width;
		getElement().setProperty( "width", width.getValue() );
	}

	/**
	 * The height to use for the button.
	 *
	 * Available options: short, standard, tall.
	 *
	 * @type {string}
	 */
	public Height getHeight()
	{
		return height;
	}

	public void setHeight( Height height )
	{
		this.height = height;
		getElement().setProperty( "height", height.getValue() );
	}

	/**
	 * The theme to use for the button.
	 *
	 * Available options: light, dark.
	 *
	 * @default 'dark'
	 */
	public Theme getTheme()
	{
		return theme;
	}

	public void setTheme( Theme theme )
	{
		this.theme = theme;
		getElement().setProperty( "theme", theme.getValue() );
	}

	/**
	 * App package name for android over-the-air installs. See the relevant
	 * [docs](https://developers.google.com/+/web/signin/android-app-installs)
	 */
	public String getAppPackageName()
	{
		return getElement().getProperty( "appPackageName" );
	}

	public void setAppPackageName( String value )
	{
		getElement().setProperty( "appPackageName", value );
	}

	/**
	 * The cookie policy defines what URIs have access to the session cookie remembering the user's sign-in state. See
	 * the relevant [docs](https://developers.google.com/+/web/signin/reference#determining_a_value_for_cookie_policy)
	 * for more information.
	 *
	 * @default 'single_host_origin'
	 */
	public String getCookiePolicy()
	{
		return getElement().getProperty( "cookiePolicy" );
	}

	public void setCookiePolicy( String value )
	{
		getElement().setProperty( "cookiePolicy", value );
	}

	/**
	 * By default the ripple expands to fill the button. Set this to true to constrain the ripple to a circle within the
	 * button.
	 */
	public boolean getFill()
	{
		return getElement().getProperty( "fill", true );
	}

	public void setFill( boolean value )
	{
		getElement().setProperty( "fill", value );
	}

	/**
	 * An optional label for the button for additional permissions.
	 */
	public String getLabelAdditional()
	{
		return getElement().getProperty( "labelAdditional" );
	}

	public void setLabelAdditional( String value )
	{
		getElement().setProperty( "labelAdditional", value );
	}

	/**
	 * An optional label for the sign-in button.
	 */
	public String getLabelSignin()
	{
		return getElement().getProperty( "labelSignin" );
	}

	public void setLabelSignin( String value )
	{
		getElement().setProperty( "labelSignin", value );
	}

	/**
	 * An optional label for the sign-out button.
	 */
	public String getLabelSignout()
	{
		return getElement().getProperty( "labelSignout" );
	}

	public void setLabelSignout( String value )
	{
		getElement().setProperty( "labelSignout", value );
	}

	/**
	 * If true, the button will be styled with a shadow.
	 */
	public boolean getRaised()
	{
		return getElement().getProperty( "raised", false );
	}

	public void setRaised( boolean value )
	{
		getElement().setProperty( "raised", value );
	}

	/**
	 * The scopes to provide access to (e.g https://www.googleapis.com/auth/drive) and should be space-delimited.
	 */
	public String getScopes()
	{
		return getElement().getProperty( "scopes" );
	}

	public void setScopes( String value )
	{
		getElement().setProperty( "scopes", value );
	}

	public void setOpenidPrompt( String value )
	{
		getElement().setProperty( "openidPrompt", value );
	}

	public Boolean isAuthorized()
	{
		return getElement().getProperty( "isAuthorized", false );
	}

	public Boolean isSignedIn()
	{
		return getElement().getProperty( "signedIn", false );
	}
}