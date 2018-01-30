import ShefRobot.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class FollowLineTest extends JFrame implements Runnable, KeyListener, WindowListener, ActionListener {
	private static final int MOVING_SPEED = 300;
	private static final int TURNING_SPEED = 200;
	private static final int SEARCHING_SPEED = 150;
	private static final int TURNING_TIME = 1500;
	private static final int GRID_SIZE = 16;
	private static final int SEGMENT_SIZE = 4;
	private static final int OUTSIDE_SQUARE = 3;

	private static int redCount = 0;
  private static int blueCount = 0;
  private static int greenCount = 0;
  private static int yellowCount = 0;
  private static int brownCount = 0;
	private static int[] colorCount = {redCount, blueCount, greenCount,
																					yellowCount, brownCount};
	private static boolean colorMatch = false;
	private static boolean hasBall = false;

	private static float leftRotation = 0;
	private static float rightRotation = 0;

	private static Robot myRobot = new Robot();
	private static Motor leftMotor = myRobot.getLargeMotor(Motor.Port.B);
	private static Motor rightMotor = myRobot.getLargeMotor(Motor.Port.C);
	private static Motor grabMotor = myRobot.getMediumMotor(Motor.Port.D);
	private static ColorSensor myColor = myRobot.getColorSensor(Sensor.Port.S1);
	private static Speaker mySpeaker = myRobot.getSpeaker();
	private static UltrasonicSensor myDistance = myRobot.getUltrasonicSensor(Sensor.Port.S2);
	//private static GyroSensor myAngle = myRobot.getGyroSensor(Sensor.Port.S3);


	//private static ColorSensor.Color[] colorGrid;

	//Defining the behaviour of the prgram
	enum Command {STOP, LEFT, RIGHT, FORWARD, REVERSE, DANCE };
	private static final int DELAY_MS = 50;
	
	// Make the window, text label and menu
	private static final int FRAME_WIDTH = 400;
	private static final int FRAME_HEIGHT = 200;
	
	private JLabel label = new JLabel("Stop",JLabel.CENTER);
			
	public FollowLineTest() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Quit");
		JMenuItem menuItem = new JMenuItem("Really Quit?");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		this.add(label, BorderLayout.CENTER);
		label.setFont(new Font("SansSerif", Font.PLAIN, 48));
		this.setBounds(0,0,FRAME_WIDTH,FRAME_HEIGHT);
		this.setTitle("Sheffield Robot Football Controller");
		this.addKeyListener(this);
		this.addWindowListener(this);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	// Start the program	
	private Command command = Command.STOP;	 

	// Select the command corresponding to the key pressed	
	public void keyPressed(KeyEvent e) {
		switch ( e.getKeyCode()) {
			case java.awt.event.KeyEvent.VK_UP:
				command = Command.FORWARD;
				break;
			case java.awt.event.KeyEvent.VK_DOWN:
				command = Command.REVERSE;
				break;
			case java.awt.event.KeyEvent.VK_LEFT:
				command = Command.LEFT;
				break;
			case java.awt.event.KeyEvent.VK_RIGHT:
				command = Command.RIGHT;
				break;
			case java.awt.event.KeyEvent.VK_SPACE:
				command = Command.DANCE;
				break;
			default:
				command = Command.STOP;
				break;
		}
	}
    //and released
	public void keyReleased(KeyEvent e) {
		command = Command.STOP;
	}
	//ignore everything else
	public void keyTyped(KeyEvent e) {}	
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}	
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	// handle the quit menu item	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Really Quit?")) {
			System.out.println("Closing Bluetooth");
			myRobot.close();
			System.exit(0);
		}
	}
	//and the window closing
	public void windowClosing(WindowEvent e) {
		System.out.println("Closing Bluetooth");
		myRobot.close();
	}

	public static void storeColor() {
		//currentColor = ColorSensor.getColor();
		if (myColor.getColor() == ColorSensor.Color.RED) {
				redCount++;
			}
			else if (myColor.getColor() == ColorSensor.Color.BLUE) {
				blueCount++;
			}
			else if (myColor.getColor() == ColorSensor.Color.GREEN) {
				greenCount++;
			}
			else if (myColor.getColor() == ColorSensor.Color.YELLOW) {
				yellowCount++;
		}
	}

	public static void followCircle() {
		leftMotor.setSpeed(MOVING_SPEED);
		rightMotor.setSpeed(MOVING_SPEED);
		while (myColor.getColor() != ColorSensor.Color.BLACK) {
			while (myColor.getColor() != ColorSensor.Color.WHITE) {
				leftMotor.forward();
			}
			leftMotor.stop();
			while (myColor.getColor() == ColorSensor.Color.WHITE) {
				rightMotor.forward();
			}
			rightMotor.stop();
		}
		leftMotor.stop();
		rightMotor.stop();
		followLine();
	}

	public static void checkColor() {
		for (int counter : colorCount){
			if (counter == 2) {
				colorMatch = true;
			}
		}
		System.out.println(colorMatch);
	}
	public static void findGrid() {
		leftMotor.setSpeed(MOVING_SPEED);
		rightMotor.setSpeed(MOVING_SPEED);

		while (myColor.getColor() != ColorSensor.Color.BLACK) {
			leftMotor.forward();
			rightMotor.forward();
		}
		leftMotor.stop();
		rightMotor.stop();
		leftMotor.forward();
		rightMotor.forward();
		myRobot.sleep(500);
		leftMotor.stop();
		rightMotor.stop();


		while (myColor.getColor() == ColorSensor.Color.WHITE) {
			leftMotor.forward();
		}
		leftMotor.stop();
		while (myColor.getColor() == ColorSensor.Color.BLACK) {
			leftMotor.forward();
		}
		leftMotor.stop();
	}


	public static void followLine() {
		
		leftMotor.setSpeed(MOVING_SPEED);
		rightMotor.setSpeed(MOVING_SPEED);
		while (!colorMatch){
			while (myColor.getColor() == ColorSensor.Color.BLACK ||
								myColor.getColor() == ColorSensor.Color.WHITE) {
				while (myColor.getColor() == ColorSensor.Color.BLACK) {
					leftMotor.forward();
				}
				System.out.println("1 " + myColor.getColor());
				leftMotor.stop();
				while (myColor.getColor() == ColorSensor.Color.WHITE) {
					rightMotor.forward();
				}
				rightMotor.stop();
			}
			System.out.println(myColor.getColor());
			storeColor();
			checkColor();
			if (myColor.getColor() != ColorSensor.Color.BLUE){
				followCircle();
			}
			else{
				findBall();
			}
		}

	}

	public static void findBall() {

		leftMotor.setSpeed(SEARCHING_SPEED);
		rightMotor.setSpeed(SEARCHING_SPEED);
		leftMotor.stop();
		rightMotor.stop();
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();

		while (!hasBall) {
			leftMotor.forward();
			rightMotor.forward();

			System.out.println(myDistance.getDistance());
			if (myDistance.getDistance() < 0.053) {
				grabMotor.rotate(90);
				hasBall = true;

			}

			if (myColor.getColor() == ColorSensor.Color.WHITE || myColor.getColor() == ColorSensor.Color.BLACK) {
				//leftMotor.backward();
				//rightMotor.backward();
				//myRobot.sleep(200);
				//while (myColor.getColor() == ColorSensor.Color.BLUE) {
				//	myRobot.sleep(20);
				//}
				leftMotor.rotate(360, true);
				rightMotor.rotate(-360);
				leftMotor.forward();
				rightMotor.forward();
				myRobot.sleep(100);
			}

			myRobot.sleep(100);
		}
	}

	public void run() {
		
		while (true) {
			switch (command) {
				case STOP:
					label.setText("Stop");
					leftMotor.stop();
					rightMotor.stop();
					
					// put your code for stopping here
					
					break;					
				case FORWARD:
					label.setText("Forward");
					leftMotor.forward();
					rightMotor.forward();
					
					// put your code for going forwards here
					
					break;					
				case REVERSE:
					label.setText("Reverse");
					leftMotor.backward();
					rightMotor.backward();
					
					// put your code for going backwards here
					
					break;					
				case LEFT:
					label.setText("Left");
					leftMotor.backward();
					rightMotor.forward();
					
  					// put your code for turning left here

					break;
				case RIGHT:
					label.setText("Right");
					leftMotor.forward();
					rightMotor.backward();
					
    				// put your code for turning right here

					break;
				case DANCE:
					label.setText("Dance");
					
    				// put your code for kicking here

 					break;
			}
			try {
				Thread.sleep(DELAY_MS);
			} catch (InterruptedException e) {};
		}
	}

	public static void main(String[] args) {
		Thread t = new Thread(new FollowLineTest());
		findGrid();
		followLine();
		t.start();
 	}
}
