package global.unet.service.Receiver;

import global.unet.id.UnionId;
import global.unet.messages.ContentHolders;
import global.unet.messages.FindContentHolders;
import global.unet.messages.Message;
import global.unet.service.KadContentRouter;
import global.unet.service.SearchResult;
import global.unet.service.UnidRouter;

import java.util.function.Consumer;

/**
 * Содержит логику работы с контентом
 */
public class RegularReceiver extends UnionRouterReceiver {

    private final KadContentRouter contentRouter;

    public RegularReceiver(UnidRouter unidRouter, Consumer<Message> messageSender, KadContentRouter contentRouter) {
        super(unidRouter, messageSender);
        this.contentRouter = contentRouter;
    }


    public void handle(FindContentHolders findContentHolders){
        SearchResult searchResult = contentRouter.contentLookup((UnionId) findContentHolders.getSource());
        //тут пихаем результат в сообщение
        ContentHolders contentHolders = new ContentHolders();

        messageSender.accept(null);
    }

    public void handle(ContentHolders contentHolders){

    }


}
