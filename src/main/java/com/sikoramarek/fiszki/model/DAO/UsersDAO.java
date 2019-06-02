package com.sikoramarek.fiszki.model.DAO;

import com.sikoramarek.fiszki.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersDAO extends JpaRepository<User, Long> {
	User getUserById(Long user_id);
}
