import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonsManager {
    private GpioController controller;
    //private boolean DEBUGGING = true;

    public ButtonsManager(GpioController controller) {
        this.controller = controller;
    }
    //public void SetDebugging

    public void ButtonHold(Pin pin, int millis, ButtonListener callback) {
        GpioPinDigitalInput buttonSwitch = controller.provisionDigitalInputPin(pin);
        buttonSwitch.setDebounce(250);

        buttonSwitch.addListener(new GpioPinListenerDigital() {
            long currentMilliDown;
            boolean isDown = false;

            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) {
                    // Switch is down
                    currentMilliDown = System.currentTimeMillis();
                    System.out.println(currentMilliDown);
                    isDown = true;
                } //else if (event.getState() == PinState.LOW) {
                //    // Switch is up
                //    if (System.currentTimeMillis() - currentMilliDown ) {
                //    }
                //}

                do {
                    if (isDown && System.currentTimeMillis() - currentMilliDown > millis) {
                        callback.handleButtonCallback();
                        isDown = false;
                    } //else if (isDown) {
                        //System.out.printf("%n User has been holding button for %s milliseconds", System.currentTimeMillis() - currentMilliDown);
                    //}
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } while (isDown);
            }
        });
    }
    public void ButtonTaps(Pin pin, int taps, ButtonListener callback) {

    }
    public interface ButtonListener {
        void handleButtonCallback();
    }
}
