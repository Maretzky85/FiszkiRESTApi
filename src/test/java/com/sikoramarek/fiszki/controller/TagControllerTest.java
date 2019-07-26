package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static com.sikoramarek.fiszki.UserType.UNLOGGED;
import static com.sikoramarek.fiszki.UserType.USER;
import static org.junit.Assert.*;

public class TagControllerTest extends AbstractTest {

	@Autowired
	TagRepository tagRepository;
	@Autowired
	QuestionRepository questionRepository;
	private String uri = "/tags";

	@Before
	public void before() {
		tagRepository.deleteAll();
	}

	@Test
	public void getAllTagsExpectedList() throws Exception {
		assertEquals(tagRepository.findAll().size(), 0);
		for (int i = 0; i < 2; i++) {
			tagRepository.save(Tag.builder().tagName("test" + i).build());
		}
		MvcResult mvcResult = performGet(uri, UNLOGGED);

		int tagCount = (int) tagRepository.count();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		String content = mvcResult.getResponse().getContentAsString();
		Tag[] tags = super.mapFromJson(content, Tag[].class);
		assertEquals(tagCount, tags.length);
	}


	@Test
	public void getTagByExistingIdExpectedSingletonList() throws Exception {
		assertEquals(tagRepository.findAll().size(), 0);
		for (int i = 0; i < 5; i++) {
			tagRepository.save(Tag.builder().tagName("test" + i).build());
		}
		Long tagId = tagRepository.findAll().get(0).getId();

		MvcResult mvcResult = performGet(uri + "/" + tagId, UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		String content = mvcResult.getResponse().getContentAsString();
		Tag[] tag = super.mapFromJson(content, Tag[].class);

		assertEquals(1, tag.length);
		assertNotNull(tag[0].getTagName());
		assertEquals(tagId, tag[0].getId());
	}


	@Test
	public void getTagByNonExistingIdExpects404() throws Exception {
		assertEquals(tagRepository.findAll().size(), 0);
		MvcResult mvcResult = performGet(uri + "/100", UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		String result = mvcResult.getResponse().getContentAsString();
		assertEquals("", result);
	}


	@Test
	public void getQuestionsByExistingTagIdExpectsListOfQuestions() throws Exception {
		assertEquals(tagRepository.findAll().size(), 0);

		tagRepository.save(Tag.builder().tagName("test").build());
		Tag tag = tagRepository.findAll().get(0);
		Question question = Question.builder()
				.accepted(true)
				.title("testTitle")
				.question("testQuestion")
				.tags(Collections.singleton(tag))
				.build();
		performPost("/questions", mapToJson(question), USER);

		MvcResult mvcResult = performGet(uri + "/" + tag.getId() + "/questions", UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		String content = mvcResult.getResponse().getContentAsString();
		int questionsCount = questionRepository.countQuestionByTagsContainingAndAcceptedTrue(
				tagRepository.getOne(tag.getId())
		);

		Question[] questions = super.mapFromJson(content, Question[].class);
		assertEquals(questionsCount, questions.length);

	}

	@Test
	public void getQuestionsByTagIdNonExistingReturns404() throws Exception {
		MvcResult mvcResult = performGet(uri + "/100/questions", UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);
		assertTrue(mvcResult.getResponse().getErrorMessage().contains("not found"));

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void newValidTagAndLoggedReturns201WithTag() throws Exception {
		assertEquals(tagRepository.count(), 0);
		String TAG_NAME = "SomeNewTagName";
		Tag tag = Tag.builder().tagName(TAG_NAME).build();
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPost(uri, requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);

		String content = mvcResult.getResponse().getContentAsString();
		Tag[] tagResponse = super.mapFromJson(content, Tag[].class);
		assertNotNull(tagResponse[0].getId());
		assertEquals(1, tagResponse.length);
		assertEquals(TAG_NAME, tagResponse[0].getTagName());
	}

	@Test
	public void newTagValidAndNotLoggedReturns401() throws Exception {
		assertEquals(tagRepository.count(), 0);
		Tag tag = Tag.builder().tagName("JustSomeName").build();
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPost(uri, requestJson, UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(401, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void newNullNameTagAndLoggedReturns400() throws Exception {
		Tag tag = Tag.builder().tagName("").build();
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPost(uri, requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void newExistingNameTagAndLoggedReturns409() throws Exception {
		assertEquals(tagRepository.count(), 0);
		tagRepository.save(Tag.builder().tagName("test").build());
		Tag tag = Tag.builder().tagName("test").build();
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPost(uri, requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(409, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void editTagLoggedValidTagReturns200() throws Exception {
		assertEquals(tagRepository.count(), 0);
		tagRepository.save(Tag.builder().tagName("Java").build());
		Tag tag = tagRepository.findAll().get(0);
		tag.setTagName("JavaSecond");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/" + tag.getId(), requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);


		String responseString = mvcResult.getResponse().getContentAsString();
		Tag[] mappedResponse = mapFromJson(responseString, Tag[].class);
		assertEquals(1, mappedResponse.length);
		assertEquals("JavaSecond", mappedResponse[0].getTagName());
		assertEquals(tag.getId(), mappedResponse[0].getId());

	}


	@Test
	public void editTagNotLoggedValidTagReturns401() throws Exception {
		assertEquals(tagRepository.count(), 0);
		tagRepository.save(Tag.builder().tagName("Java").build());
		Tag tag = tagRepository.findTagByTagNameEquals("Java").get();
		tag.setTagName("JavaSecond");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + tag.getId(), requestJson, UNLOGGED);

		int status = mvcResult.getResponse().getStatus();

		assertEquals(401, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void editNonExistingTagLoggedReturns404() throws Exception {
		assertEquals(tagRepository.count(), 0);
		Tag tag = Tag.builder().tagName("Java").id(2L).build();
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/2", requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void editTagToNotValidLoggedReturns400() throws Exception {
		assertEquals(tagRepository.count(), 0);
		tagRepository.save(Tag.builder().tagName("java").build());
		Tag tag = tagRepository.findTagByTagNameEquals("java").get();
		tag.setTagName("");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/" + tag.getId(), requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void editTagToNameExistingLoggedReturns409() throws Exception {
		assertEquals(tagRepository.count(), 0);
		tagRepository.save(Tag.builder().tagName("java").build());
		tagRepository.save(Tag.builder().tagName("java2").build());
		Long idSecondTag = tagRepository.findTagByTagNameEquals("java2").get().getId();
		Tag tag = Tag.builder().tagName("java").build();
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/" +  idSecondTag, requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(409, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void deleteTagNotLoggedReturns401() throws Exception {
		assertEquals(tagRepository.count(), 0);
		tagRepository.save(Tag.builder().tagName("test").build());
		Long tagId = tagRepository.findTagByTagNameEquals("test").get().getId();

		MvcResult mvcResult = performDelete(uri + tagId, UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(401, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void deleteTagLoggedReturns200() throws Exception {
		assertEquals(tagRepository.count(), 0);
		tagRepository.save(Tag.builder().tagName("test").build());

		Long tagId  = tagRepository.findAll().get(0).getId();


		MvcResult mvcResult = performDelete(uri + "/" + tagId, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void deleteNonExistentTagLoggedReturns404() throws Exception {
		MvcResult mvcResult = performDelete(uri + "/100", USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		assertEquals(0, mvcResult.getResponse().getContentLength());
	}

	@Test
	public void getRandomNotLoggedTagExistsReturn200() throws Exception {
		int QUESTION_COUNT = 3;
		assertEquals(tagRepository.count(), 0);
		questionRepository.deleteAll();
		tagRepository.save(Tag.builder().tagName("test").build());
		Tag tag = tagRepository.findTagByTagNameEquals("test").get();

		for (int i = 0; i < QUESTION_COUNT; i++) {
			Question question = Question.builder()
					.accepted(true)
					.question("test" + i)
					.title("test" + i)
					.tags(Collections.singleton(tag))
					.build();
			String jsonPost = mapToJson(question);
			performPost("/questions", jsonPost, USER);
		}

		MvcResult mvcResult = performGet(uri + "/" + tag.getId() + "/questions/random", UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);


		String response = mvcResult.getResponse().getContentAsString();
		Question[] question = super.mapFromJson(response, Question[].class);

		assertNotNull(question[0].getId());
		assertNotNull(question[0].getTitle());
		assertNotNull(question[0].getQuestion());
		assertEquals(1, question.length);
	}

	@Test
	public void getRandomQuestionNotLoggedTagNotExistsReturn404() throws Exception {
		MvcResult mvcResult = performGet(uri + "/100/questions/random", UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		String response = mvcResult.getResponse().getContentAsString();
		assertEquals("", response);
	}

	@Test
	public void getRandomNotLoggedTagExistsNoQuestionsExistsReturn200() throws Exception {
		assertEquals(tagRepository.count(), 0);
		tagRepository.save(Tag.builder().tagName("test").build());

		Long tagId = tagRepository.findTagByTagNameEquals("test").get().getId();

		MvcResult mvcResult = performGet(uri + "/" + tagId + "/questions/random", UNLOGGED);


		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);


		String response = mvcResult.getResponse().getContentAsString();
		Question[] question = super.mapFromJson(response, Question[].class);
		assertEquals(0, question.length);
	}
}