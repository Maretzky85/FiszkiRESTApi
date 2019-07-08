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
import static org.junit.Assert.*;

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
		assertEquals(1, tagResponse.length);
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
	public void editTagLoggedValidTagReturns200() throws Exception {
		Tag tag = new Tag();
		tag.setId(3L);
		tag.setTagName("JavaSecond");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri+"/3")
				.contentType(MediaType.APPLICATION_JSON).header(HEADER_STRING, getToken(UserType.USER)).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();
		Tag[] tagResponse = mapFromJson(responseString, Tag[].class);

		assertEquals(200, status);
		assertEquals(1, tagResponse.length);
		assertEquals("JavaSecond", tagResponse[0].getTagName());
		assertEquals(3L, (long) tagResponse[0].getId());

	}


	@Test
	public void editTagNotLoggedValidTagReturns401() throws Exception {
		Tag tag = new Tag();
		tag.setId(3L);
		tag.setTagName("JavaSecond");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri+"/3")
				.contentType(MediaType.APPLICATION_JSON).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(401, status);
		assertEquals("", responseString);
	}

	@Test
	public void editNonExistingTagLoggedReturns404() throws Exception {
		Tag tag = new Tag();
		tag.setId(2L);
		tag.setTagName("JavaSecond");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri+"/2")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_STRING, getToken(UserType.USER)).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(404, status);
		assertEquals("", responseString);
	}

	@Test
	public void editTagToNotValidLoggedReturns400() throws Exception {
		Tag tag = new Tag();
		tag.setId(3L);
		tag.setTagName("");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri+"/3")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_STRING, getToken(UserType.USER)).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(400, status);
		assertEquals("", responseString);
	}

	@Test
	public void editTagToNameExistingLoggedReturns409() throws Exception {
		Tag tag = new Tag();
		tag.setId(3L);
		tag.setTagName("Java");
		String requestJson = super.mapToJson(tag);

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri+"/3")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_STRING, getToken(UserType.USER)).content(requestJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(409, status);
		assertEquals("", responseString);
	}

	@Test
	public void deleteTagNotLoggedReturns401() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri+"/3")
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(401, status);
		assertEquals("", responseString);
	}

	@Test
	public void deleteTagLoggedReturns200() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri+"/12")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_STRING, getToken(UserType.USER))).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertEquals("", responseString);
	}

	@Test
	public void deleteNonExistentTagLoggedReturns404() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri+"/100")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_STRING, getToken(UserType.USER))).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String responseString = mvcResult.getResponse().getContentAsString();

		assertEquals(404, status);
		assertEquals("", responseString);
	}

	@Test
	public void getRandomNotLoggedTagExistsReturn200() throws Exception {
		MvcResult mvcResult =
				mvc.perform(MockMvcRequestBuilders.get(uri+"/1/questions/random")
						.accept(MediaType.APPLICATION_JSON))
						.andReturn();

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
	public void getRandomNotLoggedTagNotExistsReturn404() throws Exception {
		MvcResult mvcResult =
				mvc.perform(MockMvcRequestBuilders.get(uri+"/2/questions/random")
						.accept(MediaType.APPLICATION_JSON))
						.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);

		String response = mvcResult.getResponse().getContentAsString();
		assertEquals("", response);
	}

	@Test
	public void getRandomNotLoggedTagExistsNoQuestionsExistsReturn200() throws Exception {
		MvcResult mvcResult =
				mvc.perform(MockMvcRequestBuilders.get(uri+"/13/questions/random")
						.accept(MediaType.APPLICATION_JSON))
						.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String response = mvcResult.getResponse().getContentAsString();
		Question[] question = super.mapFromJson(response, Question[].class);

		assertEquals(0, question.length);
	}
}