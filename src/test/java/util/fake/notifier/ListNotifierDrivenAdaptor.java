package util.fake.notifier;

import global.unet.domain.messages.Message;
import global.unet.domain.notitifier.NotifierDrivenPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Just print message to Console
 */
public class ListNotifierDrivenAdaptor<T extends Message> implements NotifierDrivenPort<T> {

    List<T> messages = new ArrayList<>();

    @Override
    public void notify(T message) {
       messages.add(message);
    }

    public List<T> getMessages() {
        return messages;
    }
}
