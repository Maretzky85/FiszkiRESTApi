package com.sikoramarek.fiszki.repository;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.projections.AnswerOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

	List<Answer> findByQuestion_Id(Long question_id);
	Answer findAnswerById(Long answerId);

//	@Query(nativeQuery = true, value = "select * from answers a where a.user_id = ?1")
	Collection<AnswerOnly> findAnswersByUser(String userName);
}
