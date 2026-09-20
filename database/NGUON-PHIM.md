# Nguồn dữ liệu 10 phim mẫu

Đối chiếu nguồn hãng phim ngày 20/09/2026. Dữ liệu nằm trong `../movie-seed-data.json`. Mô tả tiếng Việt được viết mới từ nội dung cốt truyện trên các trang hãng, không sao chép nguyên văn. `description_en` là bản tiếng Anh tự viết, chỉ dùng ký tự ASCII, để tương thích với cột `varchar(max)` hiện tại mà không thay đổi cấu trúc cơ sở dữ liệu.

| Phim | Nguồn thông tin | Thời lượng trang hãng | Ngày/năm nguồn công bố | Nguồn ảnh |
| --- | --- | --- | --- | --- |
| Luca | [Disney Movies](https://movies.disney.com/luca) | 1h 35min | 2021-06-18 | [Trang Luca của Pixar](https://www.pixar.com/luca), ảnh đầu trang |
| Soul | [Disney Movies](https://movies.disney.com/soul) | 1h 40min | 2020-12-25 | Cùng trang, Featured Content Banner |
| Encanto | [Disney Movies](https://movies.disney.com/encanto) | 1h 42min | 2021-11-24 | Cùng trang, Featured Content Banner |
| Turning Red | [Disney Movies](https://movies.disney.com/turning-red) | 1h 40min | 2022-03-11 | Cùng trang, Featured Content Banner |
| Elemental | [Disney Movies](https://movies.disney.com/elemental) | 1h 43min | 2023-06-16 | Cùng trang, Featured Content Banner |
| Wish | [Disney Movies](https://movies.disney.com/wish) | 1h 35min | 2023-11-22 | Cùng trang, Featured Content Banner |
| Onward | [Disney Movies](https://movies.disney.com/onward) | 1h 42min | 2020-03-06 | [Trang Onward của Pixar](https://www.pixar.com/onward), ảnh đầu trang |
| The Wild Robot | [Universal Pictures At Home](https://www.universalpicturesathome.com/movies/the-wild-robot) | 1hr 42min | 2024-09-27, nguồn ngày bên dưới | Cùng trang, Digital poster |
| The Bad Guys | [Universal Pictures At Home](https://www.universalpicturesathome.com/movies/the-bad-guys) | 1hr 40min | 2022-04-22, nguồn ngày bên dưới | Cùng trang, Digital poster |
| Lightyear | [Disney Movies](https://movies.disney.com/lightyear) | 1h 40min | 2022-06-17 | [Trang Lightyear của Pixar](https://www.pixar.com/lightyear), ảnh đầu trang |

## Quy ước dữ liệu

- `release_date` dùng ngày phát hành tại Mỹ, không phải lịch chiếu Việt Nam. Tám phim Disney lấy ngày trên trang Disney Movies; hai phim Universal bổ sung ngày từ các nguồn hãng dưới đây.
- `runtime_minutes` chuyển từ thời lượng chính trang hãng đang hiển thị. Soul là 100 phút và Elemental là 103 phút theo nguồn này; đây không phải lời khẳng định rằng mọi bản phát hành có cùng thời lượng.
- Thể loại dùng tiếng Anh và chuẩn hóa `Animated` thành `Animation`, `Kids & Family` thành `Family`, `Action-Adventure` thành hai giá trị `Action`, `Adventure`.
- `directors` và `co_directors` tách vai trò theo phần mô tả của hãng, cụ thể Soul có đồng đạo diễn Kemp Powers; Encanto có đồng đạo diễn Charise Castro Smith.
- Không gán phân loại tuổi Việt Nam, điểm IMDb, điểm đánh giá khán giả hay lượt đánh giá. JSON không chứa dữ liệu điểm/phân loại tuổi. Nếu ứng dụng bắt buộc khởi tạo điểm bằng 0, cần coi đó là trạng thái chưa có đánh giá của ứng dụng.
- `poster_url` là tên trường dùng để đưa ảnh vào ứng dụng. Hãy đọc `image_kind`: năm ảnh Disney là banner ngang, ba ảnh Pixar là artwork đầu trang, hai ảnh Universal là poster. Không phải cả 10 ảnh đều là poster dọc.
- Tất cả URL ảnh được lấy từ liên kết ảnh của chính trang hãng phim. Chưa tải tệp ảnh/video về máy, chưa kiểm tra kích thước ảnh Pixar hay khả năng tải của trình duyệt ứng dụng. CDN có thể đổi đường dẫn trong tương lai.
- Tám trailer là trang xem chính thức trên Disney Video; Luca dùng teaser, Lightyear dùng Official Trailer 2. The Bad Guys dùng video Official Trailer 2 trên kênh Universal Pictures đã xác minh của YouTube. Đây là liên kết trang xem, không phải URL tệp MP4. The Wild Robot giữ `trailer_url = null`.

## Xác minh ngày phát hành hai phim Universal

- **The Wild Robot — 2024-09-27:** [NBCUniversal, bài kỷ niệm 30 năm DreamWorks, ngày 08/10/2024](https://www.nbcuniversal.com/article/dreamworks-celebrates-30th-anniversary) xác nhận phim đã mở màn phòng vé nội địa ngày 27/09. [Lịch phim 2024 của NBCUniversal, ngày 20/06/2024](https://www.nbcuniversal.com/article/universal-filmed-entertainment-group-bringing-summer-2024-blockbusters-box-office) cũng ghi The Wild Robot phát hành ngày 27/09 và dẫn mua vé Fandango. Hai nguồn công ty xác nhận năm và ngày, bao gồm sự kiện đã diễn ra.
- **The Bad Guys — 2022-04-22:** [Official Trailer 2 trên kênh Universal Pictures đã xác minh](https://www.youtube.com/watch?v=zpDuBXB_glk), đăng ngày 23/02/2022, thông báo chiếu rạp ngày 22/04. [Bộ hoạt động chính thức của DreamWorks](https://www.dreamworks.com/downloads/TheBadGuys_ActivityPack_Complete.pdf) cũng ghi ngày chiếu 22/04 và bản quyền Universal Studios 2022. [Trang phim của Universal](https://www.universalpicturesathome.com/movies/the-bad-guys) xác nhận năm 2022. Trang trailer cũ trên `universalstudios.com/videos/zpDuBXB_glk/` còn được lập chỉ mục nhưng hiện chuyển hướng sang UDX, nên URL YouTube chính thức được lưu làm nguồn hoạt động.

## Tài nguyên phụ đã xác minh

- [Elemental: poster nhà phát hành Fandango, bản hiển thị ngang trên trang Disney](https://lumiere-a.akamaihd.net/v1/images/g_disney_elemental_exhibitorposters_793_01_b547144d.jpeg?region=1%2C0%2C1298%2C730)
- [Wish: poster nhân vật Magnifico, bản hiển thị ngang trên trang Disney](https://lumiere-a.akamaihd.net/v1/images/g_disney_wish_799_02_f8752f81.jpeg?region=1%2C0%2C1298%2C730)
- [Turning Red: hình Panda Mei vui vẻ từ trang Disney](https://lumiere-a.akamaihd.net/v1/images/g_disneyplus_turningred_happy_rebrand_5ffb613e.jpeg?region=1%2C0%2C1298%2C730)

Chỉ chuẩn bị dữ liệu nguồn; không đọc hoặc chỉnh sửa cơ sở dữ liệu trong phần việc này.
