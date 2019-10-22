package global.unet.uname;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;

public interface UnameResolver {
    UnionId resolve(NetworkId networkId);
}
