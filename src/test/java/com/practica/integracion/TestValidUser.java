package com.practica.integracion;

import com.practica.integracion.DAO.User;
import com.practica.integracion.manager.SystemManager;
import com.practica.integracion.manager.SystemManagerException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;

import com.practica.integracion.DAO.AuthDAO;
import com.practica.integracion.DAO.GenericDAO;
import com.practica.integracion.DAO.User;
import com.practica.integracion.manager.SystemManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.mockito.InjectMocks;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import javax.naming.OperationNotSupportedException;

@ExtendWith(MockitoExtension.class)
public class TestValidUser {

	/**
	 * RELLENAR POR EL ALUMNO
	 */
	@InjectMocks
	SystemManager systemManager;

	@Mock
	AuthDAO authDao;
	@Mock
	GenericDAO genericDao;

	private String validSystemId;
	private String inValidSystemId;
	private User validUser;
	private InOrder ordered;

	private ArrayList<Object> lista = new ArrayList<>(Arrays.asList("uno", "dos"));

	@BeforeEach
	public void setUp() throws OperationNotSupportedException {

		validSystemId="12345";
		inValidSystemId = "54321";
		validUser = new User("1","Ana","Lopez","Madrid", new ArrayList<Object>(Arrays.asList(1, 2)));
		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
	}

	@Test //Espero que salga bien, sale bien
	public void testStartRemoteSystemWithValidUserAndSystem() throws Exception {
		when(genericDao.getSomeData(validUser, "where id=" + validSystemId))
				.thenReturn(lista);
		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.startRemoteSystem(validUser.getId(), validSystemId);

		assertEquals(retorno.toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + validSystemId);
	}

	@Test //Espero que salga mal, sale mal
	public void testStartRemoteSystemWithValidUserAndInvalidSystem()
			throws Exception {
		when(genericDao.getSomeData(validUser, "where id=" + inValidSystemId)).
				thenThrow(new OperationNotSupportedException());
		ordered = inOrder(authDao, genericDao);
		Collection<Object> retorno = null;
		try
		{
			retorno = systemManager.startRemoteSystem(validUser.getId(), inValidSystemId);
		}
		catch (SystemManagerException e)
		{
			e.printStackTrace();
		}
		
		assertNotEquals(retorno, "[uno, dos]");

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + inValidSystemId);
	}

	@Test //Espero que
	//@DisplayName("Patata")
	public void testStopRemoteSystemWithValidUserAndSystem() throws Exception {
		when(genericDao.getSomeData(validUser, "where id=" + validSystemId))
				.thenReturn(lista);
		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.stopRemoteSystem(validUser.getId(), validSystemId);

		assertNotEquals(retorno.toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + validSystemId);
	}

	@Test
	public void testStopRemoteSystemWithValidUserAndInValidSystem() throws Exception {

		when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.getSomeData(validUser, "where id=" + inValidSystemId)).thenReturn(null);

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.stopRemoteSystem(validUser.getId(), inValidSystemId);

		assertThrows(NullPointerException.class, () -> {
			retorno.toString();
		});

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + inValidSystemId);
	}

	@Test
	public void testAddRemoteSystemWithValidUserAndSystem() throws OperationNotSupportedException {

		when(genericDao.updateSomeData(validUser, validSystemId)).thenReturn(true);

		ordered = inOrder(authDao, genericDao);

		try
		{
			systemManager.addRemoteSystem(validUser.getId(), validSystemId);

		}
		catch(SystemManagerException e)
		{
			e.printStackTrace();
		}
		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).updateSomeData(validUser, validSystemId);
	}

	@Test
	public void testAddRemoteSystemWithValidUserAndInValidSystem() throws Exception {

		when(genericDao.updateSomeData(validUser, inValidSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);

		try
		{
			systemManager.addRemoteSystem(validUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e)
		{
			e.printStackTrace();
		}
		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).updateSomeData(validUser, inValidSystemId);
	}

	@Test
	public void testDeleteRemoteSystemWithValidUserAndValidSystem() throws Exception {

		when(genericDao.deleteSomeData(validUser, validSystemId)).thenReturn(true);

		ordered = inOrder(authDao, genericDao);

		try
		{
			systemManager.deleteRemoteSystem(validUser.getId(), validSystemId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).deleteSomeData(validUser, validSystemId);
	}

	@Test
	public void testDeleteRemoteSystemWithValidUserAndInValidSystem() throws Exception {

		when(genericDao.deleteSomeData(validUser, inValidSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);
		try
		{
			systemManager.deleteRemoteSystem(validUser.getId(), inValidSystemId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).deleteSomeData(validUser, inValidSystemId);

	}

}
