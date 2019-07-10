package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Role;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.RoleRepository;
import com.sikoramarek.fiszki.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Collection;
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

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void newUser() throws Exception {
        UserModel newUser = new UserModel();

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findRoleByRoleEquals("USER");
        roles.add(role);

        newUser.setUsername("newUser");
        newUser.setPassword(super.bCryptPasswordEncoder.encode("abc"));
        newUser.setEmail("newUser@user.pl");
        newUser.setRoles(roles);

        String requestJson = super.mapToJson(newUser);
        MvcResult mvcResult = performPost(uri, requestJson, UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        UserModel[] userModelResponse = super.mapFromJson(responseString, UserModel[].class);
        assertNotNull( userModelResponse[0].getId());
        assertEquals(1, userModelResponse.length);
        assertEquals("newUser", userModelResponse[0].getUsername());
        assertEquals("newUser@user.pl", userModelResponse[0].getPassword());
    }

    @Test
    public void newUserNullName() throws Exception {
        UserModel newUser = new UserModel();

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findRoleByRoleEquals("USER");
        roles.add(role);

        newUser.setUsername("");
        newUser.setPassword(super.bCryptPasswordEncoder.encode("abc"));
        newUser.setEmail("newUser@user.pl");
        newUser.setRoles(roles);

        String requestJson = super.mapToJson(newUser);
        MvcResult mvcResult = performPost(uri, requestJson, UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void newUserExist() throws Exception {
        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setRole("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        UserModel newUser = new UserModel();
        newUser.setUsername(super.userName);
        newUser.setPassword(bCryptPasswordEncoder.encode(super.password));
        newUser.setRoles(roles);
        newUser.setEmail("blabla@blabla.com");

        String requestJson = super.mapToJson(newUser);
        MvcResult mvcResult = performPost(uri, requestJson, UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(409, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void getMarkedQuestionsNotLogged() throws Exception{
        MvcResult mvcResult = performGet(uri + "/known_questions", UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void getMarkedQuestionsLogged() throws Exception{

        MvcResult mvcResult = performGet(uri + "/known_questions", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);



        Collection<Question> knownQuestions = userRepository.getUserByUsername(super.userName).getKnownQuestions();
        int questionsCount = knownQuestions.size();
//        int questionsCount = 0; it works


        String responseString = mvcResult.getResponse().getContentAsString();
        Question[] questionsResponse = super.mapFromJson(responseString, Question[].class);

        assertEquals(questionsCount, questionsResponse.length);
    }

    @Test
    public void markQuestion() throws Exception{;

        Question question = new Question();
        question.setId((long) 1);
        question.setTitle("uuu");
        question.setQuestion("aaa");
        questionRepository.save(question);

        MvcResult mvcResult = performGet(uri + "users/mark_question/1", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        Question[] questionsResponse = super.mapFromJson(responseString, Question[].class);
        assertEquals("uuu", questionsResponse[0].getTitle());

    }

    @Test
    public void markQuestionNotLogged() throws Exception{
        MvcResult mvcResult = performGet(uri + "users/mark_question/100", UNLOGGED);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(401, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }

    @Test
    public void markQuestionNonExisting() throws Exception{
        MvcResult mvcResult = performGet(uri + "users/mark_question/100", USER);

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);

        String responseString = mvcResult.getResponse().getContentAsString();
        assertEquals("", responseString);
    }


}