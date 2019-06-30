package com.sikoramarek.fiszki.repository;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

	Long countQuestionByTagsContaining(Tag tag);

	Long countByAcceptedTrue();

	Page<Question> getQuestionsByAcceptedFalse(Pageable pageable);

	List<Question> findQuestionsByTagsContaining(Tag tag);

	Page<Question> findAll(Pageable pageable);

	Page<Question> findAllByAcceptedTrue(Pageable pageable);

	Page<Question> findAllByTagsContainingAndAcceptedTrue(Tag tag, Pageable pageable);

	Optional<Question> findQuestionById(Long questionID);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE questions q SET accepted = TRUE where q.id = ?1")
	void setAccepted(Long questionID);
}
