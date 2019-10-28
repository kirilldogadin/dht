package global.unet.routing.table;

import global.unet.config.NodeConfiguration;
import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.uname.UnameResolver;

import java.util.Optional;
import java.util.Set;

/**
 * Implementation of RoutingTable as KademliaTree
 */
//TODO мб конфиг отдельный для этого класса?
public class XorTreeRoutingTable implements KademliaRoutingTable {

    private final UnionId selfNodeUnionId;
    private final Bucket[] buckets;
    private final UnameResolver resolver;
    private final NodeConfiguration nodeConfiguration;
    private final BucketFabric bucketFabric;

    public XorTreeRoutingTable(UnionId selfNodeUnionId, UnameResolver resolver, NodeConfiguration nodeConfiguration) {
        this.selfNodeUnionId = selfNodeUnionId;
        this.resolver = resolver;
        this.nodeConfiguration = nodeConfiguration;

        //Todo фабрику другое место?
        bucketFabric = new BucketFabric(() ->
                nodeConfiguration.getCapacity() == null
                        ? BucketFabric.MAX_CAPACITY_DEFAULT
                        : nodeConfiguration.getCapacity());

        this.buckets = initializeBuckets(selfNodeUnionId.getSpaceOfUnionId());
    }

    private Bucket[] initializeBuckets(int bucketsCount) {
        Bucket[] buckets = new Bucket[bucketsCount];
        for (int i = 0, bucketsLength = buckets.length; i < bucketsLength; i++) {
            buckets[i] = bucketFabric.createBucket(i);
        }
        return buckets;
    }

    @Override
    public Set<NodeInfo> findClosestUnionIds(UnionId unid) {
        return findBucket(unid).getNodeInfoList();
    }

    @Override
    public Set<NodeInfo> findClosestUnionIds(NetworkId networkId) {
        return findClosestUnionIds(resolveNetworkId(networkId));
    }

    @Override
    public void addNode(NodeInfo nodeInfo) {
        Optional.ofNullable(nodeInfo)
                .map(NodeInfo::getNetworkId)
                .map(this::resolveNetworkId)
                .map(this::findBucket)
                //TODO логика ЕСЛИ найден . Должен быть всегда найден
                .ifPresent(bucket -> bucket.add(nodeInfo));
    }


    //Todo public на private

    /**
     * суть алгоритма в документе
     * первый отличающийся бит от собственного UNID
     *
     * @param unid аргумент
     * @return
     */
    public Bucket findBucket(UnionId unid) {
        //compute distance
        byte[] distanceUnid = unid.computeDistance(selfNodeUnionId);
        //2. number of first bit of distance is number of k-buckets.
        return buckets[getBucketNumberForUnid(distanceUnid)];

    }

    //Todo public на private

    /**
     * Принимает дистанцию
     * вычисляет номер ответсвенного бакета для unid
     * ищет старший бит
     * <p>
     * Big endian порядок
     * <p>
     * суть метода
     * в каждом байте перебираем биты, пока не
     * байт & (2 в степени 8 - номер бита)
     * Пример для байта     0100 0111
     * bit number = 0;
     * 2^(7 - bitNumber) =
     * 2^7 = 128 = 1000 0000
     * 1000 0000 & 0100 0111 = 0000 0000 -> равно нулю, не наш случай
     * <p>
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
