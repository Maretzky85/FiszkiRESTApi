package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import com.sikoramarek.fiszki.repository.UserRepository;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RestController
public class QuestionController extends AbstractController {
	private QuestionRepository questionsDAO;
	private AnswerRepository answersDAO;
	private TagRepository tagDAO;
	private UserRepository usersDAO;

	@Autowired
	public QuestionController(QuestionRepository questionsDAO,
	                          TagRepository tagDAO,
	                          AnswerRepository answersDao,
	                          UserRepository usersDAO) {
		this.questionsDAO = questionsDAO;
		this.tagDAO = tagDAO;
		this.answersDAO = answersDao;
		this.usersDAO = usersDAO;
	}

	@GetMapping("questions")
	public Page<Question> getAllQuestions() {
		return questionsDAO.findAll(PageRequest.of(0, 10));
	}

	@GetMapping(value = "questions", params = {"page", "size"})
	public Page<Question> getPageableQuestions(@RequestParam("page") int page, @RequestParam("size") int size) {
		return questionsDAO.findAll(PageRequest.of(page, size));
	}

	@GetMapping("questions/{question_id}")
	public ResponseEntity<ArrayList<Question>> getQuestionById(@PathVariable("question_id") Long question_id) {
		return returnIfNotEmpty(questionsDAO.findById(question_id));
	}

	@PostMapping("questions")
	public ResponseEntity<Question> newQuestion(@RequestBody Question question, Principal principal) {
		question.setUser(getUserFromPrincipalIfExist(principal));
		questionsDAO.save(question);
		if (question.getAnswers().size() > 0) {
			try {
				Collection<Answer> answers = question.getAnswers();
				answers.forEach(answer -> {
					answer.setQuestion(question);
					answersDAO.save(answer);
				});
			} catch (DataAccessException | ConstraintViolationException e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	@PutMapping("questions/{question_id}")
	public ResponseEntity<Question> editQuestion(@RequestBody Question question,
	                                             @PathVariable("question_id") Long question_id) {
		if (questionsDAO.existsById(question.getId())) {
			for (Tag tag : question.getTags()
			) {
				if (!tagDAO.existsById(tag.getId())) {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			questionsDAO.save(question);
			return new ResponseEntity<>(question, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
	}

	@DeleteMapping("questions/{question_id}")
	public ResponseEntity<Question> deleteQuestion(@PathVariable("question_id") Long question_id) {
		Optional<Question> optionalQuestion = questionsDAO.findById(question_id);
		if (optionalQuestion.isPresent()) {
			Question question = optionalQuestion.get();
			questionsDAO.delete(question);
			return new ResponseEntity<>(question, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("questions/random")
	public ResponseEntity<ArrayList<Question>> getRandom(Principal principal) {
		UserModel user = getUserFromPrincipalIfExist(principal);
		Long quantity = questionsDAO.count();
		int index = (int) (Math.random() * quantity);
		Page<Question> questionPage = questionsDAO.findAll(PageRequest.of(index, 1, Sort.unsorted()));
		if (questionPage.hasContent()) {
			return new ResponseEntity<>(packToArray(questionPage.getContent().get(0)), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ArrayList<>(1), HttpStatus.OK);
	}
}
