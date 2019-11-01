package global.unet.messages;

public interface Message<T extends Message> {

    //Builder<? extends T> builder1();

    public interface Builder<T>{
        T build();
    }
}
