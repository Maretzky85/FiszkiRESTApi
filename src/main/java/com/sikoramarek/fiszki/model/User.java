package com.sikoramarek.fiszki.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String name;

	String email;

	String password;

	@OneToMany(mappedBy = "user")
	List<Question> questionList;

}
