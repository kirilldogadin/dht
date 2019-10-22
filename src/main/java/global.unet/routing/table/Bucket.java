package global.unet.routing.table;

import java.util.List;

/**
 * содержит список UNID
 * k-bucket
 *
 */
interface Bucket {
    //todo maybe use Set instead of List?
    List<NodeInfo> getPeers();
    void add(NodeInfo peer);
}
