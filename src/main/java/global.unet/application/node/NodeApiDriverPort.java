package global.unet.application.node;

import global.unet.domain.protocol.ping.PingMessageRequest;
import global.unet.domain.protocol.ping.PingMessageResponse;

public interface NodeApiDriverPort {
    PingMessageResponse ping (PingMessageRequest pingMessageRequest);
}
