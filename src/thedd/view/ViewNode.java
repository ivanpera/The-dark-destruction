package thedd.view;

import java.util.Objects;

/**
 * Enum representing the scene of application.
 */
public enum ViewNode {

    /**
     * Menu scene.
     */
    MENU("menu.fxml"),

    /**
     * New game scene.
     */
    NEW_GAME("newGame.fxml"),

    /**
     * Game over scene.
     */
    END_GAME("endGame.fxml"),

    /**
     * Game scene.
     */
    GAME("game.fxml"),

    /**
     * The top pane.
     */
    TOP_PANE("gameContent_form.fxml"),

    /**
     * Inventory overview scene.
     */
    INVENTORY("inventory.fxml"),

    /**
     * Statistics overview scene.
     */
    STATISTICS("statistics.fxml"),

    /**
     * Statistics overview scene.
     */
    ACTION_SELECTOR("actionSelector.fxml");

    /**
     * Use of the char '/' because it's used by istructions that required that type
     * of char, doesn't matter if the application is used on linux, windows or os.
     */
    private static final String FXML_PATH = "scene/";

    private final String nameOfFxml;

    /**
     * Enum constructor.
     * 
     * @param nameOfFxml of fxml scene
     */
    ViewNode(final String nameOfFxml) {
        Objects.requireNonNull(nameOfFxml);
        this.nameOfFxml = nameOfFxml;
    }

    /**
     * Getter of fxml nameOfFxml with unix separator.
     * 
     * @return fxml nameOfFxml
     */
    public String getFXMLPath() {
        return FXML_PATH + this.nameOfFxml;
    }
}
