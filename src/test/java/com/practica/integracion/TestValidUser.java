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
	private Object validData;
	private Object inValidData;

	private ArrayList<Object> lista = new ArrayList<>(Arrays.asList("uno", "dos"));

	@BeforeEach
	public void setUp() throws OperationNotSupportedException {

		validSystemId="12345";
		inValidSystemId = "54321";
		validUser = new User("1","Ana","Lopez","Madrid", new ArrayList<Object>(Arrays.asList(1, 2)));
		systemManager = new SystemManager(authDao, genericDao);
		validData= new Object();
		inValidData = null;

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
		//authDao devuelve el usuario bien
		//genericDao devuelve la lista correcta
		//Cumple lo esperado
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

		//authDao devuelve el usuario bien
		//genericDao no puede devolver nada y debe lanzar una excepcion
		//Cumple lo esperado
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
		//authDao devuelve el usuario bien
		//genericDao devuelve la misma lista que en start
		//Esta función debería hacer algo distinto, tal vez devolver
		//una lista vacía o realizar una acción no especificada
		//Se precisa una documentación para hacer un juicio mas acertado
	}

	@Test
	public void testStopRemoteSystemWithValidUserAndInValidSystem() throws Exception {

		//when(authDao.getAuthData(validUser.getId())).thenReturn(validUser);
		when(genericDao.getSomeData(validUser, "where id=" + inValidSystemId)).thenThrow(new OperationNotSupportedException());

		ordered = inOrder(authDao, genericDao);
		Collection<Object> retorno=null;
		try
		{
			retorno = systemManager.stopRemoteSystem(validUser.getId(), inValidSystemId);
		}
		catch(SystemManagerException e){
			e.printStackTrace();
		}
		assertNotEquals(retorno, lista);

		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).getSomeData(validUser, "where id=" + inValidSystemId);
		/*
		Como el anterior, intenta relizar las mismas acciones que start, pero al no tener un
		systemId Valido, lanza una excepción que está bien manejada
		 */
	}

	@Test
	public void testAddRemoteSystemWithValidUserAndData() throws OperationNotSupportedException {

		when(genericDao.updateSomeData(validUser, validData)).thenReturn(true);

		ordered = inOrder(authDao, genericDao);

		try
		{
			systemManager.addRemoteSystem(validUser.getId(), validData);

		}
		catch(SystemManagerException e)
		{
			e.printStackTrace();
		}
		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).updateSomeData(validUser, validData);
		/*
		Solo se comprueba que hace las llamadas sin tener excepciones porque no nos devuelve datos
		 */
	}

	@Test
	public void testAddRemoteSystemWithValidUserAndInValidData() throws Exception {

		ordered = inOrder(authDao, genericDao);

		try
		{
			when(genericDao.updateSomeData(validUser, inValidData)).thenReturn(false);
			systemManager.addRemoteSystem(validUser.getId(), inValidData);
		}
		catch(SystemManagerException e)
		{
			e.printStackTrace();
		}
		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).updateSomeData(validUser, inValidData);
		/*
		Como el anterior, así que damos los detalles de la excepción, pero cumple lo esperado
		 */
	}

	@Test
	public void testDeleteRemoteSystemWithValidUserAndValidData() throws Exception {

		ordered = inOrder(authDao, genericDao);

		try
		{
			when(genericDao.deleteSomeData(validUser, (String) validData)).thenReturn(true);
			systemManager.deleteRemoteSystem(validUser.getId(), (String) validData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		ordered.verify(authDao).getAuthData(validUser.getId());
		ordered.verify(genericDao).deleteSomeData(validUser, (String) validData);
		/*
		No llama a authDao, por eso no pasa la prueba
		 */
	}

	@Test
	public void testDeleteRemoteSystemWithValidUserAndInValidData() throws Exception {

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
		/*
		No llama a Authdao y levanta una excepción al intentar usar un sistema inválido.
		 */
	}

}
