import ShefRobot.*;

public class GyroTest {
	public static void main(String[] args) {
		Robot robot = new Robot();
		GyroSensor sensor = robot.getGyroSensor(Sensor.Port.S3);
	
		while(true) {
			System.out.println(sensor.getAngle());
			robot.sleep(1000);
		}
	}
}