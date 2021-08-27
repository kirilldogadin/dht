package util.fake;

import global.unet.domain.messages.Message;
import global.unet.domain.notitifier.NotifierDrivenPort;

/**
 * Just print message to Console
 */
public class ConsoleNotifierDrivenAdaptor<T extends Message> implements NotifierDrivenPort<T> {

    @Override
    public void notify(T message) {
        System.out.println(message);
    }
}
