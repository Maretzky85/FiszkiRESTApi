package com.sikoramarek.fiszki.repository;

import com.sikoramarek.fiszki.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

	UserModel getUserByUsername(String username);

	boolean existsUserModelByUsername(String username);

	@Transactional
	@Query(nativeQuery = true, value = "SELECT id FROM users where name = ?1")
	Long getId(String username);

}
