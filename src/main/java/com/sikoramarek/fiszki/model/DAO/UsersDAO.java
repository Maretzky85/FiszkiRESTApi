package com.sikoramarek.fiszki.model.DAO;

import com.sikoramarek.fiszki.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersDAO extends JpaRepository<UserModel, Long> {

	UserModel getUserById(Long user_id);

	UserModel getUserByName(String username);
}
