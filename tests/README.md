# Kiểm thử tính năng Movies

Bộ kiểm thử này chạy DAO và service với **H2 2.3.232 ở chế độ MSSQLServer**, dùng dữ liệu tạo riêng trong bộ nhớ. Không đọc hoặc thay đổi database SQL Server hay file `MovieWeb.bak` của bạn. H2 chỉ phục vụ kiểm thử; ứng dụng thật vẫn dùng SQL Server.

Yêu cầu: JDK 21 và Tomcat 9. Bản đầy đủ đã có Tomcat ở `runtime/tomcat`. Với bản đưa lên GitHub, truyền thư mục Tomcat của bạn qua `-TomcatHome`.

Tải [H2 2.3.232 từ Maven Central](https://repo.maven.apache.org/maven2/com/h2database/h2/2.3.232/h2-2.3.232.jar), sau đó chạy trong thư mục dự án:

```powershell
.\test.cmd -H2Jar "C:\Downloads\h2-2.3.232.jar"
```

Nếu dùng Tomcat và JDK đặt tại thư mục khác:

```powershell
.\test.cmd -H2Jar "C:\Downloads\h2-2.3.232.jar" -TomcatHome "C:\Tools\apache-tomcat-9" -JavaHome "C:\Program Files\Java\jdk-21"
```

Các nhóm được kiểm tra:

- Tham số, ngày tháng, mã phim, bộ lọc trùng, phân trang và thứ tự sắp xếp.
- Truy vấn SQL thật cho danh sách, tìm theo tên phim/diễn viên/đạo diễn/tác giả và các bộ lọc kết hợp.
- Ẩn phim bị xóa/ngừng hoạt động; chỉ hiển thị suất chiếu tương lai còn hoạt động.
- Quan hệ thể loại, nhãn, người tham gia; danh sách phổ biến theo số yêu thích.
- Thêm/xóa yêu thích, thêm nhiều lần không sinh bản ghi trùng.
- Phản hồi servlet 200/400/401/403/404/503, CSRF, chuyển hướng sau POST và thông báo lỗi không lộ thông tin kết nối.
- Escape HTML, URL trailer và đường dẫn ảnh.

Kiểm thử H2 không thay thế bước restore và kiểm tra kết nối trên SQL Server thật. Không đưa file H2 vào `src/main/webapp/WEB-INF/lib`.
