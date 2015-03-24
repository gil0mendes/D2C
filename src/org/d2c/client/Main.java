package org.d2c.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.d2c.common.CallbackHandler;
import org.d2c.common.Logger;

import java.net.URL;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        // @TODO TEST
        try {
            MasterEngine me = new MasterEngine(Registry.REGISTRY_PORT);
            me.calculatePrimeNumbers(0, 20000);
            me.setCallback(new CallbackHandler() {
                @Override
                public void callback(Object... args)
                {
                    List<Integer> list = (List<Integer>) args[0];

                    System.out.println("Number of found number: " + list.size());
                    System.out.println("Founded numbers: ");
                    Iterator it = list.iterator();

                    while (it.hasNext()) {
                        System.out.print(it.next() + " ");
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        // start Logger
        Logger.config(1);

        // set configurations for the JAVA Security Policy
        ClassLoader cl = Main.class.getClassLoader();
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
