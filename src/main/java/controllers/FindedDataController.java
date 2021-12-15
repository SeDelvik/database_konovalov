package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mainClasses.Organisation;

import java.net.URL;
import java.util.ResourceBundle;

public class FindedDataController implements Initializable {
    @FXML
    private ListView<Organisation> viewData;
    @FXML
    private Text dataText;
    @FXML
    private Button closeBut;

    private StringProperty str = new SimpleStringProperty("");

    private ObservableList<Organisation> list;

    public FindedDataController(ObservableList<Organisation> list){
        this.list=list;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewData.setItems(list);
        viewData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue!=null) {
                str.set("");
                newValue.getData().forEach((key,value)->{
                    str.set(str.getValue()+ key+": "+value+"\n");
                });
                str.set(str.getValue()+"\n Old data:\n");
                newValue.getOldData().forEach((key,list)->{
                    str.set(str.getValue()+key+": ");
                    list.forEach(value->{
                        str.set(str.getValue()+value+"; ");
                    });
                    str.set(str.getValue()+"\n");
                });
            }
        });

        viewData.getSelectionModel().selectFirst();
        dataText.textProperty().bind(str);
    }

    public void close() {
        Stage stage = (Stage) closeBut.getScene().getWindow();
        stage.close();
    }
}
