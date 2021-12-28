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
    @FXML
    private Text olddataTextObserve;

    private StringProperty dataText= new SimpleStringProperty("");
    private StringProperty olddataText= new SimpleStringProperty("");
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

        olddataText.set("");
        org.getOldData().forEach((key,value)->{
            olddataText.set(olddataText.getValue()+key+": ");
            value.forEach((str)->{
                olddataText.set(olddataText.getValue()+str+", ");
            });
            olddataText.set(olddataText.getValue().substring(0,olddataText.getValue().length()-2));
            olddataText.set(olddataText.getValue()+"\n");
        });
    }

    public void exitAct(){
        Platform.exit();
    }

    public void findInExtraInfo(){
        try {
            HashMap<String,String> hash = new HashMap<>();
            //ArrayList<String> list = connector.getAllTypeOrganisation();
            //ArrayList<String> list = connector.getAllTypeInformationWithoutPhoneNumber();

            Stage stage = new Stage();
            stage.setTitle("?");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/FindInExtraInfo.fxml"));
            FindInExtraInfoController findInExtraInfoController = new FindInExtraInfoController(hash);
            loader.setController(findInExtraInfoController);
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
            else if(params.containsKey("dataAll")){
                obs = FXCollections.observableArrayList(connector.findInAllExtraData(params.get("dataAll")));
            }
            else{
                obs = FXCollections.observableArrayList(connector.getOrganisationToNum(params));
            }

            viewData.setItems(obs);
            viewData.getSelectionModel().selectFirst();
            /*Stage stage = new Stage();
            stage.setTitle("?");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/FindedData.fxml"));
            FindedDataController findedDataController = new FindedDataController(obs);
            loader.setController(findedDataController);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();*/
        } catch (SQLException /*| IOException*/ throwables) {
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
    public void addNewType(){
        try {
            ArrayList<String> array = connector.getAllTypeCompany();
            Stage stage = new Stage();
            stage.setTitle("Add new type of company");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/AddNewType.fxml"));
            AddNewTypeController addNewTypeController = new AddNewTypeController(connector,array);
            loader.setController(addNewTypeController);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataTextObserve.textProperty().bind(this.dataText);
        olddataTextObserve.textProperty().bind(this.olddataText);

        viewData.setItems(dataListObs);
        viewData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue!=null) hashToNormalText(newValue);
        });
        viewData.getSelectionModel().selectFirst();
    }
}
