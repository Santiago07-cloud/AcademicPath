package com.academicpath.backend.services.usuarios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academicpath.backend.manager.usuarios.UsuariosManager;
import com.academicpath.backend.models.entity.Usuarios;

/**
 * Controlador REST para operaciones con usuarios <br>
 *
 * @author Academic Path
 */
@RestController
@RequestMapping("usuarios")
public class UsuariosServiceImpl {

	@Autowired
	private UsuariosManager usuariosManager;

	@GetMapping("obtenerTodos")
	public ResponseEntity<List<Usuarios>> obtenerTodos() {
		List<Usuarios> usuarios = usuariosManager.obtenerTodos();
		return ResponseEntity.ok(usuarios);
	}

	@GetMapping("obtenerPorId/{id}")
	public ResponseEntity<Usuarios> obtenerPorId(@PathVariable("id") Long id) {
		Usuarios usuario = usuariosManager.obtenerPorId(id);
		if (usuario == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(usuario);
	}

	@GetMapping("obtenerPorCorreo/{correo}")
	public ResponseEntity<Usuarios> obtenerPorCorreo(@PathVariable("correo") String correo) {
		Usuarios usuario = usuariosManager.obtenerPorCorreo(correo);
		if (usuario == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(usuario);
	}

	@PostMapping("registrar")
	public ResponseEntity<Usuarios> registrarUsuario(@RequestBody Usuarios usuario) throws Exception {
		Usuarios usuarioRegistrado = usuariosManager.registrarUsuario(usuario);
		return ResponseEntity.ok(usuarioRegistrado);
	}

	@PutMapping("actualizar")
	public ResponseEntity<Object> actualizarUsuario(@RequestBody Usuarios usuario) {
		try {
			Usuarios usuarioActualizado = usuariosManager.actualizarUsuario(usuario);
			return ResponseEntity.ok(usuarioActualizado);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ocurrió un error actualizando el usuario: " + e.getMessage());
		}
	}

	@DeleteMapping("eliminar/{id}")
	public ResponseEntity<Object> eliminarUsuario(@PathVariable("id") Long id) {
		try {
			boolean eliminado = usuariosManager.eliminarUsuario(id);
			if (eliminado) {
				return ResponseEntity.ok("Usuario eliminado exitosamente");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al eliminar el usuario");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ocurrió un error eliminando el usuario: " + e.getMessage());
		}
	}
}


