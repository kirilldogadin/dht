package global.unet.messages;

import global.unet.id.NodeInfoHolder;
import org.junit.Test;
import util.TestUtil;

import static java.util.Arrays.asList;

public class FabricGeneratorTest extends TestUtil {


    @Test
    public void generateFabricTest(){
        NodeInfoHolder nodeInfoHolder = nodeInfoHolder();
        BaseMessage.MessageBuilderFabric messageBuilderFabric = BuilderFabricGenerator.createFabricBuilder(Pong.Builder.class, nodeInfoHolder);
        Class<?>[] interfaces = messageBuilderFabric.getClass().getInterfaces();
        if (!asList(interfaces)
                .contains(BaseMessage.MessageBuilderFabric.class))
        throw new RuntimeException("type"+ Pong.BuilderFabric.class +" not Found");
    }
}
