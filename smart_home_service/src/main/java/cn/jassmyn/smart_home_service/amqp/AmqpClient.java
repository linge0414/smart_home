package cn.jassmyn.smart_home_service.amqp;

import cn.jassmyn.smart_home_service.ConstantPropertiesUtils;
import cn.jassmyn.smart_home_service.mapper.HistoricalDataMapper;
import cn.jassmyn.smart_home_service.po.HistoricalData;
import cn.jassmyn.smart_home_service.utils.DateConvertUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;
import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("rawtypes")
@Component
public class AmqpClient {

    @Autowired
    private HistoricalDataMapper historicalDataMapper;

    private static AmqpClient amqpClient;

    @PostConstruct
    public void init() {
        amqpClient = this;
        amqpClient.historicalDataMapper = this.historicalDataMapper;
    }

    private final static Logger logger = LoggerFactory.getLogger(AmqpClient.class);
    private static String accessKey = ConstantPropertiesUtils.ACCESS_KEY;
    private static String accessSecret = ConstantPropertiesUtils.ACCESS_SECRET;
    private static String consumerGroupId = ConstantPropertiesUtils.CONSUMER_GROUP_ID;

    //iotInstanceId：实例ID。若是2021年07月30日之前（不含当日）开通的公共实例，请填空字符串。
    private static String iotInstanceId = ConstantPropertiesUtils.IOT_INSTANCE_ID;

    //控制台服务端订阅中消费组状态页客户端ID一栏将显示clientId参数。
    //建议使用机器UUID、MAC地址、IP等唯一标识等作为clientId。便于您区分识别不同的客户端。
    private static String clientId = ConstantPropertiesUtils.CLIENT_ID;

    //${YourHost}为接入域名，请参见AMQP客户端接入说明文档。
    private static String host = ConstantPropertiesUtils.HOST;

    // 指定单个进程启动的连接数
    private static int connectionCount = 4;

    //业务处理异步线程池，线程池参数可以根据您的业务特点调整，或者您也可以用其他异步方式处理接收到的消息。
    private final static ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue(50000));

    @PostConstruct
    public void connect() throws Exception {
        List<Connection> connections = new ArrayList<>();

        //参数说明，请参见AMQP客户端接入说明文档。
        for (int i = 0; i < connectionCount; i++) {
            long timeStamp = System.currentTimeMillis();
            //签名方法：支持hmacmd5、hmacsha1和hmacsha256。
            String signMethod = "hmacsha1";

            //userName组装方法，请参见AMQP客户端接入说明文档。
            String userName = clientId + "-" + i + "|authMode=aksign"
                    + ",signMethod=" + signMethod
                    + ",timestamp=" + timeStamp
                    + ",authId=" + accessKey
                    + ",iotInstanceId=" + iotInstanceId
                    + ",consumerGroupId=" + consumerGroupId
                    + "|";
            //计算签名，password组装方法，请参见AMQP客户端接入说明文档。
            String signContent = "authId=" + accessKey + "&timestamp=" + timeStamp;
            String password = doSign(signContent, accessSecret, signMethod);
            String connectionUrl = "failover:(amqps://" + host + ":5671?amqp.idleTimeout=80000)"
                    + "?failover.reconnectDelay=30";

            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put("connectionfactory.SBCF", connectionUrl);
            hashtable.put("queue.QUEUE", "default");
            hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
            Context context = new InitialContext(hashtable);
            ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
            Destination queue = (Destination) context.lookup("QUEUE");
            // 创建连接。
            Connection connection = cf.createConnection(userName, password);
            connections.add(connection);

            ((JmsConnection) connection).addConnectionListener(myJmsConnectionListener);
            // 创建会话。
            // Session.CLIENT_ACKNOWLEDGE: 收到消息后，需要手动调用message.acknowledge()。
            // Session.AUTO_ACKNOWLEDGE: SDK自动ACK（推荐）。
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            connection.start();
            // 创建Receiver连接。
            MessageConsumer consumer = session.createConsumer(queue);
            consumer.setMessageListener(messageListener);
        }
    }

    private static MessageListener messageListener = message -> {
        try {
            executorService.submit(() -> processMessage(message));
        } catch (Exception e) {
            logger.error("submit task occurs exception ", e);
        }
    };

    /**
     * 在这里处理您收到消息后的具体业务逻辑。
     */
    private static void processMessage(Message message) {
        try {
            byte[] body = message.getBody(byte[].class);
            String content = new String(body);
            String topic = message.getStringProperty("topic");
            String messageId = message.getStringProperty("messageId");
            //处理设备属性上报消息。
            if ("post".equals(topic.substring(topic.lastIndexOf("/") + 1))) {
                saveData(content);
            }
            logger.info("receive message"
                    + ",\n topic = " + topic
                    + ",\n messageId = " + messageId
                    + ",\n content = " + content);
        } catch (Exception e) {
            logger.error("processMessage occurs error ", e);
        }
    }

    private static JmsConnectionListener myJmsConnectionListener = new JmsConnectionListener() {
        /**
         * 连接成功建立。
         */
        @Override
        public void onConnectionEstablished(URI remoteURI) {
            logger.info("onConnectionEstablished, remoteUri:{}", remoteURI);
        }

        /**
         * 尝试过最大重试次数之后，最终连接失败。
         */
        @Override
        public void onConnectionFailure(Throwable error) {
            logger.error("onConnectionFailure, {}", error.getMessage());
        }

        /**
         * 连接中断。
         */
        @Override
        public void onConnectionInterrupted(URI remoteURI) {
            logger.info("onConnectionInterrupted, remoteUri:{}", remoteURI);
        }

        /**
         * 连接中断后又自动重连上。
         */
        @Override
        public void onConnectionRestored(URI remoteURI) {
            logger.info("onConnectionRestored, remoteUri:{}", remoteURI);
        }

        @Override
        public void onInboundMessage(JmsInboundMessageDispatch envelope) {
        }

        @Override
        public void onSessionClosed(Session session, Throwable cause) {
        }

        @Override
        public void onConsumerClosed(MessageConsumer consumer, Throwable cause) {
        }

        @Override
        public void onProducerClosed(MessageProducer producer, Throwable cause) {
        }
    };

    /**
     * 计算签名，password组装方法，请参见AMQP客户端接入说明文档。
     */
    private static String doSign(String toSignString, String secret, String signMethod) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), signMethod);
        Mac mac = Mac.getInstance(signMethod);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(toSignString.getBytes());
        return Base64.encodeBase64String(rawHmac);
    }

    //保存数据到数据库
    static void saveData(String content) {
        HistoricalData historicalData = new HistoricalData();
        Map map = (Map) JSON.parse(content);
        Map properityMap = (Map) map.get("items");

        Map lightSwitchMap = (Map) properityMap.get("LightSwitch");
        historicalData.setLightSwitch((Integer) lightSwitchMap.get("value"));
        historicalData.setTime(DateConvertUtil.getDateTime((Long) lightSwitchMap.get("time"), 0));
        historicalData.setId(String.valueOf((Long) lightSwitchMap.get("time")));

        Map humidityMap = (Map) properityMap.get("Humidity");
        historicalData.setHumidity((Integer) humidityMap.get("value"));

        Map temperatureMap = (Map) properityMap.get("Temperature");
        historicalData.setTemperature((Integer) temperatureMap.get("value"));

        Map humidifiedMap = (Map) properityMap.get("Humidified");
        historicalData.setHumidified((Integer) humidifiedMap.get("value"));


        Map ambientBrightnessMap = (Map) properityMap.get("AmbientBrightness");
        historicalData.setAmbientBrightness((Integer) ambientBrightnessMap.get("value"));

        Map vehACSwitch = (Map) properityMap.get("VehACSwitch");
        historicalData.setVehACSwitch((Integer) vehACSwitch.get("value"));

        Map lightLuminanceMap = (Map) properityMap.get("LightLuminance");
        historicalData.setLightLuminance((Integer) lightLuminanceMap.get("value"));
        amqpClient.historicalDataMapper.insert(historicalData);
    }
}
