package global.unet.application.receiver;

import global.unet.application.SearchResult;
import global.unet.router.KadContentRouter;
import global.unet.router.UnidRouter;
import global.unet.domain.id.UnionNodeInfo;
import global.unet.domain.id.UnionId;
import global.unet.domain.messages.ContentHolders;
import global.unet.domain.messages.FindContentHolders;
import global.unet.domain.messages.Message;

import java.util.function.Consumer;

/**
 * Содержит логику работы с контентом
 */
public class RegularReceiver extends UnionRouterReceiver {

    private final KadContentRouter contentRouter;

    public RegularReceiver(UnidRouter unidRouter, Consumer<Message> messageSender, UnionNodeInfo unionNodeInfo, KadContentRouter contentRouter) {
        super(unidRouter, messageSender, unionNodeInfo);
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
