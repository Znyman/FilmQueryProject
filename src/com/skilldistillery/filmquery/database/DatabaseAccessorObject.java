package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {

	private static final String URL = "jdbc:mysql://localhost:3306/sdvid";
	private static final String USER = "student";
	private static final String PASS = "student";

	public DatabaseAccessorObject() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Film findFilmById(int filmId) {
		Film film = null;

		String sqlQuery = "SELECT * FROM film WHERE id = ?";
		PreparedStatement statement;
		try {
			Connection conn = DriverManager.getConnection(URL, USER, PASS);
			statement = conn.prepareStatement(sqlQuery);
			statement.setInt(1, filmId);
			ResultSet results = statement.executeQuery();
			film = constructFilm(results);
			results.close();
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return film;
	}
	
	@Override
	public List<Film> findFilmsByActorId(int actorId) {
		List<Film> films = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(URL, USER, PASS);
			String sqlQuery = "SELECT * FROM film JOIN film_actor ON film.id = film_actor.film_id "
					+ " WHERE film_actor.actor_id = ?";
			PreparedStatement statement = conn.prepareStatement(sqlQuery);
			statement.setInt(1, actorId);
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				Film film;
				// DO NOT USE constructFilm() BECAUSE IT WILL RESULT IN RECURSIVE CALLS
				film = new Film(results.getInt("id"), results.getString("title"),
						results.getString("description"), results.getShort("release_year"),
						results.getInt("language_id"), results.getInt("rental_duration"),
						results.getDouble("rental_rate"), results.getInt("length"),
						results.getDouble("replacement_cost"), results.getString("rating"),
						results.getString("special_features"));
				films.add(film);
			}
			results.close();
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}
	
	@Override
	public Film findFilmByKeyword(String keyword) {
		Film film = null;
		
		String sqlQuery = "SELECT * FROM film WHERE title LIKE ? OR description LIKE ?";
		PreparedStatement statement;
		try {
			Connection conn = DriverManager.getConnection(URL, USER, PASS);
			statement = conn.prepareStatement(sqlQuery);
			statement.setString(1, "%" + keyword + "%");
			statement.setString(2, "%" + keyword + "%");
			ResultSet results = statement.executeQuery();
			film = constructFilm(results);
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return film;
	}
	
	private Film constructFilm(ResultSet results) {
		Film film = null;
		try {
			if (results.next()) {
				film = new Film(results.getInt("id"), results.getString("title"),
						results.getString("description"), results.getShort("release_year"),
						results.getInt("language_id"), results.getInt("rental_duration"),
						results.getDouble("rental_rate"), results.getInt("length"),
						results.getDouble("replacement_cost"), results.getString("rating"),
						results.getString("special_features"), findActorsByFilmId(results.getInt("id")));
				film.setLanguage(findFilmLanguage(results.getInt("id")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return film;
	}
	
	private String findFilmLanguage(int filmID) {
		String language = null;
		//select language.name from language join film on film.language_id = language.id where film.id = 1;
		
		String sqlQuery = "SELECT language.name FROM language JOIN film ON film.language_id = language.id WHERE film.id = ?";
		PreparedStatement statement;
		try {
			Connection conn = DriverManager.getConnection(URL, USER, PASS);
			statement = conn.prepareStatement(sqlQuery);
			statement.setInt(1, filmID);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				language = results.getString("name");
			}
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return language;
	}

	@Override
	public List<Actor> findActorsByFilmId(int filmId) {
		List<Actor> actors = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(URL, USER, PASS);
			String sqlQuery = "SELECT * FROM actor JOIN film_actor ON actor.id = film_actor.actor_id "
					+ " WHERE film_actor.film_id = ?";
			PreparedStatement statement = conn.prepareStatement(sqlQuery);
			statement.setInt(1, filmId);
			ResultSet actorResult = statement.executeQuery();
			while (actorResult.next()) {
				Actor actor = new Actor();
				actor.setId(actorResult.getInt("id"));
				actor.setFirstName(actorResult.getString("first_name"));
				actor.setLastName(actorResult.getString("last_name"));
				actor.setFilms(findFilmsByActorId(actor.getId()));
				actors.add(actor);
			}
			actorResult.close();
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actors;
	}

	@Override
	public Actor findActorById(int actorId) {
		Actor actor = null;

		String sqlQuery = "SELECT * FROM actor WHERE id = ?";
		PreparedStatement statement;
		try {
			Connection conn = DriverManager.getConnection(URL, USER, PASS);
			statement = conn.prepareStatement(sqlQuery);
			statement.setInt(1, actorId);
			ResultSet actorResult = statement.executeQuery();
			if (actorResult.next()) {
				actor = new Actor();
				actor.setId(actorResult.getInt("id"));
				actor.setFirstName(actorResult.getString("first_name"));
				actor.setLastName(actorResult.getString("last_name"));
				actor.setFilms(findFilmsByActorId(actorId));
			}
			actorResult.close();
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return actor;
	}

}
