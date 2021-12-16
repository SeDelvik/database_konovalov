package controllers;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mainClasses.DBtoApp;
import mainClasses.Organisation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Text dataTextObserve;
    @FXML
    private ListView<Organisation> viewData;

    private StringProperty dataText= new SimpleStringProperty("");
    private ObservableList<Organisation> dataListObs;
    private DBtoApp connector;

    public MainController(DBtoApp connector,ObservableList<Organisation> data){
        dataListObs = data;
        this.connector = connector;
    }

    public void hashToNormalText(Organisation org){
        dataText.set("");
        org.getData().forEach((key,value)->{
            dataText.set(dataText.getValue()+key+": "+value+"\n");
        });
    }

    public void exitAct(){
        Platform.exit();
    }

    public void findInExtraInfo(){

    }

    public void findNumber(){
        try {
            HashMap<String,String> hash = new HashMap<>();
            //ArrayList<String> list = connector.getAllTypeOrganisation();
            ArrayList<String> list = connector.getAllTypeInformationWithoutPhoneNumber();

            Stage stage = new Stage();
            stage.setTitle("?");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/AskDataController.fxml"));
            AskDataController askDataController = new AskDataController(hash,list);
            loader.setController(askDataController);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

            if (!hash.isEmpty()){
                System.out.println(hash);
                filteredObjects(hash);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void filteredObjects(HashMap<String,String> params){
        ObservableList<Organisation> obs = null;
        try {
            if(params.containsKey("name")){
                obs = FXCollections.observableArrayList(connector.getFilteredNumber(params));
            }
            else{
                obs = FXCollections.observableArrayList(connector.getOrganisationToNum(params));
            }

            Stage stage = new Stage();
            stage.setTitle("?");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/FindedData.fxml"));
            FindedDataController findedDataController = new FindedDataController(obs);
            loader.setController(findedDataController);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }
    public void showAll(){
        viewData.setItems(dataListObs);
        viewData.getSelectionModel().selectFirst();
    }

    public void findOrg(){
        try {
            HashMap<String,String> hash = new HashMap<>();
            //ArrayList<String> list = connector.getAllTypeOrganisation();
            //ArrayList<String> list = connector.getAllTypeInformationWithoutPhoneNumber();

            Stage stage = new Stage();
            stage.setTitle("?");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/AskNumber.fxml"));
            AskNumberController askNumberController = new AskNumberController(hash);
            loader.setController(askNumberController);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

            if (!hash.isEmpty()){
                System.out.println(hash);
                filteredObjects(hash);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataTextObserve.textProperty().bind(this.dataText);

        viewData.setItems(dataListObs);
        viewData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue!=null) hashToNormalText(newValue);
        });
        viewData.getSelectionModel().selectFirst();
    }
}
