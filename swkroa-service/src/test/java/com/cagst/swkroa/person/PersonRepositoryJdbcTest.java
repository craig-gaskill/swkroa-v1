package com.cagst.swkroa.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.cagst.common.db.StatementLoader;
import com.cagst.swkroa.codevalue.CodeValue;
import com.cagst.swkroa.codevalue.CodeValueRepository;
import com.cagst.swkroa.test.BaseTestRepository;
import com.cagst.swkroa.user.User;

/**
 * Test class for PersonRepositoryJdbc class.
 * 
 * @author Craig Gaskill
 * 
 * @version 1.0.0
 * 
 */
@RunWith(JUnit4.class)
public class PersonRepositoryJdbcTest extends BaseTestRepository {
	private PersonRepositoryJdbc repo;

	private CodeValueRepository codeValueRepo;

	private CodeValue home;
	private User user;

	@Before
	public void setUp() {
		user = new User();
		user.setUserUID(1L);

		home = new CodeValue();
		home.setCodeValueUID(1L);
		home.setMeaning("HOME");
		home.setDisplay("Home");

		CodeValue cvWork = new CodeValue();
		cvWork.setCodeValueUID(2L);
		cvWork.setMeaning("WORK");
		cvWork.setDisplay("Work");

		codeValueRepo = Mockito.mock(CodeValueRepository.class);

		Mockito.when(codeValueRepo.getCodeValueByUID(1L)).thenReturn(home);
		Mockito.when(codeValueRepo.getCodeValueByUID(2L)).thenReturn(cvWork);

		DataSource dataSource = createTestDataSource();

		repo = new PersonRepositoryJdbc(dataSource, codeValueRepo);
		repo.setStatementDialect(StatementLoader.HSQLDB_DIALECT);
	}

	/**
	 * Test the getPersonByUID and not finding the person.
	 */
	@Test(expected = EmptyResultDataAccessException.class)
	public void testGetPersonByUID_NoneFound() {
		repo.getPersonByUID(999L);
	}

	/**
	 * Test the getPersonByUID and finding the person.
	 */
	@Test
	public void testGetPersonByUID_Found() {
		Person person = repo.getPersonByUID(1L);

		assertNotNull("Ensure the person is not NULL!", person);
		assertEquals("Ensure we found the correct person!", 1, person.getPersonUID());
		assertEquals("Ensure we found the correct person (by name)!", "Gaskill", person.getLastName());
	}

	/**
	 * Test the savePerson method by inserting a new Person.
	 */
	@Test
	public void testSavePerson_Insert() {
		Person person = new Person();
		person.setLastName("Person");
		person.setFirstName("Test");
		person.setTitle(home);
		person.setDob(new DateTime(1970, 04, 19, 0, 0));

		assertEquals("Ensure our new Person doesn't have an Id yet.", 0, person.getPersonUID());

		Person newPerson = repo.savePerson(person, user);
		assertNotNull("Ensure we have a new Person.", newPerson);
		assertTrue("Ensure the Person has an Id.", newPerson.getPersonUID() > 0L);
		assertEquals("Ensure it is the same person.", newPerson.getLastName(), person.getLastName());
	}

	/**
	 * Test the savePerson method by updating a Person.
	 */
	@Test
	public void testSavePerson_Update() {
		Person person = new Person();
		person.setPersonUID(1L);
		person.setLastName("Gaskill");
		person.setFirstName("Craig");
		person.setDob(new DateTime(1970, 04, 19, 0, 0));

		Person updatedPerson = repo.savePerson(person, user);
		assertNotNull("Ensure we have a Person.", person);
		assertEquals("Ensure the Person Id is corect.", updatedPerson.getPersonUID(), person.getPersonUID());
		assertEquals("Ensure the Person update count has been incremented.", 1, updatedPerson.getPersonUpdateCount());
	}

	/**
	 * Test the savePerson method by updating a Person by failing due to an invalid update count.
	 */
	@Test(expected = OptimisticLockingFailureException.class)
	public void testSavePerson_Update_Failed() {
		Person person = new Person();
		person.setPersonUID(1L);
		person.setLastName("Gaskill");
		person.setFirstName("Craig");
		person.setDob(new DateTime(1970, 04, 19, 0, 0));

		// force a failure due to update count
		person.setPersonUpdateCount(99L);

		repo.savePerson(person, user);
	}
}