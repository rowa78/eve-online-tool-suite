package de.ronnywalter.eve.frontend.views.login;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Login")
@Route(value = "login")
@AnonymousAllowed
public class LoginViewEve extends FlexLayout {
    private static final String URL = "/oauth2/authorization/eve";

    /**
     * This methods gets the user into google sign in page.
     */
    public LoginViewEve() {
        Anchor googleLoginButton = new Anchor(URL, "Login with Eve");
        googleLoginButton.getElement().setAttribute("router-ignore", true);
        add(googleLoginButton);
        setSizeFull();
    }
}
