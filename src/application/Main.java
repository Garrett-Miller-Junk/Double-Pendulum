/*
Main Double Pendulum File
This File is used for the booting of the Double Pendulum FXML file. It is also used to keep the program constantly running
Garrett Miller-Junk
June 7th, 2020
All that is required is for the user to run this program to begin the simulation. Once the simulation has begun, the user can move the pendulum around by clicking on it
and change the settings of the motion by opening the setting window.
 */
package application;
	
//import starting libraries
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	//at the start of the application
	@Override
	public void start(Stage primaryStage) {
		//attempt to do the following, if an error occurs, display it
		try {
			//create the monkey game application using its FXML file
			primaryStage.setTitle("Double Pendulum Simulation");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DoublePendulum.fxml"));
			//load it into a 1000x700 screen
			BorderPane root = (BorderPane)loader.load();
			Scene scene = new Scene(root,600,600);
			//import the CSS from the CSS file
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			//load the controller
			DoublePendulumController controller = loader.getController();
			//set the primary stage
			primaryStage.setScene(scene);
			//set make the screen nonresixable (leads to ease with mouse tracking)
			primaryStage.setResizable(false);
			//show the controller the stage
			controller.getScene(primaryStage);
			//begin the game loop
			controller.gameLoop();
			//show the stage
			primaryStage.show();
		//if an error occurs, show it
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}