package global.unet.domain.notitifier;

public interface Notifier<T> {

    void notify(T message);
}
