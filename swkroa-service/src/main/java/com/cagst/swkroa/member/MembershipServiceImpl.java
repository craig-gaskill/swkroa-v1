package com.cagst.swkroa.member;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cagst.swkroa.codevalue.CodeValue;
import com.cagst.swkroa.codevalue.CodeValueRepository;
import com.cagst.swkroa.comment.Comment;
import com.cagst.swkroa.comment.CommentRepository;
import com.cagst.swkroa.contact.Address;
import com.cagst.swkroa.contact.ContactRepository;
import com.cagst.swkroa.contact.EmailAddress;
import com.cagst.swkroa.contact.PhoneNumber;
import com.cagst.swkroa.document.Document;
import com.cagst.swkroa.document.DocumentRepository;
import com.cagst.swkroa.exception.NotFoundException;
import com.cagst.swkroa.transaction.Transaction;
import com.cagst.swkroa.transaction.TransactionEntry;
import com.cagst.swkroa.transaction.TransactionRepository;
import com.cagst.swkroa.transaction.TransactionType;
import com.cagst.swkroa.user.User;
import com.google.common.net.MediaType;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link MembershipService} interface.
 *
 * @author Craig Gaskill
 */
@Named("membershipService")
public final class MembershipServiceImpl implements MembershipService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MembershipServiceImpl.class);

  private static final String MEMBERSHIP_RENEWAL_PDF = "/com/cagst/swkroa/report/jasper/accounting/MembershipDuesRenewalPdf.jasper";

  private final MembershipRepository membershipRepo;
  private final MemberRepository memberRepo;
  private final MemberTypeRepository memberTypeRepo;
  private final CodeValueRepository codeValueRepo;
  private final ContactRepository contactRepo;
  private final CommentRepository commentRepo;
  private final TransactionRepository transactionRepo;
  private final DocumentRepository documentRepo;
  private final DataSource dataSource;

  /**
   * Primary Constructor used to create an instance of <i>MembershipServiceImpl</i>.
   *
   * @param membershipRepo
   *    The {@link MembershipRepository} used to retrieve {@link Membership Memberships}.
   * @param memberRepo
   *    The {@link MemberRepository} used to retrieve {@link Member Members} for a membership.
   * @param memberTypeRepo
   *    The {@link MemberTypeRepository} used to retrieve {@link MemberType MemberTypes} from.
   * @param codeValueRepo
   *    The {@link CodeValueRepository} used to retrieve {@link CodeValue} from.
   * @param contactRepo
   *    The {@link ContactRepository} used to retrieve / persist {@link Address}, {@link PhoneNumber}, and
   *    {@link EmailAddress} objects.
   * @param commentRepo
   *    The {@link CommentRepository} used to retrieve / persist {@link Comment} objects related to a
   *    {@link Membership}.
   * @param transactionRepo
   *    The {@link TransactionRepository} used to retrieve / persist {@link Transaction} objects related to a
   *    {@link Membership}.
   * @param documentRepo
   *    The {@link DocumentRepository} used to persist {@link DocumentRepository} objects.
   * @param dataSource
   *    The {@link DataSource} used to retrieve additional data from.
   */
  @Inject
  public MembershipServiceImpl(final MembershipRepository membershipRepo,
                               final MemberRepository memberRepo,
                               final MemberTypeRepository memberTypeRepo,
                               final CodeValueRepository codeValueRepo,
                               final ContactRepository contactRepo,
                               final CommentRepository commentRepo,
                               final TransactionRepository transactionRepo,
                               final DocumentRepository documentRepo,
                               final DataSource dataSource) {

    this.membershipRepo = membershipRepo;
    this.memberRepo = memberRepo;
    this.memberTypeRepo = memberTypeRepo;
    this.codeValueRepo = codeValueRepo;
    this.contactRepo = contactRepo;
    this.commentRepo = commentRepo;
    this.transactionRepo = transactionRepo;
    this.documentRepo = documentRepo;
    this.dataSource = dataSource;
  }

  @Override
  public Membership getMembershipByUID(final long uid) {
    LOGGER.info("Calling getMembershipByUID for [{}]", uid);

    Membership membership = membershipRepo.getMembershipByUID(uid);

    List<Member> members = memberRepo.getMembersForMembership(membership);
    membership.setMembers(members);

    for (Member member : membership.getMembers()) {
      member.setAddresses(contactRepo.getAddressesForMember(member));
      member.setPhoneNumbers(contactRepo.getPhoneNumbersForMember(member));
      member.setEmailAddresses(contactRepo.getEmailAddressesForMember(member));
    }

    List<MembershipCounty> counties = memberRepo.getMembershipCountiesForMembership(membership);
    membership.setMembershipCounties(counties);

    List<Comment> comments = commentRepo.getCommentsForMembership(membership);
    Collections.sort(comments);
    membership.setComments(comments);

    List<Transaction> transactions = transactionRepo.getTransactionsForMembership(membership);
    Collections.sort(transactions);
    membership.setTransactions(transactions);

    return membership;
  }

  @Override
  public List<Membership> getMemberships(final Status status, final MembershipBalance balance) {
    LOGGER.info("Calling getActiveMemberships");

    return membershipRepo.getMemberships(status, balance);
  }

  @Override
  public List<Membership> getMembershipsForName(final String name, final Status status, final MembershipBalance balance) {
    LOGGER.info("Calling getMembershipsByName for [{}]", name);

    return membershipRepo.getMembershipsByName(name, status, balance);
  }

  @Override
  public List<Membership> getMembershipsDueInXDays(final int days) {
    LOGGER.info("Calling getMembershipsDueInXDays for [{}]", days);

    return membershipRepo.getMembershipsDueInXDays(days);
  }

  @Override
  @Transactional
  public Membership saveMembership(final Membership membership, final User user) {
    LOGGER.info("Calling saveMembership for [{}]", membership.getMembershipUID());

    Membership savedMembership = membershipRepo.saveMembership(membership, user);

    // save Comments
    for (Comment comment : savedMembership.getComments()) {
      comment.setParentEntityUID(savedMembership.getMembershipUID());
      comment.setParentEntityName(Comment.MEMBERSHIP);

      commentRepo.saveComment(comment, user);
    }

    // save Transactions
    for (Transaction transaction : savedMembership.getTransactions()) {
      transaction.setMembershipUID(savedMembership.getMembershipUID());

      transactionRepo.saveTransaction(transaction, user);
    }

    return savedMembership;
  }

  @Override
  @Transactional
  public int closeMemberships(final Set<Long> membershipIds, final CodeValue closeReason, final String closeText, final User user) {
    LOGGER.info("Calling closeMemberships for [{}]", closeReason.getDisplay());

    return membershipRepo.closeMemberships(membershipIds, closeReason, closeText, user);
  }

  @Override
  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 1800)
  public void billMemberships(final DateTime transactionDate,
                              final String transactionDescription,
                              final String transactionMemo,
                              final Set<Long> membershipIds,
                              final User user)
      throws DataAccessException {

    LOGGER.info("Calling createBillingInvoicesForMemberships [{}]", transactionDescription);

    CodeValue renewalLetter = codeValueRepo.getCodeValueByMeaning("RENEWAL");

    CodeValue baseDues = codeValueRepo.getCodeValueByMeaning("TRANS_DUES_BASE");
    CodeValue familyDues = codeValueRepo.getCodeValueByMeaning("TRANS_DUES_FAMILY");
    CodeValue incrementalDues = codeValueRepo.getCodeValueByMeaning("TRANS_DUES_INC");

    // load the report template
    JasperReport jasperReport = null;

    InputStream stream = this.getClass().getResourceAsStream(MEMBERSHIP_RENEWAL_PDF);
    try {
      jasperReport = (JasperReport) JRLoader.loadObject(stream);
    } catch (JRException ex) {
      LOGGER.error(ex.getMessage(), ex);
    } finally {
      IOUtils.closeQuietly(stream);
    }

    for (Long membershipId : membershipIds) {
      Membership membership = membershipRepo.getMembershipByUID(membershipId);
      if (membership == null) {
        throw new NotFoundException("Membership [" + membershipId + "] was not found.");
      }

      // Create the Document for the Renewal Membership Letter
      byte[] reportContent = generateDueRenewalReport(jasperReport, membershipId, transactionDescription);

      Document document = new Document();
      document.setParentEntityUID(membershipId);
      document.setParentEntityName(Document.MEMBERSHIP);
      document.setDocumentType(renewalLetter);
      document.setDocumentName(transactionDescription);
      document.setDocumentFormat(MediaType.PDF.toString());
      document.setDocumentContents(reportContent);
      document.setBeginEffectiveDate(new DateTime());
      document.setDocumentDescription(transactionDescription);

      // Save the Renewal Membership Letter document
      documentRepo.saveDocument(document, user);

      List<Member> members = memberRepo.getMembersForMembership(membership);

      // Create Transaction
      Transaction invoice = new Transaction();
      invoice.setMembershipUID(membershipId);
      invoice.setTransactionDate(transactionDate);
      invoice.setTransactionDescription(transactionDescription);
      invoice.setMemo(transactionMemo);
      invoice.setTransactionType(TransactionType.INVOICE);
      invoice.setActive(true);

      // Create Transaction Entries
      for (Member member : members) {
        MemberType type = memberTypeRepo.getMemberTypeByUID(member.getMemberType().getMemberTypeUID());
        if (type.getDuesAmount().compareTo(BigDecimal.ZERO) > 0) {
          TransactionEntry entry = new TransactionEntry();
          entry.setTransaction(invoice);
          entry.setMember(member);
          entry.setTransactionEntryType(type.isPrimary() ? baseDues : familyDues);
          entry.setTransactionEntryAmount(type.getDuesAmount().negate());
          entry.setActive(true);

          invoice.addEntry(entry);
        }
      }

      // Create the incremental dues transaction entry
      if (membership.getIncrementalDues() != null) {
        Member primary = memberRepo.getMemberByUID(membership.getMemberUID());

        TransactionEntry entry = new TransactionEntry();
        entry.setTransaction(invoice);
        entry.setMember(primary);
        entry.setTransactionEntryType(incrementalDues);
        entry.setTransactionEntryAmount(membership.getIncrementalDues().negate());
        entry.setActive(true);

        invoice.addEntry(entry);
      }

      // Save the Invoice (transaction)
      transactionRepo.saveTransaction(invoice, user);

      // Update Membership (next_due_dt)
      membershipRepo.updateNextDueDate(membershipId, user);
    }

    LOGGER.debug("CAG Finished generating transactions");
  }

  private byte[] generateDueRenewalReport(final JasperReport jasperReport, final long membershipId, final String transactionDescription) {
    if (jasperReport == null) {
      return null;
    }

    try {
      List<Long> membershipIds = new ArrayList<>(1);
      membershipIds.add(membershipId);

      Map<String, Object> params = new HashMap<>();
      params.put("memberships", membershipIds);
      params.put("membershipPeriod", transactionDescription);

      return JasperRunManager.runReportToPdf(jasperReport, params, dataSource.getConnection());
    } catch (JRException|SQLException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return null;
    }
  }
}
