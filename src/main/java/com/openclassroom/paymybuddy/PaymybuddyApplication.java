package com.openclassroom.paymybuddy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application Pay My Buddy.
 *
 * <p>Cette classe configure et lance l'application Spring Boot.</p>
 */
@SpringBootApplication
@Slf4j
public class PaymybuddyApplication {


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

}
