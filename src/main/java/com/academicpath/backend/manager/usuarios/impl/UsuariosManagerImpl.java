package com.academicpath.backend.manager.usuarios.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academicpath.backend.dao.usuarios.usuariosDao;
import com.academicpath.backend.manager.usuarios.UsuariosManager;
import com.academicpath.backend.models.entity.Usuarios;

/**
 * Implementación del Manager para operaciones lógicas de usuarios <br>
 * 
 * @author Academic Path
 */
@Service
public class UsuariosManagerImpl implements UsuariosManager {

	@Autowired
	private usuariosDao usuariosDao;

	@Override
	public List<Usuarios> obtenerTodos() {
		try {
			return usuariosDao.obtenerTodos();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Usuarios obtenerPorId(Long id) {
		try {
			if (id == null || id <= 0) {
				throw new IllegalArgumentException("El ID del usuario debe ser válido");
			}
			return usuariosDao.obtenerPorId(id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Usuarios obtenerPorCorreo(String correo) {
		try {
			if (correo == null || correo.trim().isEmpty()) {
				throw new IllegalArgumentException("El correo no puede estar vacío");
			}
			return usuariosDao.obtenerPorCorreo(correo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Usuarios registrarUsuario(Usuarios usuario) throws Exception {
		try {
			// Validaciones
			if (usuario == null) {
				throw new IllegalArgumentException("El usuario no puede ser nulo");
			}

			if (usuario.getNombres() == null || usuario.getNombres().trim().isEmpty()) {
				throw new IllegalArgumentException("El nombre del usuario es requerido");
			}

			if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
				throw new IllegalArgumentException("El apellido del usuario es requerido");
			}

			if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
				throw new IllegalArgumentException("El correo es requerido");
			}

			if (!validarFormatoCorreo(usuario.getCorreo())) {
				throw new IllegalArgumentException("El formato del correo no es válido");
			}

			if (!validarCorreoDisponible(usuario.getCorreo())) {
				throw new IllegalArgumentException("El correo ya está registrado");
			}

			if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
				throw new IllegalArgumentException("La contraseña es requerida");
			}

			if (usuario.getUniversidad() == null || usuario.getUniversidad().trim().isEmpty()) {
				throw new IllegalArgumentException("La universidad es requerida");
			}

			if (usuario.getCarrera() == null || usuario.getCarrera().trim().isEmpty()) {
				throw new IllegalArgumentException("La carrera es requerida");
			}

			// Guardar usuario
			Usuarios usuarioGuardado = usuariosDao.guardar(usuario);

			if (usuarioGuardado == null) {
				throw new Exception("Error al guardar el usuario en la base de datos");
			}

			return usuarioGuardado;
		} catch (Exception e) {
			throw new Exception("Error al registrar el usuario: " + e.getMessage());
		}
	}

	@Override
	public Usuarios actualizarUsuario(Usuarios usuario) throws Exception {
		try {
			// Validaciones
			if (usuario == null) {
				throw new IllegalArgumentException("El usuario no puede ser nulo");
			}

			if (usuario.getId() == null || usuario.getId() <= 0) {
				throw new IllegalArgumentException("El ID del usuario es requerido");
			}

			// Verificar que el usuario existe
			Usuarios usuarioExistente = usuariosDao.obtenerPorId(usuario.getId());
			if (usuarioExistente == null) {
				throw new IllegalArgumentException("El usuario no existe");
			}

			if (usuario.getNombres() == null || usuario.getNombres().trim().isEmpty()) {
				throw new IllegalArgumentException("El nombre del usuario es requerido");
			}

			if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
				throw new IllegalArgumentException("El apellido del usuario es requerido");
			}

			if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
				throw new IllegalArgumentException("El correo es requerido");
			}

			// Validar que el correo sea único (si cambió)
			if (!usuarioExistente.getCorreo().equals(usuario.getCorreo())) {
				if (!validarCorreoDisponible(usuario.getCorreo())) {
					throw new IllegalArgumentException("El correo ya está registrado");
				}
			}

			// Actualizar usuario
			Usuarios usuarioActualizado = usuariosDao.actualizar(usuario);

			if (usuarioActualizado == null) {
				throw new Exception("Error al actualizar el usuario en la base de datos");
			}

			return usuarioActualizado;
		} catch (Exception e) {
			throw new Exception("Error al actualizar el usuario: " + e.getMessage());
		}
	}

	@Override
	public boolean eliminarUsuario(Long id) throws Exception {
		try {
			if (id == null || id <= 0) {
				throw new IllegalArgumentException("El ID del usuario debe ser válido");
			}

			// Verificar que el usuario existe
			Usuarios usuario = usuariosDao.obtenerPorId(id);
			if (usuario == null) {
				throw new IllegalArgumentException("El usuario no existe");
			}

			boolean eliminado = usuariosDao.eliminar(id);

			if (!eliminado) {
				throw new Exception("Error al eliminar el usuario de la base de datos");
			}

			return true;
		} catch (Exception e) {
			throw new Exception("Error al eliminar el usuario: " + e.getMessage());
		}
	}

	@Override
	public boolean validarCorreoDisponible(String correo) {
		try {
			if (correo == null || correo.trim().isEmpty()) {
				return false;
			}
			Usuarios usuario = usuariosDao.obtenerPorCorreo(correo);
			return usuario == null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Valida que el formato del correo sea válido <br>
	 * 
	 * @param correo Correo a validar
	 * @return true si el formato es válido, false si no
	 */
	private boolean validarFormatoCorreo(String correo) {
		if (correo == null || correo.trim().isEmpty()) {
			return false;
		}
		String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		return correo.matches(regex);
	}
}

