package com.imooc.employee.jms;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSSampleSender {
    public static void main(String[] args) {
        // 设置 JMS Provider 的连接信息
        String brokerURL = "tcp://localhost:61616";
        String username = null;
        String password = null;

        try (Connection connection = createConnection(brokerURL, username, password);
             Session session = createSession(connection);
             MessageProducer producer = createProducer(session, "YourQueueName")) {

            // 启动连接
            connection.start();

            // 创建文本消息
            TextMessage message = session.createTextMessage();
            message.setText("Hello, World!");

            // 发送消息
            producer.send(message);

            System.out.println("Message sent successfully.");
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

    private static MessageProducer createProducer(Session session, String queueName) throws JMSException {
        Queue queue = session.createQueue(queueName);
        return session.createProducer(queue);
    }
}
