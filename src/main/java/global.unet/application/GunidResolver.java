package global.unet.application;

import global.unet.domain.id.GlobalUnionId;
import global.unet.domain.id.UnionId;

/**
 * Отличие от UNid в том, чтот тот резолвит только Uname -> в GUNID
 */
public interface GunidResolver {
    UnionId gunidToUnid(GlobalUnionId globalUnionId);
}
