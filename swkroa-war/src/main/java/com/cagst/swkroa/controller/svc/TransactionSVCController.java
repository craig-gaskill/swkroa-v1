package com.cagst.swkroa.controller.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cagst.swkroa.transaction.Transaction;
import com.cagst.swkroa.transaction.TransactionRepository;
import com.cagst.swkroa.web.util.WebAppUtils;

/**
 * Handles and retrieves {@link Transaction} objects depending on the URI template.
 * 
 * @author Craig Gaskill
 * 
 * @version 1.0.0
 * 
 */
@Controller
public final class TransactionSVCController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionSVCController.class);

	@Autowired
	private TransactionRepository transactionRepo;

	/**
	 * Handles the request and persists the {@link Transaction} to persistent storage.
	 * 
	 * @param transaction
	 *          The {@link Transaction} to persist.
	 * 
	 * @return The {@link Transaction} after it has been persisted.
	 */
	@RequestMapping(value = "/svc/transaction", method = RequestMethod.PUT)
	@ResponseBody
	public Transaction saveTransaction(final @RequestBody Transaction transaction) {
		LOGGER.info("Received request to save transaction.");

		return transactionRepo.saveTransaction(transaction, WebAppUtils.getUser());
	}

}