package scouts.cne.pt.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import scouts.cne.pt.app.view.MailingListView;
import scouts.cne.pt.app.view.SIIELoginView;

/**
 * @author 62000465 2019-04-16
 *
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
	private static final String			LOGIN_PROCESSING_URL	= "/" + SIIELoginView.VIEW_NAME;
	private static final String			LOGIN_FAILURE_URL		= "/" + SIIELoginView.VIEW_NAME + "?error";
	private static final String			LOGIN_URL				= "/" + SIIELoginView.VIEW_NAME;
	private static final String			LOGOUT_SUCCESS_URL		= "/" + MailingListView.VIEW_NAME;
	private final UserDetailsService userDetailsService;

	@Autowired
	public SecurityConfiguration( UserDetailsService userDetailsService )
	{
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Registers our UserDetailsService and the password encoder to be used on login attempts.
	 */
	@Override
	protected void configure( AuthenticationManagerBuilder auth ) throws Exception
	{
		super.configure( auth );
		auth.userDetailsService( userDetailsService ); // .passwordEncoder(passwordEncoder);
	}

	/**
	 * Require login to access internal pages and configure login form.
	 */
	@Override
	protected void configure( HttpSecurity http ) throws Exception
	{
		// Not using Spring CSRF here to be able to use plain HTML for the login page
		http.csrf().disable()
						// Register our CustomRequestCache, that saves unauthorized access attempts, so
						// the user is redirected after login.
						.requestCache().requestCache( new CustomRequestCache() )
						// Restrict access to our application.
						.and().authorizeRequests()
						// Allow all flow internal requests.
						.requestMatchers( SecurityUtils::isFrameworkInternalRequest ).permitAll()
						// Allow all requests by logged in users.
						// .anyRequest().hasAnyAuthority( Role.getAllRoles() )
						// Configure the login page.
						.and().formLogin().loginPage( LOGIN_URL ).permitAll().loginProcessingUrl( LOGIN_PROCESSING_URL )
						.failureUrl( LOGIN_FAILURE_URL )
						// Register the success handler that redirects users to the page they last tried
						// to access
						.successHandler( new SavedRequestAwareAuthenticationSuccessHandler() )
						// Configure logout
						.and().logout().logoutSuccessUrl( LOGOUT_SUCCESS_URL );
	}
}
