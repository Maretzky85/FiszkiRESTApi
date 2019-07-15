package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.sikoramarek.fiszki.UserType.USER;
import static org.junit.Assert.assertEquals;

public class AnswerControllerTest extends AbstractTest {

    private String uri = "/answers/";
    private Long rowCount = 5L;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        prepareData();
    }

    private void prepareData() throws Exception {

        for (Long i = 1L; i < rowCount; i++) {
            Tag tag = new Tag();
            tag.setTagName("test" + i);
            tagRepository.save(tag);

            Question question = new Question();
            question.setAccepted(true);
            question.setQuestion("Question" + i);
            question.setTitle("Question title" + i);
            question.setTags(Collections.singleton(tag));
            String jsonPost = mapToJson(question);
            Long questionId = mapFromJson(performPost("/questions", jsonPost, USER).getResponse().getContentAsString(), Question.class).getId();

            Answer answer = new Answer();
            answer.setAnswer("Answer " + i);
            answer.setQuestion(question);
            jsonPost = mapToJson(answer);

            performPost("/questions/"+ questionId + "/answers", jsonPost, USER);
        }
    }

    @Test
    public void editAnswerExpects200() throws Exception {
        Long answer1Id = answerRepository.findAll().get(0).getId();
        Long answer2Id = answerRepository.findAll().get(1).getId();
        assertEquals(200, editAnswerStatus(answer1Id, UserType.USER, ""));
        assertEquals(200, editAnswerStatus(answer2Id, UserType.ADMIN, ""));
    }

    @Test
    public void editAnswerExpects401() throws Exception {
        Long answer1Id = answerRepository.findAll().get(0).getId();
        assertEquals(401, editAnswerStatus(answer1Id, UserType.UNLOGGED, ""));
    }

    @Test
    public void editAnswerExpects404() throws Exception {
        Long answer1Id = answerRepository.findAll().get(0).getId();
        Long answer2Id = answerRepository.findAll().get(1).getId();
        assertEquals(404, editAnswerStatus(answer1Id, UserType.USER, "14444"));
        assertEquals(404, editAnswerStatus(answer2Id, UserType.ADMIN, "14444"));
    }

    @Test
    public void editAnswerExpects400() throws Exception {
        assertEquals(400, editAnswerStatus(6L, UserType.USER, ":user"));
        assertEquals(400, editAnswerStatus(7L, UserType.ADMIN, ":user"));
    }

    @Test
    public void deleteAnswerExpects200() throws Exception {
        Long answer1Id = answerRepository.findAll().get(0).getId();
        Long answer2Id = answerRepository.findAll().get(1).getId();
        assertEquals(200, deleteAnswerStatus(answer1Id, UserType.USER, ""));
        assertEquals(200, deleteAnswerStatus(answer2Id, UserType.ADMIN, ""));
    }

    @Test
    public void deleteAnswerExpects401() throws Exception {
        assertEquals(401, deleteAnswerStatus(10L, UserType.UNLOGGED, ""));
    }

    //TODO implement Edit only by owner/admin first
//    @Test
//    public void deleteAnswerExpects403() throws Exception {
//        assertEquals(403, deleteAnswerStatus(11L, UserType.UNLOGGED, ""));
//    }

    @Test
    public void deleteAnswerExpects404() throws Exception {
        assertEquals(404, deleteAnswerStatus(13L, UserType.USER, "2222222"));
        assertEquals(404, deleteAnswerStatus(14L, UserType.ADMIN, "2222222"));
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