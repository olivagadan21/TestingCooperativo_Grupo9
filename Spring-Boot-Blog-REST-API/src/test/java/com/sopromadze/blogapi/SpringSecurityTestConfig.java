package com.sopromadze.blogapi;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;

@TestConfiguration
public class SpringSecurityTestConfig {


    @Bean("customUserDetailsService")
    @Primary
    public UserDetailsService userDetailsService() {

        List<Role> listRol = new ArrayList<>();
        listRol.add(new Role(RoleName.ROLE_ADMIN));
        listRol.add(new Role(RoleName.ROLE_USER));
        List<Role> listaRolUser = new ArrayList<>();
        listaRolUser.add(new Role(RoleName.ROLE_USER));

        User admin = new User ();
        admin.setUsername("ADMIN");
        admin.setPassword("ADMIN");
        admin.setRoles(listRol);
        UserPrincipal userAdmin = UserPrincipal.create(admin);

        User user = new User();
        user.setUsername("USER");
        user.setPassword("USER");
        user.setRoles(listaRolUser);

        UserPrincipal userPrincipal= UserPrincipal.create(user);

        List<UserPrincipal> usuariosPrincipales = new ArrayList<>();

        usuariosPrincipales.add(userAdmin);
        usuariosPrincipales.add(userPrincipal);

        return new InMemoryUserDetailsManager(List.of(userAdmin,userPrincipal));

    }

}
