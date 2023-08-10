package com.imooc.employee.jms;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSSampleReceiver {
    public static void main(String[] args) {
        // 设置 JMS Provider 的连接信息
        String brokerURL = "tcp://localhost:61616";
        String username = null;
        String password = null;

        try (Connection connection = createConnection(brokerURL, username, password);
             Session session = createSession(connection);
             MessageConsumer consumer = createConsumer(session, "YourQueueName")) {

            // 启动连接
            connection.start();

            // 接收消息
            Message message = consumer.receive();

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Received message: " + textMessage.getText());
            } else {
                System.out.println("Received message of unsupported type: " + message.getClass().getName());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static Connection createConnection(String brokerURL, String username, String password) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL, username, password);
        return connectionFactory.createConnection();
    }

    private static Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private static MessageConsumer createConsumer(Session session, String queueName) throws JMSException {
        Queue queue = session.createQueue(queueName);
        return session.createConsumer(queue);
    }
}
