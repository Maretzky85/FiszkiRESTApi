package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.AbstractTest;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.model.UserModel;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;

import static com.sikoramarek.fiszki.service.authentication.SecurityConstants.HEADER_STRING;
import static com.sikoramarek.fiszki.service.authentication.SecurityConstants.TOKEN_PREFIX;
import static org.junit.Assert.*;

public class TagControllerTest extends AbstractTest {

	String loginUri = "/login";
	String userName = "admin2";
	String password = "123456";

	@Test
	public void getAllTags() throws Exception{
		String uri = "/tags";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		String content = mvcResult.getResponse().getContentAsString();
		Tag[] tags = super.mapFromJson(content, Tag[].class);
		assertEquals(6, tags.length);
	}

	@Test
	public void getTagByIdExisting() throws Exception {
		String uri = "/tags/1";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
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
	public void getTagByIdNonExisting() throws Exception{
		String uri = "/tags/2";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String result = mvcResult.getResponse().getContentAsString();
		assertEquals(404, status);
		assertEquals("", result);
	}

	@Test
	public void getQuestionsByTagIdExisting() throws Exception {
		String uri = "/tags/1/questions";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		Question[] questions = super.mapFromJson(content, Question[].class);
		assertEquals(200, status);
		assertEquals(3, questions.length );

	}

	@Test
	public void getQuestionsByTagIdNonExisting() throws Exception {
		String uri = "/tags/2/questions";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(404, status);
		assertEquals("", content);
	}

	@Test
	public void newTagValidAndLogged() throws Exception {
		UserModel user = new UserModel();
		user.setUsername(userName);
		user.setPassword(password);
		String jsonLogin = "{\"username\":\""+userName+"\", \"password\":\""+password+"\"}";
		MvcResult mvcResultLogin = mvc.perform(MockMvcRequestBuilders.post(loginUri)
				.contentType(MediaType.APPLICATION_JSON).content(jsonLogin)).andReturn();
		String fullToken = mvcResultLogin.getResponse().getHeader(HEADER_STRING);
		System.out.println("======================================================="+fullToken+"  "+
				mvcResultLogin.getResponse().getStatus() + "  "+jsonLogin);
		String uri = "/tags";
		Tag tag = new Tag();
		tag.setTagName("SomeNewTagName");
		String inputJson = super.mapToJson(tag);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON).param(HEADER_STRING, fullToken).content(inputJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);
		String content = mvcResult.getResponse().getContentAsString();
		Tag tagResponse = super.mapFromJson(content, Tag.class);
		assertNotNull(tagResponse.getId());
		assertEquals("SomeNewTagName", tagResponse.getTagName());
	}

	@Test
	public void newTagValidAndNotLogged() throws Exception {
		String uri = "/tags";
		Tag tag = new Tag();
		tag.setTagName("Blablabla");
		String inputJson = super.mapToJson(tag);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON).content(inputJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(403, status);
		String content = mvcResult.getResponse().getContentAsString();
		Tag tagResponse = super.mapFromJson(content, Tag.class);
//		assertNotNull(tagResponse.getId());
//		assertEquals("SomeNewTagName", tagResponse.getTagName());
	}

	@Test
	public void editTag() throws Exception{
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/users")
				.contentType(MediaType.APPLICATION_JSON)).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
		assertTrue(mvcResult.getResponse().getContentAsString().length() > 1);
	}

	@Test
	public void deleteTag() {
	}

	@Test
	public void getRandom() {
	}
}