package com.sikoramarek.fiszki.model;

import com.fasterxml.jackson.annotation.*;
import com.sikoramarek.fiszki.service.audit.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = {"tags", "answers", "usersKnownThisQuestion"}, callSuper = false)
@Table(name = "questions")
@EntityListeners(AuditingEntityListener.class)
public class Question extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String title;

	@NotNull
	@Type(type = "org.hibernate.type.TextType")
	private String question;

	@OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "id")
	private Set<Answer> answers;

	@ToString.Exclude
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "questions_tag",
			joinColumns = @JoinColumn(name = "question_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags;

	@ManyToMany
	@JsonIgnore
	@JoinTable(
			name = "user_known_question",
			joinColumns = @JoinColumn(name = "question_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private Set<UserModel> usersKnownThisQuestion;

	private boolean accepted;
}
