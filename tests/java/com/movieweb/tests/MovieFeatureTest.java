package com.movieweb.tests;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.movieweb.DAO.MovieCatalogDAO;
import com.movieweb.model.MovieCatalog;
import com.movieweb.model.MovieDetails;
import com.movieweb.model.MovieFilter;
import com.movieweb.model.Movies;
import com.movieweb.model.Users;
import com.movieweb.service.MovieService;
import com.movieweb.servlet.MovieDetailsServlet;
import com.movieweb.servlet.MovieFavouriteServlet;
import com.movieweb.servlet.MoviesServlet;
import com.movieweb.util.ViewUtils;

/** Standalone regression suite. H2 is used ONLY for isolated test data. */
public final class MovieFeatureTest {
    private static final String URL = "jdbc:h2:mem:movie_features;MODE=MSSQLServer;DB_CLOSE_DELAY=-1";
    private static int checks;
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    public static void main(String[] args) throws Exception {
        Logger.getLogger(MoviesServlet.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(MovieDetailsServlet.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(MovieFavouriteServlet.class.getName()).setLevel(Level.OFF);
        validationAndRendering();
        Class.forName("org.h2.Driver");
        createFixture();
        MovieCatalogDAO dao = new MovieCatalogDAO(() -> DriverManager.getConnection(URL));
        databaseQueries(dao);
        servletFlows(new MovieService(dao));
        System.out.println("PASS: " + checks + " assertions (validation, presentation safety, catalogue SQL, details, favourites, servlet HTTP flows).");
        System.out.println("Database integration ran on isolated H2 in MSSQLServer mode, not on the user's SQL Server backup.");
    }

    private static void validationAndRendering() {
        MovieFilter defaults = filter();
        equal(1, defaults.getPage(), "default first page");
        equal(0, defaults.getOffset(), "first page offset");
        equal("newest", defaults.getSort(), "default sort");
        equal(false, defaults.isHasFilters(), "default filter state");
        equal(12, filter("page", "2").getOffset(), "second page offset");
        equal("Timothee", filter("search", "  Timothee  ").getSearch(), "trim search");
        equal(LocalDate.of(2028, 2, 29), filter("date", "2028-02-29").getDate(), "valid leap date");
        for (String[] pair : List.of(new String[]{"page", "0"}, new String[]{"page", "100001"},
                new String[]{"genre", "-1"}, new String[]{"tag", "1 OR 1=1"},
                new String[]{"theater", "2147483648"}, new String[]{"date", "2026-02-29"},
                new String[]{"date", "2026-2-2"}, new String[]{"date", "1752-12-31"},
                new String[]{"rating", "NaN"}, new String[]{"rating", "10.01"},
                new String[]{"sort", "movie_name; DROP TABLE Movies"},
                new String[]{"status", "hidden"}, new String[]{"age-rating", "bad\u0000label"},
                new String[]{"search", "x".repeat(201)})) {
            rejects(() -> filter(pair), "reject invalid " + pair[0]);
        }
        rejects(() -> MovieFilter.fromParameters(Map.of("genre", new String[]{"1", "2"})), "reject duplicate filter");
        rejects(() -> MovieFilter.requiredId("", "ID"), "require movie ID");
        equal("R / MA15+", filter("age-rating", "R / MA15+").getAgeRating(), "compound DB age rating supported");
        equal("&lt;script&gt;&amp;&quot;&#39;", ViewUtils.h("<script>&\"'"), "escape HTML and quotes");
        equal("", ViewUtils.externalUrl("javascript:alert(1)"), "reject JavaScript trailer URL");
        equal("", ViewUtils.externalUrl("https://user:password@example.com/a"), "reject credentials in URL");
        equal("https://example.com/a", ViewUtils.externalUrl("https://example.com/a"), "accept HTTPS trailer URL");
        equal("/MovieWeb/pictures/movie-placeholder.svg", ViewUtils.asset("/MovieWeb", "../../secret"), "reject traversal image path");
        equal("/MovieWeb/pictures/movie-placeholder.svg", ViewUtils.asset("/MovieWeb", "//evil.example/a"), "reject protocol-relative image");
        equal("/MovieWeb/pictures/movies/a.jpg", ViewUtils.asset("/MovieWeb", "pictures/movies/a.jpg"), "prefix local asset context");
    }

    private static void createFixture() throws Exception {
        try (Connection c = DriverManager.getConnection(URL); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE Movies (movie_id INT PRIMARY KEY, movie_name NVARCHAR(300), description NVARCHAR(1000), age_rating VARCHAR(20), avg_rating DOUBLE, duration_minute INT, available_from TIMESTAMP, available_until TIMESTAMP, poster_path VARCHAR(500), trailer_path VARCHAR(500), trailer_link VARCHAR(500), isActive INT, deleted_at TIMESTAMP)");
            s.execute("CREATE TABLE Actors (actor_id INT PRIMARY KEY, actor_name NVARCHAR(100))");
            s.execute("CREATE TABLE Directors (director_id INT PRIMARY KEY, director_name NVARCHAR(100))");
            s.execute("CREATE TABLE Authors (author_id INT PRIMARY KEY, author_name NVARCHAR(100))");
            s.execute("CREATE TABLE Genres (genre_id INT PRIMARY KEY, genre_name NVARCHAR(100))");
            s.execute("CREATE TABLE Tags (tag_id INT PRIMARY KEY, tag_name NVARCHAR(100))");
            s.execute("CREATE TABLE Theaters (theater_id INT PRIMARY KEY, theater_name NVARCHAR(100), theater_address NVARCHAR(100), isActive INT, deleted_at TIMESTAMP)");
            s.execute("CREATE TABLE Rooms (room_id INT PRIMARY KEY, theater_id INT, room_name NVARCHAR(100), isActive INT)");
            s.execute("CREATE TABLE Showtimes (showtime_id INT PRIMARY KEY, movie_id INT, room_id INT, start_at TIMESTAMP, end_at TIMESTAMP, status VARCHAR(30))");
            s.execute("CREATE TABLE Favourite_movies (user_id INT, movie_id INT, added_at TIMESTAMP, PRIMARY KEY(user_id,movie_id))");
            for (String[] relation : List.of(new String[]{"actors", "actor"}, new String[]{"directors", "director"},
                    new String[]{"authors", "author"}, new String[]{"genres", "genre"}, new String[]{"tags", "tag"})) {
                s.execute("CREATE TABLE Movie_" + relation[0] + " (movie_id INT, " + relation[1] + "_id INT)");
            }
            movie(c, 1, "Alpha <Film>", "T16", 8.5, 120, -30, 1, false);
            movie(c, 2, "Future Film", "P", 9.5, 110, 30, 1, false);
            movie(c, 3, "Inactive Film", "P", 10, 100, -30, 0, false);
            movie(c, 4, "Deleted Film", "P", 10, 90, -30, 1, true);
            movie(c, 5, "Literal%_[X]", "T16", 7.5, 140, -30, 1, false);
            movie(c, 6, "LiteralABX", "K", 6.5, 150, -30, 1, false);
            for (int id = 10; id <= 22; id++) movie(c, id, "Page Film " + id, "P", 5, 100 + id, -id, 1, false);
            s.execute("INSERT INTO Actors VALUES(1,'Nguyen Actor')");
            s.execute("INSERT INTO Directors VALUES(1,'Director One')");
            s.execute("INSERT INTO Authors VALUES(1,'Author Two')");
            s.execute("INSERT INTO Genres VALUES(1,'Action'),(2,'Comedy')");
            s.execute("INSERT INTO Tags VALUES(1,'IMAX'),(2,'2D')");
            s.execute("INSERT INTO Movie_actors VALUES(1,1)");
            s.execute("INSERT INTO Movie_directors VALUES(2,1)");
            s.execute("INSERT INTO Movie_authors VALUES(5,1)");
            s.execute("INSERT INTO Movie_genres VALUES(1,1),(1,1),(2,2)");
            s.execute("INSERT INTO Movie_tags VALUES(1,1),(2,2)");
            s.execute("INSERT INTO Theaters VALUES(1,'Open Cinema','First street',1,NULL),(2,'Closed Cinema','Second street',0,NULL),(3,'Deleted Cinema','Third street',1,CURRENT_TIMESTAMP)");
            s.execute("INSERT INTO Rooms VALUES(1,1,'Room A',1),(2,1,'Closed Room',0),(3,2,'Room B',1),(4,3,'Room C',1)");
            showtime(c, 1, 1, 1, 1, "SCHEDULED");
            showtime(c, 2, 1, 1, -1, "scheduled");
            showtime(c, 3, 1, 1, 1, "cancelled");
            showtime(c, 4, 1, 2, 1, "scheduled");
            showtime(c, 5, 1, 3, 1, "scheduled");
            showtime(c, 6, 1, 4, 1, "scheduled");
            s.execute("INSERT INTO Favourite_movies VALUES(7,2,CURRENT_TIMESTAMP)");
        }
    }

    private static void movie(Connection c, int id, String title, String age, double rating, int duration,
            int availableDays, int active, boolean deleted) throws Exception {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO Movies VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            p.setInt(1, id); p.setString(2, title); p.setString(3, "Description " + title); p.setString(4, age);
            p.setDouble(5, rating); p.setInt(6, duration);
            p.setTimestamp(7, Timestamp.valueOf(LocalDate.now().plusDays(availableDays).atStartOfDay()));
            p.setTimestamp(8, Timestamp.valueOf(LocalDate.now().plusYears(1).atStartOfDay()));
            p.setString(9, "pictures/movies/test.jpg"); p.setString(10, null);
            p.setString(11, "https://example.com/trailer"); p.setInt(12, active);
            p.setTimestamp(13, deleted ? Timestamp.valueOf(LocalDate.now().atStartOfDay()) : null);
            p.executeUpdate();
        }
    }

    private static void showtime(Connection c, int id, int movieId, int roomId, int days, String status) throws Exception {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO Showtimes VALUES(?,?,?,?,?,?)")) {
            p.setInt(1, id); p.setInt(2, movieId); p.setInt(3, roomId);
            p.setTimestamp(4, Timestamp.valueOf(LocalDate.now().plusDays(days).atTime(14, 0)));
            p.setTimestamp(5, Timestamp.valueOf(LocalDate.now().plusDays(days).atTime(16, 0)));
            p.setString(6, status); p.executeUpdate();
        }
    }

    private static void databaseQueries(MovieCatalogDAO dao) throws Exception {
        MovieCatalog first = dao.findCatalog(filter());
        equal(17, first.getTotal(), "exclude inactive and deleted movies");
        equal(12, first.getMovies().size(), "catalogue first page size");
        equal(2, first.getTotalPages(), "catalogue total pages");
        equal(true, first.isHasNext(), "first page next link");
        equal(1, first.getTheaters().size(), "only active nondeleted theater choices");
        equal(2, first.getPopular().get(0).getMovie_id(), "popular from real favorite counts");
        equal(List.of(2), ids(first.getComingSoon()), "coming soon group");
        check(first.getNowShowing().stream().noneMatch(m -> m.getMovie_id() == 2), "future film absent from now showing");
        MovieCatalog second = dao.findCatalog(filter("page", "2"));
        equal(5, second.getMovies().size(), "last page size");
        HashSet<Integer> ids = new HashSet<>(ids(first.getMovies()));
        for (Movies m : second.getMovies()) check(ids.add(m.getMovie_id()), "pagination has no duplicate movie");
        equal(List.of(1), ids(dao.findCatalog(filter("search", "Nguyen Actor")).getMovies()), "search by actor");
        equal(List.of(2), ids(dao.findCatalog(filter("search", "Director One")).getMovies()), "search by director");
        equal(List.of(5), ids(dao.findCatalog(filter("search", "Author Two")).getMovies()), "search by writer");
        equal(List.of(5), ids(dao.findCatalog(filter("search", "%_[")).getMovies()), "LIKE wildcard characters treated literally");
        equal(0, dao.findCatalog(filter("search", "' OR 1=1 --")).getTotal(), "SQL injection text is ordinary search data");
        equal(List.of(1), ids(dao.findCatalog(filter("genre", "1", "tag", "1", "age-rating", "T16", "rating", "8", "theater", "1", "date", TOMORROW.toString(), "status", "now-showing")).getMovies()), "all catalogue filters combine correctly");
        equal(1, dao.findCatalog(filter("genre", "1")).getTotal(), "duplicate relation does not duplicate movie");
        equal(0, dao.findCatalog(filter("theater", "2")).getTotal(), "inactive theater does not match");
        equal(0, dao.findCatalog(filter("date", LocalDate.now().minusDays(1).toString())).getTotal(), "past showtimes do not match");
        equal(2, dao.findCatalog(filter("sort", "rating")).getMovies().get(0).getMovie_id(), "rating sort");
        equal(2, dao.findCatalog(filter("sort", "popular")).getMovies().get(0).getMovie_id(), "popular sort uses favorite counts");
        equal(List.of(2), ids(dao.findCatalog(filter("status", "coming-soon")).getMovies()), "coming soon filter");
        equal(16, dao.findCatalog(filter("status", "now-showing")).getTotal(), "now showing filter");
        equal(0, dao.findCatalog(filter("page", "100000")).getMovies().size(), "page beyond result is empty");
        MovieDetails details = dao.findDetails(1, 7);
        equal("Alpha <Film>", details.getMovie().getMovie_name(), "details title comes from DB");
        equal("Nguyen Actor", details.getActors().get(0).getActor_name(), "details actor relation");
        equal(1, details.getGenres().size(), "details deduplicates genres");
        equal("IMAX", details.getTags().get(0).getTag_name(), "details tag relation");
        equal(1, details.getShowtimes().size(), "only future scheduled active showtime");
        equal(1, details.getShowtimes().get(0).getShowtimeId(), "correct showtime retained");
        equal("Director One", dao.findDetails(2, null).getDirectors().get(0).getDirector_name(), "details director relation");
        equal("Author Two", dao.findDetails(5, null).getAuthors().get(0).getAuthor_name(), "details author relation");
        equal(null, dao.findDetails(3, null), "inactive details inaccessible");
        equal(null, dao.findDetails(4, null), "deleted details inaccessible");
        equal(null, dao.findDetails(999, null), "unknown details absent");
        equal(false, details.isFavourite(), "initial favorite state");
        equal(true, dao.setFavourite(7, 1, true), "add favorite");
        equal(true, dao.setFavourite(7, 1, true), "repeat add is idempotent");
        equal(true, dao.findDetails(1, 7).isFavourite(), "favorite saved in DB");
        try (Connection c = DriverManager.getConnection(URL); Statement s = c.createStatement();
                var rows = s.executeQuery("SELECT COUNT(*) FROM Favourite_movies WHERE user_id=7 AND movie_id=1")) {
            rows.next(); equal(1, rows.getInt(1), "no duplicate favorite row");
        }
        equal(true, dao.setFavourite(7, 1, false), "remove favorite");
        equal(false, dao.findDetails(1, 7).isFavourite(), "favorite removed in DB");
        equal(false, dao.setFavourite(7, 3, true), "cannot favorite an inactive movie");
        MovieService service = new MovieService(dao);
        rejects(() -> service.getDetails(0, null), "service rejects invalid movie ID");
        rejects(() -> service.setFavourite(0, 1, true), "service rejects invalid user ID");
    }

    private static void servletFlows(MovieService service) throws Exception {
        Exchange list = new Exchange("GET");
        list.run(new MoviesServlet(service));
        equal(200, list.status, "catalogue HTTP success");
        equal("/WEB-INF/views/movies.jsp", list.forward, "catalogue forwards to protected view");
        equal(17, ((MovieCatalog) list.attributes.get("catalog")).getTotal(), "catalogue request model");
        equal("no-store", list.headers.get("Cache-Control"), "movie response avoids stale session data");
        Exchange invalid = new Exchange("GET", "page", "bad"); invalid.run(new MoviesServlet(service));
        equal(400, invalid.status, "bad filter HTTP 400");
        Exchange missing = new Exchange("GET", "id", "999"); missing.run(new MovieDetailsServlet(service));
        equal(404, missing.status, "missing movie HTTP 404");
        Exchange mismatch = new Exchange("GET", "id", "1", "movie_id", "2"); mismatch.run(new MovieDetailsServlet(service));
        equal(400, mismatch.status, "conflicting movie ID aliases rejected");
        Exchange duplicate = new Exchange("GET"); duplicate.parameters.put("id", new String[]{"1", "2"});
        duplicate.run(new MovieDetailsServlet(service)); equal(400, duplicate.status, "duplicate movie IDs rejected");
        Exchange detail = new Exchange("GET", "movie_id", "1"); detail.run(new MovieDetailsServlet(service));
        equal(200, detail.status, "legacy detail ID supported");
        equal("/WEB-INF/views/movie_details.jsp", detail.forward, "details forwards to protected view");
        check(((String) detail.attributes.get("csrfToken")).length() >= 40, "detail generates unpredictable CSRF token");
        Exchange noLogin = new Exchange("POST", "movieId", "1", "action", "add");
        noLogin.run(new MovieFavouriteServlet(service)); equal(401, noLogin.status, "favorite requires login");
        Users user = new Users(); user.setUserId(7); user.setActive(true);
        Exchange noCsrf = new Exchange("POST", "movieId", "1", "action", "add");
        noCsrf.sessionAttributes.put("user", user); noCsrf.hasSession = true;
        noCsrf.run(new MovieFavouriteServlet(service)); equal(403, noCsrf.status, "favorite requires CSRF token");
        Exchange save = new Exchange("POST", "movieId", "1", "action", "add", "csrfToken", "valid-test-token");
        save.sessionAttributes.put("user", user); save.sessionAttributes.put("movieCsrfToken", "valid-test-token"); save.hasSession = true;
        save.run(new MovieFavouriteServlet(service)); equal(303, save.status, "favorite uses post-redirect-get");
        equal("/MovieWeb/movie-details?id=1&favourite=added", save.headers.get("Location"), "favorite redirects within app context");
        equal(true, service.getDetails(1, 7).isFavourite(), "authenticated favorite persisted");
        MovieService unavailable = new MovieService(new MovieCatalogDAO(() -> { throw new SQLException("private connection details"); }));
        // The expected failure is asserted below; avoid printing its stack trace as a test failure.
        Logger.getLogger(MoviesServlet.class.getName()).setLevel(Level.OFF);
        Exchange dbFailure = new Exchange("GET"); dbFailure.run(new MoviesServlet(unavailable));
        equal(503, dbFailure.status, "unavailable DB yields HTTP 503");
        check(!dbFailure.attributes.get("errorMessage").toString().contains("private connection"), "DB error does not leak connection details");
        equal("30", dbFailure.headers.get("Retry-After"), "database error includes retry hint");
    }

    private static MovieFilter filter(String... values) { return MovieFilter.fromParameters(parameters(values)); }
    private static Map<String, String[]> parameters(String... values) {
        Map<String, String[]> result = new HashMap<>();
        for (int i = 0; i < values.length; i += 2) result.put(values[i], new String[]{values[i + 1]});
        return result;
    }
    private static List<Integer> ids(List<Movies> movies) { return movies.stream().map(Movies::getMovie_id).toList(); }
    private static void equal(Object expected, Object actual, String label) {
        check(Objects.equals(expected, actual), label + " (expected=" + expected + ", actual=" + actual + ")");
    }
    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        checks++;
    }
    @FunctionalInterface private interface Action { void run() throws Exception; }
    private static void rejects(Action action, String label) {
        try { action.run(); } catch (IllegalArgumentException expected) { checks++; return; }
        catch (Exception wrong) { throw new AssertionError(label + ": wrong exception", wrong); }
        throw new AssertionError(label + ": accepted invalid input");
    }

    private static final class Exchange {
        final String method;
        final Map<String, String[]> parameters;
        final Map<String, Object> attributes = new HashMap<>(), sessionAttributes = new HashMap<>();
        final Map<String, String> headers = new HashMap<>();
        int status = 200;
        boolean hasSession;
        String forward;
        Exchange(String method, String... values) { this.method = method; this.parameters = parameters(values); }
        void run(HttpServlet servlet) throws Exception {
            HttpSession session = proxy(HttpSession.class, (p, m, args) -> switch (m.getName()) {
                case "getAttribute" -> sessionAttributes.get(args[0]);
                case "setAttribute" -> { sessionAttributes.put((String) args[0], args[1]); yield null; }
                default -> defaultValue(m.getReturnType());
            });
            HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, args) -> switch (m.getName()) {
                case "getMethod" -> method;
                case "getProtocol" -> "HTTP/1.1";
                case "getContextPath" -> "/MovieWeb";
                case "getParameterMap" -> parameters;
                case "getParameter" -> parameters.containsKey(args[0]) ? parameters.get(args[0])[0] : null;
                case "getAttribute" -> attributes.get(args[0]);
                case "setAttribute" -> { attributes.put((String) args[0], args[1]); yield null; }
                case "getSession" -> {
                    boolean create = args == null || args.length == 0 || Boolean.TRUE.equals(args[0]);
                    if (create) hasSession = true;
                    yield hasSession ? session : null;
                }
                case "getRequestDispatcher" -> proxy(RequestDispatcher.class, (dispatcher, operation, input) -> {
                    if (operation.getName().equals("forward")) forward = (String) args[0];
                    return null;
                });
                default -> defaultValue(m.getReturnType());
            });
            HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, args) -> switch (m.getName()) {
                case "setStatus", "sendError" -> { status = (Integer) args[0]; yield null; }
                case "setHeader", "addHeader" -> { headers.put((String) args[0], (String) args[1]); yield null; }
                default -> defaultValue(m.getReturnType());
            });
            servlet.service(request, response);
        }
        private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
            return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler));
        }
        private static Object defaultValue(Class<?> type) {
            if (type == boolean.class) return false;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            return null;
        }
    }
}
