import ShefRobot.*;

public class Sensing {
	private Robot robot;
	private ColorSensor sensor

	public boolean isBlack() {

	}

	public boolean isBlue() {

	}

	public void initRobot(Robot r) {
		robot = r;
		sensor = robot.getColorSensor(Sensor.port.S1)
	}
}