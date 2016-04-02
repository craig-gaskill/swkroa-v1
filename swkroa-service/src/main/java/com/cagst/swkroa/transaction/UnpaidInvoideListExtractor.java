package com.cagst.swkroa.transaction;

import com.cagst.swkroa.codevalue.CodeValueRepository;
import com.cagst.swkroa.member.MemberRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnpaidInvoideListExtractor implements ResultSetExtractor<List<UnpaidInvoice>> {
  private final UnpaidInvoiceMapper unpaidInvoiceMapperMapper;
  private final TransactionEntryMapper entryMapper;

  public UnpaidInvoideListExtractor(final CodeValueRepository codeValueRepo,
                                    final MemberRepository memberRepo) {

    unpaidInvoiceMapperMapper = new UnpaidInvoiceMapper();
    entryMapper = new TransactionEntryMapper(codeValueRepo, memberRepo);
  }

  @Override
  public List<UnpaidInvoice> extractData(ResultSet rs) throws SQLException, DataAccessException {
    Map<Long, UnpaidInvoice> transactions = new HashMap<>();

    while (rs.next()) {
      long transactionId = rs.getLong(TransactionMapper.TRANSACTION_ID);
      if (transactions.containsKey(transactionId)) {
        addChild(transactions.get(transactionId), entryMapper.mapRow(rs, rs.getRow()));
      } else {
        UnpaidInvoice inv = unpaidInvoiceMapperMapper.mapRow(rs, rs.getRow());
        transactions.put(transactionId, inv);

        addChild(inv, entryMapper.mapRow(rs, rs.getRow()));
      }
    }

    return new ArrayList<>(transactions.values());
  }

  private void addChild(final Transaction trans, final TransactionEntry entry) {
    trans.addEntry(entry);
    entry.setTransaction(trans);
  }
}
