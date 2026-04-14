package com.academicpath.backend.manager.usuarios;

import com.academicpath.backend.models.entity.Usuarios;
import java.util.List;

/**
 * Manager para operaciones lógicas de usuarios <br>
 * 
 * @author Academic Path
 */
public interface UsuariosManager {

	/**
	 * Obtiene todos los usuarios registrados <br>
	 * 
	 * @return Lista de usuarios
	 */
	List<Usuarios> obtenerTodos();

	/**
	 * Obtiene un usuario por su ID <br>
	 * 
	 * @param id ID del usuario
	 * @return Usuario encontrado o null
	 */
	Usuarios obtenerPorId(Long id);

	/**
	 * Obtiene un usuario por su correo <br>
	 * 
	 * @param correo Correo del usuario
	 * @return Usuario encontrado o null
	 */
	Usuarios obtenerPorCorreo(String correo);

	/**
	 * Registra un nuevo usuario <br>
	 * 
	 * @param usuario Usuario a registrar
	 * @return Usuario registrado
	 * @throws Exception si hay error en la validación o persistencia
	 */
	Usuarios registrarUsuario(Usuarios usuario) throws Exception;

	/**
	 * Actualiza la información de un usuario <br>
	 * 
	 * @param usuario Usuario a actualizar
	 * @return Usuario actualizado
	 * @throws Exception si hay error en la validación o actualización
	 */
	Usuarios actualizarUsuario(Usuarios usuario) throws Exception;

	/**
	 * Elimina un usuario por su ID <br>
	 * 
	 * @param id ID del usuario a eliminar
	 * @return true si se eliminó, false si no
	 * @throws Exception si hay error en la eliminación
	 */
	boolean eliminarUsuario(Long id) throws Exception;

	/**
	 * Valida que el correo no esté registrado <br>
	 * 
	 * @param correo Correo a validar
	 * @return true si el correo es válido (no existe), false si ya está registrado
	 */
	boolean validarCorreoDisponible(String correo);
}

