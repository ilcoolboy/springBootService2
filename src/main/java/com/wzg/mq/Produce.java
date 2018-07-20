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
		Destination destination = new ActiveMQQueue(destinationName);
		// 如果不打开服务质量的开关，消息的递送模式、优先级和存活时间的设置就没有作用。
		jmsTemplate.setExplicitQosEnabled(true);
		// 设置消息有效时间为10毫秒  （设置消息有效时间为10毫秒之后，如果10毫秒之后还无人消费消息 消息自动进入死信队列）
		jmsTemplate.setTimeToLive(1000*10);
		// 支持消息事务
		jmsTemplate.setSessionTransacted(true);
		// 消息应答模式为事务   （当消息为支持事务的时候 应答模型设置为其他的都无效 系统都认为应答模式为事务模式）
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
