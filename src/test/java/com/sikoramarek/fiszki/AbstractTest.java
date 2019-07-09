package com.sikoramarek.fiszki;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sikoramarek.fiszki.model.UserModel;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.io.IOException;

import static com.sikoramarek.fiszki.service.authentication.SecurityConstants.HEADER_STRING;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FiszkiApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
public abstract class AbstractTest {

	private String loginUri = "/login";
	private String userName = "admin2";
	private String adminName = "admin12";
	private String password = "123456";

	protected String USER_TOKEN;
	private String ADMIN_TOKEN;

	protected MockMvc mvc;
	@Autowired
	WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;

	@Before
	public void setUp() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain).build();
		USER_TOKEN = getToken(userName);
		ADMIN_TOKEN = getToken(adminName);
	}

	protected String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	protected <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	private String getToken(String userName) throws Exception {
		String jsonLogin = "{\"username\":\""+userName+"\", \"password\":\""+password+"\"}";
		MvcResult mvcResultLogin = mvc.perform(MockMvcRequestBuilders.post(loginUri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonLogin))
				.andExpect(status().isOk()).andReturn();
		String fullToken = mvcResultLogin.getResponse().getHeader(HEADER_STRING);
		return fullToken.replace(HEADER_STRING, "");
	}

	public MvcResult performGet(String uri, UserType userType) throws Exception {
		if (userType == UserType.USER){
			return mvc.perform(MockMvcRequestBuilders.get(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HEADER_STRING, USER_TOKEN))
					.andReturn();
		}
		if (userType == UserType.ADMIN){
			return mvc.perform(MockMvcRequestBuilders.get(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HEADER_STRING, ADMIN_TOKEN))
					.andReturn();
		}
		if (userType == UserType.UNLOGGED) {
			return mvc.perform(MockMvcRequestBuilders.get(uri)
					.contentType(MediaType.APPLICATION_JSON))
					.andReturn();
		}
		throw new RuntimeException("Unknown user type");

	}

	public MvcResult performPost(String uri, String jsonContent, UserType userType) throws Exception {
		if (userType == UserType.USER){
			return  mvc.perform(MockMvcRequestBuilders.post(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HEADER_STRING, USER_TOKEN)
					.content(jsonContent))
					.andReturn();
		}
		if (userType == UserType.ADMIN){
			return mvc.perform(MockMvcRequestBuilders.post(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HEADER_STRING, ADMIN_TOKEN)
					.content(jsonContent))
					.andReturn();
		}
		if (userType == UserType.UNLOGGED){
			return mvc.perform(MockMvcRequestBuilders.post(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonContent))
					.andReturn();
		}
		throw new RuntimeException("Unknown user type");
	}

	public MvcResult performPut(String uri, String jsonContent, UserType userType) throws Exception {
		if (userType == UserType.USER) {
			return  mvc.perform(MockMvcRequestBuilders.put(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HEADER_STRING, USER_TOKEN)
					.content(jsonContent)).andReturn();
		}
		if (userType == UserType.ADMIN) {
			return  mvc.perform(MockMvcRequestBuilders.put(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HEADER_STRING, ADMIN_TOKEN)
					.content(jsonContent)).andReturn();
		}
		if (userType == UserType.UNLOGGED) {
			return  mvc.perform(MockMvcRequestBuilders.put(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonContent)).andReturn();
		}
		throw new RuntimeException("Unknown user type");
	}

	public MvcResult performDelete(String uri, UserType userType) throws Exception {
		if (userType == UserType.USER){
			return mvc.perform(MockMvcRequestBuilders.delete(uri)
					.header(HEADER_STRING, USER_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)).andReturn();
		}
		if (userType == UserType.ADMIN){
			return mvc.perform(MockMvcRequestBuilders.delete(uri)
					.header(HEADER_STRING, ADMIN_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)).andReturn();
		}
		if (userType == UserType.UNLOGGED){
			return mvc.perform(MockMvcRequestBuilders.delete(uri)
					.contentType(MediaType.APPLICATION_JSON)).andReturn();
		}
		throw new RuntimeException("Unknown user type");
	}
}

