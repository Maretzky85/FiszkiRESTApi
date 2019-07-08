package com.sikoramarek.fiszki.repository;

import com.sikoramarek.fiszki.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findTagByTagNameEquals(String tagName);
}
