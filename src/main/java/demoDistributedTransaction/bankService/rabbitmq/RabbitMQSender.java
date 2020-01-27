package demoDistributedTransaction.bankService.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Component

//***not used*****

@Service
public class RabbitMQSender {
	
	@Autowired
	private AmqpTemplate rabbitTemplate;
 
	@Value("${exchangeNameBank}")
	private String exchange2;
 
	@Value("${routingkeyBank}")
	private String routingkey2;
 
	@Scheduled
	public void send(String msg) {
		//String msg = "(batch_id,transaction_type,amount):"+ customerLedger.getBatchId()+","+customerLedger.getTransactionType()+","+customerLedger.getAmount()+"\n";
  
		rabbitTemplate.convertAndSend(exchange2, routingkey2, msg);
		System.out.println("Sent msg info " + msg);
 
	}
}