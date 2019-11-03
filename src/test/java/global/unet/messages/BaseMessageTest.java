package global.unet.messages;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;

public class BaseMessageTest {


    @Test
    public void messageTypeDefinition(){
        //Todo свой тип для провеврки
        assertFalse(BaseMessage.BaseBuilder.isRequest(Pong.class));
    }
}
