package com.sikoramarek.fiszki.model.projections;

import lombok.Value;

@Value
public class QuestionOnly {

	private Long id;

	private String title, question, user;

	private boolean accepted;

}