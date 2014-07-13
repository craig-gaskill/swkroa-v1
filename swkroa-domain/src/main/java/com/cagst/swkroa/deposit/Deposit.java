package com.cagst.swkroa.deposit;

import com.cagst.swkroa.transaction.Transaction;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Deposit within the system.
 *
 * @author Craig Gaskill
 *
 * @version 1.0.0
 *
 */
public final class Deposit implements Serializable {

	private long deposit_id;
	private DateTime deposit_dt;
	private String deposit_ref;
	private BigDecimal deposit_amount;

	private List<Transaction> transactions = new ArrayList<Transaction>();

	// meta-data
	private boolean active_ind = true;
	private long updt_cnt;

	public long getDepositUID() {
		return deposit_id;
	}

	/* package */ void setDepositUID(final long uid) {
		this.deposit_id = uid;
	}

	public DateTime getDepositDate() {
		return deposit_dt;
	}

	public void setDepositDate(final DateTime dt) {
		this.deposit_dt = dt;
	}

	public String getDepositNumber() {
		return deposit_ref;
	}

	public void setDepositNumber(final String ref) {
		this.deposit_ref = ref;
	}

	public BigDecimal getDepositAmount() {
		return deposit_amount;
	}

	/* package */ void setDepositAmount(final BigDecimal amount) {
		this.deposit_amount = amount;
	}

	public void clearTransactions() {
		transactions.clear();
	}

	public void addTransaction(final Transaction transaction) {
		this.transactions.add(transaction);
	}

	public void removeTransaction(final Transaction transaction) {
		this.transactions.remove(transaction);
	}

	public List<Transaction> getTransactions() {
		return Collections.unmodifiableList(transactions);
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append(deposit_dt);
		builder.append(deposit_ref);
		builder.append(deposit_amount);

		return builder.build();
	}
}