package com.sikoramarek.fiszki.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class UserModel implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	String name;

	String email;

	@NotNull
	@JsonIgnore
	String password;

	@OneToMany(mappedBy = "user")
	@JsonBackReference
	List<Question> questionList;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "role", nullable = false)
	Roles role;

	@Transient
	List<SimpleGrantedAuthority> authorities = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		if (authorities.isEmpty()){
//			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.role.toUpperCase()));
//			System.out.println(authorities);
//		}
		return authorities;
	}

	@Override
	public String getUsername() {
		return name;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}
}
