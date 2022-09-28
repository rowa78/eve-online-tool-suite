package de.ronnywalter.eve.frontend.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import de.ronnywalter.eve.frontend.security.*;
import de.ronnywalter.eve.frontend.service.SecurityService;
import de.ronnywalter.eve.frontend.views.about.AboutView;
import de.ronnywalter.eve.frontend.views.corps.EveCorpView;
import de.ronnywalter.eve.frontend.views.evecharacters.EveCharactersView;
import de.ronnywalter.eve.frontend.views.helloworld.HelloWorldView;
import org.springframework.context.ApplicationContext;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private SecurityService securityService;
    private ApplicationContext applicationContext;

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames("font-medium", "text-s");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {
            public LineAwesomeIcon(String lineawesomeClassnames) {
                // Use Lumo classnames for suitable font size and margin
                addClassNames("me-s", "text-l");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }

    }

    private H1 viewTitle;

    private AccessAnnotationChecker accessChecker;

    public MainLayout(SecurityService securityService, AccessAnnotationChecker accessChecker) {
        this.accessChecker = accessChecker;
        this.securityService = securityService;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Eve Online Tool Suite");
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("list-none", "m-0", "p-0");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //


                new MenuItemInfo("Characters", "la la-list", EveCharactersView.class), //


                new MenuItemInfo("Corporations", "la la-list", EveCorpView.class), //

                /*

                new MenuItemInfo("Transactions", "la la-list", TransactionsView.class), //

                new MenuItemInfo("Journal", "la la-list", JournalView.class), //

                new MenuItemInfo("My Orders", "la la-list", MyOrdersView.class), //


                 */
                new MenuItemInfo("Hello World", "la la-globe", HelloWorldView.class), //

                new MenuItemInfo("About", "la la-file", AboutView.class), //

        };
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

        if (securityService.isUserLoggedIn()) {
            EveUserDetails user = securityService.getAuthenticatedUser();
            if(user != null) {
                Avatar avatar = new Avatar("logged in", "https://images.evetech.net/characters/" + user.getId() + "/portrait?tenant=tranquility&size=128");
                avatar.addClassNames("me-xs");

                ContextMenu userMenu = new ContextMenu(avatar);
                userMenu.setOpenOnClick(true);
                userMenu.addItem("Logout", e -> {
                    securityService.logout();
                });

                Span userName = new Span(user.getName());
                userName.addClassNames("font-medium", "text-s", "text-secondary");

                layout.add(avatar, userName);
            }
        } else {
            Anchor loginLink = new Anchor("/eve-esi/authorization/eve-characters", new Image("images/eve-sso-login-black-small.png", "Login"));
            loginLink.getElement().setAttribute("router-ignore", true);
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
