/*
Double Pendulum Controller File
This file is used for processing the operations of the double pendulum simulation
Garrett Miller-Junk
June 7th, 2020
This file only processes the information, all that is required to run the simulation is to run the main file and read the instructions for operation.
 */
package application;

//import starting libraries
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class DoublePendulumController{
	
	//import the sliders and text fields for the settings window
	@FXML Slider angle1Slider;
	@FXML Slider angle2Slider;
	@FXML Slider mass1Slider;
	@FXML Slider mass2Slider;
	@FXML Slider length1Slider;
	@FXML Slider length2Slider;
	@FXML Slider frictionSlider;
	
	@FXML private TextField angle1Text;
	@FXML private TextField angle2Text;
	@FXML private TextField mass1Text;
	@FXML private TextField mass2Text;
	@FXML private TextField length1Text;
	@FXML private TextField length2Text;
	@FXML private TextField frictionText;
	
	//import game canvas and graphics context
	@FXML
	Canvas gameCanvas;
		
	GraphicsContext gc;
	
	//declare the fame scene and second stage variables
	Scene gameScene;
	
	static Stage secondaryStage;
	
	//set the variables for where the focus of the pendulum will be
	int focusX = 300;
	int focusY = 300;
	
	//set starting length, angle, angular velocity, angular, acceleration, and mass for the first part of the pendulum
	static double length1 = 125;
	static double angle1 =  ((Math.PI)*0);
	static double angleV1 = 0;
	static double angleA1 = 0;
	static double mass1 = 50;
	
	//declare the x and y variables for the first pendulum
	static double x1;
	static double y1;
	
	//set starting length, angle, angular velocity, angular, acceleration, and mass for the second part of the pendulum
	static double length2 = 125;
	static double angle2 = ((Math.PI) * 0);
	static double angleV2 = 0;
	static double angleA2 = 0;
	static double mass2 = 50;
	
	//declare the x and y variables for the second pendulum
	static double x2;
	static double y2;
	
	//set a variable for the gravity
	static double gravity = 10;
	
	//set a variable for the friction amount
	static double friction = 1;
	
	//set booleans to see if the user is moving the pendulum around and which part of the pendulum they are moving
	static boolean isHolding = false;
	static boolean mouseMoving1 = false;
	static boolean mouseMoving2 = false;
	
	//create the step for the Runge-Kutta Method
	static double step = 0.1;
	
	// Dot List for Trail
	public ArrayList<PendulumTrail> dotList;
	
	//create a function to get the game scene
	public void getScene(Stage primaryStage) {
		gameScene = primaryStage.getScene();
	}
	//create the game loop
	public void gameLoop() {
		//get the graphics context from the scene
		gc = gameCanvas.getGraphicsContext2D();
		
		//create an array list for the trail of dots following the pendulum
		dotList = new ArrayList<PendulumTrail>();
				
		//create the animation loop
		new AnimationTimer() {
			//actual game loop that repeats
			@Override
			public void handle (long currentNanoTime) {
				//empty the canvas by colouring over it with white
				gc.setFill(Color.WHITE);
				gc.fillRect(0, 0, 1000, 1000);
				
				//find the X and Y positions of the first pendulum using the length of the first string, position of the focus, and angle of the first pendulum
				x1 = focusX + length1 * Math.sin(angle1);
				y1 = focusY + length1 * Math.cos(angle1);
				
				//find the X and Y positions of the second pendulum using the length of the second string, position of the first pendulum, and angle of the second pendulum
				x2 = x1 + length2 * Math.sin(angle2);
				y2 = y1 + length2 * Math.cos(angle2);
				
				
				//if the pendulum's are not being moved
				if (!mouseMoving1 && !mouseMoving2) {
					
					//Calculate next velocity from current values
					calculateNextVel();
					
					//Decrease the angular velocities by the friction amount
					angleV1 *= friction;
					angleV2 *= friction;
					
					//Update new angle positions
					angle1 += angleV1 * step;
					angle2 += angleV2 * step;
					
					//increase the number of dots by 1, then call the pendulum trail class and create a trail dot. Add this dot to the array list
					PendulumTrail.numDots +=1;
					dotList.add(new PendulumTrail(gc,gameCanvas, x2, y2));
					
					//if there are less than 300 dots, go through every dot
					if (PendulumTrail.numDots < 300) {
						//for every dot, draw it on the board
						for (int i = 0; i < PendulumTrail.numDots; i++) {
							dotList.get(i).draw();
						}
					//if there are more than 300 dots
					} else {
						//for every one of the most recent 300 dots, draw it on the board (stops the board from getting filled with dots and makes animation run faster)
						for (int i = (PendulumTrail.numDots - 300); i < PendulumTrail.numDots; i++) {
							dotList.get(i).draw();
						}
					}
				}
				//if the mouse is pressed
				gameScene.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						//if the user is already moving the mouse
						if (isHolding) {
							//set the moving variables to false
							mouseMoving1 = false;
							mouseMoving2 = false;
							isHolding = false;
						//if the user is not already moving the mouse
						}else {
							// Empty List
							dotList.clear();
							PendulumTrail.numDots = 0;
							
							//check to see if the mouse it touching the first mass and that nothing else is being moved
							if (((x1 - mass1/2) <= event.getX()) &&(event.getX() <= x1 + (mass1/2)) && (!isHolding)) {
								if ((y1 - (mass1/2) <= event.getY() - 15 ) &&(event.getY() - 15 <= y1 + (mass1/2))) {
									//if it is, set the angular velocities and acceleration to zero and set the first pendulum moving variables to true
									mouseMoving1 = true;
									angleV1 = 0;
									angleV2 = 0;
									angleA1 = 0;
									angleA2 = 0;
									isHolding=true;
								}
							}
							//check to see if the user is touching the second mass, and that nothing else is being moved
							if ((x2 - (mass2/2) <= event.getX()) &&(event.getX() <= x2 + (mass2/2)) && (!isHolding)) {
								if ((y2 - (mass2/2)<= event.getY() -15 ) &&(event.getY() - 15 <= y2 + (mass2/2))) {
									//if it is, set the angular velocities and acceleration to zero and set the second pendulum moving variables to true
									mouseMoving2 = true;
									angleV1 = 0;
									angleV2 = 0;
									angleA1 = 0;
									angleA2 = 0;
									isHolding=true;
								}
							}
						}
					}
				});
				//create an event handler for if the mouse is being moved
				gameScene.setOnMouseMoved(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						//if the pendulums are being moved
						if (mouseMoving1 || mouseMoving2) {
							//find the x and y position of the mouse (subtract 15 from the y for the mouse because the mouse event tracks where it is on the screen, not the canvas, so the height of the menu bar must be subtracted)
							double mouseX = event.getX();
							double mouseY = event.getY() - 15;
							//if the first pendulum is being moved
							if (mouseMoving1) {
								//set the mouse angle to the arctangent of a triangle formed between the focus and the mouse position using the X and Y axis
								double mouseAngle =  Math.atan((mouseX - focusX)/(mouseY - focusY));
								//if the mouseY is less than the focusY, increase the mouse angle by Pi (the arctangent function only goes from -Pi/2 to +Pi/2 so this allows full rotation
								if (mouseY < focusY) {
									mouseAngle += Math.PI;
								}
								//set the angle of the first pendulum to the mouse angle
								angle1 = mouseAngle;
							//if the second pendulum is instead being moved
							} else if (mouseMoving2) {
								//set the mouse angle to the arctangent of a triangle formed between the first pendulum and the mouse position using the X and Y axis
								double mouseAngle =  Math.atan((mouseX - x1)/(mouseY - y1));
								//the mouseY is less than the y position of the first pendulum, increase the mouse angle by Pi
								if (mouseY < y1) {
									mouseAngle += Math.PI;
								}
								//set the angle of the second pendulum to the mouse angle
								angle2 = mouseAngle;
							}
						}
					}
				});
				//set the fill and stroke colours to draw the pendulums
				gc.setFill(Color.GREEN);
				gc.setStroke(Color.BLUE);
				gc.setLineWidth(2);
			     
			     
			     //create the pendulum lines between the focus and first X and Y positions and between the firset X and Y positions and the second X and Y positions.
			     gc.strokeLine(focusX, focusY, x1, y1);
			     gc.strokeLine(x1, y1, x2, y2);
			     
			     //create an circle that represents the masses of the pendulums, make the diameter of each of these circles their respective masses.
			     gc.fillOval(x1 - (mass1/2), y1-(mass1/2), mass1, mass1);
			     gc.fillOval(x2 - (mass2/2), y2 - (mass2/2), mass2, mass2);
			    
			     
			    //reset the angles to the smallest amount they can be expressed as (stops them from overcoming the double data limit as the simulation goes on)
				angle1 = (angle1 % (2* Math.PI) + 2*Math.PI) % (2*Math.PI);
				angle2 = (angle2 % (2* Math.PI) + 2*Math.PI) % (2* Math.PI);
			}
		}.start();
	}
	
	// create a handler for the menu
	public void menuClickHandler(ActionEvent evt) {
		//when the menu is clicked find which part of the menu was clicked and get its name
	    MenuItem clickedMenu = (MenuItem) evt.getTarget();
	    String menuLabel = clickedMenu.getText();
	    //if the user chose to quit the program
        if ("Quit".equals(menuLabel)) {
        	//exit the platform and system, shutting down the entire program
        	Platform.exit();
        	System.exit(0);
        }
        //if the user chooses to alter the settings
        if ("Settings".equals(menuLabel)) {
        	//open the settings FXML file
        	openNewWindow("Settings.fxml");
        }
        //if the user chooses to read the instruction to the simulation
        if ("Instructions".equals(menuLabel)) {
        	//open the Instructions FXML file
        	openNewWindow("Instructions.fxml");
        }
	}
	
	//create a function for when the user wants to open a new window based off the window's name
	private void openNewWindow(String windowName) {
		//create a try statement for if an error occurs
		try {
			//load the pop up you created based off of the name given
			Pane howTo = (Pane)FXMLLoader.load(getClass().getResource(windowName));
			
			//create a new scene
			Scene howToScene = new Scene (howTo,700,600);
			
			//add css to the new scene
			howToScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//create new stage to put scene in
			secondaryStage = new Stage();
			secondaryStage.setScene(howToScene);
			secondaryStage.setResizable(false);
			secondaryStage.showAndWait();
		//if an error occurs print the error	
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//creates a function to close the window that is currently open
	public void closeCurrentWindow(final ActionEvent evt) {
		final Node source = (Node) evt.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}
	
	//create a function for when the user moves the slider in the settings popup window
	public void changeTextHandler(MouseEvent e) {
		// find the value of the slider and save it as a double
		double sliderValue = angle1Slider.getValue();
		//set the respective text bubble to the value obtained from the slider
		angle1Text.setText(Double.toString(sliderValue));
		
		// find the value of the slider and save it as a double
		sliderValue = angle2Slider.getValue();
		//set the respective text bubble to the value obtained from the slider
		angle2Text.setText(Double.toString(sliderValue));
		
		// find the value of the slider and save it as a double
		sliderValue = (int) mass1Slider.getValue();
		//set the respective text bubble to the value obtained from the slider
		mass1Text.setText(Double.toString(sliderValue));
		
		// find the value of the slider and save it as a double
		sliderValue = (int) mass2Slider.getValue();
		//set the respective text bubble to the value obtained from the slider
		mass2Text.setText(Double.toString(sliderValue));
		
		// find the value of the slider and save it as a double
		sliderValue = (int) length1Slider.getValue();
		//set the respective text bubble to the value obtained from the slider
		length1Text.setText(Double.toString(sliderValue));
		
		// find the value of the slider and save it as a double
		sliderValue = (int) length2Slider.getValue();
		//set the respective text bubble to the value obtained from the slider
		length2Text.setText(Double.toString(sliderValue));
		
		// find the value of the slider, divide it by 10, and save it as a double
		sliderValue = (frictionSlider.getValue())/10;
		//based off the value of the slider, display the preset message for the amount of friction that will be occurring given that value
		if (sliderValue == 0) {
				frictionText.setText("No friction");
			}else if (sliderValue < 0.005) {
				frictionText.setText("low friction");
			}else if (sliderValue < 0.012) {
				frictionText.setText("Moderate friction");
			}else if (sliderValue <0.02) {
				frictionText.setText("Somewhat high friction");
			}else if (sliderValue < 0.04) {
				frictionText.setText("very high friction");
			}else {
				frictionText.setText("Extreme friction");
		}
	}
	
	//create a function for when the user saves their settings
	public void settingsSaveHandler (final ActionEvent evt) {
		//reset the angular velocities
		angleV1 = 0;
		angleV2 = 0;
		//set the angles to the values read from their sliders
		angle1 = angle1Slider.getValue();
		angle2 = angle2Slider.getValue();
		
		//set the masses and lengths to integer amounts of the values read from their sliders
		mass1 = (int) mass1Slider.getValue();
		mass2 = (int) mass2Slider.getValue();
		length1 = (int) length1Slider.getValue();
		length2 = (int) length2Slider.getValue();
		
		//set the friction amount to 1 - 1/0 of the value read from the slider
		friction = 1 - (frictionSlider.getValue() * (1/10));
		
		//find the source of the action event and find the stage that corresponds to it
		final Node source = (Node) evt.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		//use the stage to close the settings popup
		stage.close();
	}
	
	//create a function to calculate the next velocity using 4th Order Runge-Kutta
	private void calculateNextVel () {
		//calculate the K1 values for angular velocities 1 and 2
		double onek1 = this.step * calculateVelocityAngle1(this.angle1, this.angle2, this.angleV1, this.angleV2);
		double twok1 = this.step * calculateVelocityAngle2(this.angle1, this.angle2, this.angleV1, this.angleV2);
		//calculate the K2 values for angular velocities 1 and 2
		double onek2 = this.step * calculateVelocityAngle1(this.angle1 + (this.step / 2), this.angle2 + (this.step / 2), this.angleV1 + (onek1 / 2), this.angleV2 + (twok1 / 2));
		double twok2 = this.step * calculateVelocityAngle2(this.angle1 + (this.step / 2), this.angle2 + (this.step / 2), this.angleV1 + (onek1 / 2), this.angleV2 + (twok1 / 2));
		//calculate the K3 values for angular velocities 1 and 2
		double onek3 = this.step * calculateVelocityAngle1(this.angle1 + (this.step / 2), this.angle2 + (this.step / 2), this.angleV1 + (onek2 / 2), this.angleV2 + (twok2 / 2));
		double twok3 = this.step * calculateVelocityAngle2(this.angle1 + (this.step / 2), this.angle2 + (this.step / 2), this.angleV1 + (onek2 / 2), this.angleV2 + (twok2 / 2));
		//calculate the K4 values for angular velocities 1 and 2
		double onek4 = this.step * calculateVelocityAngle1(this.angle1 + this.step, this.angle2 + this.step, this.angleV1 + onek3, this.angleV2 + twok3);
		double twok4 = this.step * calculateVelocityAngle2(this.angle1 + this.step, this.angle2 + this.step, this.angleV1 + onek3, this.angleV2 + twok3);
		//using the 4 K values calculated, approximate what the actual angular velocities will be
		this.angleV1 += (onek1 + 2*onek2 + 2*onek3 + onek4) / 6;
		this.angleV2 += (twok1 + 2*twok2 + 2*twok3 + twok4) / 6;
		return;
	}
	
	//function of Euler's Double pendulum equation for angular acceleration of the first pendulum
	private double calculateVelocityAngle1 (double angle1, double angle2, double angleV1, double angleV2) {
		return (-Math.sin(angle1 - angle2)  * (this.mass2 * this.length1 * angleV1 * angleV1 * Math.cos(angle1 - angle2) + angleV2 * angleV2 * this.length2 * this.mass2) - this.gravity * ((this.mass1 + this.mass2) * Math.sin(angle1) - this.mass2 * Math.sin(angle2) * Math.cos(angle1 - angle2)))/ (this.length1 * (this.mass1 + this.mass2 * Math.sin(angle1-angle2) * Math.sin(angle1-angle2)));
	}
	//function of Euler's Double pendulum equation for angular acceleration of the second pendulum
	private double calculateVelocityAngle2 (double angle1, double angle2, double angleV1, double angleV2) {
		return (Math.sin(angle1 - angle2)  * ((this.mass1 + this.mass2) * this.length1 * angleV1 * angleV1 + angleV2 * angleV2 * this.length2 * this.mass2 * Math.cos(angle1 - angle2)) + this.gravity * ((this.mass1 + this.mass2) * Math.sin(angle1) * Math.cos(angle1 - angle2) - (this.mass1 + this.mass2) * Math.sin(angle2)))/ (this.length2 * (this.mass1 + this.mass2 * Math.sin(angle1-angle2) * Math.sin(angle1-angle2)));
	}
	
}
