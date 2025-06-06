# Детальный анализ архитектуры DHT проекта

## Общая статистика
- **Всего файлов**: 94
- **Всего строк кода**: 2706
- **Всего методов**: 140
- **Всего классов**: 60
- **Всего интерфейсов**: 34
- **Всего TODO**: 75
- **Количество пакетов**: 28

## Архитектурные слои
### Application Layer
Файлов: 24
#### Пакет: `global.unet.application`
- **/src/main/java/global.unet/application/GunidResolver.java** (12 строк)
  - Интерфейсы: GunidResolver
- **/src/main/java/global.unet/application/NestedUnionService.java** (15 строк)
  - Интерфейсы: NestedUnionService
- **/src/main/java/global.unet/application/SearchResult.java** (22 строк)
  - Классы: SearchResult
- **/src/main/java/global.unet/application/UnameResolver.java** (12 строк)
  - Интерфейсы: UnameResolver
#### Пакет: `global.unet.application.client`
- **/src/main/java/global.unet/application/client/Client.java** (8 строк)
  - Интерфейсы: Client
- **/src/main/java/global.unet/application/client/ClientService.java** (18 строк)
  - Классы: ClientService
- **/src/main/java/global.unet/application/client/RawSocketBlockingClient.java** (39 строк)
  - Классы: RawSocketBlockingClient
#### Пакет: `global.unet.application.layer`
- **/src/main/java/global.unet/application/layer/AppStream.java** (16 строк)
  - Интерфейсы: AppStream
- **/src/main/java/global.unet/application/layer/Bus.java** (8 строк)
  - Интерфейсы: Bus
- **/src/main/java/global.unet/application/layer/In.java** (6 строк)
  - Классы: In
- **/src/main/java/global.unet/application/layer/Out.java** (5 строк)
  - Классы: Out
- **/src/main/java/global.unet/application/layer/StreamLayerConfig.java** (16 строк)
  - Интерфейсы: StreamLayerConfig
#### Пакет: `global.unet.application.managers`
- **/src/main/java/global.unet/application/managers/LoggerManager.java** (5 строк)
  - Классы: LoggerManager
- **/src/main/java/global.unet/application/managers/PersistenceManager.java** (5 строк)
  - Классы: PersistenceManager
- **/src/main/java/global.unet/application/managers/StatisticsManager.java** (5 строк)
  - Классы: StatisticsManager
#### Пакет: `global.unet.application.node`
- **/src/main/java/global.unet/application/node/NodeApiDriverPort.java** (9 строк)
  - Интерфейсы: NodeApiDriverPort
- **/src/main/java/global.unet/application/node/NodeHolder.java** (5 строк)
  - Интерфейсы: NodeHolder
- **/src/main/java/global.unet/application/node/NodeStat.java** (5 строк)
  - Интерфейсы: NodeStat
#### Пакет: `global.unet.application.receiver`
- **/src/main/java/global.unet/application/receiver/SynchronousMessageBusDriverAdaptor.java** (33 строк)
  - Классы: SynchronousMessageBusDriverAdaptor
- **/src/test/java/global/unet/application/receiver/SynchronousMessageBusDriverAdaptorTest.java** (39 строк)
  - Классы: SynchronousMessageBusDriverAdaptorTest
#### Пакет: `global.unet.application.server`
- **/src/main/java/global.unet/application/server/BaseServer.java** (36 строк)
  - Классы: BaseServer
- **/src/main/java/global.unet/application/server/NioServer.java** (5 строк)
  - Классы: NioServer
- **/src/main/java/global.unet/application/server/RawSocketBlockingServer.java** (60 строк)
  - Классы: RawSocketBlockingServer
- **/src/main/java/global.unet/application/server/Server.java** (18 строк)
  - Интерфейсы: Server

### Domain Layer
Файлов: 59
#### Пакет: `global.unet.domain.config`
- **/src/main/java/global.unet/domain/config/NodeConfiguration.java** (31 строк)
  - Классы: NodeConfiguration
#### Пакет: `global.unet.domain.exception`
- **/src/main/java/global.unet/domain/exception/UnionConfigurationException.java** (8 строк)
  - Классы: UnionConfigurationException
- **/src/main/java/global.unet/domain/exception/UnionServerException.java** (8 строк)
  - Классы: UnionServerException
#### Пакет: `global.unet.domain.id`
- **/src/main/java/global.unet/domain/id/BaseId.java** (19 строк)
  - Интерфейсы: BaseId
- **/src/main/java/global.unet/domain/id/GlobalUnionId.java** (9 строк)
  - Интерфейсы: GlobalUnionId
- **/src/main/java/global.unet/domain/id/KademliaId.java** (66 строк)
  - Классы: KademliaId
- **/src/main/java/global.unet/domain/id/Uname.java** (9 строк)
  - Интерфейсы: Uname
- **/src/main/java/global.unet/domain/id/UnionId.java** (48 строк)
  - Интерфейсы: UnionId, Metric
- **/src/main/java/global.unet/domain/id/UnionInfo.java** (22 строк)
  - Классы: UnionInfo
- **/src/test/java/global/unet/domain/id/KademliaIdTest.java** (42 строк)
  - Классы: KademliaIdTest
#### Пакет: `global.unet.domain.messages`
- **/src/main/java/global.unet/domain/messages/BaseMessage.java** (70 строк)
  - Классы: BaseMessage
- **/src/main/java/global.unet/domain/messages/BaseMessageWithResource.java** (29 строк)
  - Классы: BaseMessageWithResource
- **/src/main/java/global.unet/domain/messages/ContentHolders.java** (5 строк)
  - Классы: ContentHolders
- **/src/main/java/global.unet/domain/messages/FindContentHolders.java** (18 строк)
  - Классы: FindContentHolders
- **/src/main/java/global.unet/domain/messages/InitReq.java** (24 строк)
  - Классы: InitReq
- **/src/main/java/global.unet/domain/messages/Message.java** (10 строк)
  - Интерфейсы: Message
- **/src/main/java/global.unet/domain/messages/ResourceResponse.java** (28 строк)
  - Классы: ResourceResponse
- **/src/main/java/global.unet/domain/messages/UnionBootstrap.java** (33 строк)
  - Классы: UnionBootstrap
- **/src/test/java/global/unet/domain/messages/BaseMessageTest.java** (14 строк)
  - Классы: BaseMessageTest
#### Пакет: `global.unet.domain.node`
- **/src/main/java/global.unet/domain/node/KademliaRegularNode.java** (110 строк)
  - Классы: KademliaRegularNode
- **/src/main/java/global.unet/domain/node/KademliaRoutingNode.java** (45 строк)
  - Классы: KademliaRoutingNode
- **/src/main/java/global.unet/domain/node/KademliaStorageNode.java** (53 строк)
  - Классы: KademliaStorageNode
- **/src/main/java/global.unet/domain/node/Node.java** (13 строк)
  - Интерфейсы: Node
- **/src/main/java/global.unet/domain/node/NodeType.java** (33 строк)
- **/src/main/java/global.unet/domain/node/RegularNode.java** (19 строк)
  - Интерфейсы: RegularNode
- **/src/main/java/global.unet/domain/node/RoutingNode.java** (8 строк)
  - Интерфейсы: RoutingNode
- **/src/main/java/global.unet/domain/node/StorageNode.java** (25 строк)
  - Интерфейсы: StorageNode
#### Пакет: `global.unet.domain.notitifier`
- **/src/main/java/global.unet/domain/notitifier/NotifierDrivenPort.java** (7 строк)
  - Интерфейсы: NotifierDrivenPort
#### Пакет: `global.unet.domain.protocol`
- **/src/main/java/global.unet/domain/protocol/MessageHandler.java** (11 строк)
  - Интерфейсы: MessageHandler
#### Пакет: `global.unet.domain.protocol.find`
- **/src/main/java/global.unet/domain/protocol/find/ClosestIdRequest.java** (19 строк)
  - Классы: ClosestIdRequest
#### Пакет: `global.unet.domain.protocol.ping`
- **/src/main/java/global.unet/domain/protocol/ping/PingMessageHandler.java** (33 строк)
  - Классы: PingMessageHandler
- **/src/main/java/global.unet/domain/protocol/ping/PingMessageRequest.java** (34 строк)
  - Классы: PingMessageRequest
- **/src/main/java/global.unet/domain/protocol/ping/PingMessageResponse.java** (31 строк)
  - Классы: PingMessageResponse
- **/src/test/java/global/unet/domain/protocol/ping/PingMessageRequestHandlerTest.java** (29 строк)
  - Классы: PingMessageRequestHandlerTest
#### Пакет: `global.unet.domain.receiver`
- **/src/main/java/global.unet/domain/receiver/MessageBusDriver.java** (14 строк)
  - Интерфейсы: MessageBusDriver
- **/src/main/java/global.unet/domain/receiver/MessageReceiver.java** (119 строк)
  - Классы: MessageReceiver
- **/src/main/java/global.unet/domain/receiver/Receiver.java** (10 строк)
  - Интерфейсы: Receiver
- **/src/main/java/global.unet/domain/receiver/Regular.java** (40 строк)
  - Классы: Regular
- **/src/test/java/global/unet/domain/receiver/MessageReceiverTest.java** (73 строк)
  - Классы: MessageReceiverTest
#### Пакет: `global.unet.domain.router`
- **/src/main/java/global.unet/domain/router/ContentRouter.java** (39 строк)
  - Интерфейсы: ContentRouter
- **/src/main/java/global.unet/domain/router/KadContentRouter.java** (53 строк)
  - Классы: KadContentRouter
- **/src/main/java/global.unet/domain/router/KadUnidRouter.java** (35 строк)
  - Классы: KadUnidRouter
- **/src/main/java/global.unet/domain/router/UnidRouter.java** (21 строк)
  - Интерфейсы: UnidRouter
- **/src/test/java/global/unet/domain/router/KadUnidRouterTest.java** (25 строк)
  - Классы: KadUnidRouterTest
#### Пакет: `global.unet.domain.storage`
- **/src/main/java/global.unet/domain/storage/Content.java** (5 строк)
  - Интерфейсы: Content
- **/src/main/java/global.unet/domain/storage/PersistentStorage.java** (5 строк)
  - Классы: PersistentStorage
- **/src/main/java/global.unet/domain/storage/RamStorage.java** (27 строк)
  - Классы: RamStorage
- **/src/main/java/global.unet/domain/storage/Storage.java** (16 строк)
  - Интерфейсы: Storage
#### Пакет: `global.unet.domain.structures`
- **/src/main/java/global.unet/domain/structures/Bucket.java** (53 строк)
  - Классы: Bucket
- **/src/main/java/global.unet/domain/structures/BucketFabric.java** (33 строк)
  - Классы: BucketFabric
- **/src/main/java/global.unet/domain/structures/ContentHashTable.java** (14 строк)
  - Классы: ContentHashTable
- **/src/main/java/global.unet/domain/structures/HashTable.java** (12 строк)
  - Интерфейсы: HashTable
- **/src/main/java/global.unet/domain/structures/KademliaRoutingTable.java** (5 строк)
  - Интерфейсы: KademliaRoutingTable
- **/src/main/java/global.unet/domain/structures/NodeInfo.java** (73 строк)
  - Классы: NodeInfo
- **/src/main/java/global.unet/domain/structures/RoutingTable.java** (22 строк)
  - Интерфейсы: RoutingTable
- **/src/main/java/global.unet/domain/structures/TagHashTable.java** (11 строк)
  - Классы: TagHashTable
- **/src/main/java/global.unet/domain/structures/XorListStorages.java** (24 строк)
  - Классы: XorListStorages
- **/src/main/java/global.unet/domain/structures/XorTreeRoutingTable.java** (112 строк)
  - Классы: XorTreeRoutingTable
- **/src/test/java/global/unet/domain/structures/XorTreeRoutingTableTest.java** (66 строк)
  - Классы: XorTreeRoutingTableTest

### Other
Файлов: 11
#### Пакет: `cases`
- **/src/test/java/cases/Base2NodesUseCaseContext.java** (47 строк)
  - Классы: Base2NodesUseCaseContext
- **/src/test/java/cases/Ping2NodesContext.java** (49 строк)
  - Классы: Ping2NodesContext
- **/src/test/java/cases/UseCase.java** (8 строк)
  - Интерфейсы: UseCase
#### Пакет: `cases.network`
- **/src/test/java/cases/network/PairMessaging.java** (5 строк)
  - Интерфейсы: PairMessaging
#### Пакет: `integration`
- **/src/test/java/integration/ServerPingMessageRequestTest.java** (76 строк)
  - Классы: ServerPingMessageRequestTest
#### Пакет: `util`
- **/src/test/java/util/UnionGenerator.java** (98 строк)
  - Классы: UnionGenerator
#### Пакет: `util.fake`
- **/src/test/java/util/fake/ConsoleNotifierDrivenAdaptor.java** (16 строк)
  - Классы: ConsoleNotifierDrivenAdaptor
#### Пакет: `util.fake.messages`
- **/src/test/java/util/fake/messages/BaseMessagePairCaseContext.java** (28 строк)
  - Классы: BaseMessagePairCaseContext
- **/src/test/java/util/fake/messages/PingMessagePairCaseContext.java** (35 строк)
  - Классы: PingMessagePairCaseContext
#### Пакет: `util.fake.node`
- **/src/test/java/util/fake/node/NodeInfoPairGenerator.java** (47 строк)
  - Классы: NodeInfoPairGenerator
#### Пакет: `util.fake.notifier`
- **/src/test/java/util/fake/notifier/FakeNotifierDrivenAdaptor.java** (25 строк)
  - Классы: FakeNotifierDrivenAdaptor

## Ключевые компоненты DHT
### Node Management
Компоненты: NodeConfiguration, Node, Base2NodesUseCaseContext, NodeHolder, StorageNode, NodeInfoPairGenerator, NodeStat, KademliaStorageNode, RegularNode, KademliaRoutingNode, NodeInfo, Ping2NodesContext, RoutingNode, KademliaRegularNode, NodeApiDriverPort

### Routing
Компоненты: XorTreeRoutingTableTest, ContentRouter, KadContentRouter, KadUnidRouter, KademliaRoutingTable, KadUnidRouterTest, RoutingTable, UnidRouter, XorTreeRoutingTable

### Storage
Компоненты: Content, PersistentStorage, XorListStorages, Storage, RamStorage

### Protocol
Компоненты: PingMessageHandler, MessageHandler, PingMessageRequest, ClosestIdRequest, PingMessageResponse, PingMessageRequestHandlerTest

### Messages
Компоненты: UnionBootstrap, BaseMessageWithResource, BaseMessageTest, SynchronousMessageBusDriverAdaptorTest, ServerPingMessageRequestTest, Message, MessageReceiverTest, FindContentHolders, BaseMessagePairCaseContext, MessageBusDriver, PingMessagePairCaseContext, BaseMessage, SynchronousMessageBusDriverAdaptor, ContentHolders, MessageReceiver, ResourceResponse, InitReq

### ID Management
Компоненты: Metric, Uname, UnionId, KademliaIdTest, GunidResolver, KademliaId, BaseId, UnionInfo, GlobalUnionId

### Configuration
Компоненты: UnionConfigurationException, StreamLayerConfig

### Exceptions
Компоненты: UnionServerException

## Анализ TODO (незавершенные задачи)
- //TODO может быть в Handlers
- //TODO ничего не должен знать о сервере и о том КАК он работает, его должен стартатовать кто-то во внешнем слое
- //TODO удалить или изменить
- //Todo валидатор конструтора
- //todo проверяем на типы, тут зашит, но вообще список динамический List<Handler>
- //TODO здесь создавать или он ещё в других местах мб?
- //Todo подумать над моделью многоппоточности у каждого потока своя таблица или синхронизировать доступ
- //Todo public на private
- // TODO свой фреймворк?
- //TODO мб конфиг отдельный для этого класса?
- //TODO  в приватный метод - че вот вот здесь? че блять за операция че внутри If?
- //TODO создать тип значение по аналогии с NodeInfo = ContentInfo
- //Todo isEmptry
- //TODO сделать отдельную таблицу в которой хранить ВСЕ unid известные Ноде, чтобы пинговать их и тд, тогда
- //Todo блокировку на messageHandlerIsSet
- //TODO вместо этих методов для работы с нодой через http/console также передавать как обработчик
- //Todo Equals Hashcode зранятся в джава коллекции
- //Todo в Fake Class
- //TODO а где тест то?
- //Todo невверно, т.к. в случае если нода Выложила контент, то
- // TODO может как DSL читать из файла настройки - а-ля аспектное программирование
- //TODO для всех типов все фарбрики? мб список фабрик
- //TODO тут должен быть не server::send message а вообще что-то другое
- //TODO разделить инстациаию и ИНИЦИАЛИЗАЦИЮ ?
- //Todo точно ли должен расширять интерфейсы или может просто содержать в себе has RoutingNode и StorageNode, ведь придется имплементировать интерфейсы
- //Todo сделать все свойства проверить
- // TODO ещё маску первые совпадающие биты)
- //Todo свой тип для провеврки
- //Todo не очень хорошее решение управлять потоками вручную
- //Todo должен хранить не IP, а список IP (несколько сервисов могут работать под одной нодой (иметь одну зону ответсвенности)(реплики)
- // TODO избавиться от сервера в этой зависимости, здесть нижнеуровнвый зависит от верхнеуровнего
- //TODO тогда структура данных ничего не будет знать о NodeInfo
- //TODO вынести в константы метода числа
- //TOdo подумать мб передавать тип класса и тп
- //TODO мб вынести в отдельный класс и отдельно связать их?
- //TODO дубли кода
- //Todo на уровень фреймворка
- //TODO возвращаемый тип
- //TODo можно переписать на фабрику которая будет
- //Todo наверное nodeId , а не unionId?
- //TODO синхронизировать
- //Todo здесь попытка сделать асинхронный ответ, но это блокирующий сервер, так что овтет надо сразу давать
- //TODO extends UseCase
- //TODO в вызывающем коде не используется все равно передается просто значение
- // TODO статический анализатор смотрит, чтобы все классы НЕ ИНЖЕКТИЛИСЬ НАПРЯМУЮ, А ТОЛЬКО ЧЕРЕЗ СЛОИ
- //TODO написать тест
- //TODO логика ЕСЛИ найден . Должен быть всегда найден
- //Todo CustomId
- //TODO ДЕЛАЙ ЗАЕБИСЬ, А НЕ ЗАЕБИСЬ НЕ ДЕЛАЙ
- //TODO поменять на SearchResult?
- //TODO тесты на конструктор
- //todo юзать верхний метод, а это в него перенести
- //TODO к сожалению пока лучше нет идеи имени
- //TODO check 1 - разные объекты, различные UUID, общий NetworkId
- //TODO Сначала тесты для всего ИЗ чего состоит этот код
- //TODO идея , обработчик каждого типа это объект
- //Todo подумать как вынести в Base
- //TODO константа?
- //Todo другой тип или вынести его и сделать общим
- //TODO сделать вложенным в BucketFabric? а бакет сделать интерфейсом?
- //TODO дженерифицировать возвращаемое значение
- //todo из тестов в Ютилс основного пакета
- //TODO разделить инстациаию и ИНИЦИАЛИЗАЦИЮ
- //TODO подбирать порты из доступных в init Методе
- //TODO метод по первым байтам подбирает тип сообщения,  (не десериализация, она в другом месте маппер или подобное)
- //Todo параметры можно брать из конфига длина, метрика(xor или другая операция)
- //TOdo вынести в метрику
- //TODO пока что сделать один простой класс , чтобы протестировать вместе с сервером всю структуру
- //Todo должно ли быть здесь с точки зрения организация инкапусляции( находится в другом пакейдже)
- //todo подумать мб возвращать key, т.к. хеш может считаться только внутри

## Зависимости между пакетами
### global.unet.application
- global.unet.domain.id.BaseId
- global.unet.domain.id.GlobalUnionId
- global.unet.domain.id.UnionId
- global.unet.domain.structures.NodeInfo

### global.unet.application.client
- global.unet.domain.messages.Message

### global.unet.application.node
- global.unet.domain.protocol.ping.PingMessageRequest
- global.unet.domain.protocol.ping.PingMessageResponse

### global.unet.application.receiver
- global.unet.domain.messages.Message
- global.unet.domain.protocol.MessageHandler
- global.unet.domain.protocol.ping.PingMessageRequest
- global.unet.domain.protocol.ping.PingMessageResponse
- global.unet.domain.receiver.MessageBusDriver

### global.unet.application.server
- global.unet.domain.exception.UnionServerException
- global.unet.domain.messages.Message
- global.unet.domain.protocol.ping.PingMessageRequest

### global.unet.domain.id
- global.unet.domain.structures.NodeInfo

### global.unet.domain.messages
- global.unet.domain.id.GlobalUnionId
- global.unet.domain.id.UnionId
- global.unet.domain.structures.NodeInfo

### global.unet.domain.node
- global.unet.application.UnameResolver
- global.unet.domain.config.NodeConfiguration
- global.unet.domain.id.BaseId
- global.unet.domain.id.UnionId
- global.unet.domain.receiver.MessageReceiver
- global.unet.domain.router.KadUnidRouter
- global.unet.domain.router.UnidRouter
- global.unet.domain.storage.Content
- global.unet.domain.storage.Storage
- global.unet.domain.structures.NodeInfo

### global.unet.domain.protocol
- global.unet.domain.messages.Message

### global.unet.domain.protocol.find
- global.unet.domain.id.UnionId
- global.unet.domain.messages.BaseMessageWithResource
- global.unet.domain.structures.NodeInfo

### global.unet.domain.protocol.ping
- global.unet.domain.id.UnionId
- global.unet.domain.messages.*
- global.unet.domain.messages.Message
- global.unet.domain.notitifier.NotifierDrivenPort
- global.unet.domain.protocol.MessageHandler
- global.unet.domain.structures.NodeInfo

### global.unet.domain.receiver
- global.unet.application.SearchResult
- global.unet.application.client.RawSocketBlockingClient
- global.unet.application.server.RawSocketBlockingServer
- global.unet.application.server.Server
- global.unet.domain.id.UnionId
- global.unet.domain.id.UnionInfo
- global.unet.domain.messages.*
- global.unet.domain.messages.BaseMessage
- global.unet.domain.messages.ContentHolders
- global.unet.domain.messages.FindContentHolders
- global.unet.domain.messages.Message
- global.unet.domain.notitifier.NotifierDrivenPort
- global.unet.domain.protocol.find.ClosestIdRequest
- global.unet.domain.protocol.ping.PingMessageHandler
- global.unet.domain.protocol.ping.PingMessageRequest
- global.unet.domain.router.KadContentRouter
- global.unet.domain.router.KadUnidRouter
- global.unet.domain.router.UnidRouter
- global.unet.domain.structures.NodeInfo

### global.unet.domain.router
- global.unet.application.SearchResult
- global.unet.domain.id.UnionId
- global.unet.domain.structures.NodeInfo
- global.unet.domain.structures.RoutingTable
- global.unet.domain.structures.XorTreeRoutingTable

### global.unet.domain.storage
- global.unet.domain.id.BaseId

### global.unet.domain.structures
- global.unet.domain.id.*
- global.unet.domain.id.UnionId
- global.unet.domain.node.NodeType

### cases
- global.unet.domain.id.UnionId
- global.unet.domain.messages.Message
- global.unet.domain.protocol.ping.PingMessageHandler
- global.unet.domain.protocol.ping.PingMessageRequest
- global.unet.domain.protocol.ping.PingMessageResponse

### integration
- global.unet.application.client.RawSocketBlockingClient
- global.unet.application.server.*
- global.unet.domain.id.UnionId
- global.unet.domain.id.UnionInfo
- global.unet.domain.messages.*
- global.unet.domain.notitifier.NotifierDrivenPort
- global.unet.domain.protocol.ping.PingMessageRequest
- global.unet.domain.receiver.MessageReceiver
- global.unet.domain.router.KadUnidRouter
- global.unet.domain.structures.NodeInfo

### util
- global.unet.domain.id.KademliaId
- global.unet.domain.id.UnionId
- global.unet.domain.id.UnionInfo
- global.unet.domain.structures.NodeInfo
- global.unet.domain.structures.XorTreeRoutingTable

### util.fake
- global.unet.domain.messages.Message
- global.unet.domain.notitifier.NotifierDrivenPort

### util.fake.messages
- global.unet.domain.id.UnionId
- global.unet.domain.protocol.ping.PingMessageRequest
- global.unet.domain.protocol.ping.PingMessageResponse
- global.unet.domain.structures.NodeInfo

### util.fake.node
- global.unet.domain.id.UnionId
- global.unet.domain.structures.NodeInfo

### util.fake.notifier
- global.unet.domain.messages.Message
- global.unet.domain.notitifier.NotifierDrivenPort
