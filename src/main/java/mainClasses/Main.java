package mainClasses;

import controllers.MainController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

/*TODO заменить боковые панели на scrolpanel и добавить перенос текста*/
public class Main extends Application {
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/postgres";
    static final String USER = "admin";
    static final String PASS = "admin";


    @Override
    public void start(Stage primaryStage) throws Exception {


        DBtoApp connector = new DBtoApp(DB_URL,USER,PASS);
        //ArrayList<Organisation> list = FXCollections.observableArrayList();
        ArrayList<Organisation> arrayList = connector.getCurrentData();
        ObservableList<Organisation> list =FXCollections.observableArrayList(arrayList);
        //System.out.println(list);

        primaryStage.setTitle("DB");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/MainController.fxml"));
        //loader.setLocation(getClass().getResource("/controllers/MainController.fxml"));
        MainController mainController = new MainController(connector,list);
        loader.setController(mainController);
        Parent mainView = loader.load(); //тут происходит загрузка вью и инициализация контроллера ( через метод initialize)

        //MainController controller = loader.getController();
        //controller.setText();
        //controller.setData(list);

        Scene scene = new Scene(mainView);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String []args) {
        launch(args);

    }
}

