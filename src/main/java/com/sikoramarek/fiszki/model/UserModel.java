package com.sikoramarek.fiszki.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@Table(name = "users")
//@JsonIgnoreProperties({ "password", "role", "authorities" })
public class UserModel{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	String name;

	String email;

	@NotNull
	String password;

	@OneToMany(mappedBy = "user")
	@JsonBackReference
	List<Question> questionList;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "role")
	Roles role;

}
