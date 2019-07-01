package com.sikoramarek.fiszki.repository;

import com.sikoramarek.fiszki.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

	UserModel getUserByUsername(String username);

}
