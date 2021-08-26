package global.unet.domain.receiver;

import global.unet.domain.messages.Message;

/**
 * Mapping Event2EventHandler
 * Command - сценарий использования,
 * CommandBus оркестрирует процесс выполнения Command
 */
public interface MessageBusDriver {

    <T extends Message> void handle(T message);
}
