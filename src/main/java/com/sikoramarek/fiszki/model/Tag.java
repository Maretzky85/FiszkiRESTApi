package com.sikoramarek.fiszki.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = "questions")
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "tag")
	String tagName;

	@ToString.Exclude
	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	@JsonBackReference
	Set<Question> questions = new HashSet<>();
}
