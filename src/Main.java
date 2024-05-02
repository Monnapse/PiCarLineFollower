import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.Gpio;

import java.io.IOException;

public class Main {
    // Configurations
    static final Pin RED_SWITCH = RaspiPin.GPIO_26;
    static final Pin GREEN_SWITCH = RaspiPin.GPIO_25;
    static final Pin RED_LED = RaspiPin.GPIO_27;
    static final Pin GREEN_LED = RaspiPin.GPIO_24;
    static final double MAX_ANGLE = 25.0;
    static final double MIN_ANGLE = 1;
    static final double RATE = 35;
    static final int MIDPOINT = 50;
    static boolean CarTurnedOn = false;

    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {
        //final boolean DEBUGGING = true;
        GpioController controller = GpioFactory.getInstance();
        LineFollowerAlgorithm LFA = new LineFollowerAlgorithm(MIDPOINT);

        // WORK IN PROGRESS FOR LATER
        //ButtonsManager buttonManager = new ButtonsManager(controller);
        //LedsManager ledManager = new LedsManager(controller);

        // GPIOS
        //Switches
        GpioPinDigitalInput redSwitch = controller.provisionDigitalInputPin(RED_SWITCH);
        GpioPinDigitalInput greenSwitch = controller.provisionDigitalInputPin(GREEN_SWITCH);
        // LED's
        GpioPinDigitalOutput redLed = controller.provisionDigitalOutputPin(RED_LED, PinState.LOW);
        GpioPinDigitalOutput greenLed = controller.provisionDigitalOutputPin(GREEN_LED, PinState.LOW);

        // Motor's
        Motors motors = new Motors();

        // Servo's
        Servo servo = new Servo();

        // Sensor's
        LightSensorModule lightSensor = new LightSensorModule();

        redSwitch.setDebounce(250);
        greenSwitch.setDebounce(250);

        System.out.println("PI Car Successfully Configured");

        //buttonManager.ButtonHold(redSwitch, 2,new ButtonsManager.ButtonListener() {
        //    @Override
        //    public void handleButtonCallback() {
        //        System.out.println("Held button for 2 seconds");
        //        try {
        //            ledManager.BlinkLed(redLed, 2, 500);
        //        } catch (InterruptedException e) {
        //            throw new RuntimeException(e);
        //        }
        //    }
        //});

        greenSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) {
                    CarTurnedOn = !CarTurnedOn;
                    if (CarTurnedOn) {greenLed.setState(PinState.HIGH);}
                    else {greenLed.setState(PinState.LOW);}
                }
            }
        });

        while (true) {
            if (CarTurnedOn) {
                int[] sensorData;
                sensorData = lightSensor.readSensorValues();
                double linePosition = LFA.GetLinePosition(sensorData);

                if (linePosition != -1.0) {
                    double lineSplitInversion = LFA.GetLinePositionSplitInversion(linePosition);
                    double angle = 0;

                    if (lineSplitInversion != 0) {
                        angle = LFA.GetAngle(lineSplitInversion, MIN_ANGLE, MAX_ANGLE, RATE);
                    }
                    System.out.printf("%n Line Position: %.2f %n Line Split Inversion: %.2f %n Servo Angle: %.2f", linePosition, lineSplitInversion, angle);

                    servo.setServoAngle((int) angle);
                }
            }

            Thread.sleep(250);
        }
    }
}