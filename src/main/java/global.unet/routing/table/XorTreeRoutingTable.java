package global.unet.routing.table;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.uname.UnameResolver;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of RoutingTable as KademliaTree
 *
 */
public class XorTreeRoutingTable implements KademliaRoutingTable {

    private final UnionId selfNodeUnionId;
    private final Bucket[] buckets;
    private final UnameResolver resolver;

    public XorTreeRoutingTable(UnionId selfNodeUnionId, UnameResolver resolver) {
        this.selfNodeUnionId = selfNodeUnionId;
        this.buckets = new Bucket[selfNodeUnionId.getSpaceOfUnionId()];
        this.resolver = resolver;
    }

    @Override
    public List<NodeInfo> findClosestUnionIds(UnionId unid) {
        return findBucket(unid).getPeers();
    }

    @Override
    public List<NodeInfo> findClosestUnionIds(NetworkId networkId) {
        return findClosestUnionIds(resolveNetworkId(networkId));
    }

    @Override
    public void addNode(NodeInfo nodeInfo) {
        Optional.ofNullable(nodeInfo)
                .map(NodeInfo::getNetworkId)
                .map(this::resolveNetworkId)
                .map(this::findBucket)
                .ifPresent(bucket -> bucket.add(nodeInfo));
    }

    private Bucket findBucket(UnionId unid) {
        //asd. compute distance
        byte[] distanceUnid = unid.computeDistance(selfNodeUnionId);
        //2. number of first bit of distance is number of k-buckets.
        return buckets[bucketNumber(distanceUnid)];

    }

    //Todo This mock
    private int bucketNumber(byte[] unidBytes) {
        return 1;
    }

    private UnionId resolveNetworkId(NetworkId networkId) {
        return resolver.resolve(networkId);
    }


}
