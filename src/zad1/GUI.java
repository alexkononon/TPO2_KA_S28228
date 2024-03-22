package zad1;
/**
 *
 *  @author Konan Aliaksandr  S28228
 *
 */

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;

public class GUI {
    private WebEngine webEngine;
    private final JFXPanel jfxPanel;
    private Scene scene;
    private final JLabel label1;
    private final JLabel label2;
    private final JLabel label3;

    GUI() {
        JFrame frame = new JFrame("TPO2 by alexkononon");
        jfxPanel = new JFXPanel();
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        JButton button = new JButton("Change info");
        button.addActionListener(e -> SwingUtilities.invokeLater(InputFormExample::new));
        label1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        label1.setBorder(BorderFactory.createEtchedBorder());
        label2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        label2.setBorder(BorderFactory.createEtchedBorder());
        label3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        label3.setBorder(BorderFactory.createEtchedBorder());
        topPanel.add(label1);
        topPanel.add(label2);
        topPanel.add(label3);
        topPanel.add(button);
        frame.add(topPanel, BorderLayout.NORTH);
        runWikipediaApplication(jfxPanel);
        frame.add(jfxPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public void runWikipediaApplication(JFXPanel ignoredJfxPanel) {
        Platform.runLater(() -> {
            WebView browser = new WebView();
            webEngine = browser.getEngine();
            scene = new Scene(browser);
        });
    }
    public void setUrlWikipedia(String city) {
        Platform.runLater(() -> {
            String www = "https://en.wikipedia.org/wiki/" + city;
            webEngine.load(www);
            jfxPanel.setScene(scene);
        });

    }
    public void updateLabel1(String newText) {
        label1.setText(newText);
    }
    public void updateLabel2(String newText) {
        label2.setText(newText);
    }
    public void updateLabel3(String newText) {
        label3.setText(newText);
    }
}


class InputFormExample extends JFrame {
    private final JTextField countryField;
    private final JTextField cityField;
    private final JTextField currencyCodeField;
    private GUI gui;
    public InputFormExample() {
        setTitle("Input In English");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 5, 5));
        JLabel countryLabel = new JLabel("Country:");
        countryField = new JTextField();
        add(countryLabel);
        add(countryField);
        JLabel cityLabel = new JLabel("City:");
        cityField = new JTextField();
        add(cityLabel);
        add(cityField);
        JLabel currencyCodeLabel = new JLabel("Currency Code:");
        currencyCodeField = new JTextField();
        add(currencyCodeLabel);
        add(currencyCodeField);

        if(gui == null) {
            gui = new GUI();
        }
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            Service serviceInput = new Service(countryField.getText());
            String cityInput = cityField.getText();
            String currencyInput = currencyCodeField.getText();
            //
            StringBuilder sb = new StringBuilder();
            sb.append(serviceInput.getWeather(cityInput));
            JSONParser parser = new JSONParser();
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) parser.parse(sb.toString());
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
            String city = (String) jsonObject.get("name");
            JSONObject weather = ((JSONObject) ((JSONArray) jsonObject.get("weather")).get(0));
            String sky = (String) weather.get("main");
            JSONObject main = (JSONObject) jsonObject.get("main");
            double temperature = (double) main.get("temp") -273;
            double pressure = ((Long) main.get("pressure")).intValue();
            double humidity = ((Long) main.get("humidity")).intValue();
            JSONObject wind = (JSONObject) jsonObject.get("wind");
            double windSpeed = (double) wind.get("speed");
            gui.updateLabel1( "<html>Weather:<br>City: " + city + "<br>Sky: " + sky + "<br>Temperature: " +String.format("%.2f", temperature) + "<br>Pressure: " + String.format("%.2f", pressure) + "<br>Humidity: " +String.format("%.2f", humidity) + "<br>Wind: " + String.format("%.2f", windSpeed) + "</html>");
            gui.updateLabel2( "Currency rate: " + serviceInput.getRateFor(currencyInput));
            gui.updateLabel3("PLN rate:" + serviceInput.getNBPRate());
            gui.setUrlWikipedia(cityInput);
            dispose();
        });
        add(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

