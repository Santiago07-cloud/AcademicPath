package com.academicpath.backend.dao.usuarios.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.academicpath.backend.dao.usuarios.usuariosDao;
import com.academicpath.backend.models.entity.Usuarios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Implementación del DAO para operaciones con usuarios <br>
 * 
 * @author Academic Path
 */
@Repository
public class usuariosDaoImpl implements usuariosDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Usuarios> obtenerTodos() {
		try {
			String jpql = "SELECT u FROM Usuarios u";
			Query query = entityManager.createQuery(jpql);
			@SuppressWarnings("unchecked")
			List<Usuarios> resultado = query.getResultList();
			return resultado;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Usuarios obtenerPorId(Long id) {
		try {
			return entityManager.find(Usuarios.class, id);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Usuarios obtenerPorCorreo(String correo) {
		try {
			String jpql = "SELECT u FROM Usuarios u WHERE u.correo = :correo";
			Query query = entityManager.createQuery(jpql);
			query.setParameter("correo", correo);

			@SuppressWarnings("unchecked")
			Optional<Usuarios> resultado = query.getResultStream().findFirst();

			return resultado.isPresent() ? resultado.get() : null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@Transactional
	public Usuarios guardar(Usuarios usuario) {
		entityManager.persist(usuario);
		entityManager.flush();
		return usuario;
	}

	@Override
	@Transactional
	public Usuarios actualizar(Usuarios usuario) {
		try {
			Usuarios usuarioActualizado = entityManager.merge(usuario);
			entityManager.flush();
			return usuarioActualizado;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@Transactional
	public boolean eliminar(Long id) {
		try {
			Usuarios usuario = entityManager.find(Usuarios.class, id);
			if (usuario != null) {
				entityManager.remove(usuario);
				entityManager.flush();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}



