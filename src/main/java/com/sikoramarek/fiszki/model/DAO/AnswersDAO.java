package com.sikoramarek.fiszki.model.DAO;

import com.sikoramarek.fiszki.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AnswersDAO extends JpaRepository<Answer, Long> {

	List<Answer> findByQuestion_Id(Long question_id);
}
