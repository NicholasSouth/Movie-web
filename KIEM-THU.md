# Kết quả kiểm thử

Kiểm tra trên JDK 21.0.12.1, Apache Tomcat 9.0.122; không sửa database/tệp nguồn bạn gửi.

## Đã thực hiện

- Biên dịch toàn bộ Java và tạo WAR từ bản mã nguồn cuối.
- 99 kiểm tra tự động đạt: bộ lọc, tham số/ngày/mã phim, SQL tìm kiếm và quan hệ, phân trang, phim ẩn/xóa, lịch chiếu, thêm/xóa yêu thích idempotent, CSRF, URL và escape HTML, xử lý mã HTTP. SQL được chạy trên H2 2.3.232 với MODE=MSSQLServer và dữ liệu kiểm thử riêng.
- 35 kiểm tra qua HTTP trên Tomcat thật đạt: ba JSP chính và các JSP forward/grid được biên dịch; trang danh sách/chi tiết, tìm kiếm/lọc/phân trang, trạng thái rỗng, URL cũ và alias, cookie đăng nhập/CSRF, POST yêu thích → chuyển hướng → đọc lại dữ liệu. Tomcat thử nghiệm dùng dữ liệu H2 trong bộ nhớ và đã được dừng. Không đưa bộ dữ liệu hay đường đăng nhập thử vào bản giao.
- 6 kiểm tra bổ sung trên WAR cấu hình SQL Server: mã phim sai/URL cũ/bộ lọc sai trả 400; SQL Server không kết nối được trả 503 qua JSP lỗi; CSS và ảnh dự phòng trả 200.
- Các script PowerShell được kiểm tra cú pháp. Tomcat đã khởi động thành công qua script run.ps1; dùng connector NIO2 tương thích môi trường Windows đang kiểm tra.
- Đã xem ảnh render trang danh sách và chi tiết ở độ rộng 1440px bằng Edge; không tràn ngang, chữ và các vùng thông tin hiển thị rõ. Ảnh sử dụng dữ liệu kiểm thử riêng.

## Chưa xác minh trên máy bạn

- Chưa restore thành công tệp MovieWeb.bak hoặc kết nối SQL Server thật bằng Windows Authentication. Tại thời điểm kiểm tra, localhost:1433 từ chối kết nối; chưa có tên instance/cổng đã xác nhận. Vì vậy không thể khẳng định dữ liệu thực tế đã hiển thị trên máy bạn ngay sau giải nén.
- Script setup-database.ps1 đã kiểm tra cú pháp nhưng chưa chạy nhánh restore trên SQL Server thật. Nó kiểm tra database đã tồn tại và không dùng WITH REPLACE. Nếu không đủ quyền đọc backup/restore, dùng SSMS theo README.
- Bộ kiểm tra tự động không chứng minh quyền đăng nhập Windows, quyền SQL, collation, hành vi khóa đồng thời của SQL Server hay độ khớp hoàn toàn của backup. Sau restore, chạy check-database.cmd để kiểm tra kết nối và truy vấn thật.
- Trong sandbox kiểm tra, javac trả 0 và tạo các class/WAR, nhưng in chẩn đoán AccessDeniedException khi đóng JAR vì thao tác chuẩn hóa đường dẫn của Java bị hạn chế. Các class đã được thực thi thành công trong 99 kiểm tra và trên Tomcat. Đây được ghi nhận là giới hạn môi trường build, không bỏ qua lỗi cú pháp Java.
- Maven pom được cung cấp, chưa chạy Maven vì máy không có Maven. Các tính năng ngoài Movies/Movie Details không nằm trong đợt kiểm thử này.

## Cập nhật ngày 18/09/2026

- Người dùng đã cung cấp ảnh SSMS xác nhận restore MovieWeb thành công trên localhost,1433.
- Đã sửa lỗi `Keyword not supported: 'DataSource'` trong setup-database.ps1: dùng tên khóa SQL chuẩn cho cả sáu thiết lập kết nối. Kiểm tra khởi tạo kết nối với hai cặp máy chủ/cổng đều đạt trên Windows PowerShell 5.1 và PowerShell 7; vẫn giữ Windows Authentication, mã hóa và thời gian chờ.
- Khi thử kết nối SQL thật từ phiên chạy công cụ, bước khởi tạo đã qua nhưng đăng nhập gặp `Cannot generate SSPI context`. Do đó chưa xác nhận toàn bộ setup chạy thành công dưới tài khoản Windows của người dùng; cần chạy lại setup-database.cmd từ cửa sổ Windows của người dùng. Không thực hiện restore trong lần kiểm tra này.

## Chạy lại

Xem tests/README.md. H2 chỉ dành cho kiểm thử và không có trong WEB-INF/lib của ứng dụng thật. Bản chạy dùng Microsoft SQL Server JDBC 13.4.0 cùng DLL Windows x64.
