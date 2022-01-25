package com.sopromadze.blogapi;


import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

   @Autowired
    RoleRepository roleRepository;

   @Test
    void findRoleByRoleName(){
       Role role = new Role();
       role.setName(RoleName.ROLE_USER);
       roleRepository.save(role);
       assertEquals(roleRepository.findByName(RoleName.ROLE_USER).get(), role);

   }

}
