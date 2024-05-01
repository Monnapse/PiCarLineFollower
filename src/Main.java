import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {
        GpioController controller = GpioFactory.getInstance();
        ButtonsManager buttonManager = new ButtonsManager(controller);
        Pin redSwitch = RaspiPin.GPIO_26;

        buttonManager.ButtonHold(redSwitch, ButtonsManager.Callback(){
            
        });
    }
}