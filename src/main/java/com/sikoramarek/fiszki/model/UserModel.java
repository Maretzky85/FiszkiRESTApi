package com.sikoramarek.fiszki.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class UserModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Column(name = "name")
	String username;

	@NotNull
	String email;

	@NotNull
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	String password;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	Set<Role> roles;

}
