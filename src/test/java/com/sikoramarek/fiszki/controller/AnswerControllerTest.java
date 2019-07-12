package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;

public class AnswerControllerTest extends AbstractTest {

    private String uri = "/answers/";

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void editAnswerExpects200() throws Exception {
        assertEquals(200, editAnswerStatus(2L, UserType.USER, ""));
        assertEquals(200, editAnswerStatus(1L, UserType.ADMIN, ""));
    }

    @Test
    public void editAnswerExpects401() throws Exception {
        assertEquals(401, editAnswerStatus(1L, UserType.UNLOGGED, ""));
    }

    @Test
    public void editAnswerExpects404() throws Exception {
        assertEquals(404, editAnswerStatus(2L, UserType.USER, "14444"));
        assertEquals(404, editAnswerStatus(1L, UserType.ADMIN, "14444"));
    }

    @Test
    public void editAnswerExpects400() throws Exception {
        assertEquals(400, editAnswerStatus(2L, UserType.USER, ":user"));
        assertEquals(400, editAnswerStatus(1L, UserType.ADMIN, ":user"));
    }

    @Test
    public void deleteAnswerExpects200() throws Exception {
        assertEquals(200, deleteAnswerStatus(74L, UserType.USER, ""));
        assertEquals(200, deleteAnswerStatus(76L, UserType.ADMIN, ""));
    }

    @Test
    public void deleteAnswerExpects401() throws Exception {
        assertEquals(401, deleteAnswerStatus(77L, UserType.UNLOGGED, ""));
    }

    @Test
    public void deleteAnswerExpects403() throws Exception {
        assertEquals(403, deleteAnswerStatus(87L, UserType.USER, ""));
        assertEquals(403, deleteAnswerStatus(88L, UserType.USER, ""));
    }

    @Test
    public void deleteAnswerExpects404() throws Exception {
        assertEquals(404, deleteAnswerStatus(88L, UserType.USER, "2222222"));
        assertEquals(404, deleteAnswerStatus(88L, UserType.ADMIN, "2222222"));
    }

    private int editAnswerStatus(Long answerId, UserType userType, String postfix) throws Exception {
        Answer answer = answerRepository.findAnswerById(answerId);

        String beforeAnswerContents = answer.getAnswer();
        String newAnswerContents = "New answer";
        answer.setAnswer(newAnswerContents);
        String requestJson = mapToJson(answer);
        MvcResult mvcResult = performPut(uri + answerId + postfix, requestJson, userType);
        int status = mvcResult.getResponse().getStatus();

        answer = answerRepository.findAnswerById(answerId);
        if (status != 200) {
            assertEquals(beforeAnswerContents, answer.getAnswer());
        } else {
            assertEquals(newAnswerContents, answer.getAnswer());
        }

        return status;
    }

    private int deleteAnswerStatus(Long answerId, UserType userType, String postfix) throws Exception {
        MvcResult mvcResult = performDelete(uri + answerId + postfix, userType);
        return mvcResult.getResponse().getStatus();
    }
}