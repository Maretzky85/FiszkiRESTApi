package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.DAO.AnswersDAO;
import com.sikoramarek.fiszki.model.DAO.QuestionsDAO;
import com.sikoramarek.fiszki.model.DAO.TagDAO;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*")
public class QuestionController extends ReturnController {
	private QuestionsDAO questionsDAO;
	private AnswersDAO answersDAO;
	private TagDAO tagDAO;

	public QuestionController(@Autowired QuestionsDAO questionsDAO,
	                          @Autowired TagDAO tagDAO,
	                          @Autowired AnswersDAO answersDao){
		this.questionsDAO = questionsDAO;
		this.tagDAO = tagDAO;
		this.answersDAO = answersDao;
	}

	@GetMapping("questions")
	public ResponseEntity getAllQuestions() {
		return returnCollectionIfNotEmpty(questionsDAO.findAll());
	}

	@GetMapping("questions/{question_id}")
	public ResponseEntity<Question> getQuestionById(@PathVariable("question_id") Long question_id){
		return returnIfNotEmpty(questionsDAO.findById(question_id));
	}

	@PostMapping("questions")
	public ResponseEntity<Question> newQuestion(@RequestBody Question question){
		questionsDAO.save(question);
		try {
			Collection<Answer> answers = question.getAnswers();
			answers.forEach(answer -> {
				answer.setQuestion(question);
				answersDAO.save(answer);
			});
			return new ResponseEntity<>(question, HttpStatus.OK);
		}catch (DataAccessException | ConstraintViolationException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PatchMapping("questions/{question_id}")
	public ResponseEntity<Question> editQuestion(@RequestBody Question question, @PathVariable("question_id") Long question_id){
		Optional<Question> optionalQuestion = questionsDAO.findById(question_id);
		if (optionalQuestion.isPresent()){
			for (Tag tag : question.getTags()
			) {
				Optional<Tag> tagCheck = tagDAO.findById(tag.getId());
				if (!tagCheck.isPresent()){
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			Question questionToEdit = optionalQuestion.get();
			questionToEdit.setTitle(question.getTitle());
			questionToEdit.setQuestion(question.getQuestion());
			questionToEdit.setTags(question.getTags());
			questionsDAO.save(questionToEdit);
			return new ResponseEntity<>(questionToEdit, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
	}

	@DeleteMapping("questions/{question_id}")
	public ResponseEntity<Question> deleteQuestion(@PathVariable("question_id") Long question_id){
		Optional<Question> optionalQuestion = questionsDAO.findById(question_id);
		if (optionalQuestion.isPresent()) {
			Question question = optionalQuestion.get();
			questionsDAO.delete(question);
			return new ResponseEntity<>(question, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	private Question getRandom(){
		Long qty = questionsDAO.count();
		int idx = (int)(Math.random() * qty);
		Page<Question> questionPage = questionsDAO.findAll(PageRequest.of(idx, 1, Sort.unsorted()));
		Question q = null;
		if (questionPage.hasContent()) {
			q = questionPage.getContent().get(0);
		}
		return q;
	}
}
