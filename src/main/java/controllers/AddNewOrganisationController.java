package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainClasses.DBtoApp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AddNewOrganisationController implements Initializable {
    @FXML
    private VBox box;
    @FXML
    private Label errorMessage;

    private DBtoApp connector;
    private HashMap<String,String> oldValue;
    private HashMap<String,String> newValue = new HashMap<>();
    private GridPane gridPane;
    private ChoiceBox<String> type = new ChoiceBox<>();
    private ArrayList<String> keys;
    private ArrayList<TextField> fields = new ArrayList<>();

    public AddNewOrganisationController(DBtoApp connector, HashMap<String,String> oldValue){
        this.connector =connector;
        this.oldValue=oldValue;
        keys = new ArrayList<>(oldValue.keySet());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //ArrayList<TextField> fields = new ArrayList<>();
        gridPane = new GridPane();
        //ArrayList<String> keys = new ArrayList<>(oldValue.keySet());
        for (int i=0;i<keys.size();i++) {
            if(keys.get(i).equals("Type")){
                ObservableList<String> obsList = FXCollections.observableList(connector.getAllTypeCompany());
                type.setItems(obsList);
                //type.getSelectionModel().selectFirst();
                type.setValue(oldValue.get(keys.get(i)));

                Button btn = new Button("Добавить новый тип");
                btn.setOnAction(e->{
                    try {
                        //close();

                        //ArrayList<String> array = connector.getAllTypeCompany();
                        Stage stage = new Stage();
                        stage.setTitle("Add new type of company");
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/AddNewType.fxml"));
                        AddNewTypeController addNewTypeController = new AddNewTypeController(connector,obsList);
                        loader.setController(addNewTypeController);
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.showAndWait();

                        /*Stage osStage = (Stage) box.getScene().getWindow();
                        osStage.show();*/
                        /*Stage osStage = (Stage) box.getScene().getWindow();
                        osStage.refre*/
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                gridPane.add(new Label("Type"),0,i);
                gridPane.add(type,1,i);
                gridPane.add(btn,2,i);

            }else{
                String key = keys.get(i);
                TextField field = new TextField(oldValue.get(key));
                fields.add(field);
                gridPane.add(new Label(key),0,i);
                gridPane.add(field,1,i);
            }
        }
       /* button = new Button("Добавить новый тип информации");
        button.setOnAction(e ->{
            close();

        });
        gridPane.add(button,1,keys.size(),1,2);*/
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        box.getChildren().add(gridPane);
    }
    public void close(){
        Stage stage = (Stage) box.getScene().getWindow();
        stage.close();
    }
    public void accept(){

        int p = 0;
        String value;
        for (String key : keys) {
            value = fields.get(p).getText().strip();
            if (key.equals("Type")) {
                newValue.put(key, type.getValue());
            } else if((key.equals("Name")&&value.equals("")) ||
                    (key.equals("Main Address")&&value.equals(""))||
                    ((Pattern.matches(".*phone.*",key.toLowerCase())&&(!Pattern.matches("^\\d{1,11}$",value))&&
                            (!value.equals(""))))){
                errorMessage.opacityProperty().setValue(1);
                newValue.clear();
                return;
            }else {
                newValue.put(key, value);
                p++;
            }

        }
        //oldValue.clear();
        //oldValue.putAll(newValue);
        //System.out.println(newValue);
        close();
    }

    public void scrapNewData(){
        int p = 0;
        for (String key : keys) {
            if (key.equals("Type")) {
                newValue.put(key, type.getValue());
            } else {
                newValue.put(key, fields.get(p).getText());
                p++;
            }

        }
    }
    public void addNewInfoType(){
        //ArrayList<String> array = connector.getAllTypeCompany();
        //close();
        try {
            Stage stage = new Stage();
            //System.out.println(stage);
            stage.setTitle("Add new information type");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/AddNewInfoType.fxml"));
            AddNewInfoTypeController addNewInfoTypeController = new AddNewInfoTypeController(connector);
            loader.setController(addNewInfoTypeController);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

            if (addNewInfoTypeController.refreshable()){
                //System.out.println("refresh");
                keys.add(addNewInfoTypeController.getText());
                TextField field = new TextField();
                fields.add(field);

                gridPane.add(new Label(addNewInfoTypeController.getText()),0,keys.size()-1);
                gridPane.add(field,1,keys.size()-1);
                gridPane.getScene().getWindow().setWidth(gridPane.getScene().getWidth() + 1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public HashMap<String,String> getNewValue(){
        return newValue;
    }
}
