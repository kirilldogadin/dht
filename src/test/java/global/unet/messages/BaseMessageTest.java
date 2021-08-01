package global.unet.messages;

import global.unet.messages.builders.BaseMessageBuilder;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;

public class BaseMessageTest {


    @Test
    public void messageTypeDefinition(){
        //Todo свой тип для провеврки
        assertFalse(BaseMessageBuilder.isRequest(Pong.class));
    }
}
