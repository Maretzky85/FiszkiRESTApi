package com.sikoramarek.fiszki.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sikoramarek.fiszki.service.audit.Auditable;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "answers")
@EqualsAndHashCode(exclude = "question", callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Type(type = "org.hibernate.type.TextType")
	String answer;

	@NotNull
	@ToString.Exclude
	@JsonIgnore
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false)
	Question question;

}
