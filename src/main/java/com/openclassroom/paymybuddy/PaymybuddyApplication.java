package com.openclassroom.paymybuddy;

import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

//Supprimer getUsers?
//v1 ajout
/**
 * Classe principale de l'application Pay My Buddy.
 *
 * <p>Cette classe configure et lance l'application Spring Boot.
 * Elle implémente {@link CommandLineRunner} pour exécuter des tâches
 * de démarrage.</p>
 */
@SpringBootApplication
@Slf4j
public class PaymybuddyApplication implements CommandLineRunner {

	@Autowired
	private IUserService userService;

	/**
	 * Point d'entrée principal de l'application.
	 *
	 * Lance le contexte Spring Boot et affiche un message de démarrage
	 * personnalisé dans les logs.
	 *
	 * @param args les arguments de ligne de commande passés à l'application
	 */
	public static void main(String[] args) {
		log.info("APPLICATION_STARTING - Lancement de l'application Pay My Buddy");
		SpringApplication.run(PaymybuddyApplication.class, args);
		log.info("APPLICATION_STARTED - L'application Pay My Buddy est démarrée");
	}

	/**
	 * Méthode exécutée après le démarrage de l'application.
	 *
	 * Affiche la liste des utilisateurs présents en base de données dans la console.
	 * Cette méthode est annotée {@code @Transactional} pour assurer l'accès aux données.
	 *
	 * @param args les arguments de ligne de commande passés à l'application
	 */	@Override
	@Transactional
	public void run(String... args) throws Exception {
		Iterable<User> users = userService.getUsers();
		users.forEach(user -> System.out.println(user.getUsername()));
	}
}
