package de.ronnywalter.eve.frontend.views.evecharacters;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.frontend.security.EveUserDetails;
import de.ronnywalter.eve.frontend.service.SecurityService;
import de.ronnywalter.eve.frontend.views.MainLayout;
import de.ronnywalter.eve.rest.CharacterService;
import de.ronnywalter.eve.rest.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Characters")
@Route(value = "chars", layout = MainLayout.class)
@RolesAllowed("USER")
@Slf4j
public class EveCharactersView extends Div implements AfterNavigationObserver {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    Grid<EveCharacterDTO> grid = new Grid<>();

    public EveCharactersView() {
        setHeight("100%");
        addClassName("eve-characters-view");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(eveCharacter -> createCard(eveCharacter));

        Anchor addLink = new Anchor("/eve-esi/authorization/eve-characters", new Button(new Icon(VaadinIcon.PLUS)));
        addLink.getElement().setAttribute("router-ignore", true);

        VerticalLayout layout = new VerticalLayout(addLink, grid);
        layout.setPadding(false);
        layout.setHeight("100%");

        add(layout);
    }

    private HorizontalLayout createCard(EveCharacterDTO eveCharacter) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Image image = new Image();
        if(eveCharacter.getPortrait256() != null) {
            image.setSrc(eveCharacter.getPortrait256());
        }
        image.setHeight("75px");
        image.setWidth("75px");

        Image corpImage = new Image();
        if(eveCharacter.getCorpLogo() != null) {
            corpImage.setSrc(eveCharacter.getCorpLogo());
        }
        corpImage.setHeight("75px");
        corpImage.setWidth("75px");

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span name = new Span(eveCharacter.getName());
        name.addClassName("name");

        header.add(name);

        Span corp = new Span(eveCharacter.getCorporationName() + " (" + eveCharacter.getCorporationTicker() + ")");
        corp.addClassName("corp");

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("actions");
        actions.setSpacing(false);
        actions.getThemeList().add("spacing-s");

        Icon likeIcon = VaadinIcon.HEART.create();
        likeIcon.addClassName("icon");
        Span securityStatus = new Span(String.valueOf(eveCharacter.getSecurityStatus()));

        actions.add(likeIcon, securityStatus);

        HorizontalLayout location = new HorizontalLayout();
        location.addClassName("location");
        location.setSpacing(false);
        location.getThemeList().add("spacing-s");
        if(eveCharacter.getLocationName() != null && eveCharacter.getLocationName().length() > 0) {
            location.add(new Span(eveCharacter.getLocationName()));
        } else {
            location.add(new Span(eveCharacter.getSolarSystemName()));
        }

        description.add(header, corp, location, actions);

        Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
        deleteButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {


            }
        });

        card.add(image, corpImage, description, deleteButton);

        card.addClickListener(new ComponentEventListener<ClickEvent<HorizontalLayout>>() {
            @Override
            public void onComponentEvent(ClickEvent<HorizontalLayout> horizontalLayoutClickEvent) {
                System.out.println(horizontalLayoutClickEvent.toString());
            }
        });

        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        EveUserDetails user = securityService.getAuthenticatedUser();
        List<EveCharacterDTO> characterDTOS = userService.getCharachtersForUser(user.getId());
        log.info("Got " + characterDTOS.size() + " from backend");
        grid.setItems(characterDTOS);
    }


}
