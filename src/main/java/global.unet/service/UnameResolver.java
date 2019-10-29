package global.unet.service;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;

/**
 * resolve uname -> unionId
 */
public interface UnameResolver {
    UnionId resolve(NetworkId networkId);
}
