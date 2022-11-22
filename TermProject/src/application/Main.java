package application;

import application.model.Dashboard;
import application.view.DashboardController;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;



public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
	        Dashboard dashboard = Dashboard.getInstance();
	        dashboard.setName("Drone Farm");

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/Dashboard.fxml"));
            BorderPane root = (BorderPane) loader.load();
            Scene scene = new Scene(root,1000,800);
			primaryStage.setScene(scene);
			primaryStage.show();

	        DashboardController controller = loader.getController();
	        controller.setMainApp(this);


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
