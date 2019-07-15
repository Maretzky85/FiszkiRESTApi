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

import java.util.*;

import static com.sikoramarek.fiszki.UserType.UNLOGGED;
import static com.sikoramarek.fiszki.UserType.USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TagControllerTest extends AbstractTest {

	@Autowired
	TagRepository tagRepository;
	@Autowired
	QuestionRepository questionRepository;
	private String uri = "/tags";

	@Before
	public void before() {
		tagRepository.deleteAll();
		Tag[] tags = new Tag[3];
		for (int i = 0; i < tags.length; i++) {
			Tag tag = new Tag();
			tag.setId((long) i);
			tag.setTagName("test" + i);
			tagRepository.save(tag);
		}
	}


	@Test
	public void getAllTagsExpectedList() throws Exception {
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
		Long tagId = tagRepository.findAll().get(0).getId();

		MvcResult mvcResult = performGet(uri + "/"+tagId, UNLOGGED);

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
		MvcResult mvcResult = performGet(uri + "/100", UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		String result = mvcResult.getResponse().getContentAsString();
		assertEquals("", result);
	}


	@Test
	public void getQuestionsByExistingTagIdExpectsListOfQuestions() throws Exception {
		Tag tag = tagRepository.findAll().get(0);
		Question question = new Question();
		question.setAccepted(true);
		question.setTitle("testQuestion");
		question.setQuestion("TestQuestion");
		question.setTags(Collections.singleton(tag));
		performPost("/questions", mapToJson(question), USER);

		MvcResult mvcResult = performGet(uri + "/"+tag.getId()+"/questions", UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);


		String content = mvcResult.getResponse().getContentAsString();
		Long questionsCount = questionRepository.countQuestionByTagsContainingAndAcceptedTrue(
				tagRepository.getOne(tag.getId())
		);

		Question[] questions = super.mapFromJson(content, Question[].class);
		assertEquals(questionsCount, Long.valueOf(questions.length));

	}

	@Test
	public void getQuestionsByTagIdNonExistingReturns404() throws Exception {
		MvcResult mvcResult = performGet(uri + "/100/questions", UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		String content = mvcResult.getResponse().getContentAsString();
		assertEquals("", content);
	}

	@Test
	public void newValidTagAndLoggedReturns201WithTag() throws Exception {
		Tag tag = new Tag();
		tag.setTagName("SomeNewTagName");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPost(uri, requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);


		String content = mvcResult.getResponse().getContentAsString();
		Tag[] tagResponse = super.mapFromJson(content, Tag[].class);
		assertNotNull(tagResponse[0].getId());
		assertEquals(1, tagResponse.length);
		assertEquals("SomeNewTagName", tagResponse[0].getTagName());
	}

	@Test
	public void newTagValidAndNotLoggedReturns401() throws Exception {
		Tag tag = new Tag();
		tag.setTagName("JustSomeName");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPost(uri, requestJson, UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(401, status);


		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void newNullNameTagAndLoggedReturns400() throws Exception {
		Tag tag = new Tag();
		tag.setTagName("");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPost(uri, requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void newExistingNameTagAndLoggedReturns409() throws Exception {
		Tag tag = new Tag();
		tag.setTagName("test1");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPost(uri, requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(409, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void editTagLoggedValidTagReturns200() throws Exception {
		Tag tag = tagRepository.findAll().get(0);
		tag.setTagName("JavaSecond");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/" + tag.getId(), requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);


		String responseString = mvcResult.getResponse().getContentAsString();
		Tag[] tagResponse = mapFromJson(responseString, Tag[].class);
		assertEquals(1, tagResponse.length);
		assertEquals("JavaSecond", tagResponse[0].getTagName());
		assertEquals(tag.getId(), tagResponse[0].getId());

	}


	@Test
	public void editTagNotLoggedValidTagReturns401() throws Exception {
		Tag tag = new Tag();
		tag.setId(3L);
		tag.setTagName("JavaSecond");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/3", requestJson, UNLOGGED);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(401, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void editNonExistingTagLoggedReturns404() throws Exception {
		Tag tag = new Tag();
		tag.setId(2L);
		tag.setTagName("JavaSecond");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/100", requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void editTagToNotValidLoggedReturns400() throws Exception {
		Tag tag = new Tag();
		tag.setId(3L);
		tag.setTagName("");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/3", requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void editTagToNameExistingLoggedReturns409() throws Exception {
		Tag tag = new Tag();
		tag.setId(2L);
		tag.setTagName("test1");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = performPut(uri + "/2", requestJson, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(409, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void deleteTagNotLoggedReturns401() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri + "/3")
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(401, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void deleteTagLoggedReturns200() throws Exception {
		List<Tag> tags = tagRepository.findAll();
		Long tagId = tags.get(0).getId();

		MvcResult mvcResult = performDelete(uri + "/" + tagId, USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void deleteNonExistentTagLoggedReturns404() throws Exception {
		MvcResult mvcResult = performDelete(uri + "/100", USER);

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		String responseString = mvcResult.getResponse().getContentAsString();
		assertEquals("", responseString);
	}

	@Test
	public void getRandomNotLoggedTagExistsReturn200() throws Exception {
		Tag tag = tagRepository.findAll().get(0);

		for (int i = 0; i < 3; i++) {
			Question question = new Question();
			question.setAccepted(true);
			question.setQuestion("test"+i);
			question.setTitle("test"+i);
			question.setTags(Collections.singleton(tag));
			String jsonPost = mapToJson(question);
			performPost("/questions", jsonPost, USER);
		}

		MvcResult mvcResult = performGet(uri + "/"+tag.getId()+"/questions/random", UNLOGGED);

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
		Long tagId = tagRepository.findAll().get(0).getId();

		MvcResult mvcResult = performGet(uri + "/"+tagId+"/questions/random", UNLOGGED);


		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);


		String response = mvcResult.getResponse().getContentAsString();
		Question[] question = super.mapFromJson(response, Question[].class);
		assertEquals(0, question.length);
	}
}