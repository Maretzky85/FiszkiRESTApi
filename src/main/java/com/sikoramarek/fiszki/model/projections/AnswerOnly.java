package com.sikoramarek.fiszki.model.projections;

import lombok.Value;

@Value
public class AnswerOnly {

	private Long id, question_id;

	private String user, answer;

}
