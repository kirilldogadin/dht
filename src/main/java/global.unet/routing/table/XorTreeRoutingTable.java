package global.unet.routing.table;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.uname.UnameResolver;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of RoutingTable as KademliaTree
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

    /**
     * суть алгоритма в документе
     * первый отличающийся бит от собственного UNID
     * @param unid аргумент
     * @return
     */
    public Bucket findBucket(UnionId unid) {
        //compute distance
        byte[] distanceUnid = unid.computeDistance(selfNodeUnionId);
        //2. number of first bit of distance is number of k-buckets.
        return buckets[getBucketNumberForUnid(distanceUnid)];

    }

    /**
     * Принимает дистанцию
     * вычисляет номер ответсвенного бакета для unid
     * ищет старший бит
     *
     * Big endian порядок
     *
     * суть метода
     * в каждом байте перебираем биты, пока не
     * байт & (2 в степени 8 - номер бита)
     * Пример для байта     0100 0111
     * bit number = 0;
     * 2^(7 - bitNumber) =
     * 2^7 = 128 = 1000 0000
     * 1000 0000 & 0100 0111 = 0000 0000 -> равно нулю, не наш случай
     *
     * bit number = 1;
     * 2^(7 - 1)
     * 2^6 = 64 = 0100 0000
     * 0100 0000 & 0100 0111 = 0100 0000 -> не равно нулю, старший бит найден
     *
     * @param unid вычисленная дистанция xor метрикой
     * @return
     */
    public int getBucketNumberForUnid(byte[] unid) {

        for (int byteNumber = 0; byteNumber < unid.length; byteNumber++) { // для каждого байта
            for (int bitNumber = 0; bitNumber < 8; bitNumber++) { // для каждого бита в байте
                if ((unid[byteNumber] & (1 << (7 - bitNumber))) != 0)
                    return (byteNumber * 8) + bitNumber;
            }
        }
        return 0;
    }


    private UnionId resolveNetworkId(NetworkId networkId) {
        return resolver.resolve(networkId);
    }


}
