package org.d2c.client.gui.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.d2c.client.MasterEngine;

import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.List;

public class Controller {

    /**
     * Save the start timestamp
     */
    protected static long startTime;

    /**
     * Save the end timestamp
     */
    protected static long endTime;

    @FXML
    public TextField intervalStart;
    @FXML
    public TextField intervalEnd;
    @FXML
    public Button calculateButton;

    public void startCalculation(Event event)
    {
        // @TODO TEST
        try {
            MasterEngine me = new MasterEngine(Registry.REGISTRY_PORT);

            // register start time
            startTime = System.currentTimeMillis();
            me.calculatePrimeNumbers(0, 20000);
            me.setCallback(args -> {
                // register end time
                endTime = System.currentTimeMillis();
                List<Integer> list = (List<Integer>) args[0];

                System.out.println("Diff time: " + (endTime - startTime) + " ms");
                System.out.println("Number of found number: " + list.size());
                System.out.println("Founded numbers: ");
                Iterator it = list.iterator();

                while (it.hasNext()) {
                    System.out.print(it.next() + " ");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
