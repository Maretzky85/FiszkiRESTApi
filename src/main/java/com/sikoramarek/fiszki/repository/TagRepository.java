package com.sikoramarek.fiszki.repository;

import com.sikoramarek.fiszki.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
