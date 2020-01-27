package demoDistributedTransaction.bankService.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import demoDistributedTransaction.bankService.service.BankLedgerService;

@Component
public class RabbitMQReceiver {
	private CustomerLedger incomingCustomerLedger;
	
	@Autowired
	private BankLedgerService bankLedgerService;
	
	@RabbitListener(queues = "${queueName}")
	public void receivedMessage(CustomerLedger incomingCustomerLedger) {
		setIncomingCustomerLedger(incomingCustomerLedger);
		System.out.println("Received msg:\n(batchId,transactionType,amount)="+incomingCustomerLedger.getBatchId()+","+incomingCustomerLedger.getTransactionType()+","+incomingCustomerLedger.getAmount());
		
		
		bankLedgerService.createBankLedgerUsingCustomerLedger(incomingCustomerLedger);
		
	}

	public CustomerLedger getIncomingCustomerLedger() {
		return incomingCustomerLedger;
	}

	public void setIncomingCustomerLedger(CustomerLedger incomingCustomerLedger) {
		this.incomingCustomerLedger = incomingCustomerLedger;
	}
	

}
