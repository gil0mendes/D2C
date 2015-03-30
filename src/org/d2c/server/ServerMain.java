package org.d2c.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.d2c.common.CallbackHandler;
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
     * Sidebar
     */
    private FlowPane sideBar;

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

            // creates the sidebar
            this.createSidebarMenu();

            // show the connection state
            this.showState();
        } catch (Exception ex) {
            Logger.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void generateSidebarItem(String optionName, String imageName, CallbackHandler handler)
    {
        // create a new pane
        Pane pane = new Pane();
        pane.setId("sidebarOption" + optionName);
        pane.getStyleClass().add("sidebar-tab");

        // define event
        pane.setOnMouseClicked((mouseEvent) -> {
            if (handler != null) {
                handler.callback(mouseEvent);
            }
        });

        // create the ImageView
        ImageView imageView = new ImageView(new Image(ServerMain.class.getResourceAsStream("../common/gui/resources/" + imageName)));

        // add the image to the pane
        pane.getChildren().add(imageView);

        this.sideBar.getChildren().add(pane);
    }

    /**
     * Show the connection state
     */
    private void showState()
    {
        this.rootLayout.setCenter(StateController.getInstance());
    }

    /**
     * Creates and add the sidebar to the scene
     */
    private void createSidebarMenu()
    {
        // create the sidebar
        this.sideBar = new FlowPane();
        this.sideBar.setVgap(3);
        this.sideBar.setHgap(3);
        this.sideBar.setPrefWrapLength(70);
        this.sideBar.setMaxHeight(70);
        this.sideBar.setStyle("-fx-background-color: #ed6b4d");

        // adds the sidebar options
        // -- server state
        this.generateSidebarItem("serverState", "stateIcon.png", args -> ServerMain.this.showState());

        // add the sidebar to the scene
        this.rootLayout.setLeft(this.sideBar);
    }
}
