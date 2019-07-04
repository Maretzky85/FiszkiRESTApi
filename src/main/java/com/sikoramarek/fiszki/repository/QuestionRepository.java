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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

	Long countQuestionByTagsContaining(Tag tag);

	Long countQuestionsByTagsContainingAndAcceptedTrueAndIdNotIn(Tag tag, Collection<Long> ids);

	Long countByAcceptedTrue();

	Long countQuestionByAcceptedTrueAndIdNotIn(Collection<Long> ids);

	Page<Question> findQuestionsByIdNotIn(Collection<Long> ids, Pageable pageable);

	Page<Question> getQuestionsByAcceptedFalse(Pageable pageable);

	List<Question> findQuestionsByTagsContaining(Tag tag);

	Page<Question> findAll(Pageable pageable);

	Page<Question> findAllByAcceptedTrue(Pageable pageable);

	Page<Question> findAllByTagsContainingAndAcceptedTrue(Tag tag, Pageable pageable);

	Page<Question> findQuestionsByIdNotInAndAcceptedTrueAndTagsContaining(
			Collection<Long> questions, Tag tag, Pageable pageable);

	Optional<Question> findQuestionById(Long questionID);

	@Transactional
	@Query(nativeQuery = true, value =
			"select q.user_id, question_id, id, title, question, accepted " +
					"from user_known_question " +
					"join questions q " +
					"on user_known_question.question_id = q.id " +
					"where user_known_question.user_id = ?1")
	List<Question> findQuestionsByUsersKnownThisQuestion(Long userId);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE questions q SET accepted = TRUE where q.id = ?1")
	void setAccepted(Long questionID);
}
