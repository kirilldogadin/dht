package global.unet.application;

import global.unet.domain.id.BaseId;
import global.unet.domain.id.UnionId;

/**
 * resolve uname -> unionId
 */
public interface UnameResolver {
    UnionId resolve(BaseId baseId);
}
