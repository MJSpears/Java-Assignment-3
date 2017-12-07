import Shefrobot.*;

public class Movement {
	private Robot robot;
	private Motor leftMotor;
	private Motor rightMotor;

	public void StartForward() {

	}

	public void StartBackward() {

	}

	public void RotateLeft() {

	}

	public void RotateRight() {

	}

	public void StopMovement() {

	}

	public void initRobot(Robot r) {
		robot = r;
		leftMotor = robot.getLargeMotor(Motor.Port.B);
		rightMotor = robot.getLargeMotor(Motor.Port.C);
	}
}