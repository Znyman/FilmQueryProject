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
			ResultSet filmResult = statement.executeQuery();
			if (filmResult.next()) {
				film = new Film(filmResult.getInt("id"), filmResult.getString("title"),
						filmResult.getString("description"), filmResult.getShort("release_year"),
						filmResult.getInt("language_id"), filmResult.getInt("rental_duration"),
						filmResult.getDouble("rental_rate"), filmResult.getInt("length"),
						filmResult.getDouble("replacement_cost"), filmResult.getString("rating"),
						filmResult.getString("special_features"), findActorsByFilmId(filmId));
			}
			filmResult.close();
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return film;
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
				int filmId = results.getInt("id");
				String title = results.getString("title");
				String desc = results.getString("description");
				short releaseYear = results.getShort("release_year");
				int langId = results.getInt("language_id");
				int rentDur = results.getInt("rental_duration");
				double rate = results.getDouble("rental_rate");
				int length = results.getInt("length");
				double repCost = results.getDouble("replacement_cost");
				String rating = results.getString("rating");
				String features = results.getString("special_features");
				Film film = new Film(filmId, title, desc, releaseYear, langId, rentDur, rate, length, repCost, rating,
						features);
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

}
