package com.sikoramarek.fiszki.repository;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

	Long countQuestionByTagsContaining(Tag tag);

	List<Question> findQuestionsByTagsContaining(Tag tag);

	Page<Question> findAll(Pageable pageable);

	Page<Question> findAllByTagsContaining(Tag tag, Pageable pageable);

}
