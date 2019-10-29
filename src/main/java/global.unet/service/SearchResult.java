package global.unet.service;

import global.unet.structures.NodeInfo;

import java.util.Set;

public class SearchResult {

    final Set<NodeInfo> nodeInfoSet;
    final boolean holder;

    /**
     *
     * @param nodeInfoSet список нод владельцев или ближайших
     * @param holder признак являяется ли список списком владельцев. True да, false - это closest ноды
     */
    public SearchResult(Set<NodeInfo> nodeInfoSet, boolean holder) {
        this.nodeInfoSet = nodeInfoSet;
        this.holder = holder;
    }
}
