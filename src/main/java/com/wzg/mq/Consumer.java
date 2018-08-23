package com.wzg.mq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.wzg.service.IFindService;

@Component
public class Consumer {
	@Autowired
	private JmsTemplate jmsTemplate;
	
	
	@Autowired
	private IFindService findService;
	/**
	 * 消费者消费完毕消息之后 发送消息给mq 生产者监听此队列 得知消费完毕 做相关处理   （也可以又生产者发布一个接口给消费者调用 ）.
	 * @param message
	 * @param session
	 */

	@JmsListener(destination= "bank1.back", containerFactory="jmsListenerContainer")
	public void onMsg(Message message, Session session) {
		TextMessage textMsg = (TextMessage) message;
		String text = null;
		try {
			text = textMsg.getText();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		findService.afterAddData(text);
	}
	/**
	 * 如果消费者多次消费失败或者超时未被消费  mq会把消息放入死信队列， 这里监听死信队列处理消息失败问题
	 * @param message
	 * @param session
	 */
	@JmsListener(destination= "DLQ.bank1", containerFactory="jmsListenerContainer")
	public void onMsgError(Message message, Session session) {
		TextMessage textMsg = (TextMessage) message;
		String text = null;
		try {
			text = textMsg.getText();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		findService.afterAddError(text);
	}
}
