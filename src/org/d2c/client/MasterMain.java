package org.d2c.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.d2c.common.Logger;

import java.net.URL;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.List;

public class MasterMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("./gui/view/masterView.fxml"));
        primaryStage.setTitle("D2C - Master");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        // start Logger
        Logger.config(1);

        // set configurations for the JAVA Security Policy
        ClassLoader cl = MasterMain.class.getClassLoader();
        URL policyURL = cl.getResource("org/d2c/common/policy.all");
        System.setProperty("java.security.policy", policyURL.toString());

        // set the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // launch the GUI application
        launch(args);
    }
}
