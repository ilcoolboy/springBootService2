package com.wzg.mq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class Produce {
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void sendMsg(final String destinationName, final String message) {
		System.out.println("============>>>>> 发送queue消息 " + message);
		Destination destination = new ActiveMQQueue(destinationName);
		jmsTemplate.setSessionTransacted(true);
		jmsTemplate.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage();
				msg.setText(message);
				return msg;
			}
		});
	}
}
