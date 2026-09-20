-- MovieWeb: add ten sample films. Sources are in NGUON-PHIM.md.
-- Run the complete file in SSMS. Existing movies are preserved.
-- Repeating this script skips existing titles. Dates are original US releases.
-- Descriptions use English because the existing database column is varchar.
USE [MovieWeb];
SET NOCOUNT ON;
SET XACT_ABORT ON;

DECLARE @Movies TABLE (
    title varchar(100) PRIMARY KEY,
    description varchar(max) NOT NULL,
    minutes int NOT NULL,
    release_date date NOT NULL,
    poster varchar(max) NOT NULL,
    trailer varchar(max) NULL
);
DECLARE @Genres TABLE (title varchar(100), name varchar(100));
DECLARE @Directors TABLE (title varchar(100), name varchar(100));
DECLARE @Inserted TABLE (movie_id int PRIMARY KEY, title varchar(100));

INSERT INTO @Movies VALUES
(N'Luca',N'Young sea monster Luca and his friend Alberto explore an Italian seaside town during a summer filled with friendship and secrets.',95,N'2021-06-18',N'https://images.squarespace-cdn.com/content/v1/60241cb68df65b530cd84d95/8ad5ed6e-93c8-4914-929e-657b2395e170/v300_518_bg.bkgd16.4522.jpg',N'https://video.disney.com/watch/disney-and-pixar-s-luca-teaser-trailer-5bc298ec1b854ebd6b4f2a72'),
(N'Soul',N'After an accident, music teacher Joe Gardner meets a soul named 22, and the unlikely companions discover a new way to appreciate life.',100,N'2020-12-25',N'https://lumiere-a.akamaihd.net/v1/images/pp_soul_herobanner_20901_3ec716f8.jpeg?region=0%2C0%2C2048%2C878',N'https://video.disney.com/watch/soul-official-trailer-5a0a8211bfcf4ebd6b4f2a72'),
(N'Encanto',N'Mirabel, the only Madrigal child without a magical gift, tries to save her extraordinary family home as its powers begin to fade.',102,N'2021-11-24',N'https://lumiere-a.akamaihd.net/v1/images/pp_encanto_herobanner_21512_ab24a61c.jpeg?region=0%2C0%2C2048%2C878',N'https://video.disney.com/watch/disney-s-encanto-official-trailer-5cd227e2fc288ebd6b4f2a72'),
(N'Turning Red',N'Teenager Mei Lee learns to handle her changing emotions when getting excited turns her into a giant red panda.',100,N'2022-03-11',N'https://lumiere-a.akamaihd.net/v1/images/pp_turningred_herobanner_21513_af7a7f42.jpeg?region=0%2C0%2C2048%2C878',N'https://video.disney.com/watch/turning-red-official-trailer-5d0fc7228d8beebd6b4f2a72'),
(N'Elemental',N'A friendship between fiery Ember and watery Wade challenges their assumptions about life in a city inhabited by the elements.',103,N'2023-06-16',N'https://lumiere-a.akamaihd.net/v1/images/pp_disney_elemental_herobanner_homeent_v2_2292_d481a129.jpeg?region=0%2C0%2C2048%2C878',N'https://video.disney.com/watch/elemental-official-trailer-5f7f993024c6cebd6b4f2a72'),
(N'Wish',N'Asha joins forces with a magical star to challenge King Magnifico and protect the wishes of the people of Rosas.',95,N'2023-11-22',N'https://lumiere-a.akamaihd.net/v1/images/pp_disney_wish_herobanner_799_3b488628.jpeg?region=0%2C0%2C2048%2C878',N'https://video.disney.com/watch/wish-official-trailer-6065a328360feebd6b4f2a72'),
(N'Onward',N'Elf brothers Ian and Barley set out on a magical adventure for a chance to spend one more day with their late father.',102,N'2020-03-06',N'https://images.squarespace-cdn.com/content/v1/60241cb68df65b530cd84d95/d2fdd971-b38f-45f8-b6b8-84ee4ea73998/ONWARD_4C_TEASER_1SHEET_ART_LAYERS.jpg',N'https://video.disney.com/watch/onward-official-trailer-5948f1901beedebd6b4f2a72'),
(N'The Wild Robot',N'Stranded on an island, robot Roz learns to live among wild animals and care for an orphaned gosling.',102,N'2024-09-27',N'https://images.contentstack.io/v3/assets/blt13adb7e2033fcee5/bltca27afd1f7f9aa86/693390e890293f7361d5f440/TheWildRobot_Poster.jpg?width=2560',NULL),
(N'The Bad Guys',N'A gang of animal thieves pretends to reform to avoid prison, then faces the harder challenge of becoming genuinely good.',100,N'2022-04-22',N'https://images.contentstack.io/v3/assets/blt13adb7e2033fcee5/blt90fcf010523d00ee/692fa50fb48b45c3e53fcebf/TheBadGuys_Digital_Poster_2000x3000.jpg?width=2560',N'https://www.youtube.com/watch?v=zpDuBXB_glk'),
(N'Lightyear',N'Space ranger Buzz Lightyear teams up with new recruits and robot cat Sox to face Zurg and his robot army.',100,N'2022-06-17',N'https://images.squarespace-cdn.com/content/v1/60241cb68df65b530cd84d95/80047e2f-aeed-4749-bb3a-bb1ced982494/test2.jpg',N'https://video.disney.com/watch/lightyear-official-trailer-2-5dd2ada7413b3ebd6b4f2a72');

INSERT INTO @Genres VALUES
(N'Luca',N'Action'),
(N'Luca',N'Adventure'),
(N'Luca',N'Animation'),
(N'Luca',N'Comedy'),
(N'Soul',N'Animation'),
(N'Soul',N'Comedy'),
(N'Soul',N'Family'),
(N'Soul',N'Fantasy'),
(N'Soul',N'Musical'),
(N'Encanto',N'Adventure'),
(N'Encanto',N'Animation'),
(N'Encanto',N'Fantasy'),
(N'Encanto',N'Family'),
(N'Encanto',N'Musical'),
(N'Turning Red',N'Animation'),
(N'Turning Red',N'Family'),
(N'Elemental',N'Animation'),
(N'Elemental',N'Comedy'),
(N'Elemental',N'Fantasy'),
(N'Elemental',N'Family'),
(N'Wish',N'Action'),
(N'Wish',N'Adventure'),
(N'Wish',N'Animation'),
(N'Wish',N'Fantasy'),
(N'Wish',N'Family'),
(N'Onward',N'Adventure'),
(N'Onward',N'Animation'),
(N'Onward',N'Comedy'),
(N'Onward',N'Family'),
(N'The Wild Robot',N'Animation'),
(N'The Wild Robot',N'Family'),
(N'The Bad Guys',N'Animation'),
(N'The Bad Guys',N'Family'),
(N'The Bad Guys',N'Comedy'),
(N'Lightyear',N'Action'),
(N'Lightyear',N'Adventure'),
(N'Lightyear',N'Animation'),
(N'Lightyear',N'Science Fiction');

INSERT INTO @Directors VALUES
(N'Luca',N'Enrico Casarosa'),
(N'Soul',N'Pete Docter'),
(N'Soul',N'Kemp Powers'),
(N'Encanto',N'Byron Howard'),
(N'Encanto',N'Jared Bush'),
(N'Encanto',N'Charise Castro Smith'),
(N'Turning Red',N'Domee Shi'),
(N'Elemental',N'Peter Sohn'),
(N'Wish',N'Chris Buck'),
(N'Wish',N'Fawn Veerasunthorn'),
(N'Onward',N'Dan Scanlon'),
(N'The Wild Robot',N'Chris Sanders'),
(N'The Bad Guys',N'Pierre Perifel'),
(N'Lightyear',N'Angus MacLane');

BEGIN TRY
    BEGIN TRANSACTION;
    SELECT * INTO #MovieWebBefore FROM dbo.Movies WITH (UPDLOCK, HOLDLOCK);
    DECLARE @BeforeCount int = (SELECT COUNT(*) FROM #MovieWebBefore);

    INSERT INTO dbo.Movies
        (movie_name, description, age_rating, avg_rating, duration_minute,
         available_from, available_until, poster_path, trailer_path,
         trailer_link, isActive, deleted_at)
    OUTPUT inserted.movie_id, inserted.movie_name INTO @Inserted(movie_id, title)
    SELECT s.title, s.description, NULL, NULL, s.minutes,
           s.release_date, NULL, s.poster, NULL, s.trailer, 1, NULL
    FROM @Movies AS s
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Movies AS m WITH (UPDLOCK, HOLDLOCK)
        WHERE LTRIM(RTRIM(m.movie_name)) = s.title
    );

    INSERT INTO dbo.Genres (genre_name)
    SELECT DISTINCT s.name
    FROM @Genres AS s JOIN @Inserted AS i ON i.title = s.title
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Genres AS g WITH (UPDLOCK, HOLDLOCK)
        WHERE g.genre_name = s.name
    );
    INSERT INTO dbo.Directors (director_name)
    SELECT DISTINCT s.name
    FROM @Directors AS s JOIN @Inserted AS i ON i.title = s.title
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Directors AS d WITH (UPDLOCK, HOLDLOCK)
        WHERE d.director_name = s.name
    );

    INSERT INTO dbo.Movie_genres (movie_id, genre_id)
    SELECT DISTINCT i.movie_id, g.genre_id
    FROM @Inserted AS i JOIN @Genres AS s ON s.title = i.title
    CROSS APPLY (SELECT MIN(genre_id) AS genre_id FROM dbo.Genres WHERE genre_name = s.name) AS g
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Movie_genres AS mg WITH (UPDLOCK, HOLDLOCK)
        WHERE mg.movie_id = i.movie_id AND mg.genre_id = g.genre_id
    );
    INSERT INTO dbo.Movie_directors (movie_id, director_id)
    SELECT DISTINCT i.movie_id, d.director_id
    FROM @Inserted AS i JOIN @Directors AS s ON s.title = i.title
    CROSS APPLY (SELECT MIN(director_id) AS director_id FROM dbo.Directors WHERE director_name = s.name) AS d
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Movie_directors AS md WITH (UPDLOCK, HOLDLOCK)
        WHERE md.movie_id = i.movie_id AND md.director_id = d.director_id
    );

    IF EXISTS (SELECT * FROM #MovieWebBefore EXCEPT SELECT * FROM dbo.Movies)
        THROW 51000, 'Existing movie data changed; the operation was rolled back.', 1;
    IF EXISTS (
        SELECT 1 FROM @Inserted i
        WHERE NOT EXISTS (SELECT 1 FROM dbo.Movie_genres g WHERE g.movie_id=i.movie_id)
           OR NOT EXISTS (SELECT 1 FROM dbo.Movie_directors d WHERE d.movie_id=i.movie_id)
    ) THROW 51001, 'A new movie is missing its genre or director; the operation was rolled back.', 1;
    IF (SELECT COUNT(*) FROM dbo.Movies) <> @BeforeCount + (SELECT COUNT(*) FROM @Inserted)
        THROW 51002, 'Unexpected movie count; the operation was rolled back.', 1;

    DECLARE @Added int = (SELECT COUNT(*) FROM @Inserted);
    DROP TABLE #MovieWebBefore;
    COMMIT TRANSACTION;

    SELECT CONCAT('RESULT|added=',@Added,'|total=',(SELECT COUNT(*) FROM dbo.Movies),
        '|visible=',(SELECT COUNT(*) FROM dbo.Movies WHERE isActive=1 AND deleted_at IS NULL)) AS summary;
    SELECT CAST(CONCAT('ADDED|',movie_id,'|',title) AS varchar(240)) AS added_movie FROM @Inserted ORDER BY movie_id;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;

SELECT CAST(CONCAT('MOVIE|',m.movie_id,'|',m.movie_name,'|',m.duration_minute,'|',m.age_rating,'|',m.isActive,'|',
    CASE WHEN m.deleted_at IS NULL THEN 0 ELSE 1 END) AS varchar(240)) AS catalog_line
FROM dbo.Movies m ORDER BY m.movie_id;
SELECT CAST(CONCAT('VERIFY|',m.movie_id,'|',m.movie_name,'|genres=',(SELECT COUNT(*) FROM dbo.Movie_genres g WHERE g.movie_id=m.movie_id),
    '|directors=',(SELECT COUNT(*) FROM dbo.Movie_directors d WHERE d.movie_id=m.movie_id),'|poster=',CASE WHEN LEN(m.poster_path)>0 THEN 1 ELSE 0 END,
    '|trailer=',CASE WHEN LEN(m.trailer_link)>0 THEN 1 ELSE 0 END,'|rated=',CASE WHEN m.avg_rating IS NULL THEN 0 ELSE 1 END) AS varchar(240)) AS verification
FROM dbo.Movies m JOIN @Movies s ON s.title=m.movie_name ORDER BY m.movie_id;
