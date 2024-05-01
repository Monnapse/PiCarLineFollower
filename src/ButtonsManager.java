import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonsManager {
    private GpioController controller;

    public ButtonsManager(GpioController controller) {
        this.controller = controller;
    }

    public void ButtonHold(Pin pin, int millis, Callback callback) {
        GpioPinDigitalInput buttonSwitch = controller.provisionDigitalInputPin(pin);
        buttonSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                System.out.println(event.getState());
            }
        });
        return;
    }
    public void ButtonTaps(Pin pin, int taps, Callback callback) {
        return;
    }
    public interface Callback {
        void call();
    }
}
