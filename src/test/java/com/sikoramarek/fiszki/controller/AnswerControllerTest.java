package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.model.Answer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static com.sikoramarek.fiszki.UserType.*;
import static org.junit.Assert.assertEquals;

public class AnswerControllerTest extends AbstractTest {

    private String uri = "/answers/";

    @Before
    public void before() {
        answerRepository.deleteAll();
        questionRepository.deleteAll();
    }

    @Test
    public void editAnswerExpects200() throws Exception {
        String test1String = "TestingSomeModificationToAnswer";
        String test2String = "TestingSomeOtherModificationToAnswer";
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);
        prepareQuestionAndReturnId(true);

        Answer answer = answerRepository.findAll().get(0);

        answer.setAnswer(test1String);
        MvcResult mvcResult = performPut(uri + answer.getId(), mapToJson(answer), USER);
        assertEquals(200, mvcResult.getResponse().getStatus());
        Answer answer1 = mapFromJson(mvcResult.getResponse().getContentAsString(), Answer.class);
        assertEquals(answer1.getAnswer(), test1String);

        answer.setAnswer(test2String);
        mvcResult = performPut(uri + answer.getId(), mapToJson(answer), ADMIN);
        assertEquals(200, mvcResult.getResponse().getStatus());
        Answer answer2 = mapFromJson(mvcResult.getResponse().getContentAsString(), Answer.class);
        assertEquals(answer2.getAnswer(), test2String);
    }

    @Test
    public void editAnswerExpects401() throws Exception {
        String test1String = "TestingSomeModificationToAnswer";
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);
        prepareQuestionAndReturnId(true);

        Answer answer = answerRepository.findAll().get(0);
        answer.setAnswer(test1String);

        MvcResult mvcResult = performPut(uri + answer.getId(), mapToJson(answer), UNLOGGED);
        assertEquals(401, mvcResult.getResponse().getStatus());
    }

    @Test
    public void editAnswerExpects404() throws Exception {
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);

        Answer answer = Answer.builder().answer("test").id(123L).build();

        MvcResult mvcResult = performPut(uri + answer.getId(), mapToJson(answer), USER);
        assertEquals(404, mvcResult.getResponse().getStatus());

        answer.setAnswer("test2");
        mvcResult = performPut(uri + answer.getId(), mapToJson(answer), ADMIN);
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void editAnswerExpects400() throws Exception {
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);

        prepareQuestionAndReturnId(true);

        Answer answer = answerRepository.findAll().get(0);

        answer.setAnswer("");
        MvcResult mvcResult = performPut(uri + answer.getId(), mapToJson(answer), USER);

        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult = performPut(uri + answer.getId(), mapToJson(answer), ADMIN);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteAnswerExpects200() throws Exception {
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);

        prepareQuestionAndReturnId(true);
        prepareQuestionAndReturnId(true);

        Long answer1Id = answerRepository.findAll().get(0).getId();
        Long answer2Id = answerRepository.findAll().get(1).getId();

        MvcResult mvcResult = performDelete(uri + answer1Id, USER);
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = performDelete(uri + answer2Id, ADMIN);
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteAnswerExpects401() throws Exception {
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);
        prepareQuestionAndReturnId(true);

        Long answerId = answerRepository.findAll().get(0).getId();

        MvcResult mvcResult = performDelete(uri + answerId, UNLOGGED);
        assertEquals(401, mvcResult.getResponse().getStatus());
    }

//    //TODO implement Edit only by owner/admin first
////    @Test
////    public void deleteAnswerExpects403() throws Exception {
////        assertEquals(403, deleteAnswerStatus(11L, UserType.UNLOGGED, ""));
////    }
//
    @Test
    public void deleteAnswerExpects404() throws Exception {
        assertEquals(questionRepository.count(), 0);
        assertEquals(answerRepository.count(), 0);

        MvcResult mvcResult = performDelete(uri + 123, USER);
        assertEquals(404, mvcResult.getResponse().getStatus());

        mvcResult = performDelete(uri + 123, ADMIN);
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

}