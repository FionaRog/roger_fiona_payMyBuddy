package com.openclassroom.paymybuddy;

import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

//Ajout de la javadoc
//Supprimer getUsers et getUserswithfriends?
//Ajout de logger info, error, debug
//Gestion des erreurs côté front
//v1 ajout
//Gestion des exceptions côté front sur /transaction ? Ou modifier les controller pour affichage sur chaque page
@SpringBootApplication
public class PaymybuddyApplication implements CommandLineRunner {

	@Autowired
	private IUserService userService;

	public static void main(String[] args) {
		SpringApplication.run(PaymybuddyApplication.class, args);
	}


	@Override
	@Transactional
	public void run(String... args) throws Exception {
		Iterable<User> users = userService.getUsers();
		users.forEach(user -> System.out.println(user.getUsername()));
	}
}
