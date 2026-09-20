# MovieWeb – Movies và Movie Details

Bản này nối hai trang Movies và Movie Details với database SQL Server qua DAO → service → servlet → JSP. Giao diện hiện có được giữ lại. Kết nối dùng **Windows Authentication**, theo yêu cầu của bạn.

## Chạy bản đầy đủ

1. Giải nén toàn bộ `MovieWeb-HoanChinh.zip` vào một thư mục riêng. Không chạy trực tiếp bên trong ZIP.
2. Máy cần **JDK 21 x64** và một **SQL Server 2025 (17.x) hoặc mới hơn có dịch vụ Database Engine/TCP/IP**. Bộ Tomcat 9.0.122, driver JDBC, DLL xác thực Windows x64 và file `MovieWeb.bak` đã có trong bản đầy đủ. JDK và SQL Server không nằm trong gói.
3. Bật TCP/IP cho đúng SQL Server instance trong SQL Server Configuration Manager, kiểm tra cổng thật rồi khởi động lại dịch vụ nếu vừa thay đổi. `1433` chỉ là giá trị mặc định của gói, chưa phải cổng đã xác nhận trên máy bạn. SQL Server Express có thể dùng cổng khác.
4. Chạy `setup-database.cmd`. Nhập **tên máy chủ** (ví dụ `localhost`) và **cổng TCP**. Nếu cần khôi phục database, nhập `y` ở câu hỏi restore. Công cụ đăng nhập bằng tài khoản Windows hiện tại, không hỏi mật khẩu SQL. Nó không ghi đè database `MovieWeb` đã tồn tại.
5. Chạy `check-database.cmd`. Khi có dòng `PASS`, driver xác thực, kết nối và các truy vấn danh sách/chi tiết phim đã chạy được trên SQL Server của bạn.
6. Chạy `run.cmd`, giữ cửa sổ đó mở rồi truy cập **http://localhost:8080/MovieWeb/movies**. Dừng bằng `Ctrl+C`.

Nếu `8080` đã được dùng, mở PowerShell trong thư mục dự án và chạy `powershell -ExecutionPolicy Bypass -File .\run.ps1 -Port 8081`, rồi mở `http://localhost:8081/MovieWeb/movies`.

**Tình trạng máy khi kiểm tra:** kết nối `localhost:1433` bị từ chối; chưa xác định được SQL Server instance/cổng thực tế. Phần mềm đã được kiểm tra bằng dữ liệu riêng nhưng chưa restore và xác thực trực tiếp trên database của bạn. Tệp sao lưu gốc không bị sửa.

### Nếu restore tự động không được

Trong SSMS, kết nối bằng Windows Authentication → nhấp phải **Databases → Restore Database → Device** → chọn `database\MovieWeb.bak`. Ở **Files**, chọn vị trí data/log phù hợp với SQL Server trên máy. Không chọn ghi đè một database có dữ liệu bạn cần giữ.

Backup chứa đường dẫn của máy người tạo, nên phải đổi vị trí data/log khi restore. Nếu gặp “Access denied” khi đọc `.bak`, đặt bản sao của tệp trong thư mục Backup của SQL Server mà tài khoản dịch vụ SQL có quyền đọc. Sau khi restore xong, chạy lại `setup-database.cmd` và chọn `N` ở bước restore.

File `.bak` được tạo từ SQL Server 2025. SQL Server 2022 hoặc cũ hơn không khôi phục trực tiếp được bản sao lưu này.

**LocalDB:** driver Microsoft JDBC của dự án kết nối qua TCP; `(localdb)\MSSQLLocalDB` không phải địa chỉ TCP phù hợp. Chỉ cài SSMS hoặc LocalDB chưa đủ cho cách chạy này. Dùng SQL Server Express/Developer với Database Engine và TCP/IP.

## Cấu hình Windows Authentication

Tệp riêng `config/database.properties` được bộ chạy nạp từ ngoài WAR. Ví dụ khi server thực sự dùng cổng 1433:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=MovieWeb;integratedSecurity=true;authenticationScheme=NativeAuthentication;encrypt=true;trustServerCertificate=true;
```

Không thêm `db.user` hoặc `db.password` khi dùng Windows Authentication. Tài khoản Windows chạy Java cần quyền đọc các bảng phim và quyền thêm/xóa `Favourite_movies` nếu dùng yêu thích. Công cụ restore cần quyền tạo/restore database.

`run.cmd` tự đặt đường dẫn DLL ở `runtime/native/mssql-jdbc_auth-13.4.0.x64.dll`. Java phải là x64. Nếu chạy từ Eclipse hoặc Tomcat riêng, thêm vào **VM arguments** (đổi các đường dẫn cho đúng máy):

```text
-Dmovieweb.config="C:\MovieWeb\config\database.properties"
-Djava.library.path="C:\MovieWeb\runtime\native"
-Dfile.encoding=UTF-8
```

Thiết lập tin chứng chỉ ở ví dụ dành cho SQL Server cục bộ. Nếu triển khai lên máy chủ thật, dùng chứng chỉ TLS hợp lệ và cấu hình xác minh tương ứng.

## Những chức năng đã nối database

- Movies: đang chiếu, sắp chiếu, phổ biến theo số yêu thích; danh sách đầy đủ có phân trang 12 phim/trang.
- Tìm theo tên phim, diễn viên, đạo diễn hoặc tác giả; lọc thể loại, nhãn, rạp, ngày có suất chiếu, phân loại tuổi và điểm tối thiểu.
- Sắp xếp theo ngày phát hành, điểm, số yêu thích, tên hoặc thời lượng. Phim bị xóa/ngừng hoạt động không hiển thị.
- Movie Details: poster, mô tả, đánh giá, thời lượng, độ tuổi, ngày phát hành, thể loại, nhãn, diễn viên, đạo diễn, tác giả, trailer và lịch chiếu sắp tới.
- Thêm/xóa phim yêu thích cho tài khoản đang đăng nhập; kiểm tra CSRF, không tạo bản ghi trùng khi thêm lại.
- Có thông báo khi không có kết quả, chưa có trailer/lịch chiếu, mã phim sai, phim không tồn tại hoặc mất kết nối database.

Đường dẫn chính: `/movies`, `/movie-details?id=1`. Các URL cũ `/movies.jsp`, `/movie_details.jsp?movie_id=1` tiếp tục hoạt động. Cũng có các alias `/Movies`, `/movie_details`, `/Movie_details`.

Các phần chọn ghế, đặt vé/thanh toán, đánh giá người dùng, quản trị và những trang khác nằm ngoài yêu cầu nối hai trang lần này. Chi tiết phim hiển thị lịch chiếu; không hiển thị vé/đánh giá giả.

## Bản để đưa lên GitHub

Giải nén `MovieWeb-GitHub-ThayDoi.zip`. Bên trong có thư mục `files/` với đúng đường dẫn tương đối của tất cả tệp thêm/sửa, cùng `DANH-SACH-THAY-DOI.md` để đối chiếu.

1. Lấy bản mới nhất của dự án nhóm trước khi ghép thay đổi.
2. Sao chép **nội dung bên trong `files/`** vào thư mục gốc dự án (nơi đang có `src/` và `.project`). Không chép nguyên thư mục `files` vào dự án.
3. Đối chiếu các tệp ghi `SỬA` trong danh sách nếu thành viên khác cũng đang chỉnh cùng tệp; ghép các thay đổi cần giữ.
4. Commit/push các tệp mã nguồn đã ghép. Không đưa bản ZIP chạy đầy đủ, `.bak`, `runtime/`, `dist/` hay `config/database.properties` lên GitHub. `.gitignore` đã bổ sung các mục này.

Bản thay đổi **không** chứa Tomcat, database hay cấu hình máy riêng. Có `database.example.properties` làm mẫu. Tệp `EmailService.java` cũ có thông tin đăng nhập email viết sẵn; bản này đọc `MOVIEWEB_MAIL_USER` và `MOVIEWEB_MAIL_PASSWORD` từ môi trường. Chức năng email cũ cần đặt hai biến này nếu muốn dùng; hai trang Movies không cần cấu hình email.

## Build hoặc mở bằng Eclipse

- Bản đầy đủ có sẵn `dist/MovieWeb.war`; chỉ xem/chạy thì không cần build lại.
- Khi sửa code: chạy `build.cmd`, sau đó dừng cửa sổ chạy cũ và chạy lại `run.cmd`.
- Dùng Tomcat riêng: `powershell -ExecutionPolicy Bypass -File .\build.ps1 -TomcatHome "C:\Tools\apache-tomcat-9"`.
- Có `pom.xml` để dùng Maven: `mvn clean package` sẽ tạo `target/MovieWeb.war`. Nếu dùng bộ chạy kèm, chép WAR đó sang `dist/MovieWeb.war` trước khi chạy. Maven cần mạng ở lần tải thư viện đầu.
- Eclipse: **File → Import → Existing Projects into Workspace**, chọn thư mục dự án; dùng JDK 21, Tomcat 9. Tên/context Eclipse gốc là `Endterms`, nên URL thường là `http://localhost:8080/Endterms/movies`. Khai báo VM arguments ở phần cấu hình bên trên. Không dùng Tomcat 10/11 trực tiếp vì dự án dùng `javax.servlet`.

## Cấu trúc phần bổ sung

| Phần | Tệp chính |
| --- | --- |
| SQL | `DAO/MovieCatalogDAO.java` |
| Logic | `service/MovieService.java` |
| Bộ điều khiển | `servlet/MoviesServlet.java`, `MovieDetailsServlet.java`, `MovieFavouriteServlet.java` |
| Dữ liệu màn hình | `model/MovieFilter.java`, `MovieCatalog.java`, `MovieDetails.java`, `ShowtimeView.java` |
| Giao diện | `WEB-INF/views/movies.jsp`, `movie_details.jsp`, `movie-error.jsp` |
| Kết nối | `util/DBConnection.java`, `config/database.example.properties` |
| Chạy và kiểm tra | `run.cmd`, `setup-database.cmd`, `check-database.cmd`, `build.cmd`, `test.cmd` |

Xem `KIEM-THU.md` và `tests/README.md` để biết phạm vi kiểm thử và giới hạn xác minh.

## Nguồn thư viện và tài liệu

- [Apache Tomcat 9](https://tomcat.apache.org/download-90.cgi)
- [Microsoft JDBC Driver – Windows Authentication](https://learn.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url)
- [Microsoft JDBC 13.4.0](https://github.com/microsoft/mssql-jdbc/releases/tag/v13.4.0)
- [Microsoft JDBC – giới hạn LocalDB](https://github.com/microsoft/mssql-jdbc/issues/769)

Giấy phép Tomcat được giữ trong `runtime/tomcat/LICENSE` và `NOTICE`; giấy phép Microsoft JDBC được kèm ở `runtime/native/LICENSE-mssql.txt`.
