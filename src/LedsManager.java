import com.pi4j.io.gpio.*;

public class LedsManager {
    //GpioPinDigitalOutput redLED = controller.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.LOW);
    private GpioController controller;
    //private boolean DEBUGGING = true;

    public LedsManager(GpioController controller) {
        this.controller = controller;
    }

    public void BlinkLed(GpioPinDigitalOutput LED, int intervals, int millis) throws InterruptedException {
        do{
            LED.setState(PinState.HIGH);
            Thread.sleep(millis);
            LED.setState(PinState.LOW);
            intervals -= 1;
        } while (intervals > 0);
    }
}
