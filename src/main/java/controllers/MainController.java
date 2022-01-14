package controllers;


import javafx.application.Platform;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import mainClasses.DBtoApp;
import mainClasses.Organisation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class MainController implements Initializable {
    /*@FXML
    private Text dataTextObserve;
    @FXML
    private Text olddataTextObserve;*/
    @FXML
    private ListView<Organisation> viewData;

    @FXML
    private TableColumn<Map.Entry<String,String>,String> col1;
    @FXML
    private TableColumn<Map.Entry<String,String>,String> col2;
    @FXML
    private TableView<Map.Entry<String,String>> tableCur;
    @FXML
    private TableColumn<Map.Entry<String,String>,String> col3;
    @FXML
    private TableColumn<Map.Entry<String,String>,String> col4;
    @FXML
    private TableView<Map.Entry<String, String>> tableOld;

    private StringProperty dataText= new SimpleStringProperty("");
    private StringProperty olddataText= new SimpleStringProperty("");
    private ObservableList<Organisation> dataListObs;
    private DBtoApp connector;

    public MainController(DBtoApp connector,ObservableList<Organisation> data){
        dataListObs = data;
        this.connector = connector;
    }

    /*public void hashToNormalText(Organisation org){
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
    }*/

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
    //TODO не работает поиск по главному адресу (кажется работает, всё хорошо)
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
        dataListObs = FXCollections.observableArrayList(connector.getCurrentData());
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

    public void addNewOrganisation(){
        Organisation newOrg = new Organisation(connector.getMaxOrganisationId());
        newOrg.getData().put("Name","");
        newOrg.getData().put("Main Address","");
        newOrg.getData().put("Type","default");

        HashMap<String,String> hash = changeDataSet(newOrg);
        if (hash.size()<1) return;
        hash.forEach((x,y)->{
            newOrg.getData().put(x,y);
        });
        connector.addNewOrganisation(newOrg);
        viewData.getItems().add(newOrg);
    }
    public void editOrganisation(){
        if(!viewData.getSelectionModel().isEmpty()) {
            HashMap<String, String> newDataHash = changeDataSet(viewData.getSelectionModel().getSelectedItem());
            System.out.println(newDataHash);
            if (newDataHash.size() > 0) {
                viewData.getSelectionModel().getSelectedItem().changeData(newDataHash);
                connector.editOrganisationData(viewData.getSelectionModel().getSelectedItem(), newDataHash);
            }
        }
    }

    public HashMap<String,String> changeDataSet(Organisation organisation){
        HashMap<String,String> hash = new HashMap<>();
        try {


            organisation.getData().forEach(hash::put);
            //viewData.getSelectionModel().getSelectedItem()
            for(String value:connector.getAllInfoType()){
                if(!hash.containsKey(value))
                    hash.put(value,"");
            }
            //System.out.println(hash);

            /*hash.put("Type","type");
            hash.put("1","1");
            hash.put("2","2");*/


            Stage stage = new Stage();
            stage.setTitle("Add new type of company");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/AddNewOrganisation.fxml"));
            AddNewOrganisationController addNewOrganisationController = new AddNewOrganisationController(connector,hash);
            loader.setController(addNewOrganisationController);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();
            //очистка от пустых полей и проверка на то что он вообще не пустой
            hash.clear();
            if(addNewOrganisationController.getNewValue().size()>0){
                hash = addNewOrganisationController.getNewValue();
                ArrayList<String> keySet = new ArrayList<>(hash.keySet());
                for(String key: keySet){
                    if(hash.get(key).equals("")){
                        hash.remove(key);
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public void deleteOrg(){
        if(!viewData.getSelectionModel().isEmpty()) {
            try {
                Stage stage = new Stage();
                stage.setTitle("Are you sure?");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/DeleteQuestion.fxml"));
                DeleteQuestionController deleteQuestionController = new DeleteQuestionController();
                loader.setController(deleteQuestionController);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.showAndWait();
                if (deleteQuestionController.getConsent()) {
                    Organisation org = viewData.getSelectionModel().getSelectedItem();
                    connector.deleteOrganisation(org);
                    viewData.getItems().remove(org);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void watchData(){
        if(!viewData.getSelectionModel().isEmpty()) {
            try {
                Stage stage = new Stage();
                stage.setTitle("watch data");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/watchData.fxml"));
                WatchDataController watchDataController = new WatchDataController(viewData.getSelectionModel().getSelectedItem());
                loader.setController(watchDataController);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       /* dataTextObserve.textProperty().bind(this.dataText);
        olddataTextObserve.textProperty().bind(this.olddataText);*/

        viewData.setItems(dataListObs);
        viewData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue!=null) {
                //hashToNormalText(newValue);
                ObservableList<Map.Entry<String, String>> itemsCur = FXCollections.observableArrayList(viewData.getSelectionModel().getSelectedItem().getData().entrySet());
                tableCur.setItems(itemsCur);
                //Map.Entry<String,String> entrySet = new HashSet<>();
                MapProperty<String,String> tmpMap = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<String, String>()));
                for(String key: viewData.getSelectionModel().getSelectedItem().getOldData().keySet()){
                    String string = "";
                    for (String str:viewData.getSelectionModel().getSelectedItem().getOldData().get(key)){
                        string = string+str+", ";
                    }
                    string = string.substring(0,string.length()-2);
                    tmpMap.put(key,string);
                }
                ObservableList<Map.Entry<String, String>> itemsOld = FXCollections.observableArrayList(tmpMap.entrySet());
                tableOld.setItems(itemsOld);
            }


        });
        viewData.getSelectionModel().selectFirst();

        col1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, String>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, String>, String> p) {
                // this callback returns property for just one cell, you can't use a loop here
                // for first column we use key
                return new SimpleStringProperty(p.getValue().getKey());
            }
        });
        col2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, String>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, String>, String> p) {
                // for second column we use value
                return new SimpleStringProperty(p.getValue().getValue());
            }
        });
        col3.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, String>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, String>, String> p) {
                // this callback returns property for just one cell, you can't use a loop here
                // for first column we use key
                return new SimpleStringProperty(p.getValue().getKey());
            }
        });
        col4.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, String>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, String>, String> p) {
                // for second column we use value
                return new SimpleStringProperty(p.getValue().getValue());
            }
        });
        ObservableList<Map.Entry<String, String>> items = FXCollections.observableArrayList(viewData.getSelectionModel().getSelectedItem().getData().entrySet());
        tableCur.setItems(items);// = new TableView<>(items);

        MapProperty<String,String> tmpMap = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<String, String>()));
        for(String key: viewData.getSelectionModel().getSelectedItem().getOldData().keySet()){
            String string = "";
            for (String str:viewData.getSelectionModel().getSelectedItem().getOldData().get(key)){
                string = string+str+", ";
            }
            string = string.substring(0,string.length()-2);
            tmpMap.put(key,string);
        }
        ObservableList<Map.Entry<String, String>> itemsOld = FXCollections.observableArrayList(tmpMap.entrySet());
        tableOld.setItems(itemsOld);

    }
}
