import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LedsManager {
    GpioPinDigitalOutput redLED = controller.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.LOW);
}
