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
import org.junit.jupiter.api.BeforeEach;
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
	private Object validData;
	private Object inValidData;

	@BeforeEach
	public void setUp() {

		validSystemId="12345";
		inValidSystemId = "54321";
		inValidUser = new User("1","Ana","Lopez","Madrid", new ArrayList<Object>(Arrays.asList(1, 2)));
		systemManager = new SystemManager(authDao, genericDao);

		when(authDao.getAuthData(inValidUser.getId())).thenReturn(null);
		/*
		Suponemos que authDao.getAuthData(userId) devuelve nulo al darle un iD no válido, y que esto
		levantará una excepción si se intenta usar con el id del sistema.
		Los ordered.verify están dentro del catch porque solo se considerará que el test ha siudo satisfactorio
		si ha habido una excepción.
		 */

	}

	@Test
	public void testStartRemoteSystemWithInValidUserAndValidSystem() throws Exception {

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
		//Funciona como se espera
	}

	@Test
	public void testStartRemoteSystemWithInValidUserAndInvalidSystem()
			throws Exception {

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
		// En la práctica identico al anteior, pues se levanta el mismo tipo de excepción
	}

	@Test
	public void testStopRemoteSystemWithInValidUserAndValidSystem() throws Exception {

		when(genericDao.getSomeData(null, "where id=" + validSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);
		Collection<Object> retorno = new ArrayList<>();
		try
		{
			retorno = systemManager.stopRemoteSystem(inValidUser.getId(), validSystemId);
		}
		catch (SystemManagerException e)
		{
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).getSomeData(null, "where id=" + validSystemId);
			return;
		}
		fallarTest();
	}

	/*
	Igual que start con sistema válido
	 */

	@Test
	public void testStopRemoteSystemWithInValidUserAndInValidSystem() throws Exception {
		when(genericDao.getSomeData(null, "where id=" + inValidSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);
		Collection<Object> retorno = new ArrayList<>();
		try
		{
			retorno = systemManager.stopRemoteSystem(inValidUser.getId(), inValidSystemId);
		}
		catch (SystemManagerException e)
		{
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).getSomeData(null, "where id=" + inValidSystemId);
			return;
		}
		fallarTest();
	}

	/*
	Igual que start con sistema inválido
	 */

	@Test
	public void testAddRemoteSystemWithInValidUserAndValidData() throws Exception {

		when(genericDao.updateSomeData(null, validData)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);
		try
		{
			systemManager.addRemoteSystem(inValidUser.getId(), validData);

		}
		catch (SystemManagerException e)
		{
			e.printStackTrace();
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).updateSomeData(null, validData);
			return;
		}
		fallarTest();
	}

	@Test
	public void testAddRemoteSystemWithInValidUserAndInValidData() throws Exception {

		ordered = inOrder(authDao, genericDao);
		try
		{
			when(genericDao.updateSomeData(null, inValidData)).thenThrow(new OperationNotSupportedException());
			systemManager.addRemoteSystem(inValidUser.getId(), inValidData);
		}
		catch(Exception e)
		{
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).updateSomeData(null, inValidData);
			return;
		}
		fallarTest();
	}
//Los dos siguientes siguen sin pasar la prueba porque no realizan las llamadas correctas.
	@Test
	public void testDeleteRemoteSystemWithValidUserAndValidData() throws Exception {

		ordered = inOrder(authDao, genericDao);

		try
		{
			when(genericDao.deleteSomeData(null, (String) validData)).thenThrow(new OperationNotSupportedException());
			systemManager.deleteRemoteSystem(inValidUser.getId(), (String) validData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).deleteSomeData(null, (String) validData);
		}
		/*
		No llama a authDao, por eso no pasa la prueba
		 */
	}

	@Test
	public void testDeleteRemoteSystemWithInValidUserAndInValidData() throws Exception
	{
		ordered = inOrder(authDao, genericDao);

		try
		{
			when(genericDao.deleteSomeData(null, (String)inValidData)).
					thenThrow(new OperationNotSupportedException());
			systemManager.deleteRemoteSystem(inValidUser.getId(), (String)inValidData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).deleteSomeData(null, (String)inValidData);
			return;
		}
		fallarTest();
	}

	private void fallarTest(){
		assertFalse(true);
	}
}
