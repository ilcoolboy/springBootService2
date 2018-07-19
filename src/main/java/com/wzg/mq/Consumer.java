package com.wzg.mq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.wzg.service.IFindService;

@Component
public class Consumer {
	@Autowired
	private IFindService findService;
	@JmsListener(destination="bank1.back", containerFactory="jmsListenerContainer")
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
}
