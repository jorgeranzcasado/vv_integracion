package com.practica.integracion;

import com.practica.integracion.DAO.AuthDAO;
import com.practica.integracion.DAO.GenericDAO;
import com.practica.integracion.DAO.User;
import com.practica.integracion.manager.SystemManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;


import com.practica.integracion.manager.SystemManagerException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@ExtendWith(MockitoExtension.class)
public class TestInvalidUser {
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
	private User inValidUser = new User("-1","Ana","Lopez","Madrid", new ArrayList<Object>());
	private InOrder ordered;

	private ArrayList<Object> lista = new ArrayList<>(Arrays.asList("uno", "dos"));
	private ArrayList<Object> listaVacia = new ArrayList<>(Arrays.asList());

	@Test
	public void testStartRemoteSystemWithInValidUserAndValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(null);
		when(genericDao.getSomeData((User) eq(null), anyString())).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = new ArrayList<>(Arrays.asList());

		try
		{
			retorno = systemManager.startRemoteSystem(inValidUser.getId(), validSystemId);
		}
		catch(SystemManagerException e)
		{
			assertNotEquals(retorno.toString(), "[uno, dos]");

			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).getSomeData((User) eq(null), anyString());
		}
	}

	@Test
	public void testStartRemoteSystemWithInValidUserAndInvalidSystem()
			throws Exception {
		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(inValidUser);
		when(genericDao.getSomeData(inValidUser, "where id=" + inValidSystemId)).thenReturn(lista);

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.startRemoteSystem(inValidUser.getId(), inValidSystemId);
		assertEquals(retorno.toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(inValidUser.getId());
		ordered.verify(genericDao).getSomeData(inValidUser, "where id=" + inValidSystemId);
	}

	@Test
	public void testStopRemoteSystemWithValidUserAndSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(inValidUser);
		when(genericDao.getSomeData(inValidUser, "where id=" + inValidSystemId)).thenReturn(listaVacia);

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.stopRemoteSystem(inValidUser.getId(), inValidSystemId);

		assertNotEquals(retorno.toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(inValidUser.getId());
		ordered.verify(genericDao).getSomeData(inValidUser, "where id=" + inValidSystemId);
	}

	@Test
	public void testStopRemoteSystemWithValidUserAndInValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(inValidUser);
		when(genericDao.getSomeData(inValidUser, "where id=" + inValidSystemId)).thenReturn(listaVacia);

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = systemManager.stopRemoteSystem(inValidUser.getId(), inValidSystemId);

		assertNotEquals(retorno.toString(), "[uno, dos]");

		ordered.verify(authDao).getAuthData(inValidUser.getId());
		ordered.verify(genericDao).getSomeData(inValidUser, "where id=" + inValidSystemId);
	}

	@Test
	public void testAddRemoteSystemWithValidUserAndSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(inValidUser);
		when(genericDao.updateSomeData(inValidUser, validSystemId)).thenReturn(true);

		ordered = inOrder(authDao, genericDao);
		systemManager.addRemoteSystem(inValidUser.getId(), validSystemId);

		ordered.verify(authDao).getAuthData(inValidUser.getId());
		ordered.verify(genericDao).updateSomeData(inValidUser, validSystemId);
	}

	@Test
	public void testAddRemoteSystemWithValidUserAndInValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(inValidUser);
		when(genericDao.updateSomeData(inValidUser, inValidSystemId)).thenReturn(true);

		ordered = inOrder(authDao, genericDao);
		try
		{
			systemManager.addRemoteSystem(inValidUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e)
		{
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).updateSomeData(inValidUser, inValidSystemId);
			throw e;
		}
	}

	@Test
	public void testDeleteRemoteSystemWithValidUserAndValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		//when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.deleteSomeData(any(), anyString())).thenReturn(true);

		ordered = inOrder(genericDao);

		systemManager.deleteRemoteSystem(inValidUser.getId(), validSystemId);

		//ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).deleteSomeData(inValidUser, validSystemId);
	}

	@Test
	public void testDeleteRemoteSystemWithInValidUserAndInValidSystem() throws Exception
	{
		systemManager = new SystemManager(authDao, genericDao);

		//when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.deleteSomeData(any(), anyString())).thenReturn(false);

		ordered = inOrder(genericDao);
		try
		{
			systemManager.addRemoteSystem(inValidUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e)
		{
			//ordered.verify(authDao).getAuthData(validUser.getId());
			ordered.verify(genericDao).updateSomeData(inValidUser, inValidSystemId);
			throw e;
		}
	}
}
