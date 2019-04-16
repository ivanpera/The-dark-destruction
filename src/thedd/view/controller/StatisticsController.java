package thedd.view.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import thedd.view.extensions.AdaptiveFontLabel;
import thedd.view.imageloader.ImageLoader;

/**
 * Class that handle the Statistics View.
 */
public class StatisticsController extends ViewNodeControllerImpl {
    @FXML
    private AdaptiveFontLabel healthValue;
    @FXML
    private AdaptiveFontLabel constitutionValue;
    @FXML
    private AdaptiveFontLabel strengthValue;
    @FXML
    private AdaptiveFontLabel agilityValue;
    @FXML
    private AnchorPane imageContent;
    private static final String IMAGE_TAG = "_PROFILE";
    private static final double BACKGROUND_WIDTH_PERCENTAGE = 1.0;
    private static final double BACKGROUND_HEIGHT_PERCENTAGE = 1.0;

    /**
     * Statistics controller constructor.
     */
    public StatisticsController() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // Set the Statistics
        this.healthValue.setText(this.getController().getStatisticsInformations().getHealthPointValue() + " / "
                + this.getController().getStatisticsInformations().getHealthPointMaxValue());
        this.agilityValue.setText(this.getController().getStatisticsInformations().getAgilityValue());
        this.constitutionValue.setText(this.getController().getStatisticsInformations().getConstitutionValue());
        this.strengthValue.setText(this.getController().getStatisticsInformations().getStrengthValue());
        // Set the Character's image.
        Image img = loadRelatedImage();
        BackgroundImage bg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BACKGROUND_WIDTH_PERCENTAGE, BACKGROUND_HEIGHT_PERCENTAGE, true, true, true, false));
        // Third boolean, if false ignore image's ratio, otherwise keep it.
        this.imageContent.setBackground(new Background(bg));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initView() {
        update();
    }

    private Image loadRelatedImage() {
        final String type = this.getController().getStatisticsInformations().getCharacterType();
        return ImageLoader.valueOf(type.toUpperCase() + IMAGE_TAG).getImage();
    }

}
