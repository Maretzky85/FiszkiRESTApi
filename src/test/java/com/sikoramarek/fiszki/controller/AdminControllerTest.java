package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.UserModel;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static com.sikoramarek.fiszki.UserType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AdminControllerTest extends AbstractTest {

    private String uri = "/admin/";


    @Before
    public void before() {
        questionRepository.deleteAll();
        answerRepository.deleteAll();
    }


    @Test
    public void acceptQuestion() throws Exception {
        assertEquals(questionRepository.count(), 0);

        Long questionId = prepareQuestionAndReturnId(false);

        MvcResult mvcResult = performPost(uri + "accept/" + questionId, mapToJson(""), ADMIN);

        int status = mvcResult.getResponse().getStatus();
        String responseString = mvcResult.getResponse().getContentAsString();
        Question questions = super.mapFromJson(responseString, Question.class);
        assertEquals(200, status);
        assertTrue(questions.isAccepted());
    }

    @Test
    public void acceptQuestionNotAdmin() throws Exception {
        assertEquals(questionRepository.count(), 0);

        Long questionId = prepareQuestionAndReturnId(false);

        MvcResult mvcResult = performPost(uri + "accept/" + questionId, mapToJson(""), UserType.USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void acceptQuestionUnlogged() throws Exception {
        assertEquals(questionRepository.count(), 0);

        Long questionId = prepareQuestionAndReturnId(false);

        MvcResult mvcResult = performPost(uri + "accept/" + questionId, mapToJson(""), UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getAllUsers() throws Exception {
        MvcResult mvcResult = performGet(uri + "users", UserType.ADMIN);

        int status = mvcResult.getResponse().getStatus();
        String responseString = mvcResult.getResponse().getContentAsString();
        UserModel[] users = super.mapFromJson(responseString, UserModel[].class);
        assertEquals(200, status);
        assertEquals(2, users.length);
    }

    @Test
    public void getAllUsersNotAdmin() throws Exception {
        MvcResult mvcResult = performGet(uri + "users", UserType.USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getAllUsersUnlogged() throws Exception {
        MvcResult mvcResult = performGet(uri + "users", UserType.UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getUserQuestions() throws Exception {
        assertEquals(questionRepository.count(), 0);

        prepareQuestionAndReturnId(false);

        MvcResult mvcResult = performGet(uri + "users/" + super.userName + "/questions", UserType.ADMIN);

        int status = mvcResult.getResponse().getStatus();
        String responseString = mvcResult.getResponse().getContentAsString();
        Question[] questions = super.mapFromJson(responseString, Question[].class);
        assertEquals(200, status);
        assertEquals(1, questions.length);
    }

    @Test
    public void getUserQuestionsNotAdmin() throws Exception {
        assertEquals(questionRepository.count(), 0);

        prepareQuestionAndReturnId(false);

        MvcResult mvcResult = performGet(uri + "users/" + super.userName + "/questions", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getUserQuestionsUnlogged() throws Exception {
        assertEquals(questionRepository.count(), 0);

        prepareQuestionAndReturnId(false);

        MvcResult mvcResult = performGet(uri + "users/" + super.userName + "/questions", UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getUserAnswers() throws Exception {
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);

        prepareQuestionAndReturnId(true);

        MvcResult mvcResult = performGet(uri + "users/" + super.userName + "/answers", UserType.ADMIN);

        int status = mvcResult.getResponse().getStatus();
        String responseString = mvcResult.getResponse().getContentAsString();
        System.out.println(responseString);
        assertEquals(200, status);
        //TODO check how to do projections test
        assertTrue(responseString.length() > 2);
    }

    @Test
    public void getUserAnswersNotAdmin() throws Exception {
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);

        prepareQuestionAndReturnId(true);

        MvcResult mvcResult = performGet(uri + "users/" + super.userName + "/answers", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getUserAnswersUnlogged() throws Exception {
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);

        prepareQuestionAndReturnId(true);

        MvcResult mvcResult = performGet(uri + "users/" + super.userName + "/answers", UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

}
