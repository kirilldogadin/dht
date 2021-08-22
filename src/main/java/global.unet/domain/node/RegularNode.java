package global.unet.domain.node;

import global.unet.domain.id.BaseId;
import global.unet.domain.structures.NodeInfo;

import java.util.List;

/**
 * Самая распространенная нода участник
 *
 */
//Todo точно ли должен расширять интерфейсы или может просто содержать в себе has RoutingNode и StorageNode, ведь придется имплементировать интерфейсы
public interface RegularNode extends RoutingNode, StorageNode {

    public List<NodeInfo> lookupContentStorages(BaseId baseId);


}
