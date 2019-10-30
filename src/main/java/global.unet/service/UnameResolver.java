package global.unet.service;

import global.unet.id.BaseId;
import global.unet.id.UnionId;

/**
 * resolve uname -> unionId
 */
public interface UnameResolver {
    UnionId resolve(BaseId baseId);
}
