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

	@InjectMocks
	SystemManager systemManager;

	@Mock
	AuthDAO authDao;
	@Mock
	GenericDAO genericDao;

	private String validSystemId="12345";
	private String inValidSystemId = "54321";
	private User inValidUser = new User("-1","Ana","Lopez","Madrid", new ArrayList<>());
	private InOrder ordered;

	private ArrayList<Object> lista = new ArrayList<>(Arrays.asList("uno", "dos"));
	private ArrayList<Object> listaVacia = new ArrayList<>();

	@Test
	public void testStartRemoteSystemWithInValidUserAndValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(null);
		when(genericDao.getSomeData(null, "where id=" + validSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = new ArrayList<>();

		try
		{
			retorno = systemManager.startRemoteSystem(inValidUser.getId(), validSystemId);
		}
		catch(SystemManagerException e)
		{
			assertNotEquals(retorno.toString(), "[uno, dos]");

			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).getSomeData(null, "where id=" + validSystemId);
		}
	}

	@Test
	public void testStartRemoteSystemWithInValidUserAndInvalidSystem()
			throws Exception {
		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(null);
		when(genericDao.getSomeData(null, "where id=" + inValidSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = new ArrayList<>();

		try
		{
			retorno = systemManager.startRemoteSystem(inValidUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e)
		{
			assertNotEquals(retorno.toString(), "[uno, dos]");

			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).getSomeData(null, "where id=" + inValidSystemId);
		}
	}

	@Test
	public void testStopRemoteSystemWithInValidUserAndValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(null);
		when(genericDao.getSomeData(null, "where id=" + validSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);
		Collection<Object> retorno = new ArrayList<>();
		try
		{
			retorno = systemManager.stopRemoteSystem(inValidUser.getId(), validSystemId);
		}
		catch (SystemManagerException e)
		{
			assertNotEquals(retorno.toString(), "[uno, dos]");

			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).getSomeData(null, "where id=" + validSystemId);
		}
	}

	@Test
	public void testStopRemoteSystemWithInValidUserAndInValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(inValidUser);
		when(genericDao.
				getSomeData(
						inValidUser,
						"where id=" + inValidSystemId)).
				thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);

		Collection<Object> retorno = new ArrayList<>();
		try
		{
			retorno = systemManager.stopRemoteSystem(inValidUser.getId(), inValidSystemId);
		}
		catch (SystemManagerException e){
			assertNotEquals(retorno.toString(), "[uno, dos]");

			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).getSomeData(inValidUser, "where id=" + inValidSystemId);
		}
	}

	@Test
	public void testAddRemoteSystemWithInValidUserAndValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(null);
		when(genericDao.updateSomeData(null, validSystemId)).thenReturn(false);

		ordered = inOrder(authDao, genericDao);
		try
		{
			systemManager.addRemoteSystem(inValidUser.getId(), validSystemId);

		}
		catch (SystemManagerException e)
		{
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).updateSomeData(null, validSystemId);
		}
	}

	@Test
	public void testAddRemoteSystemWithInValidUserAndInValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(null);
		when(genericDao.updateSomeData(null, inValidSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);
		try
		{
			systemManager.addRemoteSystem(inValidUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e)
		{
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).updateSomeData(null, inValidSystemId);
			//throw e;
		}
	}

	@Test
	public void testDeleteRemoteSystemWithInValidUserAndValidSystem() throws Exception {

		systemManager = new SystemManager(authDao, genericDao);

		//when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.deleteSomeData(any(), anyString())).thenReturn(true);

		ordered = inOrder(genericDao);

		systemManager.deleteRemoteSystem(inValidUser.getId(), validSystemId);

		//No usa los parámetros correctos
		ordered.verify(genericDao).deleteSomeData(any(), anyString());
	}

	@Test
	public void testDeleteRemoteSystemWithInValidUserAndInValidSystem() throws Exception
	{
		systemManager = new SystemManager(authDao, genericDao);

		//when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.deleteSomeData(any(), anyString())).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(genericDao);
		try
		{
			systemManager.deleteRemoteSystem(inValidUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e)
		{
			//No usa los parámetros correctos
			ordered.verify(genericDao).deleteSomeData(any(), anyString());
			//throw e;
		}
	}
}
