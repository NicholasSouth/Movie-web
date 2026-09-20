package com.movieweb.model;

import java.util.List;

public final class MovieCatalog {
    private final List<Movies> movies, nowShowing, comingSoon, popular;
    private final List<Genres> genres;
    private final List<Tags> tags;
    private final List<Theaters> theaters;
    private final List<String> ageRatings;
    private final int total, page;

    public MovieCatalog(List<Movies> movies, List<Movies> nowShowing, List<Movies> comingSoon,
            List<Movies> popular, List<Genres> genres, List<Tags> tags, List<Theaters> theaters,
            List<String> ageRatings, int total, int page) {
        this.movies = List.copyOf(movies);
        this.nowShowing = List.copyOf(nowShowing);
        this.comingSoon = List.copyOf(comingSoon);
        this.popular = List.copyOf(popular);
        this.genres = List.copyOf(genres);
        this.tags = List.copyOf(tags);
        this.theaters = List.copyOf(theaters);
        this.ageRatings = List.copyOf(ageRatings);
        this.total = total;
        this.page = page;
    }

    public List<Movies> getMovies() { return movies; }
    public List<Movies> getNowShowing() { return nowShowing; }
    public List<Movies> getComingSoon() { return comingSoon; }
    public List<Movies> getPopular() { return popular; }
    public List<Genres> getGenres() { return genres; }
    public List<Tags> getTags() { return tags; }
    public List<Theaters> getTheaters() { return theaters; }
    public List<String> getAgeRatings() { return ageRatings; }
    public int getTotal() { return total; }
    public int getPage() { return page; }
    public int getPageSize() { return MovieFilter.PAGE_SIZE; }
    public int getTotalPages() { return Math.max(1, (int) Math.ceil((double) total / getPageSize())); }
    public boolean isHasNext() { return page < getTotalPages(); }
    public boolean isHasPrevious() { return page > 1; }
}
