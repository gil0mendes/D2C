package org.d2c.client.gui.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.d2c.client.MasterEngine;
import org.d2c.common.gui.dialogs.StringDialog;

import java.awt.*;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.List;

/**
 * This class controls all work flow of the
 * Master GUI application
 */
public class Controller {

    /**
     * Save the start timestamp
     */
    protected static long startTime;

    /**
     * Save the end timestamp
     */
    protected static long endTime;

    /**
     * Master Engine instance
     */
    private MasterEngine masterEngine;

    @FXML
    public TextField intervalStart;
    @FXML
    public TextField intervalEnd;
    @FXML
    public Button calculateButton;
    @FXML
    public TextArea result;

    public Controller()
    {
        try {
            this.masterEngine = new MasterEngine(Registry.REGISTRY_PORT);
        } catch (Exception ex) {
        }
    }

    /**
     * Get last diff time
     *
     * @return
     */
    public long getLastDiffTime()
    {
        return endTime - startTime;
    }

    /**
     * Action for the button
     *
     * @param event
     */
    public void startCalculation(Event event)
    {
        try {
            // set callback action
            this.masterEngine.setCallback(args -> {
                // register end time
                endTime = System.currentTimeMillis();

                // @TODO Display the result on the GUI
                List<Integer> list = (List<Integer>) args[0];

                //Message for the output
                String message = "Diff time: " + this.getLastDiffTime() + " ms\n"+"Number of found number: " + list.size()+"\nFounded numbers: \n";

                Iterator it = list.iterator();

                while (it.hasNext()) {
                    message += message + it.next() + " ";
                }

                //Puts the in the output
                result.setText(message);
                // re-enable the button
                this.calculateButton.setDisable(false);
            });

            // get the interval from the GUI
            int startNumber, endNumber;
            try {
                startNumber = Integer.parseInt(this.intervalStart.getText());
                endNumber = Integer.parseInt(this.intervalEnd.getText());

                // check if the end number is great than start number
                if (endNumber < startNumber) {
                    StringDialog.buildAndShow("Invalid interval!", "Input Error");
                    return;
                }
            } catch (Exception ex) {
                StringDialog.buildAndShow("Invalid number!", "Input Error");
                return;
            }

            // disable the button to prevent multiple calls
            this.calculateButton.setDisable(true);

            // register start time
            startTime = System.currentTimeMillis();

            // start the calculation
            this.masterEngine.calculatePrimeNumbers(startNumber, endNumber);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
