package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;

public class AnswerControllerTest extends AbstractTest {

    private Long answerId;
    private Answer answer;
    private String uri = "/answers/";
    private int status;
    private MvcResult mvcResult;
    private String requestJson;

    @Autowired
    private AnswerRepository answerRepository;

    @After
    public void setDown() {
        answer = answerRepository.findAnswerById(answerId);
        System.out.println("Answer after update: " + answer);
    }

    @Test
    public void editAnswerByIdByLoggedUserExpects200() throws Exception {
        answerId = 2L;
        answer = answerRepository.findAnswerById(answerId);
        answer.setAnswer("Nowa odpowiedz");
        requestJson = mapToJson(answer);
        mvcResult = performPut(uri + answerId, requestJson, UserType.USER);
        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void editAnswerByIdByAdminExpects200() throws Exception {
        answerId = 1L;
        answer = answerRepository.findAnswerById(answerId);
        answer.setAnswer("Nowa odpowiedz");
        requestJson = mapToJson(answer);
        mvcResult = performPut(uri + answerId, requestJson, UserType.ADMIN);
        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }


    @Test
    public void editAnswerByIdByUnloggedUserExpects401() throws Exception {
        answerId = 1L;
        answer = answerRepository.findAnswerById(answerId);
        answer.setAnswer("Nowa odpowiedz");
        requestJson = super.mapToJson(answer);
        mvcResult = performPut(uri + answerId, requestJson, UserType.UNLOGGED);
        status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void editAnswerByIdByLoggedUserExpects404() throws Exception {
        answerId = 2L;
        answer = answerRepository.findAnswerById(answerId);
        answer.setAnswer("Nowa odpowiedz");
        requestJson = super.mapToJson(answer);
        mvcResult = performPut(uri + "1444", requestJson, UserType.USER);
        status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
    }

    @Test
    public void editAnswerByIdByAdminExpects404() throws Exception {
        answerId = 1L;
        answer = answerRepository.findAnswerById(answerId);
        answer.setAnswer("Nowa odpowiedz");
        requestJson = super.mapToJson(answer);
        mvcResult = performPut(uri + answerId + "1444", requestJson, UserType.ADMIN);
        status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
    }

    @Test
    public void editAnswerByIdByLoggedUserExpects400() throws Exception {
        answerId = 2L;
        answer = answerRepository.findAnswerById(answerId);
        answer.setAnswer("Nowa odpowiedz");
        requestJson = super.mapToJson(answer);
        mvcResult = performPut(uri + answerId + ":user", requestJson, UserType.USER);
        status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    public void editAnswerByIdByAdminExpects400() throws Exception {
        answerId = 1L;
        answer = answerRepository.findAnswerById(answerId);
        answer.setAnswer("Nowa odpowiedz");
        requestJson = super.mapToJson(answer);
        mvcResult = performPut(uri + answerId + ":user", requestJson, UserType.ADMIN);
        status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    public void deleteAnswerByIdByLoggedUserExpects200() throws Exception {
        answerId = 74L;
        mvcResult = performDelete(uri + answerId, UserType.USER);
        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void deleteAnswerByIdByAdminExpects200() throws Exception {
        answerId = 76L;
        mvcResult = performDelete(uri + answerId, UserType.ADMIN);
        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void deleteAnswerByIdByUnloggedUserExpects401() throws Exception {
        answerId = 77L;
        mvcResult = performDelete(uri + answerId, UserType.UNLOGGED);
        status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void deleteAnswerByIdByLoggedUserAndNoOwnerExpects403() throws Exception {
        answerId = 87L;
        mvcResult = performDelete(uri + answerId, UserType.USER);
        status = mvcResult.getResponse().getStatus();
        assertEquals(403, status);
    }

    @Test
    public void deleteAnswerByIdByLoggedUserAndNoAdminExpects403() throws Exception {
        answerId = 88L;
        mvcResult = performDelete(uri + answerId, UserType.USER);
        status = mvcResult.getResponse().getStatus();
        assertEquals(403, status);
    }

    @Test
    public void deleteAnswerByIdByLoggedUserAndAnswerNotExistsExpects404() throws Exception {
        answerId = 88L;
        mvcResult = performDelete(uri + answerId + "222222", UserType.USER);
        status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
    }

    @Test
    public void deleteAnswerByIdByAdminAndAnswerNotExistsExpects404() throws Exception {
        answerId = 88L;
        mvcResult = performDelete(uri + answerId + "222222", UserType.ADMIN);
        status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
    }
}