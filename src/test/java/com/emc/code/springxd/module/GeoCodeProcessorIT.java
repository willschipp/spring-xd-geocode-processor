package com.emc.code.springxd.module;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=GeoCodeConfiguration.class)
public class GeoCodeProcessorIT {

	@Autowired
	private GeoCodeProcessor processor;
	
	@Autowired
	MessageChannel input;
	
	@Autowired
	AbstractSubscribableChannel output;
	
	@Test
	public void test() throws Exception {
		Handler handler = new Handler();
		output.subscribe(handler);
		
		Message<?> message = MessageBuilder.withPayload("Al Fahidi St, Bur Dubai, Dubai - UAE").build();
		
		input.send(message);
		
//		System.out.println(processor.getGeocode());
		
		while (handler.message == null) {
			Thread.sleep(5 * 100);
		}//end while
		System.out.println(handler.message.getPayload());
	}

	class Handler implements MessageHandler {

		Message<?> message = null;
		
		@Override
		public void handleMessage(Message<?> message) throws MessagingException {
			this.message = message;
		}
		
	}
}
