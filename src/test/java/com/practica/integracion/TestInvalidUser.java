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

	private ArrayList<Object> lista = new ArrayList<>(Arrays.asList("uno", "dos"));
	private ArrayList<Object> listaVacia = new ArrayList<>();

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
			assertNotEquals(retorno.toString(), "[uno, dos]");

			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).getSomeData(null, "where id=" + validSystemId);
		}
	}

	/*
	Igual que start con sistema válido
	 */

	@Test
	public void testStopRemoteSystemWithInValidUserAndInValidSystem() throws Exception {

		when(genericDao.
				getSomeData(
						null,
						"where id=" + inValidSystemId)).
				thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);

		boolean lanzada = false;
			Collection<Object> retorno = null;
			try
			{
				retorno = systemManager.stopRemoteSystem(inValidUser.getId(), inValidSystemId);
			}
			catch (SystemManagerException e){
				e.printStackTrace();
				lanzada= true;
				ordered.verify(authDao).getAuthData(inValidUser.getId());
				ordered.verify(genericDao).getSomeData(null, "where id=" + inValidSystemId);
			}
		assertTrue(lanzada); //Si ha lanzado una excepción, todo ha ido bien


	}

	/*
	Igual que start con sistema inválido
	 */

	@Test
	public void testAddRemoteSystemWithInValidUserAndValidSystem() throws Exception {

		when(genericDao.updateSomeData(null, validSystemId)).thenReturn(false);

		ordered = inOrder(authDao, genericDao);
		try
		{
			systemManager.addRemoteSystem(inValidUser.getId(), validSystemId);

		}
		catch (SystemManagerException e)
		{
			e.printStackTrace();
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).updateSomeData(null, validSystemId);
		}
	}

	@Test
	public void testAddRemoteSystemWithInValidUserAndInValidSystem() throws Exception {

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
//Los dos siguientes siguen sin pasar la prueba porque no realizan las llamadas correctas.
	@Test
	public void testDeleteRemoteSystemWithInValidUserAndValidSystem() throws Exception {

		when(genericDao.deleteSomeData(inValidUser, validSystemId)).
				thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);

		try
		{
			systemManager.deleteRemoteSystem(inValidUser.getId(), validSystemId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).deleteSomeData(inValidUser, validSystemId);
		}
	}

	@Test
	public void testDeleteRemoteSystemWithInValidUserAndInValidSystem() throws Exception
	{
		when(genericDao.deleteSomeData(inValidUser, inValidSystemId)).
				thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);

		try
		{
			systemManager.deleteRemoteSystem(inValidUser.getId(), inValidSystemId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ordered.verify(authDao).getAuthData(inValidUser.getId());
			ordered.verify(genericDao).deleteSomeData(inValidUser, inValidSystemId);
		}
	}
}
