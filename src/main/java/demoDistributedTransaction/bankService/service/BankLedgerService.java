package demoDistributedTransaction.bankService.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demoDistributedTransaction.bankService.repository.BankLedgerRepository;
import demoDistributedTransaction.bankService.repository.BankMasterRepository;
import demoDistributedTransaction.bankService.BankLedger;
import demoDistributedTransaction.bankService.BankMaster;
import demoDistributedTransaction.bankService.rabbitmq.CustomerLedger;

@Service
public class BankLedgerService {
	@Autowired
	private BankLedgerRepository bankLedgerRepository;
	
	@Autowired
	private BankMasterRepository bankMasterRepository;
	
	@Autowired
	private RabbitMQSender rabbitMQSender;

	private BankLedger bankLedger;
	
	public List<BankLedger> getAllFromBankLedger(){
		List<BankLedger> bankLedgerList = new ArrayList<BankLedger>();
		bankLedgerRepository.findAll().forEach(bankLedgerList::add);
		return bankLedgerList;
	}
	
	public Optional<BankLedger> getFromBankLedger(Long id) {
		return bankLedgerRepository.findById(id);
	}
	
	public void addToBankLedger(BankLedger bankLedger) {
		Long bankId = bankLedger.getBankId();
		String transactionType = bankLedger.getTransactionType();
		Double bankLedgerAmount = bankLedger.getAmount();
		
		BankMaster bankMaster = null;
		List<BankMaster> listOfBankMasters = bankMasterRepository.findAll();
		for(int i=0;i<listOfBankMasters.size();i++) {
			if(listOfBankMasters.get(i).getAccountId().equals(bankId)) {
				bankMaster = listOfBankMasters.get(i);
				break;
			}
		}
		Double bankMasterAmount = bankMaster.getAmount();
		
		if(transactionType.equals("Withdraw")) {
			if(bankMasterAmount < bankLedgerAmount) {
				System.out.println("Withdrawal not possible");
				return;
			}
			bankMaster.setAmount(bankMasterAmount-bankLedgerAmount);
			
		}
		else if(transactionType.equals("Deposit")) {
			bankMaster.setAmount(bankMasterAmount+bankLedgerAmount);
		}
		
		bankMasterRepository.save(bankMaster);
		bankLedgerRepository.save(bankLedger);
		rabbitMQSender.send("Change status from pending to completed");
	}
	
	public void createBankLedgerUsingCustomerLedger(CustomerLedger incomingCustomerLedger) {
		bankLedger = null;
		
		bankLedger.setTransactionId(2L);
		bankLedger.setBankId(101L);
		bankLedger.setBatchId(incomingCustomerLedger.getBatchId());
		bankLedger.setAmount(incomingCustomerLedger.getAmount());
		bankLedger.setTransactionType(incomingCustomerLedger.getTransactionType());
		bankLedger.setTransactionDate(incomingCustomerLedger.getTransactionDate());
		
		addToBankLedger(bankLedger);
		
	}
	
	public void updateBankLedger(Long id, BankLedger bankLedger) {
		bankLedgerRepository.save(bankLedger);
	}
	
	public void deleteFromBankLedger(Long id) {
		bankLedgerRepository.deleteById(id);
	}
}
