package global.unet.node;

import global.unet.id.NetworkId;
import global.unet.routing.table.NodeInfo;

import java.util.List;

/**
 * Самая распространенная нода участник
 *
 */
//Todo точно ли должен расширять интерфейсы или может просто содержать в себе has RoutingNode и StorageNode, ведь придется имплементировать интерфейсы
public interface RegularNode extends RoutingNode, StorageNode {

    public List<NodeInfo> lookupContentStorages(NetworkId networkId);


}
