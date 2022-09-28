package de.ronnywalter.eve.frontend.views.corps;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.ronnywalter.eve.dto.CorpDTO;
import de.ronnywalter.eve.frontend.service.SecurityService;
import de.ronnywalter.eve.frontend.views.MainLayout;
import de.ronnywalter.eve.rest.CharacterService;
import de.ronnywalter.eve.rest.CorporationService;
import de.ronnywalter.eve.rest.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;

@PageTitle("Corporations")
@Route(value = "corps", layout = MainLayout.class)
@RolesAllowed("USER")
public class EveCorpView extends Div implements AfterNavigationObserver {

    @Autowired
    private CorporationService corporationService;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    private Grid<CorpDTO> grid = new Grid<>();

    @PostConstruct
    private void postConstruct() {
        setHeight("100%");
        addClassName("eve-corps-view");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(corporation -> createCard(corporation));

        Dialog dialog = new Dialog();

        VerticalLayout dialogLayout = createDialogLayout(dialog);
        dialog.add(dialogLayout);

        Button addCorp = new Button(new Icon(VaadinIcon.PLUS), buttonClickEvent -> dialog.open());

        VerticalLayout layout = new VerticalLayout(addCorp, grid);
        layout.setPadding(false);
        layout.setHeight("100%");
        add(layout);
    }

    private VerticalLayout createDialogLayout(Dialog dialog) {
        dialog.getElement().setAttribute("aria-label", "New corporation");

        H2 headline = new H2("New Corporation");
        headline.getStyle().set("margin-top", "0");


        TextField corpField = new TextField("Corp-Id");
        corpField.setValue("" + characterService.getCharacter(securityService.getAuthenticatedUser().getId()).getCorporationId());

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Add", e -> {
            CorpDTO corpDTO = new CorpDTO();

            corpDTO.setId(Integer.parseInt(corpField.getValue()));
            corpDTO.setUserId(securityService.getAuthenticatedUser().getId());
            corporationService.createCorporation(corpDTO);

            dialog.close();

        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton,
                saveButton);
        buttonLayout
                .setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle().set("margin-top", "var(--lumo-space-m)");

        VerticalLayout dialogLayout = new VerticalLayout(headline, corpField,
                buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private HorizontalLayout createCard(CorpDTO corporation) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Image image = new Image();
        if(corporation.getLogo() != null) {
            image.setSrc(corporation.getLogo());
        }
        image.setHeight("75px");
        image.setWidth("75px");
        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span name = new Span(corporation.getName());
        name.addClassName("name");

        header.add(name);

        Span corp = new Span(corporation.getTicker());
        corp.addClassName("corp");

        description.add(header, corp);

        Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
        deleteButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {


            }
        });

        card.add(image, description, deleteButton);

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
        grid.setItems(userService.getCorporationsForUser(securityService.getAuthenticatedUser().getId()));
    }

}
