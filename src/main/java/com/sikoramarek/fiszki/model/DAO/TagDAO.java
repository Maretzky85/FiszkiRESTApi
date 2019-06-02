package com.sikoramarek.fiszki.model.DAO;

import com.sikoramarek.fiszki.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagDAO extends JpaRepository<Tag, Long> {

}
