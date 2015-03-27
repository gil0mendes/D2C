package org.d2c.server.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.d2c.common.Logger;
import org.d2c.server.TaskBagServer;

public class StateController extends GridPane {

    /**
     * Save the controller instance
     */
    private static StateController instance = new StateController();

    // Variables related with the layout elements
    @FXML
    private Label labelWorkerNodes;
    @FXML
    private Label labelWaitingTasks;
    @FXML
    private Button switchServerState;
    @FXML
    private Label labelState;

    /**
     * Get the class instance
     *
     * @return
     */
    public static StateController getInstance()
    {
        return instance;
    }

    private StateController()
    {
        // loads the view
        FXMLLoader loader = new FXMLLoader(StateController.class.getResource("../view/serverView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception ex) {
            Logger.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void switchState()
    {
        // get the TaskBagServer instance
        TaskBagServer taskBag = TaskBagServer.getInstance();

        if (!taskBag.isConnected()) {
            // connect the server
            taskBag.connect();
        }
        else {
            // turn off the server
            taskBag.disconnect();
        }

        // update the GUI info
        this.switchStateGUIUpdate();
    }

    private void switchStateGUIUpdate()
    {
        // get the TaskBag server instance
        TaskBagServer taskBag = TaskBagServer.getInstance();

        if (taskBag.isConnected()) {
            // update the state label
            this.labelState.setText("Connected");

            // update the number of waiting tasks
            this.labelWaitingTasks.setText("Waiting tasks: " + taskBag.getNumberOfWaitingTasks());

            // update the number of workers
            this.labelWorkerNodes.setText("Num. Workers: " + taskBag.getNumberOfWorkers());

            // update the switch button
            this.switchServerState.setText("Disconnect");
            this.switchServerState.getStyleClass().clear();
            this.switchServerState.getStyleClass().add("red-button");
        }
        else {
            this.labelState.setText("Disconnected");
            this.switchServerState.setText("Connect");
            this.switchServerState.getStyleClass().clear();
            this.switchServerState.getStyleClass().add("green-button");
        }
    }

}
