package com.academicpath.backend.dao.usuarios;

import com.academicpath.backend.models.entity.Usuarios;
import java.util.List;

/**
 * DAO para operaciones de usuarios <br>
 * 
 * @author Academic Path
 */
public interface usuariosDao {

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
	 * Guarda un nuevo usuario <br>
	 * 
	 * @param usuario Usuario a guardar
	 * @return Usuario guardado
	 */
	Usuarios guardar(Usuarios usuario);

	/**
	 * Actualiza un usuario existente <br>
	 * 
	 * @param usuario Usuario a actualizar
	 * @return Usuario actualizado
	 */
	Usuarios actualizar(Usuarios usuario);

	/**
	 * Elimina un usuario por su ID <br>
	 * 
	 * @param id ID del usuario a eliminar
	 * @return true si se eliminó, false si no
	 */
	boolean eliminar(Long id);
}
