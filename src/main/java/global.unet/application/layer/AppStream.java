package global.unet.application.layer;

/**
 * Стрим - выходной/входной поток для Событий
 * на этот поток могут подписаться потребители и реагировать
 * из потоков могут быть составлен пути/пайпы, чтобы было видно как данные ходят по приложению
 * например
 * httpStream -> MessageReceiver -> ConcreteHandlers -> Out -> HttpOut
 * -> errorStream  -> statisticsManager
 *                 -> LoggerManager -> PersistenceManager
 * -> statisticsStream -> statisticsManager
 * -> statisticsManager -> PersistenceManager
 */
public interface AppStream {
}
