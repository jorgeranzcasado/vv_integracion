package com.practica.integracion;

import com.practica.integracion.DAO.User;
import com.practica.integracion.manager.SystemManager;
import com.practica.integracion.manager.SystemManagerException;
import org.junit.jupiter.api.BeforeAll;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

	private String validSystemId="12345";
	private String inValidSystemId = "54321";
	private User validUser = new User("1","Ana","Lopez","Madrid", new ArrayList<Object>(Arrays.asList(1, 2)));
	private InOrder ordered;

	private ArrayList<Object> lista = new ArrayList<>(Arrays.asList("uno", "dos"));
	private ArrayList<Object> listaVacia = new ArrayList<>(Arrays.asList());

	@Test
	public void testStartRemoteSystemWithValidUserAndSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.getSomeData(validUser, "where id=" + validSystemId)).thenReturn(lista);

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.startRemoteSystem(validUser.getId(), validSystemId);

		assertEquals(retorno.toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + validSystemId);
	}

	@Test
	public void testStartRemoteSystemWithValidUserAndInvalidSystem()
			throws Exception {
		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.getSomeData(validUser, "where id=" + inValidSystemId)).thenReturn(null);

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.startRemoteSystem(validUser.getId(), inValidSystemId);
		assertNotEquals(((User) retorno).toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + inValidSystemId);
	}

	@Test
	public void testStopRemoteSystemWithValidUserAndSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.getSomeData(validUser, "where id=" + inValidSystemId)).thenReturn(listaVacia);

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.stopRemoteSystem(validUser.getId(), inValidSystemId);

		assertNotEquals(retorno.toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + inValidSystemId);
	}

	@Test
	public void testStopRemoteSystemWithValidUserAndInValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.getSomeData(validUser, "where id=" + inValidSystemId)).thenReturn(listaVacia);

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.stopRemoteSystem(validUser.getId(), inValidSystemId);

		assertNotEquals(retorno.toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + inValidSystemId);
	}

	@Test
	public void testAddRemoteSystemWithValidUserAndSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.updateSomeData(validUser, validSystemId)).thenReturn(true);

		ordered = inOrder(authDao, genericDao);
		systemManager.addRemoteSystem(validUser.getId(), validSystemId);

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).updateSomeData(validUser, validSystemId);
	}

	@Test
	public void testAddRemoteSystemWithValidUserAndInValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.updateSomeData(validUser, inValidSystemId)).thenReturn(true);

		ordered = inOrder(authDao, genericDao);
		try
		{
			systemManager.addRemoteSystem(validUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e)
		{
			ordered.verify(authDao).getAuthData(validUser.getId());
			ordered.verify(genericDao).updateSomeData(validUser, inValidSystemId);
			throw e;
		}
	}

	@Test
	public void testDeleteRemoteSystemWithValidUserAndValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		//when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.deleteSomeData(any(), anyString())).thenReturn(true);

		ordered = inOrder(genericDao);

		systemManager.deleteRemoteSystem(validUser.getId(), validSystemId);

		//No usará el objeto adecuado
		ordered.verify(genericDao).deleteSomeData(any(), anyString());
	}

	@Test
	public void testDeleteRemoteSystemWithValidUserAndInValidSystem() throws Exception
	{
		systemManager = new SystemManager(authDao, genericDao);

		//when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.deleteSomeData(any(), anyString())).thenReturn(false);

		ordered = inOrder(genericDao);
		try
		{
			systemManager.deleteRemoteSystem(validUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e)
		{
			//No usará el objeto adecuado
			ordered.verify(genericDao).deleteSomeData(any(), anyString());
			throw e;
		}
	}

}
