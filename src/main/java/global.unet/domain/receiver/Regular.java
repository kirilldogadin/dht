package global.unet.domain.receiver;

import global.unet.application.SearchResult;
import global.unet.domain.notitifier.Notifier;
import global.unet.router.KadContentRouter;
import global.unet.router.UnidRouter;
import global.unet.domain.id.UnionNodeInfo;
import global.unet.domain.id.UnionId;
import global.unet.domain.messages.ContentHolders;
import global.unet.domain.messages.FindContentHolders;
import global.unet.domain.messages.Message;

/**
 * Содержит логику работы с контентом
 */
public class Regular extends MessageReceiver {

    private final KadContentRouter contentRouter;

    public Regular(UnidRouter unidRouter, Notifier<Message> messageSender, UnionNodeInfo unionNodeInfo, KadContentRouter contentRouter) {
        super(unidRouter, messageSender, unionNodeInfo);
        this.contentRouter = contentRouter;
    }


    public void handle(FindContentHolders findContentHolders){
        SearchResult searchResult = contentRouter.contentLookup((UnionId) findContentHolders.getSource());
        //тут пихаем результат в сообщение
        ContentHolders contentHolders = new ContentHolders();

        messageNotifier.notify(null);
    }

    public void handle(ContentHolders contentHolders){

    }


}
