/*
Double Pendulum Trail Class
This file creates the dots for the double pendulum trail
Garrett Miller-Junk
June 7th, 2020
This file is referenced from the controller file and needs no additional operation
 */
package application;

//import necessary libraries
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PendulumTrail {
	
	//declare variables for the number of dots and their locations
	static int numDots = 0;
	
	double x = 500;
	double y = 500;
	
	//declare the radius of the dots as 1
	double radius = 1;
	
	//import the game canvas and graphics context
	GraphicsContext gc;
	
	@FXML
	Canvas gameCanvas;
	
	//allow the user to create an object by supplying the graphics context, game canvas, and x and y position
	public PendulumTrail(GraphicsContext gc, Canvas gameCanvas, double x, double y) {
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		this.x = x;
		this.y = y;
	}
	
	//create a function to allow the user to draw the dot
	public void draw() {
		//place a black dot with the center of the cicrle at the desired location
		this.gc.setFill(Color.BLACK);
		this.gc.fillOval(this.x - (this.radius), this.y-(this.radius), (2* this.radius), (2*this.radius));
	}
	
}
