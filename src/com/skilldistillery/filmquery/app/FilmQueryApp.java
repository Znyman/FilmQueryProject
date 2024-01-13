package com.skilldistillery.filmquery.app;

import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
//		app.test();
		app.launch();
	}

	private void test() {
		Film film = db.findFilmById(1);
		displayFilmData(film);
	}

	private void launch() {
		Scanner keyboard = new Scanner(System.in);

		startUserInterface(keyboard);

		keyboard.close();
	}

	private void startUserInterface(Scanner keyboard) {
		int userChoice;
		Film film = null;
		do {
			System.out.println("Would you like to:");
			System.out.println("'1' Look up a film by its ID");
			System.out.println("'2' Look up a film by a keyword");
			System.out.println("'3' Exit the application");
			userChoice = keyboard.nextInt();
			keyboard.nextLine();
			
			switch (userChoice) {
			case 1:
				System.out.println("What is the film ID you are looking for? Please enter a whole number.");
				userChoice = keyboard.nextInt();
				film = db.findFilmById(userChoice);
				displayFilmData(film);
				break;
			case 2:
				System.out.println("What is the keyword you would like to search films for?");
				String keyword = keyboard.nextLine();
				film = db.findFilmByKeyword(keyword);
				displayFilmData(film);
				break;
			case 3:
				System.out.println("Goodbye.");
				return;
			default:
				System.out.println("Hmm, I don't recognize that option, please try again.");
				break;
			}
		} while (userChoice != 1 || userChoice != 2);
	}
	
	public void displayFilmData(Film film) {
		if (film == null) {
			System.out.println("I'm sorry, we couldn't find a film with that information.");
		} else {
			System.out.println("We have found your film, and here is its information:");
			System.out.println("Title: " + film.getTitle());
			System.out.println("Year released: " + film.getReleaseYear());
			System.out.println("Rating: " + film.getRating());
			System.out.println("Description: " + film.getDescription());
			System.out.println("Language: " + film.getLanguage());
			System.out.println();
			System.out.println("Starring:");
			for (Actor actor : film.getActors()) {
				System.out.println(actor.getFirstName() + " " + actor.getLastName());
			}
			
			System.out.println();
		}
	}

}
