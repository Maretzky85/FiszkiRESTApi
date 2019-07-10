INSERT INTO public.users (id, name, email, password) VALUES (22, 'newuser', '21231231231231', '$2a$10$TT.EWwIS4c8u0Xf5okGp.eVoUvDG7aaITLVnQUpsFTAAYhg1nryaS');
INSERT INTO public.users (id, name, email, password) VALUES (30, 'admin3', '123123', '$2a$10$rwGn70ilpgpZbrXqy3EuKOAYggeW4wPhY90sbfFsBObAD5E4/bmIC');
INSERT INTO public.users (id, name, email, password) VALUES (33, 'admin4', '123123@mail.com', '$2a$10$QWOWat0AMAcbfyRlnp7nz.r./L17E/gIumJtYdp7c4oN7ZEYYMPJe');
INSERT INTO public.users (id, name, email, password) VALUES (34, 'blablabla', 'costam@costam.com', '$2a$10$..anm6ukgpXRLoFX99WM3eWFYWdFbvMlHS0srjid8EIWdX0BK4p9W');
INSERT INTO public.users (id, name, email, password) VALUES (35, 'srutututu', 'dupa@lala.com', '$2a$10$55Unjw9dV95FOK2XCUKVf.mP71m/I5ajPalrA8eKlyWkN2..wb4cG');
INSERT INTO public.users (id, name, email, password) VALUES (1, 'Maro', 'Maretzky85@gmail.com', '1234');
INSERT INTO public.users (id, name, email, password) VALUES (21, 'admin2', '123123123', '$2a$10$oaqXLOtSFcbi7jfpTGbU0eZlpg8Sedor0pEz.0AdYEtgszdnKcYDO');
INSERT INTO public.users (id, name, email, password) VALUES (37, 'admin9', 'blabla@blabla3.com', '$2a$10$euJ37apfqEj4RrTtf2I6DuMn2LF8A1NJPlprgZYxTmQVhkaJTPcS2');
INSERT INTO public.users (id, name, email, password) VALUES (38, 'admin12', 'blabla@blabla4.com', '$2a$10$yvjOUqgobeS3oc15rEJ05.KEhw7VYRdUEz.cTKTOqex1eSjK8RxTy');

INSERT INTO public.roles (id, role) VALUES (1, 'ADMIN');
INSERT INTO public.roles (id, role) VALUES (2, 'USER');

INSERT INTO public.users_roles (user_id, role_id) VALUES (22, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (30, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (33, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (34, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (35, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (21, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (37, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (38, 2);
INSERT INTO public.users_roles (user_id, role_id) VALUES (38, 1);

INSERT INTO public.tag (id, tag) VALUES (1, 'Java');
INSERT INTO public.tag (id, tag) VALUES (3, 'Python');
INSERT INTO public.tag (id, tag) VALUES (10, 'OOP');
INSERT INTO public.tag (id, tag) VALUES (12, 'OOP');
INSERT INTO public.tag (id, tag) VALUES (13, 'bla bla');

INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (123, 'What is OOP?', 'What is OOP?', 38, false);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (124, 'What is OOP?', 'What is OOP?', 38, false);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (125, 'What is OOP?', 'What is OOP?', 38, false);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (90, 'Inheritance.', 'Inheritance. Explain the concept with realistic example.', 21, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (114, 'Inheritance.', 'Inheritance. Explain the concept with realistic example.', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (115, 'Inheritance.', 'Inheritance. Explain the concept with realistic example.', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (117, 'What is OOP?', 'What is OOP?', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (120, 'What is OOP?', 'What is OOP?', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (118, 'What is OOP?', 'What is OOP?', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (121, 'What is OOP?', 'What is OOP?', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (122, 'What is OOP?', 'What is OOP?', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (131, 'bla bla bla', 'asdasdasd', 38, false);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (83, 'Inheritance.', 'Inheritance. Explain the concept with realistic example.', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (82, 'What is OOP?', 'What is OOP? What?', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (109, 'Inheritance.', 'Inheritance. Explain the concept with realistic example.', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (87, 'Helllo!! :D', 'Hi!', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (88, 'new Q', 'Q', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (112, 'Inheritance.', 'Inheritance. Explain the concept with realistic example.', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (89, 'WTF', 'aaaaaaaaaaaaa', 38, true);
INSERT INTO public.questions (id, title, question, user_id, accepted) VALUES (113, 'Inheritance.', 'Inheritance. Explain the concept with realistic example.', 38, true);

INSERT INTO public.questions_tag (question_id, tag_id) VALUES (82, 10);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (83, 10);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (87, 1);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (87, 3);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (87, 10);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (88, 1);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (89, 1);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (120, 12);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (121, 10);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (122, 10);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (123, 10);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (124, 10);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (125, 10);
INSERT INTO public.questions_tag (question_id, tag_id) VALUES (131, 12);

INSERT INTO public.user_known_question (user_id, question_id) VALUES (38, 120);
INSERT INTO public.user_known_question (user_id, question_id) VALUES (21, 83);
INSERT INTO public.user_known_question (user_id, question_id) VALUES (21, 88);
INSERT INTO public.user_known_question (user_id, question_id) VALUES (38, 83);
INSERT INTO public.user_known_question (user_id, question_id) VALUES (38, 109);
INSERT INTO public.user_known_question (user_id, question_id) VALUES (38, 89);


INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (67, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 82, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (68, 'Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another.
With the use of inheritance the information is made manageable in a hierarchical order.
The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).', 83, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (74, 'Hi ?', 87, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (76, '?', 82, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (77, 'Hello ?', 87, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (1, 'how much is the fish yo ??', 123, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (2, 'how much is the fish yo ??', 123, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (87, 'Inheritance. Explain the concept with realistic example.', 115, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (88, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 117, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (89, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 118, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (90, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 120, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (101, 'asdasdsadas', 122, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (103, 'asdasdasdas', 83, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (104, 'asdasda', 83, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (91, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 121, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (92, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 122, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (93, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 123, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (94, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 124, 38);
INSERT INTO public.answers (id, answer, question_id, user_id) VALUES (95, 'Object Oriented Programming (OOP) is a programming paradigm that is based on the concept of “objects”. An object is a data structure that contains data (fields) and functions (methods).
The four major principles of OOP are:
         Inheritance
Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order. The class which inherits the properties of other is known as subclass (derived class, child class) and the class whose properties are inherited is known as superclass (base class, parent class).
Polymorphism
Polymorphism is the ability of an object to take on many forms. The most common use of polymorphism in OOP occurs when a parent class reference is used to refer to a child class object.
Abstraction
As per dictionary, abstraction is the quality of dealing with ideas rather than events. For example, when you consider the case of e-mail, complex details such as what happens as soon as you send an e-mail, the protocol your e-mail server uses are hidden from the user. Therefore, to send an e-mail you just need to type the content, mention the address of the receiver, and click send.
Likewise in Object-oriented programming, abstraction is a process of hiding the implementation details from the user, only the functionality will be provided to the user. In other words, the user will have the information on what the object does instead of how it does it.
In Java, abstraction is achieved using Abstract classes and interfaces.
Encapsulation
In Java is a mechanism of wrapping the data (variables) and code acting on the data (methods) together as a single unit. In encapsulation, the variables of a class will be hidden from other classes, and can be accessed only through the methods of their current class. Therefore, it is also known as data hiding.
', 125, 38);
