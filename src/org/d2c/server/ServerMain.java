package org.d2c.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.d2c.common.Logger;
import org.d2c.server.gui.controller.StateController;

import java.net.URL;

/**
 * This class is the entry point for the server
 * applications. This only config some security
 * parameters and loads the GUI applications.
 */
public class ServerMain extends Application {

    /**
     * Primary application stage
     */
    private Stage primaryStage;

    /**
     * Root layout class
     */
    private BorderPane rootLayout;

    /**
     * Entry point method
     *
     * @param args
     */
    public static void main(String[] args)
    {
        // start Logger to be verbose
        Logger.config(1);

        // set configurations for the JAVA Security Policy
        ClassLoader cl = ServerMain.class.getClassLoader();
        URL policyURL = cl.getResource("org/d2c/common/policy.all");
        System.setProperty("java.security.policy", policyURL.toString());

        // set the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // launch the GUI application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // save primary state instance and set the a new title
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("D2D - Server");

        try {
            // loads the root layout
            FXMLLoader loader = new FXMLLoader(ServerMain.class.getResource("../common/gui/view/rootLayout.fxml"));
            this.rootLayout = loader.load();

            // setup scene and stage
            Scene scene = new Scene(this.rootLayout);
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            // define the action on window closing
            this.primaryStage.setOnCloseRequest((windowEvent) -> TaskBagServer.getInstance().disconnect());

            // show the connection state
            this.showState();
        } catch (Exception ex) {
            Logger.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Show the connection state
     */
    private void showState()
    {
        this.rootLayout.setCenter(StateController.getInstance());
    }
}
