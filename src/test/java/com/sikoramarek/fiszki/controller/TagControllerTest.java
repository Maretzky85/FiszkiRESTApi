package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.UserType;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.sikoramarek.fiszki.service.authentication.SecurityConstants.HEADER_STRING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TagControllerTest extends AbstractTest {

	@Autowired
	TagRepository tagRepository;
	@Autowired
	QuestionRepository questionRepository;
	private String uri = "/tags";


	@Test
	public void getAllTagsReturnList() throws Exception {
		MvcResult mvcResult =
				mvc.perform(MockMvcRequestBuilders.get(uri)
						.accept(MediaType.APPLICATION_JSON))
						.andReturn();
		int tagCount = (int) tagRepository.count();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		String content = mvcResult.getResponse().getContentAsString();
		Tag[] tags = super.mapFromJson(content, Tag[].class);
		assertEquals(tagCount, tags.length);
	}


	@Test
	public void getTagByIdExistingReturnSingletonList() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/1")
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		String content = mvcResult.getResponse().getContentAsString();
		Tag[] tag = super.mapFromJson(content, Tag[].class);

		assertEquals(1, tag.length);
		assertEquals("Java", tag[0].getTagName());
		assertEquals(1L, (long) tag[0].getId());
	}


	@Test
	public void getTagByIdNonExistingReturns404() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/2")
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String result = mvcResult.getResponse().getContentAsString();

		assertEquals(404, status);
		assertEquals("", result);
	}


	@Test
	public void getQuestionsByTagIdExistingReturnsListOfQuestions() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/1/questions")
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		Long questionsCount = questionRepository.countQuestionByTagsContaining(
				tagRepository.getOne(1L)
		);

		Question[] questions = super.mapFromJson(content, Question[].class);
		assertEquals(200, status);
		assertEquals(questionsCount, Long.valueOf(questions.length));

	}

	@Test
	public void getQuestionsByTagIdNonExistingReturns404() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/2/questions")
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();

		assertEquals(404, status);
		assertEquals("", content);
	}

	@Test
	public void newValidTagAndLoggedReturns201WithTag() throws Exception {
		Tag tag = new Tag();
		tag.setTagName("SomeNewTagName");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON).header(HEADER_STRING, getToken(UserType.USER)).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		Tag[] tagResponse = super.mapFromJson(content, Tag[].class);

		assertEquals(201, status);
		assertNotNull(tagResponse[0].getId());
		assertEquals("SomeNewTagName", tagResponse[0].getTagName());
	}

	@Test
	public void newTagValidAndNotLoggedReturns401() throws Exception {
		Tag tag = new Tag();
		tag.setTagName("JustSomeName");
		String requestJson = super.mapToJson(tag);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(401, status);
		assertEquals("", responseString);
	}

	@Test
	public void newNullNameTagAndLoggedReturns400() throws Exception {
		Tag tag = new Tag();
		tag.setTagName("");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON).header(HEADER_STRING, getToken(UserType.USER)).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(400, status);
		assertEquals("", responseString);
	}

	@Test
	public void newExistingNameTagAndLoggedReturns409() throws Exception {
		Tag tag = new Tag();
		tag.setTagName("Java");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON).header(HEADER_STRING, getToken(UserType.USER)).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(409, status);
		assertEquals("", responseString);
	}

	@Test
	public void editTag() throws Exception {

	}

	@Test
	public void deleteTag() {
	}

	@Test
	public void getRandom() {
	}
}