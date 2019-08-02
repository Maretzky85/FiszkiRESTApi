package com.sikoramarek.fiszki.controller;


import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Question;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;


public class SearchControllerTest extends AbstractTest {

    String uri = "/search";

    @Before
    public void before() {
        questionRepository.deleteAll();
        answerRepository.deleteAll();
    }

    @Test
    public void search() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        for (int i = 0; i < 10; i++) {
            prepareQuestionAndReturnId(true, true);
        }

        MvcResult mvcResult = performGet(uri + "?search=Test", UserType.USER);
        assertEquals(200, mvcResult.getResponse().getStatus());
        Question[] questions = mapFromJson(mvcResult.getResponse().getContentAsString(), Question[].class);
        assertEquals(10, questions.length);
    }

    @Test
    public void searchNull() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        for (int i = 0; i < 10; i++) {
            prepareQuestionAndReturnId(true, true);
        }

        MvcResult mvcResult = performGet(uri + "?search=", UserType.USER);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    public void searchOne() throws Exception {
        assertEquals(0, questionRepository.count());
        assertEquals(0, answerRepository.count());
        for (int i = 0; i < 10; i++) {
            prepareQuestionAndReturnId(true, true);
        }

        MvcResult mvcResult = performGet(uri + "?search=a", UserType.USER);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }
}
