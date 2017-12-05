import ShefRobot.*;

public class Assignment3 {
	public static void main(String[] args) {
		Robot robot = new Robot();

		Motor leftMotor = robot.getLargeMotor(Motor.Port.B);
		Motor rightMotor = robot.getLargeMotor(Motor.Port.C);
		//Motor gripMotor = robot.getMediumMotor(Motor.Port.A);
		Speaker speaker = robot.getSpeaker();
		ColorSensor sensor = robot.getColorSensor(Sensor.Port.S1);
		TouchSensor button = robot.getTouchSensor(Sensor.Port.S2);

		leftMotor.setSpeed(360);
		rightMotor.setSpeed(360);

		ColorSensor.Color currentColor = sensor.getColor();

		for (int i=0; i<1000; i++) {
			currentColor = sensor.getColor();
			if (button.isTouched()) 
				break;

			if (currentColor != ColorSensor.Color.BLACK) {
				System.out.println("not black");
				robot.sleep(1000);
			}

			else {
				System.out.println("black");
				robot.sleep(1000);
			}

		robot.close();
		}
	}
}