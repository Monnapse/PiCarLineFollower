import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.Gpio;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {
        //final boolean DEBUGGING = true;
        GpioController controller = GpioFactory.getInstance();
        LineFollowerAlgorithm LFA = new LineFollowerAlgorithm();

        // WORK IN PROGRESS FOR LATER
        //ButtonsManager buttonManager = new ButtonsManager(controller);
        //LedsManager ledManager = new LedsManager(controller);

        // GPIOS
        //Switches
        GpioPinDigitalInput redSwitch = controller.provisionDigitalInputPin(RaspiPin.GPIO_26);
        GpioPinDigitalInput greenSwitch = controller.provisionDigitalInputPin(RaspiPin.GPIO_25);
        // LED's
        GpioPinDigitalOutput redLed = controller.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.LOW);
        GpioPinDigitalOutput greenLed = controller.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW);

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

        while (true) {
            int[] sensorData = new int[5];
            sensorData = lightSensor.readSensorValues();
            double linePosition = LFA.GetLinePosition(sensorData);
            //System.out.printf("%n%d, %d, %d, %d, %d", sensorData[0],sensorData[1],sensorData[2],sensorData[3],sensorData[4]);

            Thread.sleep(250);
        }
    }
}