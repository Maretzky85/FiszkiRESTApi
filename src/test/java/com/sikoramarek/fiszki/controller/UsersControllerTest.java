package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Role;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;

import static com.sikoramarek.fiszki.UserType.UNLOGGED;
import static com.sikoramarek.fiszki.UserType.USER;
import static org.junit.Assert.*;

public class UsersControllerTest extends AbstractTest {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    private String uri = "/users";

    @Test
    public void newUser() throws Exception {
        UserModel newUser = new UserModel();
        newUser.setUsername("newUser");
        newUser.setPassword("123456");
        newUser.setEmail("newUser@user.pl");

        String requestJson = createJsonUserFromModel(newUser);
        MvcResult mvcResult = performPost(uri, requestJson, UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        UserModel userModelResponse = super.mapFromJson(responseString, UserModel.class);
        assertNotNull(userModelResponse.getId());
        assertEquals("newUser", userModelResponse.getUsername());
        assertEquals("newUser@user.pl", userModelResponse.getEmail());
    }

    @Test
    public void newUserNullName() throws Exception {
        UserModel newUser = new UserModel();
        newUser.setUsername("");
        newUser.setPassword("123456");
        newUser.setEmail("newUser@user.pl");

        String requestJson = super.mapToJson(newUser);
        MvcResult mvcResult = performPost(uri, requestJson, UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void newUserExist() throws Exception {
        UserModel newUser = new UserModel();
        newUser.setUsername(super.userName);
        newUser.setPassword("123456");
        newUser.setEmail("blabla@blabla.com");

        String requestJson = createJsonUserFromModel(newUser);
        MvcResult mvcResult = performPost(uri, requestJson, UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(409, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void getMarkedQuestionsNotLogged() throws Exception {
        MvcResult mvcResult = performGet(uri + "/known_questions", UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void getMarkedQuestionsLogged() throws Exception {
        MvcResult mvcResult = performGet(uri + "/known_questions", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        Question[] questionsResponse = super.mapFromJson(responseString, Question[].class);

        assertEquals(0, questionsResponse.length);
    }

    @Test
    public void markQuestion() throws Exception {
        Question question = Question.builder()
                .accepted(true)
                .question("testQuestion")
                .title("TestTitle").build();

        String jsonPost = mapToJson(question);
        MvcResult postResult = performPost("/questions", jsonPost, USER);
        Long newQuestionId = mapFromJson(postResult.getResponse().getContentAsString(), Question.class).getId();

        MvcResult mvcResult = performPost(uri + "/mark_question/" + newQuestionId, jsonPost, USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void markQuestionNotLogged() throws Exception {
        Question question = Question.builder()
                .id(666L)
                .accepted(true)
                .title("TestTitle")
                .question("TestQuestion").build();
        String jsonPost = mapToJson(question);
        performPost("/questions", jsonPost, USER);

        MvcResult mvcResult = performPost(uri + "/mark_question/666", "", UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void markQuestionNonExisting() throws Exception {
        MvcResult mvcResult = performPost(uri + "/mark_question/100", "", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }
}