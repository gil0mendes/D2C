package org.d2c.common.gui.dialogs;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Create a dialog window to inform something to the user
 */
public class StringDialog {

    /**
     * Method to facilitate the Dialog creation
     *
     * @param owner
     * @param message
     * @param title
     */
    public static void buildAndShow(String message, String title)
    {
        new StringDialog(null, message, title);
    }

    /**
     * Method to facilitate the Dialog creation
     *
     * @param owner
     * @param message
     * @param title
     */
    public static void buildAndShow(Window owner, String message, String title)
    {
        new StringDialog(owner, message, title);
    }

    public StringDialog(String message, String title)
    {
        this(null, message, title);
    }

    public StringDialog(Window owner, String message, String title)
    {
        // create a new stage for the dialog
        final Stage dialog = new Stage();

        // set the dialog title
        dialog.setTitle(title);

        // define the dialog style
        dialog.initStyle(StageStyle.UTILITY);

        // define the init modality
        dialog.initModality(Modality.WINDOW_MODAL);

        // set dialog position in relation to owner
        if (owner != null) {
            dialog.initOwner(owner);
            dialog.setX(owner.getX() + owner.getWidth());
            dialog.setY(owner.getY());
        }

        // create a new label with the message to be
        // displayed
        final Label label = new Label(message);

        // create a new Ok button
        final Button submitButton = new Button("OK");

        // set the button like the default action
        submitButton.setDefaultButton(true);

        // set the button action
        submitButton.setOnAction(actionEvent -> dialog.close());

        // configure the layout
        final VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER_RIGHT);
        layout.setStyle("-fx-background-color: azure; -fx-padding: 10");
        layout.getChildren().setAll(label, submitButton);

        // set the scene to the dialog stage
        dialog.setScene(new Scene(layout));

        // show dialog
        dialog.show();
    }

}
