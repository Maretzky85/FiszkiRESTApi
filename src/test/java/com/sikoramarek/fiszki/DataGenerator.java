package com.sikoramarek.fiszki;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataGenerator {
    private int rowCount = 10;

    public Set<Tag> generateTags() {
        Set<Tag> tags = new HashSet<>();
        for (int i = 1; i < rowCount; i++) {
            Tag tag = new Tag();
            tag.setTagName("Tag" + i);
            tags.add(tag);
        }
        return tags;
    }

    public List<Answer> generateAnswers(List<Question> questions) {
        List<Answer> answers = new ArrayList<>();
        questions.forEach(question -> {
                    Answer answer = new Answer();
                    answer.setAnswer("Answer " + Math.random());
                    answer.setQuestion(question);
                    answers.add(answer);
                }
        );
        return answers;
    }

    public List<Question> generateQuestions(Set<Tag> tags) {
        List<Question> questions = new ArrayList<>();
        for (int i = 1; i < rowCount; i++) {
            Question question = new Question();
            question.setAccepted(true);
            question.setQuestion("Question" + i);
            question.setTitle("Question title" + i);
            question.setTags(tags);
            questions.add(question);
        }
        return questions;
    }
}
