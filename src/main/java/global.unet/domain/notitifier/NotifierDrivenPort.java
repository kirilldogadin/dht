package global.unet.domain.notitifier;

public interface NotifierDrivenPort<T> {

    void notify(T message);
}
