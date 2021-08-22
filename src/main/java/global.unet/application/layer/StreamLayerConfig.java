package global.unet.application.layer;

/**
 * пример конфига
 * ttpStream -> MessageReceiver -> ConcreteHandlers -> Out -> HttpOut
 * -> errorStream  -> statisticsManager
 *                 -> LoggerManager -> PersistenceManager
 * -> statisticsStream -> statisticsManager
 * -> statisticsManager -> PersistenceManager
 */
// TODO может как DSL читать из файла настройки - а-ля аспектное программирование
// TODO статический анализатор смотрит, чтобы все классы НЕ ИНЖЕКТИЛИСЬ НАПРЯМУЮ, А ТОЛЬКО ЧЕРЕЗ СЛОИ
// TODO свой фреймворк?
public interface StreamLayerConfig {
}
