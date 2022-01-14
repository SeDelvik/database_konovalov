package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainClasses.Organisation;

import java.net.URL;
import java.util.ResourceBundle;

public class WatchDataController implements Initializable {
    @FXML
    private Button closeBut;
    @FXML
    private VBox box;
    private Organisation organisation;

    public WatchDataController(Organisation organisation){
        this.organisation = organisation;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        Label label1 = new Label("Актуальные данные");
        label1.setStyle("-fx-font-size: 14;-fx-font-weight: bold");
        GridPane.setHalignment(label1, HPos.CENTER);
        gridPane.add(label1,0,0,2,1);
        int i =1;
        for(String key: organisation.getData().keySet()){
            Label tmpLabel = new Label(key+":");
            tmpLabel.setStyle("-fx-font-weight: bold");
            gridPane.add(tmpLabel,0,i);
            gridPane.add(new Label(organisation.getData().get(key)),1,i);
            i++;
        }
        Label label2 = new Label("Старые данные");
        label2.setStyle("-fx-font-size: 14;-fx-font-weight: bold");
        GridPane.setHalignment(label2, HPos.CENTER);
        gridPane.add(label2,0,i,2,1);
        GridPane.setHalignment(label2, HPos.CENTER);
        i++;
        for(String key: organisation.getOldData().keySet()){
            String string = "";
            for (String str:organisation.getOldData().get(key)){
                string = string+str+",\n";
            }
            string = string.substring(0,string.length()-2);
            Label tmpLabel = new Label(key+":");
            tmpLabel.setStyle("-fx-font-weight: bold");
            gridPane.add(tmpLabel,0,i);
            gridPane.add(new Label(string),1,i);
            i++;
        }
        gridPane.setHgap(10);
        //gridPane.setVgap(10);
        box.getChildren().add(gridPane);

    }
    public void close(){
        Stage stage = (Stage) closeBut.getScene().getWindow();
        stage.close();
    }


}
