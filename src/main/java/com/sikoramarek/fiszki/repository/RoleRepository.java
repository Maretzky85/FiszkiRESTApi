package com.sikoramarek.fiszki.repository;


import com.sikoramarek.fiszki.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findRoleByRoleEquals(String role);

}
