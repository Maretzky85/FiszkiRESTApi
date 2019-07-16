package com.sikoramarek.fiszki.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.DataGenerator;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static com.sikoramarek.fiszki.UserType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuestionControllerTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        DataGenerator dataGenerator = new DataGenerator();
        dataGenerator.generateTags().forEach(tag -> tagRepository.save(tag));

        dataGenerator.generateQuestions(getTags()).forEach(question -> {
            String jsonPost;
            try {
                jsonPost = mapToJson(question);
                performPost("/questions", jsonPost, USER);
            } catch (JsonProcessingException e) {
                System.out.println("There is a problem with JSON");
            } catch (Exception e) {
                System.out.println("There is problem with saving in DB");
            }
        });

        dataGenerator.generateAnswers(questionRepository.findAll()).forEach(answer -> {
            String jsonPost;
            try {
                Long questionId = answer.getQuestion().getId();
                jsonPost = mapToJson(answer);
                performPost("/questions/" + questionId + "/answers", jsonPost, USER);
            } catch (JsonProcessingException e) {
                System.out.println("Something is wrong with JSON");
            } catch (Exception e) {
                System.out.println("Something wrong with adding to DB");
            }
        });
    }

    private Set<Tag> getTags() {
        return new HashSet<>(tagRepository.findAll());
    }

    @Test
    public void getPageableQuestionsExpects200() throws Exception {
        int page = 1;
        int size = 10;
        int status = performGet("/questions?page=" + page + "&size=" + size, USER).getResponse().getStatus();
        assertEquals(200, status);
        status = performGet("/questions?page=" + page + "&size=" + size, ADMIN).getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void getQuestionByIdExpects200() throws Exception {
        assertEquals(200, getQuestionByIdStatus(getRandomQuestionId(), USER));
        assertEquals(200, getQuestionByIdStatus(getRandomQuestionId(), ADMIN));
    }

    @Test
    public void getQuestionByIdExpects404() throws Exception {
        assertEquals(404, getQuestionByIdStatus(1000000L, USER));
        assertEquals(404, getQuestionByIdStatus(1000000L, ADMIN));
    }

    private int getQuestionByIdStatus(Long questionId, UserType userType) throws Exception {
        return performGet("/questions/" + questionId, userType).getResponse().getStatus();
    }

    @Test
    public void newQuestionExpects200() throws Exception {
        assertEquals(200, newQuestionStatus(USER, 1));
        assertEquals(200, newQuestionStatus(ADMIN, 1));
    }

    @Test
    public void newQuestionExpects401() throws Exception {
        assertEquals(401, newQuestionStatus(UNLOGGED, 0));
    }

    @Test
    public void newQuestionExpects400() throws Exception {
        String jsonPost = "{\"user\":null,\"id\":null,\"title\":\"New question title\",\"question\":\"New Question\",}";
        assertEquals(400, performPost("/questions", jsonPost, USER).getResponse().getStatus());
        assertEquals(400, performPost("/questions", jsonPost, ADMIN).getResponse().getStatus());
    }

    private int newQuestionStatus(UserType userType, int countModifier) throws Exception {
        long countBeforeTest = questionRepository.count();

        String jsonPost = mapToJson(newQuestion());
        MvcResult result = performPost("/questions", jsonPost, userType);
        long countAfterTest = questionRepository.count();

        assertEquals(countBeforeTest + countModifier, countAfterTest);
        return result.getResponse().getStatus();
    }

    private Question newQuestion() {
        Question question = new Question();
        question.setAccepted(false);
        question.setQuestion("New Question");
        question.setTitle("New question title");
        question.setTags(getTags());
        return question;
    }

    @Test
    public void editQuestionExpects200() throws Exception {
        assertEquals(200, editQuestionStatus(getFirstAvailableQuestionId(), "New content for question", USER, ""));
        assertEquals(200, editQuestionStatus(getFirstAvailableQuestionId(), "New content for question 2", ADMIN, ""));
    }

    @Test
    public void editQuestionExpects401() throws Exception {
        assertEquals(401, editQuestionStatus(getFirstAvailableQuestionId(), "New content for question 3", UNLOGGED, ""));
    }

    @Test
    public void editQuestionExpects404() throws Exception {
        assertEquals(404, editQuestionStatus(getFirstAvailableQuestionId(), "New content for question 4", USER, "10000"));
        assertEquals(404, editQuestionStatus(getFirstAvailableQuestionId(), "New content for question 5", ADMIN, "10000"));
    }

    @Test
    public void editQuestionExpects400() throws Exception {
        assertEquals(400, editQuestionStatus(getFirstAvailableQuestionId(), "New content for question 4", USER, "somewhat"));
        assertEquals(400, editQuestionStatus(getFirstAvailableQuestionId(), "New content for question 5", ADMIN, "somewhat"));
    }

    private int editQuestionStatus(Long questionId, String newQuestionContent, UserType userType, String urlModifier) throws Exception {
        Question question = getQuestionById(questionId);
        String oldQuestionContent = question.getQuestion();
        question.setQuestion(newQuestionContent);

        String requestJson = mapToJson(question);
        MvcResult result = performPut("/questions/" + questionId + urlModifier, requestJson, userType);
        int status = result.getResponse().getStatus();
        if (status != 200) {
            assertEquals(oldQuestionContent, getQuestionById(questionId).getQuestion());
        } else {
            assertEquals(newQuestionContent, getQuestionById(questionId).getQuestion());
        }
        return status;
    }

    private Question getQuestionById(Long questionId) {
        return questionRepository.findQuestionById(questionId).get();
    }

    @Test
    public void deleteQuestionExpects200() throws Exception {
        assertEquals(200, deleteQuestionStatus(getFirstAvailableQuestionId(), 1, USER));
        assertEquals(200, deleteQuestionStatus(getFirstAvailableQuestionId(), 1, ADMIN));
    }

    private Long getFirstAvailableQuestionId() {
        return questionRepository.findAll().get(0).getId();
    }

    @Test
    public void deleteQuestionExpects401() throws Exception {
        assertEquals(401, deleteQuestionStatus(getFirstAvailableQuestionId(), 0, UNLOGGED));
    }

    @Test
    public void deleteQuestionExpects404() throws Exception {
        assertEquals(404, deleteQuestionStatus(1111111111L, 0, USER));
        assertEquals(404, deleteQuestionStatus(1111111111L, 0, ADMIN));
    }

    private int deleteQuestionStatus(Long questionId, int countModifier, UserType userType) throws Exception {
        long countBeforeDelete = questionRepository.count();

        MvcResult result = performDelete("/questions/" + questionId, userType);
        long countAfterDelete = questionRepository.count();
        assertEquals(countBeforeDelete - countModifier, countAfterDelete);

        return result.getResponse().getStatus();
    }

    @Test
    public void getRandom() throws Exception {
        assertEquals(200, getRandomStatus(USER));
        assertEquals(200, getRandomStatus(ADMIN));
        assertEquals(200, getRandomStatus(UNLOGGED));
    }

    private int getRandomStatus(UserType userType) throws Exception {
        MvcResult result = performGet("/questions/random", userType);
        assertNotNull(result.getResponse().getContentAsString());
        return result.getResponse().getStatus();
    }

    @Test
    public void getAnswersByQuestionIdExpects200() throws Exception {
        assertEquals(200, getAnswersToQuestion(getRandomQuestionId(), USER));
        assertEquals(200, getAnswersToQuestion(getRandomQuestionId(), ADMIN));
        assertEquals(200, getAnswersToQuestion(getRandomQuestionId(), UNLOGGED));
    }

    private int getAnswersToQuestion(Long questionId, UserType userType) throws Exception {
        MvcResult result = performGet("/questions/" + questionId + "/answers", userType);
        return result.getResponse().getStatus();
    }

    @Test
    public void newAnswerExpects200() throws Exception {
        assertEquals(200, addNewAnswerToQuestion(getRandomQuestionId(), 1, USER, "", ""));
        assertEquals(200, addNewAnswerToQuestion(getRandomQuestionId(), 1, ADMIN, "", ""));
    }

    @Test
    public void newAnswerExpects401() throws Exception {
        assertEquals(401, addNewAnswerToQuestion(getRandomQuestionId(), 0, UNLOGGED, "", ""));
    }

    @Test
    public void newAnswerExpects404() throws Exception {
        assertEquals(404, addNewAnswerToQuestion(getRandomQuestionId(), 0, USER, "", "ddd"));
        assertEquals(404, addNewAnswerToQuestion(getRandomQuestionId(), 0, ADMIN, "", "ddd"));
    }

    @Test
    public void newAnswerExpects400() throws Exception{
        assertEquals(400, addNewAnswerToQuestion(getRandomQuestionId(), 0, USER, "ddd", ""));
        assertEquals(400, addNewAnswerToQuestion(getRandomQuestionId(), 0, ADMIN, "ddd", ""));
    }

    private Long getRandomQuestionId() {
        List<Long> ids = new ArrayList<>();
        questionRepository.findAll().forEach(question -> ids.add(question.getId()));
        Random random = new Random();

        return ids.get(random.nextInt(ids.size()));
    }

    private int addNewAnswerToQuestion(Long questionId, int answerCountModifier, UserType userType,  String urlModifier1, String urlModifier2) throws Exception {
        Question question = questionRepository.findQuestionById(getFirstAvailableQuestionId()).get();
        long answersCountBeforeAdding = answerRepository.count();
        Answer answer = addAnswer(question);
        String jsonPost = mapToJson(answer);
        MvcResult result = performPost("/questions/" + questionId + urlModifier1 + "/answers" + urlModifier2, jsonPost, userType);
        int status = result.getResponse().getStatus();
        long answersCountAfterAdding = answerRepository.count();
        assertEquals(answersCountBeforeAdding + answerCountModifier, answersCountAfterAdding);
        return status;
    }

    private Answer addAnswer(Question question) {
        Answer answer = new Answer();
        answer.setAnswer("Answer " + Math.random());
        answer.setQuestion(question);
        return answer;
    }
}