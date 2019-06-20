package com.sikoramarek.fiszki.model.DAO;


import com.sikoramarek.fiszki.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesDAO extends JpaRepository<Roles, Long> {
}
