package global.unet.messages;

public class PongBuilderFabrice implements MessageBuilderFabrice<Pong> {

    final MessagePreBuilder messagePreBuilder;

    public PongBuilderFabrice(MessagePreBuilder messagePreBuilder) {
        this.messagePreBuilder = messagePreBuilder;
    }

    @Override
    public BaseMessage.BaseBuilder<Pong> builder() {
        return Pong.builder(messagePreBuilder::fillMessageAsResponse);
    }
}
