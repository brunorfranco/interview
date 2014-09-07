package br.com.bruno.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SimpleJMS {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		
		QueueConnection qc = (QueueConnection) factory.createConnection();
		
		QueueSession qsession = (QueueSession) qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		
		Queue queue = null;
		
		try {
			queue = (Queue) context.lookup("queue/ChatQueue");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(queue == null){
			queue = (Queue) qsession.createQueue("queue/ChatQueue");
		}		
		
		qc.start();
		
		QueueSender sender = qsession.createSender(queue);
		QueueReceiver receiver = qsession.createReceiver(queue);
		
		TextMessage txtToSend = qsession.createTextMessage();
		txtToSend.setText("TextMessage: Hello Interviwer");
		
		sender.send(txtToSend);
		
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		TextMessage txtToReceive = (TextMessage) receiver.receive();
		System.out.println("Received: " + txtToReceive.getText());
		
		qsession.close();
		qc.close();
	}
}
