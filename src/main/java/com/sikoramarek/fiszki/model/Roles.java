package com.sikoramarek.fiszki.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Roles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String role;

}
