package com.movieweb.model;

import java.util.List;

public final class MovieDetails {
    private final Movies movie;
    private final List<Actors> actors;
    private final List<Directors> directors;
    private final List<Authors> authors;
    private final List<Genres> genres;
    private final List<Tags> tags;
    private final List<ShowtimeView> showtimes;
    private final boolean favourite;

    public MovieDetails(Movies movie, List<Actors> actors, List<Directors> directors,
            List<Authors> authors, List<Genres> genres, List<Tags> tags, List<ShowtimeView> showtimes,
            boolean favourite) {
        this.movie = movie;
        this.actors = List.copyOf(actors);
        this.directors = List.copyOf(directors);
        this.authors = List.copyOf(authors);
        this.genres = List.copyOf(genres);
        this.tags = List.copyOf(tags);
        this.showtimes = List.copyOf(showtimes);
        this.favourite = favourite;
    }

    public Movies getMovie() { return movie; }
    public List<Actors> getActors() { return actors; }
    public List<Directors> getDirectors() { return directors; }
    public List<Authors> getAuthors() { return authors; }
    public List<Genres> getGenres() { return genres; }
    public List<Tags> getTags() { return tags; }
    public List<ShowtimeView> getShowtimes() { return showtimes; }
    public boolean isFavourite() { return favourite; }
}
