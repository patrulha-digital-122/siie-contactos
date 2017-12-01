package scouts.cne.pt.listeners;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.UIScope;

import scouts.cne.pt.MyUI;
import scouts.cne.pt.google.GoogleAuthenticationBean;


@UIScope
public class LoginClickListener implements RequestHandler, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 8786455515877256884L;
	private MyUI ui;

	@Autowired
	GoogleAuthenticationBean googleAuthentication;

	public LoginClickListener(MyUI vaadinUI) {
		super();
		this.ui = vaadinUI;
	}

	@Override
	public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response)
			throws IOException {

		if ("/Callback".equals(request.getPathInfo())) {

			String acessToken = request.getParameter("code");
			System.out.println(acessToken);

			String CALLBACK_URI = "http://localhost:8080/Callback";
			GoogleTokenResponse googleTokenResponse;
			try {
				googleTokenResponse = googleAuthentication.getGoogleAuthorizationCodeFlow().newTokenRequest(acessToken)
						.setRedirectUri(CALLBACK_URI).execute();

				// Credential credential =
				// flow.createAndStoreCredential(googleTokenResponse, null);
				// acessToken = newTokenRequest.getCode();
				response.setStatus(HttpStatusCodes.STATUS_CODE_OK);

				ui.addDebugInfo("received Access Token: " + googleTokenResponse.getAccessToken());
				ui.showSecondPhaseOptions();

				VaadinSession.getCurrent().setAttribute("credential", googleTokenResponse.getAccessToken());

			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}

}
