package cases;

import global.unet.domain.id.UnionId;
import global.unet.domain.messages.Message;
import util.UnionGenerator;
import util.fake.node.NodeInfoPairGenerator;
import util.fake.notifier.FakeNotifierDrivenAdaptor;

/**
 * Init^
 * PairMessageGenerator, NodeInfoPairGenerator
 * Basic context for 2 nodes Conversation:
 * node Sender/Source and Node Receiver/Destination
 * and Naming as *NodeSource NodeDestination
 */
public class Base2NodesUseCaseContext {

    protected NodeInfoPairGenerator nodeInfoPairGenerator;
    protected FakeNotifierDrivenAdaptor<Message> listNotifier;
    protected UnionId networkId;
    protected UnionGenerator unionGenerator;

    public void init() {
        listNotifier = new FakeNotifierDrivenAdaptor<>();
        unionGenerator = new UnionGenerator();
        nodeInfoPairGenerator = new NodeInfoPairGenerator(unionGenerator.constantId(), unionGenerator.constantId2());
        UnionGenerator unionGenerator = new UnionGenerator();
        networkId = unionGenerator.createKademliaIdByTemplate(1, (byte) 32, (byte) 0);
    }

    public NodeInfoPairGenerator getNodeInfoPairGenerator() {
        return nodeInfoPairGenerator;
    }

    public FakeNotifierDrivenAdaptor<Message> getListNotifier() {
        return listNotifier;
    }

    public UnionId getNetworkId() {
        return networkId;
    }

    public UnionGenerator getUnionGenerator() {
        return unionGenerator;
    }
}
