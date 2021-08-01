package global.unet.messages.builders;

import global.unet.messages.Message;

public interface MessageBuilder<T extends Message>{
    T build();
}
