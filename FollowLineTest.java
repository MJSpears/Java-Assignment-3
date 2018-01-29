import ShefRobot.*;

public class FollowLineTest {
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
	private static GyroSensor myAngle = myRobot.getGyroSensor(Sensor.Port.S3);


	//private static ColorSensor.Color[] colorGrid;


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
				System.out.println(myAngle.getAngle());
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

		returnHome();
	}

	public static void returnHome() {

		System.out.println(leftMotor.getTachoCount());
		System.out.println(rightMotor.getTachoCount());

		leftMotor.rotate(-leftMotor.getTachoCount(), true);
		rightMotor.rotate(-rightMotor.getTachoCount());

		//while(Math.abs(myAngle.getAngle()) > 185 || Math.abs(myAngle.getAngle()) < 175) {
//		//	System.out.println(myAngle.getAngle());
//		//	leftMotor.forward();
//		//	rightMotor.backward();
//		//}
//
//		//leftMotor.resetTachoCount();
//
//		//leftMotor.forward();
//		//rightMotor.forward();
//
//		//while(leftMotor.getTachoCount() < 2000) {
//		//	myRobot.sleep(100);
//		//}
//
		//leftMotor.stop();
		//rightMotor.stop();
	}

	public static void main(String[] args) {
		myAngle.reset();
		findGrid();
		followLine();
		//followCircle();
		myRobot.close();

	}
}
