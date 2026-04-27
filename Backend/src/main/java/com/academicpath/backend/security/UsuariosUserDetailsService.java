package com.academicpath.backend.security;

import com.academicpath.backend.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuariosUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        return usuariosRepository.findByCorreo(correo)
                .map(UsuarioUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));
    }
}

