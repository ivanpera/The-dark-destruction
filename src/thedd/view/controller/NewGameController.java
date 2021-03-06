package thedd.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import thedd.view.ApplicationViewState;
import thedd.view.extensions.AdaptiveFontLabel;
import thedd.view.imageloader.DirectoryPicker;
import thedd.view.imageloader.ImageLoaderImpl;

/**
 * View controller of the new game scene.
 */
public class NewGameController extends ViewNodeControllerImpl {

    private static final String TITLE_IMAGE_NAME = "game_settings";
    private static final double TITLE_HEIGHT_PERC = 1.0;
    private static final double TITLE_WIDTH_PERC = 1.0;
    private static final String ERROR_UNVALIDVALUE = "Should be greater than 0.";
    private static final String ERROR_UNPRESENTVALUE = "Should be present.";
    private static final String ERROR_VOID = " ";

    @FXML
    private TextField playerNameTextField;

    @FXML
    private TextField numberOfRoomsTextField;

    @FXML
    private TextField numberOfFloorsTextField;

    @FXML
    private AdaptiveFontLabel errorNumberOfRooms;

    @FXML
    private AdaptiveFontLabel errorNumberOfFloors;

    @FXML
    private AnchorPane newGameTitleImage;

    /**
     *Start new game.
     */
    @FXML
    protected void handlePlayButtonAction() {
        this.errorNumberOfFloors.setText(ERROR_VOID);
        this.errorNumberOfRooms.setText(ERROR_VOID);
        if (this.numberOfFloorsTextField.getText().isEmpty()) {
            this.errorNumberOfFloors.setText(ERROR_UNPRESENTVALUE);
        } else if (!this.getController().isValidNumberOfFloors(this.numberOfFloorsTextField.getText())) {
            this.errorNumberOfFloors.setText(ERROR_UNVALIDVALUE);
        }
        if (this.numberOfRoomsTextField.getText().isEmpty()) {
            this.errorNumberOfRooms.setText(ERROR_UNPRESENTVALUE);
        } else if (!this.getController().isValidNumberOfRooms(this.numberOfRoomsTextField.getText())) {
            this.errorNumberOfRooms.setText(ERROR_UNVALIDVALUE);
        }
        if (this.getController().newGame(this.playerNameTextField.getText(),
                                         this.numberOfRoomsTextField.getText(), 
                                         this.numberOfFloorsTextField.getText())) {
            this.getView().setState(ApplicationViewState.GAME);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        /* This class has nothing to update */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initView() {
        final Image imageNewGame = new ImageLoaderImpl().loadSingleImage(DirectoryPicker.TITLES, TITLE_IMAGE_NAME);
        final BackgroundImage backgroundGameOver = new BackgroundImage(imageNewGame, BackgroundRepeat.NO_REPEAT, 
                                                               BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, 
                                                               new BackgroundSize(TITLE_HEIGHT_PERC, TITLE_WIDTH_PERC, 
                                                               true, true, true, false));
        this.newGameTitleImage.setBackground(new Background(backgroundGameOver));
    }
}
