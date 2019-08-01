package com.sikoramarek.fiszki.controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AdminControllerTest.class,
        AnswerControllerTest.class,
        TagControllerTest.class,
        UsersControllerTest.class,
        QuestionControllerTest.class
})

public class ControllersTest {
}