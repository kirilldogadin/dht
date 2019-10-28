package global.unet.node;

import global.unet.id.NetworkId;
import global.unet.routing.table.NodeInfo;

import java.util.Set;

//Todo другой тип или вынести его и сделать общим
public interface RoutingNode extends Node {

    //Todo инкапсуляция явно нарушено
    Set<NodeInfo> findClosestNode(NetworkId networkId);

    void addNode(NodeInfo nodeInfo);

}
