package global.unet.domain.receiver;

import global.unet.application.SearchResult;
import global.unet.domain.notitifier.NotifierDrivenPort;
import global.unet.domain.router.KadContentRouter;
import global.unet.domain.router.UnidRouter;
import global.unet.domain.id.UnionInfo;
import global.unet.domain.id.UnionId;
import global.unet.domain.messages.ContentHolders;
import global.unet.domain.messages.FindContentHolders;
import global.unet.domain.messages.Message;

/**
 * Содержит логику работы с контентом
 */
public class Regular extends MessageReceiver {

    private final KadContentRouter contentRouter;

    public Regular(UnidRouter unidRouter, NotifierDrivenPort<Message> messageSender, UnionInfo unionInfo, KadContentRouter contentRouter) {
        super(unidRouter, messageSender, unionInfo);
        this.contentRouter = contentRouter;
    }


    public void handle(FindContentHolders findContentHolders){
        SearchResult searchResult = contentRouter.contentLookup((UnionId) findContentHolders.getSource());
        //тут пихаем результат в сообщение
        ContentHolders contentHolders = new ContentHolders();

        messageNotifierDrivenPort.notify(null);
    }

    public void handle(ContentHolders contentHolders){

    }


}
