import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

public class Main {
    // Car Configurations
    static final Pin RED_SWITCH = RaspiPin.GPIO_26;
    static final Pin GREEN_SWITCH = RaspiPin.GPIO_25;
    static final Pin RED_LED = RaspiPin.GPIO_27;
    static final Pin GREEN_LED = RaspiPin.GPIO_24;
    static final int MID_POINT = 55; // Point where it separates the line
    static boolean CarTurnedOn = false; // Does car turn on when it has power
    static boolean MotorsTurnedOff = false; // If motors work when car is turned on
    static final int CPS = 190; // Cycles per second

    // Servo Easing Configuration
    static Servo servo = new Servo();
    static final double MAX_ANGLE = 21.0;
    //static final double M2 = 7;
    //static final double M1 = 1;
    static final double TURN_RATE = 2.5; // Turn rate, lower = more turn
    static final int STRAIGHT_VALUE = 0; // Angle offset for going straight
    static final EasingType SERVO_EASE_TYPE = EasingType.Quart;
    static final EasingDirection SERVO_EASE_DIRECTION = EasingDirection.Out;
    static final double SERVO_EASE_DURATION = 0.01;

    // Motor Easing Configuration
    static Motors motors = new Motors();
    static final double SPEED_RATE = 8; // Higher = faster speed
    static final EasingType MOTOR_EASE_TYPE = EasingType.Quart;
    static final EasingDirection MOTOR_EASE_DIRECTION = EasingDirection.Out;
    static final double MOTOR_EASE_DURATION = 0.01;

    // Easing Services
    static Easing servoEase = new Easing(SERVO_EASE_TYPE, SERVO_EASE_DIRECTION, SERVO_EASE_DURATION);
    static Easing motorEase = new Easing(MOTOR_EASE_TYPE, MOTOR_EASE_DIRECTION, MOTOR_EASE_DURATION);

    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {

        //final boolean DEBUGGING = true;
        GpioController controller = GpioFactory.getInstance();
        LineFollowerAlgorithm LFA = new LineFollowerAlgorithm(MID_POINT);

        // GPIOS
        //Switches
        GpioPinDigitalInput redSwitch = controller.provisionDigitalInputPin(RED_SWITCH);
        GpioPinDigitalInput greenSwitch = controller.provisionDigitalInputPin(GREEN_SWITCH);
        // LED's
        GpioPinDigitalOutput redLed = controller.provisionDigitalOutputPin(RED_LED, PinState.LOW);
        GpioPinDigitalOutput greenLed = controller.provisionDigitalOutputPin(GREEN_LED, PinState.LOW);

        // Sensor's
        LightSensorModule lightSensor = new LightSensorModule();

        redSwitch.setDebounce(250);
        greenSwitch.setDebounce(250);

        System.out.println("PI Car Successfully Configured");

        reset();

        // Toggle car on/off
        greenSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) {
                    CarTurnedOn = !CarTurnedOn;

                    reset();

                    if (CarTurnedOn) {
                        greenLed.setState(PinState.HIGH);
                    }
                    else {greenLed.setState(PinState.LOW);}
                }
            }
        });

        // Toggle motors off/on
        redSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) {
                    MotorsTurnedOff = !MotorsTurnedOff;
                    if (MotorsTurnedOff) {
                        redLed.setState(PinState.HIGH);
                    }
                    else {redLed.setState(PinState.LOW);}
                }
            }
        });

        double neededAngle = 0; // For debugging
        while (true) {
            // Updating the ease/tween method
            servoEase.update();
            motorEase.update();

            if (CarTurnedOn) {
                // Get sensor data
                int[] sensorData;
                sensorData = lightSensor.readSensorValues();
                final double linePosition = LFA.GetLinePosition(sensorData);

                // Get line position
                if (linePosition != -1.0) {
                    double lineSplitInversion = LFA.GetLinePositionSplitInversion(linePosition);
                    double speed = LFA.GetSpeed(lineSplitInversion, SPEED_RATE);
                    double angle = LFA.GetAngleV2(lineSplitInversion, TURN_RATE, MAX_ANGLE);//LFA.GetAngleV1(lineSplitInversion, M1, M2, MAX_ANGLE, TURN_RATE, STRAIGHT_VALUE);

                    //if (lineSplitInversion != 0) {
                    //    angle = LFA.GetAngle(lineSplitInversion, MIN_ANGLE, MAX_ANGLE, TURN_RATE, STRAIGHT_VALUE);
                    //}

                    System.out.printf("%n Line Position: %s %n Line Split Inversion: %s %n Servo Angle: %s", linePosition, lineSplitInversion, angle);

                    // Motor
                    int desiredSpeed = (int) Math.min(Math.abs(speed), 100);
                    //System.out.printf("%n Speed: %s, Desired Speed: %s %n", speed, desiredSpeed);
                    motorEase.changeValue(desiredSpeed);

                    // Servo
                    int desiredAngle = (int) angle;
                    servoEase.changeValue(desiredAngle);
                    neededAngle = angle;
                    //System.out.println("Setting servo angle to: " + String.valueOf(angle));
                    //servo.setServoAngle((int) angle);
                }

                // Update Servo
                int actualAngle = (int) servoEase.getActualValue();
                //System.out.printf("%n End Angle: %s, Actual Angle: %s %n", neededAngle, actualAngle);
                servo.setServoAngle((int) actualAngle);

                // Update Motor
                int actualSpeed = (int) motorEase.getActualValue();
                //System.out.printf("%n Speed: %s%n", actualSpeed);
                if (!MotorsTurnedOff) {
                    motors.runMotors("forward", actualSpeed);
                } else {
                    motors.runMotors("forward", 0);
                }
            }

            Thread.sleep((1/CPS)*1000);
            //System.out.println("CYCLED");
            //Thread.sleep(10);
        }
    }
    public static void reset() {
        servo.setServoAngle(STRAIGHT_VALUE);
        motors.runMotors("forward", 0);
        servoEase.setValue(0, STRAIGHT_VALUE);
        motorEase.setValue(0, 0);
    }
}