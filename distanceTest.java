import Shefrobot.*;
public class distanceTest {
	public static void main(String[] args) {
		public Robot myRobot = new Robot();
		public UltrasonicSensor myDistance = myRobot.getUltrasonicSensor(Sensor.Port.S2);
	
		while(true) {
			System.out.println(myDistance.getDistance());
			myRobot.sleep(1000);
		}
	}
}