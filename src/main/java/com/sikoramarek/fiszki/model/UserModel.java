package com.sikoramarek.fiszki.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class UserModel{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Column(name = "name")
	String username;

	String email;

	@NotNull
	String password;

	@OneToMany(mappedBy = "user")
	@JsonBackReference
	List<Question> questionList;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "role")
	Role role;

}
