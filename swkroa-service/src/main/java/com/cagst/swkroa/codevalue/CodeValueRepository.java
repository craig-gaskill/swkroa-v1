package com.cagst.swkroa.codevalue;

import java.util.List;

import com.cagst.swkroa.user.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

/**
 * Interface for retrieving / saving {@link CodeSet}s and {@link CodeValue}s from / to persistent storage.
 *
 * @author Craig Gaskill
 * @version 1.0.0
 */
@Repository
public interface CodeValueRepository {
  public static final String CLOSE_REASONS     = "CLOSE_REASONS";
  public static final String GENDER            = "GENDER";
  public static final String TITLE             = "TITLE";
  public static final String ADDRESS_TYPE      = "ADDRESS_TYPE";
  public static final String PHONE_TYPE        = "PHONE_TYPE";
  public static final String EMAIL_TYPE        = "EMAIL_TYPE";
  public static final String ENTITY_TYPE       = "ENTITY_TYPE";
  public static final String MEMBERSHIP_TYPE   = "MEMBERSHIP_TYPE";
  public static final String VERIFICATION_TYPE = "VERIFICATION_TYPE";
  public static final String TRANSACTION_TYPE  = "TRANSACTION_TYPE";

  /**
   * Retrieves a {@link CodeSet} by its unique identifier.
   *
   * @param uid
   *     The {@link long} that uniquely identifies the {@link CodeSet} to retrieve.
   *
   * @return The {@link CodeSet} that corresponds to the specified {@link long} unique identifier, <code>null</code> if
   * the CodeSet does not exist.
   */
  public CodeSet getCodeSetByUID(final long uid);

  /**
   * Retrieves a {@link List} of {@link CodeSet CodeSets} that are active within the system.
   *
   * @return A {@link List} of {@link CodeSet CodeSets} that are active within the system.
   */
  public List<CodeSet> getActiveCodeSets();

  /**
   * Retrieves a {@link List} of {@link CodeValue} that are associated to the specified {@link CodeSet}.
   *
   * @param codeSet
   *     The {@link CodeSet} to retrieve CodeValues for.
   *
   * @return A {@link List} of {@link CodeValue} that are associated to the specified {@link CodeSet}, an empty list if
   * no CodeValues are associated to the CodeSet.
   */
  public List<CodeValue> getCodeValuesForCodeSet(final CodeSet codeSet);

  /**
   * Retrieves a {@link List} of {@link CodeValue} that are associated to the {@link CodeSet} associated with the
   * specified meaning.
   *
   * @param codeSetMeaning
   *     The meaning associated to the code set we want the code values for.
   *
   * @return A {@link List} of {@link CodeValue} that are associated to the specified {@link CodeSet} meaning, an empty
   * list if no CodeValues are associated to the CodeSet meaning.
   */
  public List<CodeValue> getCodeValuesForCodeSetByMeaning(final String codeSetMeaning);

  /**
   * Retrieves a {@link CodeValue} by its unique identifier.
   *
   * @param uid
   *     A {@link Long} that uniquely identifies the CodeValue to retrieve.
   *
   * @return The {@link CodeValue} associated with the specified unique identifier.
   *
   * @throws EmptyResultDataAccessException
   *     if no CodeValue was found.
   * @throws IncorrectResultSizeDataAccessException
   *     if more than 1 CodeValue was found.
   */
  public CodeValue getCodeValueByUID(final long uid);

  /**
   * Retrieves a {@link CodeValue} by its meaning.
   *
   * @param meaning
   *     The meaning associated to the CodeValue to retrieve.
   *
   * @return The {@link CodeValue} associated with the specified meaning. {@code null} if no CodeValue was found.
   *
   * @throws EmptyResultDataAccessException
   *     if no CodeValue was found.
   * @throws IncorrectResultSizeDataAccessException
   *     if more than 1 CodeValue was found.
   */
  public CodeValue getCodeValueByMeaning(final String meaning);

  /**
   * Persists the specified {@link CodeValue}.
   *
   * @param codeValue
   *     The {@link CodeValue} to persist.
   * @param user
   *     The {@link User} that performed the changes.
   *
   * @return A {@link CodeValue} after it has been persisted.
   *
   * @throws OptimisticLockingFailureException
   *     if the updt_cnt doesn't match (meaning someone has updated it since it was last read)
   * @throws IncorrectResultSizeDataAccessException
   *     if the number of rows inserted / updated exceeded the expected number
   * @throws DataAccessException
   *     if the query fails
   */
  public CodeValue saveCodeValueForCodeSet(final CodeValue codeValue, final User user)
      throws OptimisticLockingFailureException, IncorrectResultSizeDataAccessException, DataAccessException;
}
