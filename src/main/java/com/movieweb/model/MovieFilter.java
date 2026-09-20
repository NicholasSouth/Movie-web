package com.movieweb.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

/** Validated catalogue input. Values are always bound, never interpolated into SQL. */
public final class MovieFilter {
    public static final int PAGE_SIZE = 12;
    private final String search;
    private final Integer genreId, tagId, theaterId;
    private final LocalDate date;
    private final String ageRating, sort, status;
    private final Double minRating;
    private final int page;

    private MovieFilter(String search, Integer genreId, Integer tagId, Integer theaterId,
            LocalDate date, String ageRating, Double minRating, String sort, String status, int page) {
        this.search = search;
        this.genreId = genreId;
        this.tagId = tagId;
        this.theaterId = theaterId;
        this.date = date;
        this.ageRating = ageRating;
        this.minRating = minRating;
        this.sort = sort;
        this.status = status;
        this.page = page;
    }

    public static MovieFilter fromParameters(Map<String, String[]> parameters) {
        String search = value(parameters, "search");
        if (search.length() > 200) throw new IllegalArgumentException("Từ khóa tìm kiếm tối đa 200 ký tự.");
        Integer genre = optionalId(value(parameters, "genre"), "Thể loại");
        Integer tag = optionalId(value(parameters, "tag"), "Nhãn phim");
        Integer theater = optionalId(value(parameters, "theater"), "Rạp chiếu");
        String dateText = value(parameters, "date");
        LocalDate date = null;
        if (!dateText.isEmpty()) {
            try {
                if (!dateText.matches("\\d{4}-\\d{2}-\\d{2}")) throw new DateTimeParseException("format", dateText, 0);
                date = LocalDate.parse(dateText);
                if (date.getYear() < 1753) throw new IllegalArgumentException("Ngày chiếu không hợp lệ.");
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Ngày chiếu phải là ngày hợp lệ theo dạng YYYY-MM-DD.");
            }
        }
        String age = value(parameters, "age-rating");
        // The supplied database includes mixed country labels such as "PG-13 / 12A".
        if (age.length() > 100 || age.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Phân loại độ tuổi không hợp lệ.");
        String ratingText = value(parameters, "rating");
        Double rating = null;
        if (!ratingText.isEmpty()) {
            try {
                if (!ratingText.matches("\\d+(\\.\\d{1,2})?")) throw new NumberFormatException();
                rating = Double.valueOf(ratingText);
                if (!Double.isFinite(rating) || rating < 0 || rating > 10) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Điểm đánh giá phải nằm trong khoảng 0 đến 10.");
            }
        }
        String sort = value(parameters, "sort");
        if (sort.isEmpty()) sort = "newest";
        if (!Set.of("newest", "popular", "rating", "name", "duration").contains(sort))
            throw new IllegalArgumentException("Cách sắp xếp không hợp lệ.");
        String status = value(parameters, "status");
        if (status.isEmpty()) status = "all";
        if (!Set.of("all", "now-showing", "coming-soon").contains(status))
            throw new IllegalArgumentException("Trạng thái phim không hợp lệ.");
        Integer page = optionalId(value(parameters, "page"), "Trang");
        if (page != null && page > 100000) throw new IllegalArgumentException("Số trang quá lớn.");
        return new MovieFilter(search, genre, tag, theater, date, age, rating, sort, status, page == null ? 1 : page);
    }

    public static String value(Map<String, String[]> parameters, String key) {
        String[] values = parameters.get(key);
        if (values == null || values.length == 0) return "";
        if (values.length != 1) throw new IllegalArgumentException("Tham số " + key + " chỉ được xuất hiện một lần.");
        return values[0] == null ? "" : values[0].trim();
    }

    public static int requiredId(String text, String label) {
        Integer id = optionalId(text, label);
        if (id == null) throw new IllegalArgumentException(label + " là bắt buộc.");
        return id;
    }

    private static Integer optionalId(String text, String label) {
        if (text == null || text.isEmpty()) return null;
        try {
            if (!text.matches("[0-9]{1,10}")) throw new NumberFormatException();
            int id = Integer.parseInt(text);
            if (id < 1) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " phải là số nguyên dương hợp lệ.");
        }
    }

    public String getSearch() { return search; }
    public Integer getGenreId() { return genreId; }
    public Integer getTagId() { return tagId; }
    public Integer getTheaterId() { return theaterId; }
    public LocalDate getDate() { return date; }
    public String getAgeRating() { return ageRating; }
    public Double getMinRating() { return minRating; }
    public String getSort() { return sort; }
    public String getStatus() { return status; }
    public int getPage() { return page; }
    public int getOffset() { return (page - 1) * PAGE_SIZE; }
    public boolean isHasFilters() {
        return !search.isEmpty() || genreId != null || tagId != null || theaterId != null || date != null
                || !ageRating.isEmpty() || minRating != null || !"newest".equals(sort) || !"all".equals(status);
    }
}
