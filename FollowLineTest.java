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
	private static final int RETURN_SPEED = 500;
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

	Robot myRobot = new Robot();


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

	public void storeColor(Robot myRobot, ColorSensor myColor) {
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

	public void followCircle(Robot myRobot, Motor leftMotor, Motor rightMotor, Motor grabMotor, ColorSensor myColor, UltrasonicSensor myDistance) {
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
		followLine(myRobot, leftMotor, rightMotor, grabMotor, myColor, myDistance);
	}

	public void checkColor() {
		for (int counter : colorCount){
			if (counter == 2) {
				colorMatch = true;
			}
		}
	}
	public void findGrid(Robot myRobot, Motor leftMotor, Motor rightMotor, ColorSensor myColor) {
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


	public void followLine(Robot myRobot, Motor leftMotor, Motor rightMotor, Motor grabMotor, ColorSensor myColor, UltrasonicSensor myDistance) {
		
		leftMotor.setSpeed(MOVING_SPEED);
		rightMotor.setSpeed(MOVING_SPEED);
		while (!colorMatch && !hasBall){
			while (myColor.getColor() == ColorSensor.Color.BLACK ||
								myColor.getColor() == ColorSensor.Color.WHITE) {
				while (myColor.getColor() == ColorSensor.Color.BLACK) {
					leftMotor.forward();
				}
				leftMotor.stop();
				while (myColor.getColor() == ColorSensor.Color.WHITE) {
					rightMotor.forward();
				}
				rightMotor.stop();
			}
			storeColor(myRobot, myColor);
			checkColor();
			if (myColor.getColor() != ColorSensor.Color.BLUE){
				followCircle(myRobot, leftMotor, rightMotor, grabMotor, myColor, myDistance);
			}
			else{
				findBall(myRobot, leftMotor, rightMotor, grabMotor, myColor, myDistance);
			}
		}

	}

	public void findBall(Robot myRobot, Motor leftMotor, Motor rightMotor, Motor grabMotor, ColorSensor myColor, UltrasonicSensor myDistance) {

		leftMotor.setSpeed(SEARCHING_SPEED);
		rightMotor.setSpeed(SEARCHING_SPEED);
		leftMotor.stop();
		rightMotor.stop();
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();

		while (!hasBall) {
			leftMotor.forward();
			rightMotor.forward();

			if (myDistance.getDistance() < 0.053) {
				grabMotor.rotate(90);
				hasBall = true;

			}

			if (myColor.getColor() == ColorSensor.Color.WHITE || myColor.getColor() == ColorSensor.Color.BLACK) {
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

		Motor leftMotor = myRobot.getLargeMotor(Motor.Port.B);
		Motor rightMotor = myRobot.getLargeMotor(Motor.Port.C);
		Motor grabMotor = myRobot.getMediumMotor(Motor.Port.D);
		ColorSensor myColor = myRobot.getColorSensor(Sensor.Port.S1);
		Speaker speaker = myRobot.getSpeaker();
		UltrasonicSensor myDistance = myRobot.getUltrasonicSensor(Sensor.Port.S2);


		label.setText("Following Line...");
		findGrid(myRobot, leftMotor, rightMotor, myColor);

		label.setText("Finding Ball...");
		followLine(myRobot, leftMotor, rightMotor, grabMotor, myColor, myDistance);

		label.setText("Found Ball!");
		boolean hasFinished = false;
		
		leftMotor.setSpeed(RETURN_SPEED);
		rightMotor.setSpeed(RETURN_SPEED);
		while (!hasFinished) {
			switch (command) {
				case STOP:
					label.setText("Stop");
					leftMotor.stop();
					rightMotor.stop();
					
					break;					
				case FORWARD:
					label.setText("Forward");
					leftMotor.forward();
					rightMotor.forward();
					
					break;					
				case REVERSE:
					label.setText("Reverse");
					leftMotor.backward();
					rightMotor.backward();
					
					break;					
				case LEFT:
					label.setText("Left");
					leftMotor.backward();
					rightMotor.forward();

					break;
				case RIGHT:
					label.setText("Right");
					leftMotor.forward();
					rightMotor.backward();

					break;
				case DANCE:
					label.setText("Dance");
					hasFinished = true;

 					break;
			}
			try {
				Thread.sleep(DELAY_MS);
			} catch (InterruptedException e) {};
		}

		grabMotor.rotate(-90);
		leftMotor.rotate(-360);
		rightMotor.rotate(360);

		myRobot.sleep(500);

		leftMotor.rotate(-6200, true);
		rightMotor.rotate(6200, true);

		speaker.playTone(622, 400);
		speaker.playTone(622, 200);
		speaker.playTone(659, 400);
		speaker.playTone(493, 200);
		speaker.playTone(415, 200);
		
		speaker.playTone(622, 400);
		speaker.playTone(622, 200);
		speaker.playTone(659, 400);
		speaker.playTone(493, 200);
		speaker.playTone(415, 200);

		speaker.playTone(622, 400);
		speaker.playTone(622, 200);
		speaker.playTone(659, 400);
		speaker.playTone(493, 200);
		speaker.playTone(415, 200);

		speaker.playTone(622, 400);
		speaker.playTone(622, 200);
		speaker.playTone(659, 400);
		speaker.playTone(493, 200);
		speaker.playTone(415, 200);

		speaker.playTone(622, 400);
		speaker.playTone(622, 200);
		speaker.playTone(659, 400);
		speaker.playTone(493, 200);
		speaker.playTone(415, 200);

		speaker.playTone(622, 400);
		speaker.playTone(622, 200);
		speaker.playTone(659, 400);
		speaker.playTone(493, 200);
		speaker.playTone(415, 200);

		speaker.playTone(622, 400);
		speaker.playTone(622, 200);
		speaker.playTone(659, 400);
		speaker.playTone(493, 200);
		speaker.playTone(415, 200);
		
		speaker.playTone(622, 800);
		speaker.playTone(622, 200);
		speaker.playTone(659, 200);
		speaker.playTone(622, 200);
		speaker.playTone(493, 200);
		speaker.playTone(415, 200);

		label.setText("Done!");
	}

	public static void main(String[] args) {
		
		
		Thread t = new Thread(new FollowLineTest());
		t.start();
 	}
}
