package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static com.sikoramarek.fiszki.UserType.*;
import static org.junit.Assert.*;

public class QuestionControllerTest extends AbstractTest {

    String uri = "/questions/";

    @Before
    public void before() throws Exception {
        questionRepository.deleteAll();
        answerRepository.deleteAll();
        tagRepository.deleteAll();
    }

    private Set<Tag> getTags() {
        return new HashSet<>(tagRepository.findAll());
    }

    @Test
    public void getPageableQuestionsExpects200() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        int page = 0;
        int size = 10;
        for (int i = 0; i < 10; i++) {
            prepareQuestionAndReturnId(false, false);
        }

        MvcResult mvcResult = performGet("/questions?page=" + page + "&size=" + size, USER);
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(response + "should have empty:true", response.contains("\"empty\":true"));
        assertTrue(response + "should have numberOfElements: 0", response.contains("\"numberOfElements\":0"));

        mvcResult = performGet("/questions?page=" + page + "&size=" + size, ADMIN);
        response = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(response + "should have empty: false", response.contains("\"empty\":true"));
        assertTrue(response + "should have numberOfElements:" + size, response.contains("\"numberOfElements\":" + size));

        List<Question> questions = questionRepository.findAll();
        questions.forEach(question -> {
            question.setAccepted(true);
            questionRepository.save(question);
        });

        mvcResult = performGet("/questions?page=" + page + "&size=" + size, USER);
        response = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(response + "should have empty: false", response.contains("\"empty\":true"));
        assertTrue(response + "should have numberOfElements:" + size, response.contains("\"numberOfElements\":" + size));
    }

    @Test
    public void getQuestionByIdExpects200() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());

        Long questionId = prepareQuestionAndReturnId(false, false);

        MvcResult mvcResult = performGet(uri + questionId, USER);
        assertEquals(200, mvcResult.getResponse().getStatus());
        Question[] questions = mapFromJson(mvcResult.getResponse().getContentAsString(), Question[].class);
        assertEquals(1, questions.length);
        assertFalse(questions[0].getQuestion().isEmpty());
        assertFalse(questions[0].getTitle().isEmpty());

        mvcResult = performGet(uri + questionId, ADMIN);
        assertEquals(200, mvcResult.getResponse().getStatus());
        questions = mapFromJson(mvcResult.getResponse().getContentAsString(), Question[].class);
        assertEquals(1, questions.length);
        assertFalse(questions[0].getQuestion().isEmpty());
        assertFalse(questions[0].getTitle().isEmpty());
    }

    @Test
    public void getQuestionByIdExpects404() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());

        MvcResult mvcResult = performGet(uri + 1230, USER);
        assertEquals(404, mvcResult.getResponse().getStatus());

        mvcResult = performGet(uri + 1230, ADMIN);
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void newQuestionExpects200() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());

        Question question = Question.builder().question("test").title("test").build();

        MvcResult mvcResult = performPost(uri, mapToJson(question), USER);

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals("test", mapFromJson(mvcResult.getResponse().getContentAsString(), Question.class).getQuestion());

        question = Question.builder().question("test2").title("test2").build();

        mvcResult = performPost(uri, mapToJson(question), ADMIN);

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals("test2", mapFromJson(mvcResult.getResponse().getContentAsString(), Question.class).getQuestion());
    }

    @Test
    public void newQuestionExpects401() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());

        Question question = Question.builder().question("test").title("test").build();

        MvcResult mvcResult = performPost(uri, mapToJson(question), UNLOGGED);

        assertEquals(401, mvcResult.getResponse().getStatus());
    }

    @Test
    public void newQuestionExpects400() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());

        Question question = Question.builder().title("").question("").build();

        MvcResult mvcResult = performPost(uri, mapToJson(question), USER);
        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult = performPost(uri, mapToJson(question), ADMIN);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    public void editQuestionExpects200() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, false);
        String testString1 = "EditQuestionChangeAsUser";
        String testString2 = "EditQuestionChangeAsAdmin";

        Question question = Question.builder().question(testString1).title(testString1).id(questionId).build();
        MvcResult mvcResult = performPut(uri + questionId, mapToJson(question), USER);
        assertEquals(200, mvcResult.getResponse().getStatus());
        question = mapFromJson(mvcResult.getResponse().getContentAsString(), Question.class);
        assertEquals(testString1, question.getQuestion());
        assertEquals(testString1, question.getTitle());

        question.setQuestion(testString2);
        question.setTitle(testString2);
        mvcResult = performPut(uri + questionId, mapToJson(question), ADMIN);
        assertEquals(200, mvcResult.getResponse().getStatus());
        question = mapFromJson(mvcResult.getResponse().getContentAsString(), Question.class);
        assertEquals(testString2, question.getQuestion());
        assertEquals(testString2, question.getTitle());
    }

    @Test
    public void editQuestionExpects401() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, false);
        String testString1 = "EditQuestionChange";

        Question question = Question.builder().question(testString1).title(testString1).id(questionId).build();
        MvcResult mvcResult = performPut(uri + questionId, mapToJson(question), UNLOGGED);
        assertEquals(401, mvcResult.getResponse().getStatus());
    }

    @Test
    public void editQuestionExpects404() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        String testString1 = "EditQuestionChange";
        Long questionId = 1234L;
        Question question = Question.builder().question(testString1).title(testString1).id(questionId).build();

        MvcResult mvcResult = performPut(uri + questionId, mapToJson(question), USER);
        assertEquals(404, mvcResult.getResponse().getStatus());

        mvcResult = performPut(uri + questionId, mapToJson(question), ADMIN);
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void editQuestionExpects400() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, false);
        Question question = Question.builder().question("").title("").id(questionId).build();

        MvcResult mvcResult = performPut(uri + questionId, mapToJson(question), USER);
        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult = performPut(uri + questionId, mapToJson(question), ADMIN);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteQuestionExpects200() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, false);

        MvcResult mvcResult = performDelete(uri + questionId, USER);
        assertEquals(200, mvcResult.getResponse().getStatus());

        questionId = prepareQuestionAndReturnId(false, false);

        mvcResult = performDelete(uri + questionId, ADMIN);
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteQuestionExpects401() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, false);

        MvcResult mvcResult = performDelete(uri + questionId, UNLOGGED);
        assertEquals(401, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteQuestionExpects404() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = 1234L;

        MvcResult mvcResult = performDelete(uri + questionId, USER);
        assertEquals(404, mvcResult.getResponse().getStatus());

        mvcResult = performDelete(uri + questionId, ADMIN);
        assertEquals(404, mvcResult.getResponse().getStatus());

    }

    @Test
    public void getRandom() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        prepareQuestionAndReturnId(false, true);

        Arrays.stream(values()).forEach(userType -> {
            try {
                MvcResult mvcResult = performGet(uri + "random", userType);
                assertEquals(200, mvcResult.getResponse().getStatus());
                System.out.println(mvcResult.getResponse().getContentAsString());
                Question[] questions = mapFromJson(mvcResult.getResponse().getContentAsString(), Question[].class);
                assertEquals(1, questions.length);
                assertNotNull(questions[0].getQuestion());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void getAnswersByQuestionIdExpects200() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(true, true);

        Arrays.stream(values()).forEach(userType -> {
            try {
                MvcResult result = performGet("/questions/" + questionId + "/answers", userType);
                assertEquals(200, result.getResponse().getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void newAnswerExpects200() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, false);

        Question question = questionRepository.findQuestionById(questionId).get();
        Answer answer = Answer.builder().answer("testAnswer").question(question).build();

        MvcResult mvcResult = performPost(uri + questionId + "/answers", mapToJson(answer), USER);
        assertEquals(200, mvcResult.getResponse().getStatus());

        answer = Answer.builder().answer("testAnswer2").question(question).build();
        mvcResult = performPost(uri + questionId + "/answers", mapToJson(answer), ADMIN);
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void newAnswerExpects401() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, true);

        Question question = questionRepository.findQuestionById(questionId).get();
        Answer answer = Answer.builder().answer("testAnswer").question(question).build();

        MvcResult mvcResult = performPost(uri + questionId + "/answers", mapToJson(answer), UNLOGGED);
        assertEquals(401, mvcResult.getResponse().getStatus());
    }

    @Test
    public void newAnswerExpects404() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, true);
        Long nonExistentQuestionId = 12345L;

        Question question = questionRepository.findQuestionById(questionId).get();
        question.setId(nonExistentQuestionId);
        Answer answer = Answer.builder().answer("testAnswer").question(question).build();

        MvcResult mvcResult = performPost(uri + nonExistentQuestionId + "/answers", mapToJson(answer), USER);
        assertEquals(404, mvcResult.getResponse().getStatus());

        mvcResult = performPost(uri + nonExistentQuestionId + "/answers", mapToJson(answer), ADMIN);
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void newAnswerExpects400() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        Long questionId = prepareQuestionAndReturnId(false, true);

        Question question = questionRepository.findQuestionById(questionId).get();
        Answer answer = Answer.builder().answer("").question(question).build();

        MvcResult mvcResult = performPost(uri + questionId + "/answers", mapToJson(answer), USER);
        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult = performPost(uri + questionId + "/answers", mapToJson(answer), ADMIN);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }
}