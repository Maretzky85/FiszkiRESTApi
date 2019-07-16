package com.sikoramarek.fiszki.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.DataGenerator;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.service.QuestionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;

import static com.sikoramarek.fiszki.UserType.*;
import static org.junit.Assert.*;

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
    }

    private Set<Tag> getTags() {
        Set<Tag> tags = new HashSet<>();
        tags.addAll(tagRepository.findAll());
        return tags;
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
        assertEquals(200, getQuestionByIdStatus(1, USER));
        assertEquals(200, getQuestionByIdStatus(1, ADMIN));
    }

    @Test
    public void getQuestionByIdExpects404() throws Exception {
        assertEquals(404, getQuestionByIdStatus(100000, USER));
        assertEquals(404, getQuestionByIdStatus(1000000, ADMIN));
    }

    private int getQuestionByIdStatus(int questionId, UserType userType) throws Exception {
        return performGet("/questions/" + questionId, userType).getResponse().getStatus();
    }

    @Test
    public void newQuestionExpects200() throws Exception {
        assertEquals(200, newQuestionStatus(USER, 1, ""));
        assertEquals(200, newQuestionStatus(ADMIN, 1, ""));
    }

    @Test
    public void newQuestionExpects401() throws Exception {
        assertEquals(401, newQuestionStatus(UNLOGGED, 0, ""));
    }

    @Test
    public void newQuestionExpects400() throws Exception {
        String jsonPost = "{\"user\":null,\"id\":null,\"title\":\"New question title\",\"question\":\"New Question\",}";
        assertEquals(400, performPost("/questions", jsonPost, USER).getResponse().getStatus());
        assertEquals(400, performPost("/questions", jsonPost, ADMIN).getResponse().getStatus());
    }

    private int newQuestionStatus(UserType userType, int countModifier, String urlModifier) throws Exception {
        long countBeforeTest = questionRepository.count();

        String jsonPost = mapToJson(newQuestion());
        MvcResult result = performPost("/questions" + urlModifier, jsonPost, userType);
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
    public void editQuestion() {
    }

    @Test
    public void deleteQuestionExpects200() throws Exception {
        assertEquals(200, deleteQuestionStatus(3, 1, USER));
        assertEquals(200, deleteQuestionStatus(2, 1, ADMIN));
    }

    @Test
    public void deleteQuestionExpects401() throws Exception {
        assertEquals(401, deleteQuestionStatus(3, 0, UNLOGGED));
    }

    @Test
    public void deleteQuestionExpects404() throws Exception {
        assertEquals(404, deleteQuestionStatus(1111111111, 0, USER));
        assertEquals(404, deleteQuestionStatus(1111111111, 0, ADMIN));
    }

    private int deleteQuestionStatus(int questionId, int countModifier, UserType userType) throws Exception {
        long countBeforeDelete = questionRepository.count();

        MvcResult result = performDelete("/questions/" + questionId, userType);
        long countAfterDelete = questionRepository.count();
        assertEquals(countBeforeDelete - countModifier, countAfterDelete);

        return result.getResponse().getStatus();
    }

    @Test
    public void getRandom() {
    }

    @Test
    public void getUnaccepted() {
    }
}