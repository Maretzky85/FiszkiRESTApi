package com.sikoramarek.fiszki.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String role;

}
