package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.model.projections.AnswerOnly;
import com.sikoramarek.fiszki.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static com.sikoramarek.fiszki.UserType.UNLOGGED;
import static com.sikoramarek.fiszki.UserType.USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AdminControllerTest extends AbstractTest {

    @Autowired
    UserRepository userRepository;

    private String uri = "/admin/";
    private Long questionId;


    @Before
    public void before() throws Exception {
        Question question = new Question();
        question.setQuestion("uuu");
        question.setTitle("aaa");
        String jsonPost = mapToJson(question);
        MvcResult postResult = performPost("/questions", jsonPost, USER);
        questionId = mapFromJson(postResult.getResponse().getContentAsString(), Question.class).getId();

        Answer answer = new Answer();
        answer.setAnswer("Answer to question");
        answer.setQuestion(question);
        jsonPost = mapToJson(answer);
        performPost("/questions/"+ questionId + "/answers", jsonPost, USER);
    }

    private Question getQuestionById(Long questionId) {
        return questionRepository.findQuestionById(questionId).get();
    }


    @Test
    public void acceptQuestion() throws Exception {
        String jsonPost = mapToJson(getQuestionById(questionId));
        MvcResult mvcResult = performPost(uri + "accept/" + questionId, jsonPost, UserType.ADMIN);

        int status = mvcResult.getResponse().getStatus();
        String responseString = mvcResult.getResponse().getContentAsString();
        Question questions = super.mapFromJson(responseString, Question.class);
        assertEquals(200, status);
        assertEquals(true, questions.isAccepted() );
    }

    @Test
    public void acceptQuestionNotAdmin() throws Exception {
        String jsonPost = mapToJson(getQuestionById(questionId));
        MvcResult mvcResult = performPost(uri + "accept/" + questionId, jsonPost, UserType.USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void acceptQuestionUnlogged() throws Exception {
        String jsonPost = mapToJson(getQuestionById(questionId));
        MvcResult mvcResult = performPost(uri + "accept/" + questionId, jsonPost, UNLOGGED);

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
        assertEquals(2, users.length );
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
        MvcResult mvcResult = performGet(uri + "users/" + super.userName+ "/questions", UserType.ADMIN);

        int status = mvcResult.getResponse().getStatus();
        String responseString = mvcResult.getResponse().getContentAsString();
        Question[] questions = super.mapFromJson(responseString, Question[].class);
        assertEquals(200, status);
        assertEquals(1, questions.length);
    }

    @Test
    public void getUserQuestionsNotAdmin() throws Exception {
        MvcResult mvcResult = performGet(uri + "users/" + super.userName+ "/questions", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getUserQuestionsUnlogged() throws Exception {
        MvcResult mvcResult = performGet(uri + "users/" + super.userName+ "/questions", UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getUserAnswers() throws Exception {
        MvcResult mvcResult = performGet(uri + "users/" + super.userName+ "/answers", UserType.ADMIN);

        int status = mvcResult.getResponse().getStatus();
        String responseString = mvcResult.getResponse().getContentAsString();
        System.out.println(responseString);
        assertEquals(200, status);
        //TODO check how to do projections test
        assertTrue(responseString.length() > 2);
    }

    @Test
    public void getUserAnswersNotAdmin() throws Exception {
        MvcResult mvcResult = performGet(uri + "users/" + super.userName+ "/answers", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }

    @Test
    public void getUserAnswersUnlogged() throws Exception {
        MvcResult mvcResult = performGet(uri + "users/" + super.userName+ "/answers", UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);
    }
}
