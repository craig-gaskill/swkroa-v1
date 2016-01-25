package com.cagst.swkroa.job;

import com.cagst.swkroa.user.User;
import com.cagst.swkroa.user.UsernameTakenException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;

/**
 * Definition of a repository that retrieves and persists {@link Job} objects.
 *
 * @author Craig Gaskill
 */
public interface JobRepository {
  /**
   * Retrieves the {@link Job} associated with the specified unique identifier.
   *
   * @param uid
   *    A {@link long} that uniquely identifies the {@link Job} to retrieve.
   *
   * @return The {@link Job} associated with the specified uid, {@code null} if no Job was found.
   */
  Job getJobByUID(final long uid);

  /**
   * Retrieves a {@link List} of {@link Job}s that are associated with the specified {@link JobStatus}.
   *
   * @param jobStatus
   *    The {@link JobStatus} to retrieve jobs for.
   *
   * @return A {@link List} of {@link Job}s that are associated with the specified status. An empty collection if none are found.
   */
  List<Job> getJobsForStatus(final JobStatus jobStatus);

  /**
   * Retrieves a {@link List} of {@link Job}s that are associated with the specified {@link JobType}.
   *
   * @param jobType
   *    The {@link JobType} to retrieves jobs for.
   *
   * @return A {@link List} of {@link Job}s that are associated with the specified type. An empty collection if none are found.
   */
  List<Job> getJobsForType(final JobType jobType);

  /**
   * Commits the specified {@link Job} to persistent storage.
   *
   * @param job
   *    The {@link Job} to persist.
   * @param user
   *    The {@link User} that performed the changes.
   *
   * @return A {@link Job} once it has been committed to persistent storage.
   *
   * @throws OptimisticLockingFailureException
   *     if the updt_cnt doesn't match (meaning someone has updated it since it was last read)
   * @throws IncorrectResultSizeDataAccessException
   *     if the number of rows inserted / updated exceeded the expected number
   * @throws UsernameTakenException
   *     if the username associated to the {@code builder} is already being used by another
   *     user
   * @throws DataAccessException
   *     if the query fails
   */
  Job saveJob(final Job job, final User user);
}
