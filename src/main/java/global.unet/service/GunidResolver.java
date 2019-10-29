package global.unet.service;

import global.unet.id.GlobalUnionId;
import global.unet.id.UnionId;

/**
 * Отличие от UNid в том, чтот тот резолвит только Uname -> в GUNID
 */
public interface GunidResolver {
    UnionId gunidToUnid(GlobalUnionId globalUnionId);
}
