package scouts.cne.pt.app.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * @author 62000465 2019-04-16
 *
 */
public class CustomRequestCache extends HttpSessionRequestCache
{
	/**
	 * {@inheritDoc}
	 *
	 * If the method is considered an internal request from the framework, we skip saving it.
	 * 
	 * @see SecurityUtils#isFrameworkInternalRequest(HttpServletRequest)
	 */
	@Override
	public void saveRequest( HttpServletRequest request, HttpServletResponse response )
	{
		if ( !SecurityUtils.isFrameworkInternalRequest( request ) )
		{
			super.saveRequest( request, response );
		}
	}
}
