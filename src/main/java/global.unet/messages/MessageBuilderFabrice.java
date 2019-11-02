package global.unet.messages;

public interface MessageBuilderFabrice<T extends Message> {

    BaseMessage.BaseBuilder<T> builder();

}
