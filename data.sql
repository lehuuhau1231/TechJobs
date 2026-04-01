-- Xóa bảng nếu tồn tại (theo thứ tự tránh lỗi ràng buộc FK)
DROP TABLE IF EXISTS job_alert;
DROP TABLE IF EXISTS application;
DROP TABLE IF EXISTS foreign_language;
DROP TABLE IF EXISTS job_skill;
DROP TABLE IF EXISTS candidate_skill;
DROP TABLE IF EXISTS bill;
DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS district;
DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS job_level;
DROP TABLE IF EXISTS job_type;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS chat_session;
DROP TABLE IF EXISTS candidate;
DROP TABLE IF EXISTS cv_profile;
DROP TABLE IF EXISTS contract_type;
DROP TABLE IF EXISTS company_image;
DROP TABLE IF EXISTS employer;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS language;
DROP TABLE IF EXISTS level_language;
DROP TABLE IF EXISTS it_careers;

-- 1. Bảng LevelLanguage
CREATE TABLE level_language (
	id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL
);

-- 2. Bảng Language
CREATE TABLE language (
   id INT PRIMARY KEY AUTO_INCREMENT,
   name VARCHAR(50) NOT NULL
);

-- 3. Bảng Skill
CREATE TABLE skill (
   id INT PRIMARY KEY AUTO_INCREMENT,
   name VARCHAR(100) NOT NULL
);

-- 4. Bảng User
CREATE TABLE user (
	id INT PRIMARY KEY AUTO_INCREMENT,
	password VARCHAR(255) NOT NULL,
	avatar VARCHAR(255),
	email VARCHAR(100) UNIQUE NOT NULL,
	phone VARCHAR(20),
	address VARCHAR(255),
	district VARCHAR(100),
	city VARCHAR(100),
    role ENUM('CANDIDATE', 'EMPLOYER','ADMIN') NOT NULL
);

-- 4. Insert User (CANDIDATE)
INSERT INTO user VALUES 
(1, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'candidate1@gmail.com', '0123456789','phường 13', 'Quận 1', 'Ho Chi Minh','CANDIDATE'),
(2, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'candidate2@gmail.com', '0123456790','phường 15', 'Quận 5', 'Ho Chi Minh','CANDIDATE'),
(3, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'employer1@gmail.com', '0987654321','phường 13', 'Hanoi', 'Cau Giay','EMPLOYER'),
(4, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'employer2@gmail.com', '0987654391','phường 13', 'Hanoi', 'Cau Giay','EMPLOYER'),
(5, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'admin@gmail.com', '0987654322','phường 13', 'District 3', 'Cau Giay','ADMIN');


CREATE TABLE cv_profile (
   id INT PRIMARY KEY AUTO_INCREMENT,
   title TEXT,
   skills TEXT,
   education TEXT,
   major TEXT,
   experience TEXT,
   preferred_location TEXT,
   raw_text TEXT
);

-- 5. Bảng Candidate
CREATE TABLE candidate (
   id INT PRIMARY KEY AUTO_INCREMENT,
   full_name VARCHAR(50),
   self_description TEXT,
   birth_date DATE,
   cv VARCHAR(255),
   user_id INT UNIQUE,
   cv_profile_id INT UNIQUE,
   CONSTRAINT fk_candidate_user FOREIGN KEY (user_id) REFERENCES user(id),
   FOREIGN KEY (cv_profile_id) REFERENCES cv_profile(id) 
);

-- 2. Tạo Bảng Conversations (Cuộc hội thoại)
CREATE TABLE chat_session (
    id INT PRIMARY KEY AUTO_INCREMENT,
    candidate_id INT NOT NULL, -- ID của user từ hệ thống Auth
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (candidate_id) REFERENCES candidate(id) 
);

-- 3. Tạo Bảng Messages (Chi tiết tin nhắn)
CREATE TABLE message (
    id INT PRIMARY KEY AUTO_INCREMENT,
    chat_session_id INT NOT NULL,
    content TEXT NOT NULL,	
    created_at TIMESTAMP,
    sender ENUM("USER", "ASSISTANT"),
    
    -- Khóa ngoại liên kết tới bảng conversations
        FOREIGN KEY(chat_session_id) 
        REFERENCES chat_session(id) 
        ON DELETE CASCADE
);

-- 7. Insert Candidate
INSERT INTO candidate VALUES 
(1, 'Le Huu Hau', 'I am a Java developer with 3 years of experience', '1995-01-01', 'https://res.cloudinary.com/dndsrbf9s/image/upload/v1757130130/b55p56xfeshntstkd1iw.pdf', 1, null),
(2, 'Dang Van Binh', 'I am a frontend developer passionate about React', '1995-01-01', null, 2, null);


-- 6. Bảng Employer
CREATE TABLE employer (
  id INT PRIMARY KEY AUTO_INCREMENT,
  company_name VARCHAR(255) NOT NULL,
  tax_code VARCHAR(14) NOT NULL,
  status ENUM('PENDING', 'APPROVED','CANCELED') NOT NULL,
  user_id INT UNIQUE,
  FOREIGN KEY (user_id) REFERENCES user(id) 
);

-- 8. Insert Employer
INSERT INTO employer VALUES 
(1, 'Cong ty ABC', 'AB198293CB', 'APPROVED', 3),
(2, 'Cong ty X', 'AB198293CB', 'APPROVED', 4);

CREATE TABLE company_image (
  id INT PRIMARY KEY AUTO_INCREMENT,
  image VARCHAR(255),
  employer_id INT,
  FOREIGN KEY (employer_id) REFERENCES employer(id) 
);

CREATE TABLE city (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30)
);

INSERT INTO city VALUES 
(1, 'Hồ Chí Minh'),
(2, 'Hà Nội'),
(3, 'Đà Nẵng');

CREATE TABLE job_level (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30)
);

INSERT INTO job_level VALUES 
(1, 'Intern'),
(2, 'Fresher'),
(3, 'Junior'),
(4, 'Middle'),
(5, 'Senior'),
(6, 'Trưởng Nhóm'),
(7, 'Trưởng phòng');

CREATE TABLE job_type (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30)
);

INSERT INTO job_type VALUES 
(1, 'In Office'),
(2, 'Hybrid'),
(3, 'Remote');

CREATE TABLE contract_type (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30)
);

INSERT INTO contract_type VALUES 
(1, 'Fulltime'),
(2, 'Part-time'),
(3, 'Freelance');

CREATE TABLE district (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30),
  city_id INT,
  FOREIGN KEY (city_id) REFERENCES city(id)
);

INSERT INTO district VALUES 
(1, 'Quận 1', 1),
(2, 'Thành phố Thủ Đức', 1),
(3, 'Quận 3', 1),
(4, 'Quận 4', 1),
(5, 'Quận 5', 1),
(6, 'Quận 6', 1),
(7, 'Quận 7', 1),
(8, 'Quận 8', 1),
(9, 'Quận 10', 1),
(10, 'Quận 11', 1),
(11, 'Quận 12', 1),
(12, 'Quận Bình Tân', 1),
(13, 'Quận Bình Thạnh', 1),
(14, 'Quận Gò Vấp', 1),
(15, 'Quận Phú Nhuận', 1),
(16, 'Quận Tân Bình', 1),
(17, 'Quận Tân Phú', 1),
(18, 'Huyện Bình Chánh', 1),
(19, 'Huyện Cần Giờ', 1),
(20, 'Huyện Củ Chi', 1),
(21, 'Huyện Hóc Môn', 1),
(22, 'Huyện Nhà Bè', 1),
(23, 'Quận Ba Đình', 2),
(24, 'Quận Hoàn Kiếm', 2),
(25, 'Quận Tây Hồ', 2),
(26, 'Quận Long Biên', 2),
(27, 'Quận Cầu Giấy', 2),
(28, 'Quận Đống Đa', 2),
(29, 'Quận Hai Bà Trưng', 2),
(30, 'Quận Hoàng Mai', 2),
(31, 'Quận Thanh Xuân', 2),
(32, 'Quận Nam Từ Liêm', 2),
(33, 'Quận Bắc Từ Liêm', 2),
(34, 'Quận Hà Đông', 2),
(35, 'Thị xã Sơn Tây', 2),
(36, 'Huyện Ba Vì', 2),
(37, 'Huyện Chương Mỹ', 2),
(38, 'Huyện Đan Phượng', 2),
(39, 'Huyện Đông Anh', 2),
(40, 'Huyện Gia Lâm', 2),
(41, 'Huyện Hoài Đức', 2),
(42, 'Huyện Mê Linh', 2),
(43, 'Huyện Mỹ Đức', 2),
(44, 'Huyện Phú Xuyên', 2),
(45, 'Huyện Phúc Thọ', 2),
(46, 'Huyện Quốc Oai', 2),
(47, 'Huyện Sóc Sơn', 2),
(48, 'Huyện Thạch Thất', 2),
(49, 'Huyện Thanh Oai', 2),
(50, 'Huyện Thanh Trì', 2),
(51, 'Huyện Thường Tín', 2),
(52, 'Huyện Ứng Hòa', 2),
(53, 'Quận Hải Châu', 3),
(54, 'Quận Cẩm Lệ', 3),
(55, 'Quận Thanh Khê', 3),
(56, 'Quận Liên Chiểu', 3),
(57, 'Quận Ngũ Hành Sơn', 3),
(58, 'Quận Sơn Trà', 3),
(59, 'Huyện Hòa Vang', 3),
(60, 'Huyện Hoàng Sa', 3);

CREATE TABLE job (

 id INT PRIMARY KEY AUTO_INCREMENT,

 title VARCHAR(255) NOT NULL,

 description TEXT NOT NULL,

 salary_min DECIMAL(15,2) NOT NULL,

 salary_max DECIMAL(15,2) NOT NULL,

 job_require TEXT NOT NULL,

 benefits TEXT NOT NULL,

 status ENUM('PENDING', 'APPROVED','CANCELED') NOT NULL,

 created_date TIMESTAMP NOT NULL,

 updated_at TIMESTAMP,

 vector_updated_at TIMESTAMP,

 posted_date TIMESTAMP NULL,

 address VARCHAR(200) NOT NULL,

 age_from INT NOT NULL,

 age_to INT NOT NULL,

 start_date DATE NOT NULL,

 end_date DATE NOT NULL,

 start_time TIME NOT NULL,

 end_time TIME NOT NULL,

 employer_id INT,

 city_id INT,

 district_id INT,

 job_level_id INT,

 job_type_id INT,

 contract_type_id INT,

 FOREIGN KEY (employer_id) REFERENCES employer(id),

 FOREIGN KEY (city_id) REFERENCES city(id),

 FOREIGN KEY (district_id) REFERENCES district(id),

 FOREIGN KEY (job_level_id) REFERENCES job_level(id),

 FOREIGN KEY (job_type_id) REFERENCES job_type(id),

 FOREIGN KEY (contract_type_id) REFERENCES contract_type(id)

);

-- 7. Bảng Job
INSERT INTO job (
    title, description, salary_min, salary_max, job_require, benefits, 
    status, created_date, updated_at, vector_updated_at, posted_date, 
    address, age_from, age_to, start_date, end_date, start_time, 
    end_time, employer_id, city_id, district_id, job_level_id, job_type_id, contract_type_id
) VALUES 
('Senior Java Backend Developer (Spring Cloud)', 
'- Thiết kế và phát triển hệ thống Microservices cho nền tảng ngân hàng số.\n- Tối ưu hóa hiệu năng hệ thống xử lý giao dịch thời gian thực quy mô lớn.\n- Tham gia vào quá trình Code Review và hướng dẫn các thành viên Junior.\n- Phối hợp với team DevOps để triển khai hệ thống lên môi trường Kubernetes.\n- Nghiên cứu và áp dụng các công nghệ mới như Kafka, Redis vào sản phẩm.', 
35000000, 60000000, 
'- Ít nhất 5 năm kinh nghiệm làm việc chuyên sâu với Java và Spring Boot.\n- Hiểu biết sâu về Microservices architecture và RESTful API design.\n- Kinh nghiệm thực chiến với cơ sở dữ liệu PostgreSQL và NoSQL (MongoDB).\n- Thành thạo các công cụ CI/CD như Jenkins, GitLab CI và Docker.\n- Kỹ năng giải quyết vấn đề tốt và tư duy logic hệ thống nhạy bén.', 
'- Mức lương cạnh tranh và gói bonus cuối năm hấp dẫn lên đến 4 tháng lương.\n- Bảo hiểm sức khỏe PVI cao cấp dành cho nhân viên và người thân.\n- Review lương định kỳ 2 lần mỗi năm dựa trên KPI và năng lực.\n- Cấp máy tính Macbook Pro đời mới nhất kèm 2 màn hình 4K hỗ trợ.\n- Tham gia các khóa đào tạo chuyên sâu và thi chứng chỉ AWS/Java miễn phí.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 08:00:00', 'Tòa nhà TechnoPark, Gia Lâm, Hà Nội', 25, 40, '2026-04-10', '2026-07-10', '08:30:00', '17:30:00', 1, 1, 12, 3, 1, 1),

-- 2. ReactJS Middle
('Middle Frontend Developer (ReactJS/Next.js)', 
'- Phát triển giao diện người dùng cho nền tảng thương mại điện tử đa quốc gia.\n- Chuyển đổi thiết kế từ Figma sang mã nguồn React đảm bảo pixel-perfect.\n- Tối ưu hóa tốc độ tải trang và trải nghiệm người dùng (Core Web Vitals).\n- Xây dựng hệ thống Design System dùng chung cho toàn bộ hệ sinh thái.\n- Làm việc chặt chẽ với Backend team để tích hợp API và xử lý dữ liệu.', 
20000000, 35000000, 
'- Có từ 2-4 năm kinh nghiệm lập trình Frontend chuyên sâu với ReactJS.\n- Thành thạo HTML5, CSS3, JavaScript (ES6+) và đặc biệt là TypeScript.\n- Kinh nghiệm sử dụng các thư viện quản lý State như Redux Toolkit hoặc Zustand.\n- Hiểu biết về SEO, Server-side Rendering (Next.js) là một lợi thế lớn.\n- Khả năng làm việc nhóm tốt và chịu được áp lực cao trong môi trường Agile.', 
'- Lương tháng 13 và thưởng các ngày lễ tết lớn trong năm (2/9, 30/4).\n- Môi trường làm việc trẻ trung, văn phòng hạng A với đầy đủ tiện nghi.\n- Được hưởng 15 ngày phép năm và các ngày nghỉ lễ theo quy định nhà nước.\n- Chế độ làm việc Hybrid linh hoạt (3 ngày văn phòng, 2 ngày remote).\n- Teambuilding hàng quý và du lịch công ty tại các resort 5 sao hàng năm.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 09:00:00', 'Số 1 Đinh Tiên Hoàng, Quận 1, TP.HCM', 22, 32, '2026-04-15', '2026-07-15', '09:00:00', '18:00:00', 1, 2, 1, 2, 1, 1),

-- 3. Golang Specialist
('Golang Engineer (High Load System)', 
'- Phát triển các dịch vụ backend hiệu năng cao bằng ngôn ngữ Go.\n- Thiết kế kiến trúc hệ thống phân tán, xử lý hàng tỷ request mỗi ngày.\n- Tối ưu hóa concurrency, memory management cho các dịch vụ cốt lõi.\n- Xây dựng và duy trì hệ thống Message Queue sử dụng RabbitMQ hoặc Kafka.\n- Hỗ trợ team vận hành trong việc giám sát và khắc phục sự cố hệ thống.', 
30000000, 55000000, 
'- Ít nhất 3 năm kinh nghiệm làm việc với Golang trong các dự án thực tế.\n- Hiểu rõ về Goroutine, Channel và các cơ chế đồng bộ hóa dữ liệu.\n- Kinh nghiệm làm việc với Microservices, gRPC và Protocol Buffers.\n- Thành thạo SQL (PostgreSQL) và các hệ thống caching như Redis.\n- Kỹ năng debug và tối ưu hóa performance hệ thống ở mức độ sâu.', 
'- Thu nhập hấp dẫn, thỏa thuận theo năng lực (Net hoặc Gross tùy chọn).\n- Được cấp tài khoản học tập trên các nền tảng Udemy, Coursera miễn phí.\n- Phụ cấp ăn trưa, gửi xe và hỗ trợ chi phí mua sắm thiết bị cá nhân.\n- Cơ hội làm việc trực tiếp với các chuyên gia công nghệ từ Singapore.\n- Gói bảo hiểm sức khỏe toàn diện và kiểm tra sức khỏe định kỳ hàng năm.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 09:30:00', 'Etown Cộng Hòa, Tân Bình, TP.HCM', 24, 38, '2026-04-20', '2026-10-20', '08:30:00', '17:30:00', 1, 2, 6, 2, 1, 1),

-- 4. .NET Core
('C#/.NET Core Developer (Fintech Project)', 
'- Xây dựng và bảo trì các ứng dụng nghiệp vụ ngân hàng trên nền .NET Core.\n- Phát triển các dịch vụ Web API chuẩn REST để tích hợp với các đối tác.\n- Thiết kế Database Schema và tối ưu hóa các Store Procedure phức tạp.\n- Thực hiện Unit Test và Integration Test để đảm bảo chất lượng phần mềm.\n- Tham gia vào các buổi họp kỹ thuật để đóng góp ý kiến về kiến trúc.', 
18000000, 32000000, 
'- Có kinh nghiệm từ 2 năm trở lên với .NET Core (phiên bản 6.0 trở lên).\n- Thành thạo Entity Framework Core, LINQ và kỹ thuật lập trình hướng đối tượng.\n- Am hiểu về SQL Server, có khả năng viết và tối ưu hóa các câu lệnh SQL.\n- Hiểu biết về Design Patterns và các nguyên tắc SOLID trong lập trình.\n- Tiếng Anh chuyên ngành tốt, có thể giao tiếp cơ bản trong công việc.', 
'- Lương Net 100%, công ty đóng đầy đủ các loại bảo hiểm theo lương thực tế.\n- Thưởng dự án theo từng giai đoạn release và thưởng hiệu quả cuối năm.\n- Snack bar phục vụ miễn phí trà, cafe, bánh kẹo tại khu vực làm việc.\n- Môi trường ổn định, ít OT, cân bằng tốt giữa công việc và cuộc sống.\n- Được tham gia các khóa đào tạo nội bộ về nghiệp vụ tài chính, ngân hàng.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 10:00:00', 'Tòa nhà Charmvit, Trần Duy Hưng, Hà Nội', 23, 35, '2026-04-15', '2026-10-15', '08:00:00', '17:00:00', 1, 1, 1, 2, 1, 1),

-- 5. Node.js Middle
('Middle Node.js Developer (NestJS focus)', 
'- Phát triển các ứng dụng Server-side sử dụng framework NestJS.\n- Thiết kế kiến trúc hướng sự kiện (Event-driven) với RabbitMQ.\n- Xây dựng hệ thống quản lý định danh và phân quyền (IAM) cho người dùng.\n- Đảm bảo tính bảo mật và toàn vẹn dữ liệu cho các giao dịch trực tuyến.\n- Hợp tác với team Mobile để cung cấp API mượt mà cho app iOS/Android.', 
22000000, 40000000, 
'- Tối thiểu 3 năm kinh nghiệm lập trình Backend với Node.js/TypeScript.\n- Thành thạo NestJS, TypeORM và các cơ sở dữ liệu SQL/NoSQL.\n- Hiểu biết về kiến trúc Clean Architecture và cách tổ chức code khoa học.\n- Kinh nghiệm triển khai ứng dụng trên Docker và AWS (EC2, S3, Lambda).\n- Có tinh thần học hỏi cao, sẵn sàng nghiên cứu các công nghệ mới.', 
'- Cấp Macbook Pro hoặc laptop gaming tùy theo sở thích cá nhân.\n- Gói khám sức khỏe hàng năm tại bệnh viện quốc tế (Vinmec/Hạnh Phúc).\n- Thưởng sinh nhật, thưởng các ngày lễ lớn và trợ cấp hiếu hỉ, ốm đau.\n- Tham gia các câu lạc bộ thể thao và văn nghệ do công ty tài trợ 100%.\n- Không gian làm việc mở, sáng tạo, không gò bó về đồng phục hay thời gian.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 10:30:00', 'Số 33 Ung Văn Khiêm, Bình Thạnh, TP.HCM', 23, 33, '2026-05-01', '2026-08-01', '09:00:00', '18:00:00', 1, 2, 5, 2, 1, 1),

-- 6. PHP Laravel
('PHP Senior Developer (Laravel/Symphony)', 
'- Dẫn dắt team phát triển các sản phẩm SaaS cho thị trường quốc tế.\n- Thiết kế hệ thống quản lý nội dung lớn với cấu trúc phức tạp.\n- Tối ưu hóa Database và hệ thống Caching để xử lý lượng người dùng lớn.\n- Review code và hướng dẫn các bạn Junior cải thiện kỹ năng lập trình.\n- Nghiên cứu các giải pháp bảo mật web và ngăn chặn các đợt tấn công.', 
25000000, 45000000, 
'- Trên 5 năm kinh nghiệm lập trình PHP, ưu tiên chuyên sâu về Laravel.\n- Thành thạo HTML/CSS, JavaScript và một trong các JS Framework (Vue/React).\n- Kinh nghiệm làm việc với Redis, Memcached và công cụ search (Elasticsearch).\n- Hiểu biết sâu về Design Patterns và quy trình phát triển phần mềm Agile.\n- Khả năng đọc hiểu tài liệu tiếng Anh tốt, giao tiếp được là điểm cộng.', 
'- Mức lương thỏa thuận dựa trên năng lực, xứng đáng với đóng góp.\n- Thưởng lương tháng 13 và thưởng hiệu quả dự án định kỳ hàng quý.\n- Nghỉ phép 14 ngày/năm và hưởng đầy đủ bảo hiểm theo luật định.\n- Miễn phí cơm trưa tại văn phòng với thực đơn thay đổi hàng ngày.\n- Quà tặng vào các dịp đặc biệt: Trung thu, Tết, Ngày phụ nữ (cho nữ NV).', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 11:00:00', 'Tòa nhà Petrolimex, Khâm Thiên, Hà Nội', 26, 40, '2026-04-10', '2026-07-10', '08:30:00', '17:30:00', 1, 1, 3, 3, 1, 1),

-- 7. Python Engineer
('Python Engineer (Django & Automation)', 
'- Xây dựng các hệ thống quản lý và tự động hóa quy trình nội bộ.\n- Phát triển các dịch vụ thu thập và xử lý dữ liệu từ nhiều nguồn khác nhau.\n- Xây dựng API phục vụ cho các ứng dụng web và di động của công ty.\n- Tích hợp các công nghệ Machine Learning vào quy trình xử lý dữ liệu.\n- Tham gia vào việc bảo trì và nâng cấp các hệ thống Python hiện có.', 
20000000, 38000000, 
'- Có ít nhất 3 năm kinh nghiệm lập trình Python chuyên nghiệp.\n- Thành thạo Django hoặc FastAPI và các thư viện xử lý dữ liệu (Pandas).\n- Kinh nghiệm làm việc với cơ sở dữ liệu PostgreSQL và kỹ năng SQL tốt.\n- Am hiểu về Docker, Kubernetes và các dịch vụ Cloud (AWS/GCP).\n- Có tư duy logic tốt, khả năng tự học và nghiên cứu tài liệu mới nhanh.', 
'- Lương Net, bảo hiểm đóng trên mức lương thực nhận hàng tháng.\n- Teambuilding, du lịch hàng năm cùng công ty tại các điểm nổi tiếng.\n- Hỗ trợ chi phí gửi xe, ăn trưa và phụ cấp điện thoại hàng tháng.\n- Môi trường làm việc thân thiện, hòa đồng, giúp đỡ nhau cùng tiến bộ.\n- Cơ hội thăng tiến lên các vị trí quản lý kỹ thuật sau 1-2 năm gắn bó.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 11:30:00', 'Khu Công nghệ cao Láng Hòa Lạc, Hà Nội', 24, 35, '2026-05-15', '2026-11-15', '08:00:00', '17:00:00', 1, 1, 9, 2, 1, 1),

-- 8. Fullstack Java/React
('Fullstack Developer (Java Spring & ReactJS)', 
'- Tham gia vào toàn bộ vòng đời phát triển dự án từ Frontend đến Backend.\n- Xây dựng giao diện web hiện đại với React và hệ thống API với Spring.\n- Thiết kế cơ sở dữ liệu đảm bảo hiệu năng và khả năng mở rộng hệ thống.\n- Viết Unit Test và Integration Test để đảm bảo chất lượng mã nguồn.\n- Thực hiện deploy ứng dụng lên môi trường production và theo dõi vận hành.', 
25000000, 42000000, 
'- Thành thạo cả Java/Spring Boot (Backend) và ReactJS/TypeScript (Frontend).\n- Hiểu rõ về luồng dữ liệu giữa Client và Server qua REST hoặc GraphQL.\n- Kinh nghiệm làm việc với Git, Maven/Gradle và các công cụ đóng gói Docker.\n- Biết sử dụng các thư viện UI như Material UI, Ant Design hoặc Tailwind.\n- Khả năng làm việc độc lập cũng như phối hợp nhóm cực kỳ hiệu quả.', 
'- Thưởng quý dựa trên năng suất và đóng góp cá nhân cho các dự án.\n- Khám sức khỏe định kỳ hàng năm tại bệnh viện quốc tế uy tín.\n- Trà, cafe, trái cây và đồ ăn nhẹ miễn phí phục vụ tại văn phòng.\n- Tham gia các lớp học kỹ năng mềm và đào tạo chuyên môn định kỳ.\n- Văn phòng thiết kế hiện đại, có khu vực nghỉ ngơi và giải trí riêng.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 13:00:00', 'Số 115 Nguyễn Huệ, Quận 1, TP.HCM', 24, 35, '2026-04-10', '2026-10-10', '09:00:00', '18:00:00', 1, 2, 1, 2, 1, 1),

-- 9. Ruby on Rails
('Senior Ruby on Rails Developer', 
'- Phát triển các sản phẩm web quy mô lớn cho khách hàng Nhật Bản.\n- Thiết kế kiến trúc và tối ưu hóa hiệu năng cho các ứng dụng Ruby.\n- Xây dựng các module nghiệp vụ phức tạp liên quan đến tài chính, quản lý.\n- Hướng dẫn và chia sẻ kiến thức cho các thành viên trong đội ngũ phát triển.\n- Phối hợp chặt chẽ với khách hàng để thống nhất yêu cầu và tiến độ.', 
30000000, 50000000, 
'- Trên 4 năm kinh nghiệm phát triển web với Ruby on Rails.\n- Thành thạo SQL, hiểu biết sâu về tối ưu hóa ActiveRecord và truy vấn.\n- Có kiến thức về Frontend (HTML, CSS, JS) là một lợi thế không nhỏ.\n- Kinh nghiệm làm việc với AWS và quy trình deploy tự động hóa Capistrano.\n- Tiếng Nhật trình độ N3 trở lên là một điểm cộng cực lớn về lương.', 
'- Thưởng tiếng Nhật hàng tháng cho ứng viên có chứng chỉ N3/N2/N1.\n- Cơ hội onsite làm việc lâu dài tại Tokyo, Nhật Bản (hỗ trợ visa).\n- Review lương tối thiểu 1 lần/năm với mức tăng hấp dẫn từ 10-20%.\n- Tham gia các khóa học tiếng Nhật và văn hóa Nhật miễn phí tại công ty.\n- Nghỉ lễ theo lịch Việt Nam và nghỉ thêm vào các ngày lễ lớn của Nhật.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 13:30:00', 'Tòa nhà Indochina Riverside, Hải Châu, Đà Nẵng', 25, 40, '2026-05-01', '2026-11-01', '08:00:00', '17:00:00', 1, 3, 1, 3, 1, 1),

-- 10. Rust Developer
('Rust Developer (Blockchain & Web3)', 
'- Phát triển lõi cho các hệ thống Blockchain và Smart Contract bằng Rust.\n- Xây dựng các dịch vụ backend yêu cầu hiệu năng cực cao và độ trễ thấp.\n- Thực hiện kiểm tra bảo mật mã nguồn và tối ưu hóa việc sử dụng tài nguyên.\n- Tham gia vào cộng đồng Open Source để đóng góp cho các thư viện liên quan.\n- Thiết kế và triển khai các giao thức mạng ngang hàng (P2P networking).', 
40000000, 80000000, 
'- Ít nhất 2 năm kinh nghiệm lập trình Rust chuyên sâu hoặc 4 năm C++.\n- Hiểu biết về Memory Safety, Ownership, và Concurrency trong Rust.\n- Có kiến thức về mật mã học và cấu trúc dữ liệu Blockchain cơ bản.\n- Kinh nghiệm làm việc với WebAssembly (Wasm) là một điểm cộng lớn.\n- Đam mê công nghệ mới, đặc biệt là các giải pháp phi tập trung (Decentralized).', 
'- Thu nhập hấp dẫn bằng $ (USD) hoặc quy đổi theo tỷ giá thị trường.\n- Làm việc từ xa (Remote) 100%, linh hoạt về thời gian và địa điểm.\n- Thưởng cổ phiếu dự án (Token) khi hệ thống được ra mắt chính thức.\n- Hỗ trợ chi phí mua sắm thiết bị làm việc lên đến 30 triệu đồng.\n- Tham gia các hội nghị Blockchain toàn cầu tại Dubai, Singapore, Lisbon.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 14:00:00', 'Làm việc từ xa (Remote VN)', 24, 45, '2026-04-01', '2026-12-31', '09:00:00', '18:00:00', 1, 2, 1, 3, 3, 1)
,
-- 11. Senior ReactJS
('Senior Frontend Engineer (React/Next.js Architecture)', 
'- Chịu trách nhiệm thiết kế kiến trúc Frontend cho hệ thống ERP quy mô lớn.\n- Tối ưu hóa hiệu năng render, giải quyết các bài toán về nghẽn dữ liệu phía Client.\n- Xây dựng bộ thư viện UI Kit dùng chung dựa trên chuẩn thiết kế của công ty.\n- Mentor và định hướng kỹ thuật cho các thành viên trong đội ngũ Frontend.\n- Phối hợp với team Product để đưa ra các giải pháp cải thiện trải nghiệm người dùng.', 
35000000, 65000000, 
'- Ít nhất 5 năm kinh nghiệm lập trình Frontend, 3 năm chuyên sâu với ReactJS.\n- Thành thạo các kỹ thuật tối ưu hóa performance như Code Splitting, Memoization.\n- Kinh nghiệm triển khai kiến trúc Micro-Frontend và sử dụng Module Federation.\n- Hiểu biết sâu về State Management phức tạp (Redux Saga, React Query, XState).\n- Kỹ năng tiếng Anh tốt, có thể thuyết trình các giải pháp kỹ thuật chuyên sâu.', 
'- Mức lương Net cạnh tranh kèm gói cổ phiếu thưởng (ESOP) cho nhân sự nòng cốt.\n- Chế độ bảo hiểm sức khỏe quốc tế Liberty cho cá nhân và giảm giá cho người thân.\n- Review lương 2 lần/năm với mức tăng trưởng xứng đáng theo năng lực thực tế.\n- Hỗ trợ 100% chi phí tham gia các khóa học chuyên sâu trên Frontend Masters.\n- Môi trường làm việc quốc tế, năng động, khuyến khích sự sáng tạo và đổi mới.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 14:15:00', 'Tòa nhà Viettel, Quận 10, TP.HCM', 26, 40, '2026-05-01', '2026-11-01', '09:00:00', '18:00:00', 1, 2, 4, 3, 1, 1),

-- 12. iOS Developer
('Senior iOS Developer (Swift/SwiftUI)', 
'- Phát triển và duy trì ứng dụng Mobile Banking trên nền tảng iOS.\n- Tối ưu hóa UI/UX đảm bảo sự mượt mà và tính thẩm mỹ cao nhất cho ứng dụng.\n- Tích hợp các tính năng bảo mật sinh trắc học và thanh toán thẻ vào ứng dụng.\n- Nghiên cứu và áp dụng các framework mới nhất từ Apple như SwiftUI và Combine.\n- Phối hợp với team Backend để thiết kế luồng dữ liệu API đồng bộ và hiệu quả.', 
30000000, 55000000, 
'- Tối thiểu 4 năm kinh nghiệm lập trình iOS với ngôn ngữ Swift.\n- Hiểu biết sâu về kiến trúc MVVM, Clean Swift và các Design Patterns mobile.\n- Kinh nghiệm xử lý Offline Storage, Core Data và quản lý bộ nhớ hiệu quả.\n- Thành thạo việc đưa ứng dụng lên App Store và quản lý TestFlight.\n- Khả năng tư duy logic tốt, cẩn thận và có trách nhiệm cao với sản phẩm.', 
'- Thưởng lương tháng 13 và thưởng hiệu quả kinh doanh định kỳ hàng năm.\n- Được trang bị dàn máy Mac Studio và màn hình Apple Studio Display hiện đại.\n- Gói chăm sóc sức khỏe toàn diện tại các bệnh viện hàng đầu Việt Nam.\n- Tham gia các sự kiện công nghệ lớn như WWDC (trực tuyến hoặc trực tiếp).\n- Cơ hội thăng tiến lên vị trí Mobile Lead hoặc Technical Architect.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 14:30:00', 'Số 54 Liễu Giai, Ba Đình, Hà Nội', 25, 38, '2026-04-20', '2026-10-20', '08:30:00', '17:30:00', 1, 1, 2, 3, 1, 1),

-- 13. Android Developer
('Android Developer (Kotlin/Jetpack Compose)', 
'- Tham gia phát triển siêu ứng dụng (Super App) phục vụ hàng triệu người dùng.\n- Xây dựng các module tính năng mới sử dụng Jetpack Compose hiện đại.\n- Tối ưu hóa hiệu năng app trên nhiều dòng thiết bị Android khác nhau.\n- Xử lý các bài toán về Notification, Background Service và đa nhiệm.\n- Thực hiện Unit Test và UI Automation Test để đảm bảo độ ổn định của app.', 
25000000, 45000000, 
'- Có ít nhất 3 năm kinh nghiệm lập trình Android với ngôn ngữ Kotlin.\n- Thành thạo Jetpack Compose, Coroutines, Flow và Dagger Hilt.\n- Hiểu rõ về Material Design và các nguyên tắc thiết kế giao diện mobile.\n- Kinh nghiệm làm việc với WebSockets và truyền tải dữ liệu thời gian thực.\n- Tinh thần làm việc nhóm tốt, sẵn sàng hỗ trợ đồng nghiệp trong dự án.', 
'- Thu nhập hấp dẫn, thỏa thuận dựa trên kinh nghiệm và kỹ năng thực tế.\n- Thưởng các ngày lễ lớn (Tết, 30/4, 2/9) và quà tặng các dịp sinh nhật.\n- Môi trường làm việc thoải mái, pantry luôn đầy đủ đồ ăn nhẹ và trái cây.\n- Hỗ trợ chi phí gửi xe và trợ cấp ăn trưa tại nhà hàng đối tác của công ty.\n- Được tham gia các buổi chia sẻ kiến thức (Tech-talk) hàng tuần của team.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 14:45:00', 'Số 2 Duy Tân, Cầu Giấy, Hà Nội', 24, 35, '2026-04-10', '2026-07-10', '08:30:00', '17:30:00', 1, 1, 1, 2, 1, 1),

-- 14. Flutter Specialist
('Flutter Developer (Cross-platform Specialist)', 
'- Xây dựng ứng dụng đa nền tảng iOS và Android từ một cơ sở mã nguồn duy nhất.\n- Thiết kế và triển khai các UI component phức tạp với hiệu ứng mượt mà.\n- Tối ưu hóa dung lượng file cài đặt và tốc độ khởi động của ứng dụng.\n- Tích hợp các plugin native khi cần xử lý các tính năng đặc thù phần cứng.\n- Làm việc trực tiếp với Designer để hiện thực hóa các ý tưởng giao diện đột phá.', 
22000000, 40000000, 
'- Có ít nhất 2 năm kinh nghiệm làm việc chuyên sâu với Flutter và Dart.\n- Nắm vững các State Management như BLoC, Riverpod hoặc Provider.\n- Hiểu biết về cách giao tiếp giữa Flutter và Native code (Method Channel).\n- Kinh nghiệm làm việc với Firebase (Auth, Firestore, Cloud Messaging).\n- Kỹ năng giải quyết vấn đề độc lập và khả năng quản lý thời gian tốt.', 
'- Thưởng quý dựa trên hiệu quả dự án và sự hài lòng của khách hàng.\n- Gói bảo hiểm sức khỏe PVI cao cấp dành riêng cho nhân viên công ty.\n- Chế độ nghỉ phép linh hoạt, không giới hạn số ngày nghỉ nếu hoàn thành việc.\n- Cơ hội tham gia các dự án Outsourcing cho các đối tác lớn từ Mỹ và EU.\n- Môi trường Startup trẻ trung, năng động, khuyến khích tinh thần làm chủ.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 15:00:00', 'Tòa nhà Bitexco, Quận 1, TP.HCM', 23, 35, '2026-05-15', '2026-11-15', '09:00:00', '18:00:00', 1, 2, 1, 2, 1, 1),

-- 15. React Native
('React Native Developer (Fintech App)', 
'- Phát triển ứng dụng ví điện tử tích hợp thanh toán QR và chuyển tiền.\n- Xây dựng các module Bridge để tối ưu hóa hiệu năng so với giải pháp thuần JS.\n- Thực hiện bảo mật ứng dụng mobile, chống reverse engineering và rò rỉ dữ liệu.\n- Cập nhật định kỳ các phiên bản mới của thư viện để đảm bảo tính tương thích.\n- Phối hợp với team QA để thực hiện kiểm thử trên nhiều thiết bị thực tế.', 
25000000, 48000000, 
'- Trên 3 năm kinh nghiệm lập trình Mobile, 2 năm với React Native.\n- Thành thạo JavaScript/TypeScript và hiểu sâu về cơ chế Yoga Engine.\n- Kinh nghiệm tích hợp các cổng thanh toán (VNPay, Momo) là điểm cộng lớn.\n- Hiểu biết về Redux, Saga và các thư viện quản lý luồng dữ liệu mobile.\n- Kỹ năng giao tiếp tốt, có khả năng làm việc dưới áp lực thời gian gấp.', 
'- Lương tháng 13+ và các khoản thưởng nóng khi hoàn thành các milestone.\n- Review lương định kỳ hàng năm với lộ trình thăng tiến rõ ràng.\n- Chế độ làm việc remote một phần, linh hoạt thời gian bắt đầu công việc.\n- Cấp máy tính Macbook Pro hoặc máy tính cấu hình cao tùy chọn.\n- Tham gia các hoạt động ngoại khóa, câu lạc bộ bida, bóng đá của công ty.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 15:15:00', 'Số 360 Giải Phóng, Thanh Xuân, Hà Nội', 24, 38, '2026-04-01', '2026-09-30', '08:30:00', '17:30:00', 1, 1, 4, 2, 1, 1),

-- 16. Vue.js Developer
('Middle Vue.js Developer (Vue 3 & Composition API)', 
'- Xây dựng Dashboard quản trị và hệ thống báo cáo cho nền tảng Logistics.\n- Áp dụng Vue 3 và Pinia để xây dựng kiến trúc web hiện đại và dễ bảo trì.\n- Thực hiện tối ưu hóa SEO cho các trang landing page của sản phẩm.\n- Viết Unit Test bằng Vitest để đảm bảo tính ổn định của các component.\n- Tham gia vào quá trình thiết kế hệ thống cùng với Product Owner.', 
18000000, 30000000, 
'- Có từ 2-4 năm kinh nghiệm lập trình web, 2 năm chuyên sâu với Vue.js.\n- Thành thạo Vue 3, Composition API, Vite và Tailwind CSS.\n- Hiểu biết về Server-side Rendering (Nuxt.js) và Static Site Generation.\n- Kinh nghiệm làm việc với RESTful API và các cơ chế xác thực JWT.\n- Tư duy logic tốt, cẩn thận trong việc trình bày mã nguồn và comment.', 
'- Thưởng hiệu quả công việc và các khoản trợ cấp xăng xe, điện thoại.\n- Du lịch cùng công ty 2 lần/năm (1 lần trong nước, 1 lần nước ngoài).\n- Được tài trợ chi phí mua sách và tham gia các hội thảo công nghệ.\n- Chế độ nghỉ ốm vẫn hưởng nguyên lương và các bảo hiểm đi kèm.\n- Môi trường làm việc văn minh, chuyên nghiệp, sếp tâm lý và đồng nghiệp hỗ trợ.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 15:30:00', 'Lê Đại Hành, Quận 11, TP.HCM', 22, 32, '2026-04-10', '2026-10-10', '08:30:00', '17:30:00', 1, 2, 11, 2, 1, 1),

-- 17. UI/UX Designer
('Lead UI/UX Designer (Product Design Focus)', 
'- Chịu trách nhiệm thiết kế trải nghiệm người dùng toàn diện cho bộ sản phẩm.\n- Thực hiện nghiên cứu người dùng, xây dựng User Persona và Customer Journey.\n- Thiết kế wireframe, prototype và giao diện chi tiết (High-fidelity design).\n- Xây dựng và quản lý Design System để đồng bộ hóa giao diện trên các nền tảng.\n- Phối hợp với team Dev để đảm bảo thiết kế được hiện thực hóa chính xác.', 
25000000, 50000000, 
'- Ít nhất 5 năm kinh nghiệm thiết kế UI/UX cho sản phẩm Web và App.\n- Thành thạo các công cụ thiết kế như Figma, Adobe Creative Cloud, Protopie.\n- Có kiến thức tốt về tâm lý học người dùng và các nguyên lý thiết kế web.\n- Khả năng trình bày ý tưởng thiết kế một cách thuyết phục trước đối tác.\n- Có portfolio đa dạng các sản phẩm đã được triển khai thực tế trên thị trường.', 
'- Thu nhập hấp dẫn dựa trên năng lực sáng tạo và kinh nghiệm thực chiến.\n- Được làm việc trong văn phòng thiết kế sáng tạo, trang thiết bị hiện đại.\n- Hỗ trợ chi phí mua các khóa học chuyên sâu trên Interaction Design Foundation.\n- Tham gia các giải thưởng thiết kế quốc tế dưới tên tuổi công ty.\n- Nghỉ phép thêm vào các ngày kỷ niệm đặc biệt của cá nhân và gia đình.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 15:45:00', 'Tòa nhà Bitexco, Quận 1, TP.HCM', 26, 45, '2026-05-01', '2026-11-01', '09:30:00', '18:30:00', 1, 2, 1, 4, 1, 1),

-- 18. Game Developer
('Game Developer (Unity/C# - Mobile Games)', 
'- Tham gia phát triển các dự án game mobile thể loại Hyper-casual và Mid-core.\n- Lập trình logic game, xử lý va chạm, hiệu ứng vật lý và âm thanh.\n- Tối ưu hóa hiệu năng game đảm bảo chạy mượt mà trên các thiết bị cấu hình thấp.\n- Tích hợp các SDK quảng cáo, in-app purchase và hệ thống bảng xếp hạng.\n- Phối hợp với team Game Design để cân bằng các thông số trong trò chơi.', 
20000000, 40000000, 
'- Có ít nhất 3 năm kinh nghiệm lập trình game sử dụng Unity engine.\n- Thành thạo ngôn ngữ C# và các nguyên lý lập trình hướng đối tượng.\n- Hiểu biết sâu về Shader, Particle System và tối ưu hóa tài nguyên 3D.\n- Kinh nghiệm làm việc với Git và quy trình phát triển game chuẩn Agile.\n- Đam mê chơi game và luôn cập nhật xu hướng thị trường game mobile.', 
'- Thưởng doanh thu game dựa trên chỉ số lợi nhuận thực tế của dự án.\n- Môi trường làm việc cực kỳ thoải mái, trang phục tự do và giờ giấc linh hoạt.\n- Khu vực chơi game giải trí riêng với đầy đủ máy console, VR và máy bida.\n- Teambuilding hàng tháng và các chuyến du lịch mạo hiểm cho team.\n- Hỗ trợ tham gia các hội chợ game quốc tế như GDC hoặc Tokyo Game Show.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 16:00:00', 'Tòa nhà Phan Khang, Tân Bình, TP.HCM', 22, 35, '2026-04-15', '2026-10-15', '09:00:00', '18:00:00', 1, 2, 6, 2, 1, 1),

-- 19. Angular Developer
('Senior Angular Developer (Enterprise Solution)', 
'- Phát triển các module frontend cho hệ thống quản lý tài chính doanh nghiệp.\n- Áp dụng các tính năng mới của Angular 17+ để tối ưu hóa tốc độ ứng dụng.\n- Xây dựng các form nhập liệu phức tạp với cơ chế validation đa tầng.\n- Quản lý State cho các ứng dụng lớn bằng NGRX hoặc Signals.\n- Đảm bảo chất lượng mã nguồn thông qua việc viết unit test và e2e test.', 
28000000, 50000000, 
'- Ít nhất 4 năm kinh nghiệm lập trình Frontend với Angular framework.\n- Thành thạo TypeScript, RxJS và các thư viện UI như Angular Material.\n- Am hiểu sâu về Reactive Programming và kỹ thuật tối ưu hóa ứng dụng SPA.\n- Kinh nghiệm làm việc trong các dự án quy mô lớn cho các tập đoàn đa quốc gia.\n- Tiếng Anh tốt là yêu cầu bắt buộc để trao đổi trực tiếp với khách hàng.', 
'- Mức lương Net cao kèm thưởng hiệu quả công việc cuối năm lên tới 5 tháng.\n- Tham gia các dự án lớn có tác động trực tiếp đến hàng nghìn người dùng.\n- Gói bảo hiểm sức khỏe toàn diện và hỗ trợ chi phí tập gym/yoga hàng tháng.\n- Cơ hội làm việc tại văn phòng của công ty ở châu Âu trong vòng 3-6 tháng.\n- Chế độ phúc lợi cực tốt, quà tặng đầy đủ vào các dịp lễ tết và hiếu hỉ.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 16:15:00', 'Tòa nhà HITC, Cầu Giấy, Hà Nội', 26, 40, '2026-05-15', '2026-11-15', '08:30:00', '17:30:00', 1, 1, 1, 3, 1, 1),

-- 20. Web Designer
('Web Designer (Landing Page & Marketing Focus)', 
'- Thiết kế giao diện landing page chuyển đổi cao cho các chiến dịch marketing.\n- Sáng tạo các banner quảng cáo, email template đồng bộ với bộ nhận diện thương hiệu.\n- Cắt HTML/CSS/JS từ bản thiết kế đảm bảo độ tương thích cao trên các trình duyệt.\n- Thực hiện các thử nghiệm A/B Testing để tối ưu hóa tỷ lệ chuyển đổi giao diện.\n- Tham gia vào các buổi brainstorming để lên ý tưởng cho các chiến dịch truyền thông.', 
12000000, 22000000, 
'- Có ít nhất 2 năm kinh nghiệm thiết kế giao diện web hoặc làm UI Designer.\n- Thành thạo Photoshop, Illustrator và công cụ thiết kế UI như Figma.\n- Có kiến thức nền tảng về HTML5/CSS3 và các framework CSS như Bootstrap.\n- Hiểu biết về xu hướng thiết kế phẳng, tối giản và typography hiện đại.\n- Khả năng làm việc nhanh chóng, đảm bảo deadline gấp của các chiến dịch.', 
'- Thưởng theo hiệu quả chuyển đổi (conversion rate) của các trang thiết kế.\n- Môi trường làm việc trẻ trung, sáng tạo, không gò bó về quy tắc cứng nhắc.\n- Được đào tạo thêm các kỹ năng về Digital Marketing và SEO cơ bản.\n- Chế độ phúc lợi đầy đủ, đóng bảo hiểm trên mức lương thỏa thuận thực tế.\n- Thường xuyên tham gia các buổi workshop chia sẻ kỹ năng sáng tạo nội bộ.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 16:30:00', 'Số 257 Giải Phóng, Đống Đa, Hà Nội', 21, 30, '2026-04-10', '2026-07-10', '08:30:00', '17:30:00', 1, 1, 2, 2, 1, 1)
,
('Senior Data Engineer (Big Data & ETL)', 
'- Xây dựng hệ thống xử lý dữ liệu Big Data trên quy mô Petabyte phục vụ phân tích.\n- Thiết kế kiến trúc Data Warehouse và Data Lake tối ưu cho doanh nghiệp bán lẻ.\n- Phát triển các đường ống ETL tự động hóa sử dụng Apache Spark và Airflow.\n- Đảm bảo chất lượng dữ liệu (Data Quality) và tính nhất quán trên toàn hệ thống.\n- Nghiên cứu và triển khai các giải pháp lưu trữ mới như ClickHouse hoặc Iceberg.', 
35000000, 65000000, 
'- Ít nhất 5 năm kinh nghiệm làm việc với dữ liệu lớn và hệ thống phân tán.\n- Thành thạo ngôn ngữ Python, Scala hoặc Java và kỹ năng SQL nâng cao.\n- Kinh nghiệm chuyên sâu với Spark, Hadoop, Kafka và các công cụ Cloud Data.\n- Hiểu biết về kiến trúc Lambda/Kappa và các kỹ thuật xử lý dữ liệu thời gian thực.\n- Kỹ năng tư duy hệ thống cực tốt, có khả năng làm việc độc lập dưới áp lực cao.', 
'- Thu nhập hấp dẫn dựa trên năng lực thực tế, thưởng cuối năm cực kỳ xứng đáng.\n- Được đào tạo bài bản về các công nghệ dữ liệu mới nhất bởi các chuyên gia quốc tế.\n- Gói bảo hiểm sức khỏe toàn diện PVI cho nhân viên và ưu đãi cho người thân.\n- Review lương định kỳ hàng năm với lộ trình thăng tiến nghề nghiệp rõ ràng.\n- Tham gia các hội thảo công nghệ Big Data lớn trong nước và quốc tế định kỳ.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 16:45:00', 'Tòa nhà Viettel, Quận 10, TP.HCM', 26, 45, '2026-05-01', '2026-11-01', '08:30:00', '17:30:00', 1, 2, 4, 3, 1, 1),

-- 22. Machine Learning Engineer
('Machine Learning Engineer (Computer Vision Focus)', 
'- Phát triển các mô hình Computer Vision cho hệ thống định danh điện tử (eKYC).\n- Thực hiện quy trình huấn luyện, đánh giá và tối ưu hóa mô hình nhận diện khuôn mặt.\n- Triển khai mô hình AI lên môi trường sản xuất đảm bảo độ trễ thấp và độ chính xác cao.\n- Xây dựng quy trình gán nhãn dữ liệu và quản lý dataset quy mô hàng triệu hình ảnh.\n- Cập nhật các kiến trúc mạng Neural mới nhất từ các paper nghiên cứu SOTA.', 
30000000, 60000000, 
'- Tốt nghiệp Đại học/Thạc sĩ chuyên ngành AI, Computer Science hoặc Toán Tin.\n- Thành thạo Python và các framework học sâu như PyTorch, TensorFlow hoặc JAX.\n- Kinh nghiệm làm việc với OpenCV, dlib và các kỹ thuật xử lý ảnh nâng cao.\n- Hiểu biết sâu về tối ưu hóa mô hình trên thiết bị Edge hoặc môi trường Cloud.\n- Có khả năng đọc hiểu paper tiếng Anh và triển khai lại thuật toán một cách chính xác.', 
'- Mức lương Net cao, không giới hạn trần thưởng theo hiệu quả dự án thực tế.\n- Làm việc trong môi trường R&D chuyên sâu, khuyến khích các sáng kiến đột phá.\n- Được cấp máy tính workstation trang bị GPU khủng phục vụ huấn luyện mô hình.\n- Hỗ trợ chi phí tham gia các khóa học nâng cao trên Coursera, DeepLearning.AI.\n- Thưởng các ngày lễ lớn, teambuilding 2 lần/năm tại các resort cao cấp nhất.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 17:00:00', 'Khu công nghệ cao Q9, TP.HCM', 24, 40, '2026-05-15', '2026-11-15', '08:30:00', '17:30:00', 1, 2, 9, 3, 1, 1),

-- 23. DevOps Engineer
('DevOps Engineer (Azure & Terraform Specialist)', 
'- Quản lý hạ tầng đám mây Azure cho các hệ thống phần mềm của đối tác châu Âu.\n- Viết mã nguồn hạ tầng (IaC) sử dụng Terraform để tự động hóa việc cấp phát tài nguyên.\n- Xây dựng hệ thống CI/CD hoàn chỉnh trên Azure DevOps hoặc GitHub Actions.\n- Giám sát hiệu năng hệ thống (Monitoring) và thiết lập cảnh báo tự động thông minh.\n- Đảm bảo an ninh hạ tầng, thực hiện quét lỗ hổng và bảo mật mạng định kỳ.', 
28000000, 55000000, 
'- Tối thiểu 3 năm kinh nghiệm ở vị trí DevOps, ưu tiên kinh nghiệm với Azure Cloud.\n- Thành thạo Docker, Kubernetes và các kỹ thuật đóng gói ứng dụng chuyên sâu.\n- Kỹ năng scripting tốt với Bash, Python hoặc PowerShell để tự động hóa công việc.\n- Hiểu biết về Network security, Load Balancer, và quản lý chứng chỉ SSL/TLS.\n- Ưu tiên ứng viên có chứng chỉ quốc tế như AZ-400 hoặc CKA (Kubernetes).', 
'- Chế độ lương tháng 13, 14 và thưởng cổ phiếu theo thâm niên làm việc.\n- Bảo hiểm sức khỏe quốc tế Liberty chi trả 100% chi phí nội trú và ngoại trú.\n- Môi trường làm việc chuẩn quốc tế, thường xuyên giao tiếp bằng tiếng Anh.\n- Hỗ trợ phí thi chứng chỉ Cloud Azure/AWS trị giá lên đến hàng nghìn USD.\n- Chế độ làm việc Hybrid, linh hoạt thời gian bắt đầu và kết thúc công việc hàng ngày.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 17:15:00', 'Số 29 Liễu Giai, Ba Đình, Hà Nội', 25, 42, '2026-04-15', '2026-10-15', '09:00:00', '18:00:00', 1, 1, 2, 3, 1, 1),

-- 24. Data Scientist
('Data Scientist (Recommendation System Focus)', 
'- Nghiên cứu và xây dựng hệ thống gợi ý sản phẩm cá nhân hóa cho người dùng.\n- Thực hiện phân tích dữ liệu hành vi khách hàng để tìm ra các insight giá trị.\n- Xây dựng các mô hình dự báo doanh thu và phân khúc khách hàng mục tiêu.\n- Phối hợp với team Product để thử nghiệm A/B Testing các tính năng mới trên app.\n- Trình bày kết quả phân tích bằng các công cụ visualization trực quan cho ban lãnh đạo.', 
30000000, 55000000, 
'- Ít nhất 3 năm kinh nghiệm làm Data Scientist, ưu tiên mảng Ecommerce hoặc Fintech.\n- Thành thạo SQL, Python (Scikit-learn, XGBoost, LightGBM) và kỹ năng thống kê.\n- Kinh nghiệm xây dựng Recommender Systems sử dụng Collaborative Filtering hoặc Deep Learning.\n- Khả năng tư duy logic tốt, am hiểu nghiệp vụ kinh doanh và nhu cầu người dùng.\n- Kỹ năng visualization tốt với các công cụ như Tableau, PowerBI hoặc Superset.', 
'- Thu nhập cạnh tranh theo năng lực, gói bảo hiểm cao cấp dành cho gia đình.\n- Cơ hội thăng tiến lên các vị trí Lead Data Scientist hoặc Head of Data.\n- Văn phòng hiện đại, pantry đầy đủ tiện nghi, khu vực gym và yoga miễn phí.\n- Tham gia các dự án dữ liệu quy mô lớn có tác động trực tiếp đến doanh thu.\n- Thưởng các ngày lễ lớn, sinh nhật, hiếu hỉ và trợ cấp ăn trưa hàng tháng.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 17:30:00', 'Số 1 Đinh Tiên Hoàng, Quận 1, TP.HCM', 25, 40, '2026-05-01', '2026-11-01', '08:30:00', '17:30:00', 1, 2, 1, 3, 1, 1),

-- 25. System Administrator
('System Administrator (Linux & Networking Expert)', 
'- Quản trị và duy trì sự ổn định của hệ thống server Linux tại Datacenter.\n- Thiết lập cấu hình hệ thống mạng, Firewall và VPN cho toàn bộ văn phòng công ty.\n- Thực hiện sao lưu dữ liệu (Backup) và xây dựng kế hoạch phục hồi sau thảm họa.\n- Giám sát an ninh mạng, phát hiện và ngăn chặn các đợt tấn công từ bên ngoài.\n- Hỗ trợ nhân viên xử lý các vấn đề kỹ thuật liên quan đến hạ tầng mạng nội bộ.', 
15000000, 28000000, 
'- Có từ 3 năm kinh nghiệm quản trị hệ thống Linux (CentOS, Ubuntu, Debian).\n- Hiểu biết sâu về Networking (TCP/IP, DNS, DHCP, VLAN, Routing/Switching).\n- Kinh nghiệm làm việc với các giải pháp ảo hóa như VMware, Proxmox hoặc KVM.\n- Thành thạo Bash Script để tự động hóa các tác vụ quản trị hàng ngày.\n- Có tinh thần trách nhiệm cao, sẵn sàng trực on-call khi có sự cố khẩn cấp.', 
'- Lương Net, đóng bảo hiểm đầy đủ trên 100% mức lương thỏa thuận thực tế.\n- Phụ cấp trực đêm, trực lễ tết và thưởng xử lý sự cố nhanh chóng vượt KPI.\n- Miễn phí cơm trưa tại nhà ăn công ty với thực đơn phong phú hàng ngày.\n- Được tài trợ chi phí thi các chứng chỉ quốc tế như LPI, MCSA, CCNA/CCNP.\n- Môi trường làm việc ổn định, bền vững, đồng nghiệp thân thiện và hỗ trợ.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 17:45:00', 'Khu công nghệ cao Láng Hòa Lạc, Hà Nội', 24, 45, '2026-04-20', '2026-10-20', '08:30:00', '17:30:00', 1, 1, 9, 2, 1, 1),

-- 26. Cyber Security Engineer
('Cyber Security Engineer (Pentest & SOC Focus)', 
'- Thực hiện kiểm tra lỗ hổng bảo mật (Pentest) cho các ứng dụng Web và Mobile.\n- Giám sát an ninh mạng qua hệ thống SIEM, phát hiện và xử lý các cuộc tấn công.\n- Xây dựng quy trình bảo mật thông tin theo tiêu chuẩn ISO 27001 cho tổ chức.\n- Đào tạo nhận thức an toàn thông tin cho toàn bộ nhân viên trong công ty.\n- Phối hợp với team Dev để khắc phục các lỗ hổng bảo mật trong mã nguồn.', 
30000000, 58000000, 
'- Ít nhất 4 năm kinh nghiệm trong lĩnh vực An toàn thông tin hoặc An ninh mạng.\n- Thành thạo các công cụ như Burp Suite, Metasploit, Nmap, Wireshark.\n- Có kiến thức sâu về lỗ hổng OWASP Top 10 và các kỹ thuật tấn công phổ biến.\n- Hiểu biết về mật mã học, giao thức mạng và bảo mật hệ thống Linux/Windows.\n- Ưu tiên ứng viên có chứng chỉ CEH, OSCP, CISSP hoặc tương đương.', 
'- Thu nhập hấp dẫn, thỏa thuận theo năng lực và các chứng chỉ bảo mật hiện có.\n- Gói bảo hiểm sức khỏe quốc tế cao cấp, hạn mức chi trả hàng tỷ đồng mỗi năm.\n- Môi trường làm việc chuyên nghiệp, cơ sở vật chất hiện đại, đầy đủ công cụ test.\n- Thưởng cuối năm 2-4 tháng lương tùy theo hiệu quả công việc và đóng góp.\n- Tham gia các diễn đàn bảo mật lớn và đại diện công ty tham gia các cuộc thi CTF.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 18:00:00', 'Tòa nhà BIDV, 194 Trần Quang Khải, Hà Nội', 26, 45, '2026-05-15', '2026-11-15', '08:30:00', '17:30:00', 1, 1, 3, 3, 1, 1),

-- 27. DBA
('Database Administrator (PostgreSQL & Oracle)', 
'- Quản trị và tối ưu hóa hệ thống cơ sở dữ liệu lớn cho hệ thống viễn thông.\n- Thiết lập cấu hình Replication, Clustering đảm bảo tính sẵn sàng cao (HA).\n- Thực hiện giám sát hiệu năng, tinh chỉnh các câu lệnh SQL chạy chậm.\n- Lập kế hoạch Backup, Restore dữ liệu định kỳ và xử lý các sự cố mất dữ liệu.\n- Phối hợp với team Dev trong việc thiết kế DB Schema chuẩn hóa và hiệu quả.', 
25000000, 48000000, 
'- Có kinh nghiệm từ 3-5 năm quản trị PostgreSQL, Oracle hoặc SQL Server.\n- Thành thạo kỹ thuật Tuning Performance và giải quyết tranh chấp tài nguyên (Locking).\n- Am hiểu về hệ điều hành Linux và kỹ năng scripting để tự động hóa quản trị DB.\n- Kinh nghiệm làm việc với các hệ thống dữ liệu phân tán là một lợi thế lớn.\n- Cẩn thận, tỉ mỉ và có khả năng làm việc dưới áp lực cực lớn với dữ liệu.', 
'- Lương Net, thưởng lương tháng 13 và các khoản thưởng lễ tết theo quy định.\n- Chế độ bảo hiểm sức khỏe đặc biệt dành cho nhân viên và cả người thân.\n- Review lương tối thiểu 1 lần/năm với lộ trình thăng tiến nghề nghiệp rõ ràng.\n- Cung cấp trang thiết bị làm việc hiện đại, hỗ trợ các chi phí sinh hoạt hàng tháng.\n- Thường xuyên được đào tạo bởi các chuyên gia hàng đầu từ hãng (Oracle, Microsoft).', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 18:15:00', 'Láng Hạ, Ba Đình, Hà Nội', 25, 48, '2026-04-15', '2026-10-15', '08:00:00', '17:00:00', 1, 1, 2, 3, 1, 1),

-- 28. NLP Specialist
('NLP Research Engineer (Vietnamese Language Focus)', 
'- Nghiên cứu các mô hình ngôn ngữ lớn (LLMs) dành riêng cho tiếng Việt.\n- Xây dựng hệ thống phân tích cảm xúc (Sentiment Analysis) cho mạng xã hội.\n- Phát triển các ứng dụng nhận dạng tiếng nói và tổng hợp giọng nói tự nhiên.\n- Xử lý và làm sạch tập dữ liệu văn bản tiếng Việt quy mô cực lớn từ internet.\n- Thử nghiệm các kiến trúc Transformer mới nhất để cải thiện độ chính xác NLP.', 
35000000, 75000000, 
'- Tốt nghiệp Thạc sĩ hoặc Tiến sĩ chuyên ngành AI, NLP hoặc Ngôn ngữ học tính toán.\n- Thành thạo Python và các thư viện NLP như HuggingFace, SpaCy, NLTK.\n- Kinh nghiệm làm việc với các kiến trúc BERT, GPT, T5 và các mô hình đa ngôn ngữ.\n- Hiểu biết về văn hóa và đặc trưng ngôn ngữ tiếng Việt để xử lý dữ liệu chính xác.\n- Có các bài báo nghiên cứu công bố tại các hội thảo AI là một điểm cộng lớn.', 
'- Thu nhập thuộc top thị trường, gói phúc lợi dành cho chuyên gia cao cấp.\n- Được tài trợ 100% chi phí đi báo cáo tại các hội thảo quốc tế lớn (ACL, EMNLP).\n- Thời gian làm việc linh hoạt, tập trung vào kết quả nghiên cứu và chất lượng mô hình.\n- Gói bảo hiểm sức khỏe quốc tế cao cấp nhất, phòng gym và bể bơi tại tòa nhà.\n- Môi trường làm việc học thuật cao, hợp tác cùng các viện nghiên cứu hàng đầu.', 
'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 'Trần Đại Nghĩa, Hai Bà Trưng, Hà Nội', 26, 50, '2026-06-01', '2027-06-01', '09:00:00', '18:00:00', 1, 1, 5, 4, 1, 1),

-- 29. Blockchain Developer
('Blockchain Developer (Solidity & Smart Contract)', 
'- Phát triển các ứng dụng phi tập trung (DApps) trên nền tảng Ethereum.\n- Thiết kế và triển khai các Smart Contract bằng ngôn ngữ Solidity bảo mật cao.\n- Xây dựng cầu nối (Bridge) giữa các mạng blockchain khác nhau.\n- Thực hiện kiểm tra lỗi (Auditing) Smart Contract để ngăn chặn các vụ hack.\n- Nghiên cứu các giải pháp Layer 2 để tăng tốc độ giao dịch và giảm phí gas.', 
35000000, 70000000, 
'- Ít nhất 2 năm kinh nghiệm lập trình Solidity hoặc lập trình Blockchain.\n- Nắm vững các tiêu chuẩn token ERC-20, ERC-721, ERC-1155 và cơ chế EVM.\n- Kinh nghiệm sử dụng các framework như Hardhat, Truffle và thư viện ethers.js.\n- Am hiểu về cấu trúc dữ liệu, giải thuật và các lỗ hổng bảo mật Web3 phổ biến.\n- Đam mê và có kiến thức sâu về tài chính phi tập trung (DeFi) và NFT.', 
'- Thu nhập hấp dẫn bằng stablecoin hoặc USD theo thỏa thuận trực tiếp.\n- Thưởng Token dự án khi đạt được các mốc phát triển (milestones) quan trọng.\n- Làm việc từ xa 100%, không gò bó thời gian, tập trung hoàn toàn vào chất lượng code.\n- Hỗ trợ chi phí đi tham dự các sự kiện Blockchain toàn cầu (EthCC, Devcon).\n- Môi trường làm việc toàn cầu, giao tiếp với các đồng nghiệp giỏi trên khắp thế giới.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 18:30:00', 'Thảo Điền, Quận 2, TP.HCM', 24, 40, '2026-05-01', '2026-11-01', '10:00:00', '19:00:00', 1, 2, 8, 3, 1, 1),

-- 30. SRE Engineer
('Site Reliability Engineer (SRE - Global Platform)', 
'- Đảm bảo tính sẵn sàng 99.99% cho nền tảng thương mại điện tử xuyên biên giới.\n- Xây dựng hệ thống tự động hóa phản ứng với sự cố (Automated Incident Response).\n- Phân tích nguyên nhân gốc rễ (Root Cause Analysis) sau mỗi sự cố hệ thống.\n- Thiết lập và quản lý các chỉ số SLO/SLI để đánh giá độ tin cậy của dịch vụ.\n- Tối ưu hóa hiệu suất ứng dụng thông qua việc profiling và tuning hệ thống.', 
30000000, 60000000, 
'- Có trên 4 năm kinh nghiệm kết hợp giữa Software Engineering và Systems Admin.\n- Thành thạo lập trình với Python hoặc Go để viết các công cụ tự động hóa.\n- Kinh nghiệm chuyên sâu với Kubernetes, Cloud Native tools và kỹ thuật SRE.\n- Am hiểu về phân tích log, monitoring hệ thống phân tán (Grafana, Datadog).\n- Khả năng xử lý khủng hoảng tốt, tư duy nhanh nhạy khi đối mặt với sự cố lớn.', 
'- Gói lương thưởng hấp dẫn kèm phụ cấp trực sự cố (On-call) cao nhất thị trường.\n- Nghỉ phép thêm sau các đợt trực sự cố mệt mỏi hoặc hoàn thành dự án lớn.\n- Gói bảo hiểm sức khỏe quốc tế cao cấp dành cho nhân viên và 2 người thân.\n- Hỗ trợ chi phí thành lập phòng làm việc tại nhà hiện đại (ghế công thái học, màn hình).\n- Cơ hội thăng tiến lên các vị trí quản lý vận hành hoặc Architect hạ tầng.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 18:45:00', 'Etown Cộng Hòa, Tân Bình, TP.HCM', 26, 45, '2026-04-10', '2026-10-10', '09:00:00', '18:00:00', 1, 2, 6, 3, 1, 1)
,
('IT Project Manager (Agile/Scrum Expert)', 
'- Quản lý và điều phối các dự án phát triển phần mềm cho khối ngân hàng.\n- Lập kế hoạch dự án, quản lý tiến độ, ngân sách và nguồn lực con người.\n- Làm việc trực tiếp với các bên liên quan (Stakeholders) để thống nhất yêu cầu.\n- Quản trị rủi ro dự án và đưa ra các phương án ứng phó kịp thời.\n- Tổ chức các buổi họp Sprint Planning, Daily Stand-up và Retro cho team.', 
35000000, 60000000, 
'- Ít nhất 5 năm kinh nghiệm quản lý dự án phần mềm quy mô trên 20 người.\n- Hiểu biết sâu sắc về mô hình Agile/Scrum và công cụ quản lý Jira/Confluence.\n- Kỹ năng lãnh đạo, giải quyết xung đột và truyền cảm hứng cho đội ngũ tốt.\n- Có chứng chỉ quốc tế như PMP, CSM hoặc tương đương là một lợi thế.\n- Tiếng Anh lưu loát, có khả năng trình bày và thuyết phục khách hàng quốc tế.', 
'- Mức lương Net cạnh tranh kèm thưởng theo % lợi nhuận dự án cuối năm.\n- Thưởng lương tháng 13, 14 và các khoản phụ cấp quản lý đặc thù hàng tháng.\n- Gói bảo hiểm sức khỏe quốc tế cao cấp cho cá nhân và toàn bộ gia đình.\n- Tham gia các khóa học Leadership và quản trị chiến lược tại các học viện lớn.\n- Cơ hội thăng tiến lên các vị trí Giám đốc dự án hoặc Delivery Manager.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 19:00:00', 'Lê Duẩn, Quận 1, TP.HCM', 28, 45, '2026-05-01', '2027-05-01', '08:30:00', '17:30:00', 1, 2, 1, 4, 1, 1),

-- 32. Business Analyst
('Senior Business Analyst (Fintech & Banking)', 
'- Khảo sát yêu cầu người dùng và phân tích nghiệp vụ cho hệ thống ví điện tử.\n- Viết tài liệu đặc tả yêu cầu (BRD, SRS) và thiết kế User Flow, Wireframe.\n- Làm cầu nối truyền đạt yêu cầu giữa khách hàng và đội ngũ phát triển phần mềm.\n- Hỗ trợ team QA trong việc xây dựng kịch bản kiểm thử và nghiệm thu sản phẩm.\n- Tham gia nghiên cứu thị trường để đề xuất các tính năng mới cho sản phẩm.', 
22000000, 40000000, 
'- Ít nhất 4 năm kinh nghiệm làm BA cho các dự án phần mềm tài chính.\n- Kỹ năng viết tài liệu logic, rành mạch và sử dụng thành thạo UML, BPMN.\n- Khả năng phân tích dữ liệu tốt và sử dụng được các công cụ SQL cơ bản.\n- Tư duy giải quyết vấn đề linh hoạt và kỹ năng giao tiếp khéo léo, tinh tế.\n- Am hiểu về quy trình phát triển phần mềm và các mô hình Waterfall, Agile.', 
'- Thưởng quý dựa trên chất lượng sản phẩm và mức độ hài lòng của khách hàng.\n- Review lương định kỳ hàng năm với mức tăng trưởng từ 15-25% theo năng lực.\n- Teambuilding hàng quý, du lịch hàng năm tại các điểm du lịch cao cấp.\n- Hỗ trợ chi phí thi các chứng chỉ quốc tế về BA như CCBA hoặc CBAP.\n- Môi trường làm việc văn minh, sếp tâm lý, đồng nghiệp chuyên nghiệp, hỗ trợ.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 19:15:00', 'Trần Não, Quận 2, TP.HCM', 25, 38, '2026-04-20', '2026-10-20', '09:00:00', '18:00:00', 1, 2, 8, 2, 1, 1),

-- 33. Product Owner
('Product Owner (E-commerce Platform)', 
'- Xác định tầm nhìn sản phẩm và xây dựng lộ trình phát triển (Product Roadmap).\n- Quản lý Product Backlog, ưu tiên các tính năng mang lại giá trị cao nhất.\n- Làm việc chặt chẽ với team UI/UX để tối ưu hóa hành trình mua sắm của khách.\n- Theo dõi các chỉ số sản phẩm (Retention, Conversion) để đưa ra điều chỉnh.\n- Đảm bảo sản phẩm ra mắt đúng hạn và đạt tiêu chuẩn chất lượng kỳ vọng.', 
35000000, 70000000, 
'- Có ít nhất 3 năm kinh nghiệm ở vị trí PO hoặc Product Manager mảng App.\n- Tư duy sản phẩm tốt, khả năng ra quyết định dựa trên dữ liệu (Data-driven).\n- Kỹ năng trình bày và thuyết phục các cấp lãnh đạo về định hướng sản phẩm.\n- Am hiểu thị trường E-commerce và các xu hướng công nghệ mới trên thế giới.\n- Tiếng Anh giao tiếp tốt để làm việc với các đối tác cung cấp giải pháp.', 
'- Thu nhập hấp dẫn, gói lương bao gồm lương cơ bản và thưởng doanh thu sản phẩm.\n- Gói cổ phiếu thưởng ESOP dành cho nhân sự đóng góp quan trọng vào sản phẩm.\n- Nghỉ phép 15 ngày/năm và các chế độ nghỉ lễ tết theo quy định nhà nước.\n- Cấp máy tính Macbook Pro đời mới và các phụ kiện hỗ trợ làm việc cao cấp.\n- Môi trường Startup kỳ lân năng động, cơ hội phát triển bản thân cực lớn.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 19:30:00', 'Đinh Tiên Hoàng, Quận 1, TP.HCM', 27, 45, '2026-05-15', '2027-05-15', '09:00:00', '18:00:00', 1, 2, 1, 4, 1, 1),

-- 34. Solution Architect
('Solution Architect (Cloud Native Architecture)', 
'- Thiết kế kiến trúc tổng thể cho các hệ thống phần mềm quy mô lớn của tập đoàn.\n- Đưa ra các quyết định về công nghệ, framework và cấu trúc dữ liệu cho dự án.\n- Đảm bảo hệ thống đạt được các tiêu chuẩn về Performance, Scalability và Security.\n- Hướng dẫn các Lead Developer triển khai đúng theo kiến trúc đã thiết kế.\n- Nghiên cứu và thực hiện các dự án PoC (Proof of Concept) cho công nghệ mới.', 
50000000, 100000000, 
'- Trên 10 năm kinh nghiệm trong ngành CNTT, ít nhất 3 năm ở vị trí Architect.\n- Am hiểu sâu sắc về kiến trúc Microservices, Serverless và Event-driven.\n- Kinh nghiệm triển khai hệ thống trên Cloud (AWS/Azure/GCP) ở mức độ Expert.\n- Khả năng phân tích hệ thống phức tạp và đưa ra giải pháp tối ưu về chi phí.\n- Tiếng Anh thành thạo cả 4 kỹ năng để làm việc với chuyên gia nước ngoài.', 
'- Thu nhập cực cao, thỏa thuận tương xứng với trình độ và kinh nghiệm thực tế.\n- Có xe hơi công ty đưa đón riêng khi đi công tác hoặc họp với đối tác lớn.\n- Gói bảo hiểm sức khỏe quốc tế hạng Diamond cho cả gia đình nhân viên.\n- Tham gia các sự kiện công nghệ toàn cầu (AWS Re:invent, Google I/O) hàng năm.\n- Quyền lợi nghỉ dưỡng tại các khách sạn 5 sao của tập đoàn trên toàn quốc.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 19:45:00', 'Keangnam Landmark 72, Hà Nội', 32, 55, '2026-06-01', '2028-06-01', '08:30:00', '17:30:00', 1, 1, 5, 5, 1, 1),

-- 35. Technical Writer
('Technical Writer (API Documentation)', 
'- Viết tài liệu kỹ thuật hướng dẫn tích hợp API cho cộng đồng lập trình viên.\n- Xây dựng hệ thống tài liệu hướng dẫn sử dụng sản phẩm cho người dùng cuối.\n- Biên tập các bài viết kỹ thuật (Tech-blog) chia sẻ kiến thức trên trang web.\n- Làm việc với team Dev để cập nhật tài liệu khi có thay đổi trong mã nguồn.\n- Thiết kế các sơ đồ luồng dữ liệu minh họa cho tài liệu một cách trực quan.', 
12000000, 25000000, 
'- Khả năng viết tiếng Anh cực tốt (IELTS 7.0+ hoặc tương đương) là bắt buộc.\n- Hiểu biết cơ bản về lập trình, RESTful API và cấu trúc dữ liệu (JSON/XML).\n- Sử dụng thành thạo Markdown, Swagger và các công cụ quản lý tài liệu.\n- Cẩn thận, tỉ mỉ trong việc dùng từ và trình bày tài liệu khoa học, dễ hiểu.\n- Ưu tiên ứng viên có kinh nghiệm làm việc trong các công ty Outsourcing.', 
'- Lương Net, đóng bảo hiểm đầy đủ ngay từ khi bắt đầu thử việc tại công ty.\n- Nghỉ thứ 7, chủ nhật và các ngày lễ tết theo lịch của nhà nước Việt Nam.\n- Review lương định kỳ hàng năm và có lộ trình thăng tiến nghề nghiệp rõ ràng.\n- Pantry đầy đủ đồ ăn nhẹ, trà sữa, cafe phục vụ miễn phí cho nhân viên.\n- Môi trường làm việc trẻ trung, sáng tạo, khuyến khích chia sẻ kiến thức.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 20:00:00', 'Nguyễn Chí Thanh, Đống Đa, Hà Nội', 22, 35, '2026-04-10', '2026-07-10', '08:30:00', '17:30:00', 1, 1, 2, 2, 1, 1),

-- 36. Scrum Master
('Scrum Master (Agile Transformation)', 
'- Điều phối quy trình Scrum cho 2-3 đội ngũ phát triển phần mềm cùng lúc.\n- Loại bỏ các rào cản gây ảnh hưởng đến tiến độ và năng suất làm việc của team.\n- Tổ chức và điều phối các buổi lễ Agile (Daily, Planning, Review, Retro).\n- Huấn luyện các thành viên hiểu và áp dụng đúng các giá trị cốt lõi của Agile.\n- Phối hợp với PO để đảm bảo Backlog luôn được chuẩn bị tốt và ưu tiên đúng.', 
25000000, 45000000, 
'- Tối thiểu 3 năm kinh nghiệm làm Scrum Master chuyên nghiệp cho các team IT.\n- Có chứng chỉ quốc tế CSM (Certified Scrum Master) hoặc PSM là yêu cầu cần.\n- Kỹ năng giao tiếp, lắng nghe và đặt câu hỏi để thúc đẩy team phát triển.\n- Am hiểu về các mô hình Agile mở rộng như SAFe hoặc LeSS là một điểm cộng.\n- Có tinh thần phục vụ (Servant Leadership) và tư duy cải tiến liên tục.', 
'- Thưởng lương tháng 13 hấp dẫn kèm thưởng hiệu quả vận hành đội ngũ.\n- Tài trợ 100% chi phí tham gia các khóa học nâng cao kỹ năng điều phối team.\n- Gói bảo hiểm sức khỏe cao cấp và hỗ trợ chi phí tập Gym hàng tháng.\n- Cơ hội tham gia vào quá trình chuyển đổi số quy mô lớn của tập đoàn.\n- Môi trường làm việc năng động, không gian mở, khuyến khích sự tự chủ.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 20:15:00', 'Mễ Trì, Nam Từ Liêm, Hà Nội', 26, 42, '2026-04-15', '2026-10-15', '08:30:00', '17:30:00', 1, 1, 6, 3, 1, 1),

-- 37. Automation Test Lead
('Automation Test Lead (Selenium & Playwright)', 
'- Thiết kế và xây dựng Automation Testing Framework từ đầu cho dự án web/app.\n- Lập kế hoạch kiểm thử tự động cho từng giai đoạn phát triển của sản phẩm.\n- Quản lý và đào tạo đội ngũ kỹ sư kiểm thử tự động (Automation Engineers).\n- Tích hợp scripts kiểm thử tự động vào đường ống CI/CD của dự án.\n- Báo cáo kết quả kiểm thử và phân tích lỗi cho đội ngũ phát triển phần mềm.', 
30000000, 50000000, 
'- Ít nhất 5 năm kinh nghiệm làm Testing, 3 năm chuyên sâu về Automation.\n- Thành thạo lập trình Java, Python hoặc JavaScript để viết test scripts.\n- Kinh nghiệm sử dụng Selenium, Appium, Playwright hoặc Cypress thành thạo.\n- Kỹ năng quản lý team, lập kế hoạch và báo cáo chất lượng dự án tốt.\n- Hiểu biết về quy trình phát triển phần mềm và các loại kiểm thử phần mềm.', 
'- Mức lương Net cao xứng đáng với năng lực quản lý và trình độ kỹ thuật.\n- Thưởng dự án định kỳ hàng quý dựa trên chất lượng release sản phẩm.\n- Được trang bị các thiết bị test hiện đại nhất (iPhone, Samsung, Tablet).\n- Review lương tối thiểu 1 lần/năm với mức tăng trưởng từ 15% trở lên.\n- Môi trường làm việc chuyên nghiệp, đồng nghiệp giỏi, nhiều cơ hội học hỏi.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 20:30:00', 'Khu đô thị Sala, Quận 2, TP.HCM', 27, 40, '2026-05-01', '2026-11-01', '09:00:00', '18:00:00', 1, 2, 8, 3, 1, 1),

-- 38. IT Helpdesk
('IT Helpdesk (Technical Support & Network)', 
'- Hỗ trợ nhân viên xử lý các sự cố về máy tính, phần mềm và mạng nội bộ.\n- Cài đặt, cấu hình và bảo trì các thiết bị CNTT như Máy tính, Máy in, Wifi.\n- Quản lý và cấp phát tài khoản email, phần mềm làm việc cho nhân viên mới.\n- Theo dõi và bảo trì hệ thống server nội bộ và các thiết bị lưu trữ dữ liệu.\n- Thực hiện các nhiệm vụ hỗ trợ kỹ thuật khác theo yêu cầu của quản lý IT.', 
8000000, 15000000, 
'- Tốt nghiệp Cao đẳng/Đại học chuyên ngành CNTT hoặc các ngành liên quan.\n- Có kiến thức cơ bản về phần cứng máy tính, hệ điều hành Windows/MacOS.\n- Hiểu biết về quản trị mạng LAN/Wifi và các thiết bị mạng phổ biến.\n- Nhiệt tình trong công việc, kỹ năng giao tiếp và xử lý tình huống tốt.\n- Ưu tiên ứng viên có các chứng chỉ về IT Support như CCNA hoặc MCSA.', 
'- Thưởng lương tháng 13 và các khoản thưởng lễ tết theo quy định công ty.\n- Phụ cấp tiền ăn trưa, tiền gửi xe và hỗ trợ chi phí điện thoại hàng tháng.\n- Đóng đầy đủ các loại bảo hiểm xã hội, y tế ngay sau khi hết thử việc.\n- Được đào tạo nâng cao kiến thức về bảo mật và quản trị hệ thống mạng.\n- Môi trường làm việc ổn định, cơ hội thăng tiến lên System Admin.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 20:45:00', 'Quang Trung, Gò Vấp, TP.HCM', 20, 30, '2026-04-01', '2026-09-30', '08:00:00', '17:00:00', 1, 2, 12, 1, 1, 1),

-- 39. Sales IT
('Sales IT Consultant (Enterprise Solutions)', 
'- Tìm kiếm và tiếp cận các khách hàng doanh nghiệp có nhu cầu về phần mềm.\n- Tư vấn giải pháp CNTT phù hợp với quy trình vận hành của từng khách hàng.\n- Thực hiện thuyết trình sản phẩm (Demo) và đàm phán hợp đồng với đối tác.\n- Phối hợp với team Kỹ thuật để đưa ra báo giá và lộ trình triển khai dự án.\n- Chăm sóc và duy trì mối quan hệ tốt đẹp với các khách hàng sau bán hàng.', 
10000000, 30000000, 
'- Có ít nhất 1 năm kinh nghiệm làm Sales, ưu tiên trong lĩnh vực phần mềm.\n- Kỹ năng giao tiếp, đàm phán và thuyết phục khách hàng cực kỳ xuất sắc.\n- Am hiểu các kiến thức cơ bản về CNTT như Website, App, ERP, Cloud.\n- Tiếng Anh giao tiếp tốt là một lợi thế lớn để tiếp cận khách quốc tế.\n- Có tinh thần cầu tiến, chịu được áp lực doanh số và năng động trong việc.', 
'- Lương cơ bản + Hoa hồng (Commission) hấp dẫn theo giá trị mỗi hợp đồng.\n- Thưởng nóng khi đạt hoặc vượt KPI doanh số hàng tháng và hàng quý.\n- Được đào tạo bài bản về kỹ năng bán hàng và kiến thức sản phẩm chuyên sâu.\n- Môi trường làm việc năng động, mở rộng mạng lưới quan hệ với các CEO/CTO.\n- Teambuilding, du lịch thường xuyên cùng đội ngũ kinh doanh sôi nổi.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 21:00:00', 'Phố Duy Tân, Cầu Giấy, Hà Nội', 22, 35, '2026-04-15', '2026-07-15', '08:30:00', '18:00:00', 1, 1, 1, 2, 1, 1),

-- 40. IT Recruiter
('IT Recruiter (Talent Acquisition Specialist)', 
'- Tìm kiếm và thu hút ứng viên tiềm năng cho các vị trí IT của công ty.\n- Thực hiện sàng lọc hồ sơ, phỏng vấn sơ loại và điều phối lịch phỏng vấn.\n- Xây dựng mạng lưới kết nối với cộng đồng lập trình viên trên mạng xã hội.\n- Tham gia các sự kiện tuyển dụng, Job Fair tại các trường Đại học lớn.\n- Quản lý cơ sở dữ liệu ứng viên và thực hiện các báo cáo tuyển dụng.', 
12000000, 25000000, 
'- Ít nhất 2 năm kinh nghiệm làm tuyển dụng trong lĩnh vực Công nghệ thông tin.\n- Hiểu biết về các ngôn ngữ lập trình, framework và các vị trí trong ngành IT.\n- Kỹ năng networking tốt trên các nền tảng như LinkedIn, Facebook, GitHub.\n- Giao tiếp khéo léo, giọng nói truyền cảm và khả năng thuyết phục ứng viên.\n- Ưu tiên ứng viên có khả năng phỏng vấn bằng tiếng Anh cơ bản.', 
'- Lương thỏa thuận xứng đáng kèm thưởng theo số lượng ứng viên tuyển được.\n- Môi trường làm việc trẻ trung, sáng tạo, tiếp xúc với nhiều người tài giỏi.\n- Thưởng các ngày lễ tết và quà tặng sinh nhật, hiếu hỉ dành cho nhân viên.\n- Được đào tạo về các xu hướng tuyển dụng mới và kỹ năng phỏng vấn hiện đại.\n- Tham gia các hoạt động ngoại khóa sôi nổi cùng team HR và toàn công ty.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 21:15:00', 'Lê Văn Lương, Thanh Xuân, Hà Nội', 22, 32, '2026-04-10', '2026-10-10', '08:30:00', '17:30:00', 1, 1, 4, 2, 1, 1)
,
('Intern Java Backend Developer (Training Program)', 
'- Tham gia khóa đào tạo chuyên sâu về quy trình phát triển dự án Java thực tế.\n- Hỗ trợ các kỹ sư Senior trong việc viết Unit Test và fix các lỗi nhỏ.\n- Tìm hiểu và thực hành triển khai các API cơ bản bằng Spring Boot framework.\n- Cập nhật tài liệu kỹ thuật và báo cáo tiến độ học tập hàng ngày cho mentor.\n- Tham gia các buổi họp kỹ thuật của dự án để làm quen với môi trường Agile.', 
3000000, 7000000, 
'- Sinh viên năm cuối hoặc mới tốt nghiệp chuyên ngành CNTT, Điện tử viễn thông.\n- Nắm vững kiến thức nền tảng về Java Core, OOP và cấu trúc dữ liệu giải thuật.\n- Hiểu biết cơ bản về SQL và cách tương tác với cơ sở dữ liệu quan hệ.\n- Có tinh thần học hỏi cao, thái độ cầu tiến và không ngại đối mặt với thử thách.\n- Tiếng Anh đọc hiểu tài liệu kỹ thuật cơ bản là một lợi thế bắt buộc.', 
'- Được hỗ trợ phụ cấp thực tập hàng tháng và hỗ trợ chi phí gửi xe, ăn trưa.\n- Cơ hội trở thành nhân viên chính thức sau 3-6 tháng thực tập dựa trên kết quả.\n- Được hướng dẫn trực tiếp bởi các Senior/Lead có nhiều năm kinh nghiệm thực chiến.\n- Hỗ trợ cung cấp số liệu, đóng dấu thực tập phục vụ làm đồ án tốt nghiệp.\n- Môi trường làm việc trẻ trung, năng động, văn hóa chia sẻ kiến thức nhiệt tình.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 22:00:00', 'Khu đô thị Đại học Quốc gia, Thủ Đức, TP.HCM', 18, 24, '2026-05-01', '2026-08-01', '08:00:00', '17:00:00', 1, 2, 9, 1, 2, 3),

-- 42. Intern Frontend
('Intern Frontend Developer (ReactJS)', 
'- Học tập và phát triển giao diện web sử dụng thư viện ReactJS hiện đại.\n- Thực hành cắt HTML/CSS từ thiết kế Figma sang các component reusable.\n- Hỗ trợ tối ưu hóa giao diện cho các thiết bị di động (Responsive Design).\n- Tìm hiểu về cách quản lý state và tương tác với API từ phía Backend.\n- Phối hợp với team Design để hoàn thiện các hiệu ứng animation đơn giản.', 
3000000, 6000000, 
'- Sinh viên đang theo học ngành CNTT hoặc các khóa học lập trình Frontend.\n- Có kiến thức tốt về HTML5, CSS3 và nền tảng ngôn ngữ JavaScript.\n- Biết sử dụng cơ bản các công cụ quản lý mã nguồn như Git/GitHub.\n- Tư duy thẩm mỹ tốt, tỉ mỉ trong việc trình bày giao diện người dùng.\n- Khả năng tự nghiên cứu và giải quyết vấn đề dưới sự hướng dẫn của mentor.', 
'- Trợ cấp thực tập cạnh tranh kèm thưởng theo mức độ hoàn thành task học tập.\n- Được tham gia vào các dự án thực tế của công ty sau khi vượt qua bài test.\n- Miễn phí trà, cafe và đồ ăn nhẹ tại văn phòng trong suốt quá trình thực tập.\n- Tham gia đầy đủ các hoạt động Teambuilding và sự kiện văn hóa của công ty.\n- Lộ trình đào tạo rõ ràng, giúp sinh viên nhanh chóng nắm bắt công nghệ mới.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 22:15:00', 'Số 54 Nguyễn Lương Bằng, Liên Chiểu, Đà Nẵng', 18, 24, '2026-05-01', '2026-08-01', '08:00:00', '17:00:00', 1, 3, 2, 1, 2, 3),

-- 43. Fullstack Middle
('Fullstack Developer (Node.js & ReactJS)', 
'- Chịu trách nhiệm phát triển cả Frontend và Backend cho các sản phẩm Web.\n- Thiết kế kiến trúc cơ sở dữ liệu hiệu quả và bảo mật cho hệ thống CMS.\n- Xây dựng hệ thống API chuẩn RESTful và tích hợp các cổng thanh toán online.\n- Tối ưu hóa hiệu năng render phía client và tốc độ xử lý phía server-side.\n- Thực hiện deploy và quản lý ứng dụng trên các dịch vụ Cloud như AWS/Heroku.', 
25000000, 45000000, 
'- Có ít nhất 3 năm kinh nghiệm lập trình Fullstack với Node.js và ReactJS.\n- Thành thạo JavaScript/TypeScript và hiểu sâu về cơ chế bất đồng bộ (Async/Await).\n- Kinh nghiệm làm việc với MongoDB, PostgreSQL và kỹ thuật quản lý cache Redis.\n- Am hiểu về Docker, CI/CD và quy trình triển khai sản phẩm thực tế.\n- Kỹ năng giao tiếp tốt, có khả năng làm việc độc lập và chịu được áp lực cao.', 
'- Lương Net hấp dẫn, bảo hiểm đóng trên mức lương thực nhận hàng tháng.\n- Thưởng tháng lương 13 và các khoản thưởng nóng khi hoàn thành dự án gấp.\n- Cấp máy tính Macbook Pro hoặc Laptop cấu hình cao kèm màn hình rời Dell.\n- Chế độ nghỉ phép 14 ngày/năm và hưởng đầy đủ các loại bảo hiểm theo luật.\n- Tham gia các câu lạc bộ thể thao và teambuilding 2 lần mỗi năm của công ty.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 22:30:00', 'Võ Văn Kiệt, Quận 1, TP.HCM', 23, 35, '2026-04-15', '2026-10-15', '09:00:00', '18:00:00', 1, 2, 1, 2, 1, 1),

-- 44. Game Dev
('Game Developer (C++ & Cocos2d-x)', 
'- Phát triển các trò chơi mobile đa nền tảng sử dụng engine Cocos2d-x.\n- Tối ưu hóa tài nguyên game (texture, âm thanh) đảm bảo dung lượng app nhỏ.\n- Lập trình logic gameplay, xử lý hiệu ứng đặc biệt và hệ thống vật lý trong game.\n- Tích hợp các hệ thống In-app Purchase và quảng cáo của bên thứ ba.\n- Phối hợp với nghệ sĩ đồ họa để đưa các nhân vật 2D/3D vào trò chơi sinh động.', 
18000000, 35000000, 
'- Ít nhất 2 năm kinh nghiệm lập trình game với ngôn ngữ C++ hoặc C#.\n- Thành thạo engine Cocos2d-x hoặc Unity và các kiến thức về game design.\n- Hiểu biết về giải toán hình học, vật lý và các thuật toán trong phát triển game.\n- Có kinh nghiệm làm việc với Git và quy trình phát triển sản phẩm Agile/Scrum.\n- Đam mê mãnh liệt với trò chơi điện tử và luôn cập nhật xu hướng game mới.', 
'- Thưởng doanh thu dựa trên số lượng tải xuống và lợi nhuận của trò chơi.\n- Môi trường làm việc cực kỳ sáng tạo, trang phục tự do, không gian mở.\n- Khu vực giải trí riêng biệt với máy PS5, Switch và bàn bida tại văn phòng.\n- Được tài trợ tham dự các hội thảo game lớn như GameStart hoặc GDC.\n- Chế độ đãi ngộ tốt, review lương định kỳ và quà tặng vào các dịp lễ tết.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 22:45:00', 'Phan Đăng Lưu, Phú Nhuận, TP.HCM', 22, 33, '2026-05-01', '2026-08-01', '09:00:00', '18:00:00', 1, 2, 3, 2, 1, 1),

-- 45. Embedded
('Embedded Software Engineer (IoT Solutions)', 
'- Lập trình nhúng cho các thiết bị điều khiển thông minh trong hệ sinh thái IoT.\n- Phát triển driver và firmware trên các dòng vi điều khiển STM32, ESP32, ARM.\n- Thiết kế giao thức truyền thông giữa các thiết bị thông qua Wifi, Bluetooth, Zigbee.\n- Thực hiện kiểm thử tính ổn định và độ bền của phần mềm nhúng trên phần cứng thực tế.\n- Phối hợp với team phần cứng để tối ưu hóa việc tiêu thụ năng lượng của thiết bị.', 
20000000, 38000000, 
'- Tốt nghiệp chuyên ngành Điện tử viễn thông, Cơ điện tử hoặc CNTT.\n- Thành thạo ngôn ngữ C/C++ cho lập trình nhúng và hiểu biết về RTOS.\n- Kinh nghiệm làm việc với các chuẩn giao tiếp I2C, SPI, UART, RS485.\n- Khả năng đọc hiểu sơ đồ mạch điện tử và sử dụng các công cụ đo đạc kỹ thuật.\n- Cẩn thận, tỉ mỉ trong công việc và có tinh thần trách nhiệm với sản phẩm.', 
'- Thu nhập cạnh tranh, phụ cấp độc hại và làm việc trong phòng Lab hiện đại.\n- Thưởng dự án dựa trên số lượng sản phẩm được thương mại hóa thành công.\n- Gói bảo hiểm sức khỏe cao cấp dành riêng cho kỹ sư nghiên cứu và phát triển.\n- Được đào tạo bởi các chuyên gia hàng đầu về bán dẫn và IoT từ nước ngoài.\n- Review lương định kỳ hàng năm và lộ trình thăng tiến lên Expert hoặc Manager.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 23:00:00', 'Khu công nghệ cao Q9, TP.HCM', 23, 35, '2026-04-15', '2026-10-15', '08:30:00', '17:30:00', 1, 2, 9, 2, 1, 1),

-- 46. ERP Consultant
('ERP Functional Consultant (SAP/Odoo Specialist)', 
'- Tư vấn và triển khai các phân hệ ERP phù hợp với quy trình của doanh nghiệp.\n- Khảo sát thực tế, phân tích khoảng cách (Gap Analysis) giữa thực tế và phần mềm.\n- Cấu hình hệ thống, thiết lập luồng nghiệp vụ và thực hiện đào tạo người dùng.\n- Hỗ trợ khách hàng trong giai đoạn vận hành thử nghiệm và nghiệm thu chính thức.\n- Đề xuất các cải tiến quy trình nhằm tối ưu hóa hiệu quả sử dụng hệ thống ERP.', 
22000000, 45000000, 
'- Ít nhất 3 năm kinh nghiệm triển khai các hệ thống ERP (SAP, Oracle, Odoo).\n- Am hiểu sâu về nghiệp vụ Kế toán, Kho, Sản xuất hoặc Nhân sự.\n- Kỹ năng giao tiếp, thuyết trình và giải quyết vấn đề xuất sắc trước khách hàng.\n- Có khả năng làm việc độc lập tại công trường hoặc văn phòng đối tác.\n- Tiếng Anh giao tiếp lưu loát để làm việc với các chuyên gia triển khai quốc tế.', 
'- Mức lương thỏa thuận dựa trên kinh nghiệm và các chứng chỉ ERP đang sở hữu.\n- Phụ cấp công tác phí cao khi đi triển khai tại văn phòng khách hàng.\n- Thưởng hiệu quả triển khai dự án dựa trên mức độ hài lòng của đối tác.\n- Được tài trợ chi phí thi các chứng chỉ chuyên gia SAP hoặc Oracle quốc tế.\n- Môi trường làm việc chuyên nghiệp, mở rộng mối quan hệ với lãnh đạo doanh nghiệp.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 23:15:00', 'Phố Kim Mã, Ba Đình, Hà Nội', 25, 45, '2026-05-01', '2026-12-31', '08:30:00', '17:30:00', 1, 1, 2, 3, 1, 1),

-- 47. Data Analyst Junior
('Data Analyst Junior (Business Intelligence)', 
'- Thực hiện thu thập và làm sạch dữ liệu từ nhiều nguồn khác nhau phục vụ báo cáo.\n- Xây dựng các Dashboard theo dõi chỉ số kinh doanh (KPIs) hàng ngày.\n- Phân tích các xu hướng dữ liệu để đưa ra các đề xuất cải thiện doanh thu.\n- Hỗ trợ ban lãnh đạo trong việc chuẩn bị các báo cáo số liệu định kỳ.\n- Làm việc với team IT để đảm bảo tính chính xác và kịp thời của nguồn dữ liệu.', 
12000000, 20000000, 
'- Có ít nhất 1 năm kinh nghiệm làm việc với dữ liệu hoặc Business Intelligence.\n- Thành thạo ngôn ngữ truy vấn SQL và sử dụng Excel ở mức độ nâng cao.\n- Kinh nghiệm sử dụng các công cụ Visualization như Power BI, Tableau hoặc Looker.\n- Tư duy logic tốt, am hiểu về các chỉ số kinh doanh cơ bản của doanh nghiệp.\n- Cẩn thận, trung thực và có trách nhiệm cao đối với các con số báo cáo.', 
'- Lương thưởng cạnh tranh kèm review năng lực định kỳ 2 lần mỗi năm.\n- Được đào tạo chuyên sâu về kỹ năng phân tích dữ liệu và tư duy Business.\n- Môi trường làm việc thân thiện, hòa đồng và khuyến khích sự chủ động.\n- Gói bảo hiểm y tế và xã hội theo đúng quy định của nhà nước Việt Nam.\n- Tham gia đầy đủ các hoạt động ngoại khóa, du lịch, teambuilding cùng công ty.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 23:30:00', 'Lý Thường Kiệt, Quận 10, TP.HCM', 21, 28, '2026-04-10', '2026-07-10', '08:30:00', '17:30:00', 1, 2, 4, 1, 1, 1),

-- 48. Cloud Architect
('Cloud Solutions Architect (AWS/Azure Expert)', 
'- Thiết kế kiến trúc hạ tầng Cloud tối ưu về chi phí và hiệu năng cho tập đoàn.\n- Xây dựng các giải pháp Migration dữ liệu từ On-premise lên môi trường Cloud.\n- Đảm bảo tính sẵn sàng cao và khả năng phục hồi sau thảm họa cho hệ thống.\n- Tư vấn về bảo mật hạ tầng và tuân thủ các tiêu chuẩn quốc tế trên mây.\n- Đào tạo và hướng dẫn đội ngũ DevOps triển khai hạ tầng theo thiết kế chuẩn.', 
45000000, 90000000, 
'- Trên 8 năm kinh nghiệm IT, 3 năm kinh nghiệm thiết kế giải pháp Cloud.\n- Sở hữu các chứng chỉ cấp cao như AWS Solution Architect Professional.\n- Am hiểu sâu sắc về kiến trúc Microservices, Serverless và Cloud Security.\n- Kỹ năng tư duy chiến lược và khả năng trình bày giải pháp trước ban giám đốc.\n- Tiếng Anh thành thạo cả 4 kỹ năng để làm việc trực tiếp với các hãng công nghệ.', 
'- Thu nhập cực cao, thỏa thuận tương xứng với đẳng cấp và kinh nghiệm chuyên gia.\n- Có cổ phần thưởng (ESOP) dành cho nhân sự cấp cao gắn bó lâu dài.\n- Gói bảo hiểm sức khỏe quốc tế đặc biệt cho cả gia đình (vợ/chồng, con cái).\n- Tham gia các sự kiện công nghệ toàn cầu lớn nhất thế giới hàng năm tại Mỹ.\n- Quyền lợi nghỉ dưỡng tại các biệt thự cao cấp của tập đoàn trên toàn quốc.', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 23:45:00', 'Keangnam Landmark 72, Hà Nội', 30, 50, '2026-05-01', '2028-05-01', '09:00:00', '18:00:00', 1, 1, 5, 5, 1, 1),

-- 49. Vue.js Intern
('Intern Vue.js Developer (Frontend Training)', 
'- Tìm hiểu và làm quen với framework Vue 3 trong các dự án thực tế của team.\n- Hỗ trợ viết các component giao diện đơn giản sử dụng Composition API.\n- Tham gia kiểm tra lỗi hiển thị (UI Bug) trên các trình duyệt khác nhau.\n- Học cách tích hợp API và quản lý state ứng dụng với thư viện Pinia.\n- Thực hiện báo cáo công việc và kết quả học tập định kỳ cho mentor hướng dẫn.', 
4000000, 8000000, 
'- Sinh viên chuyên ngành CNTT đang tìm kiếm nơi thực tập tốt nghiệp.\n- Có kiến thức cơ bản về HTML, CSS và JavaScript, ưu tiên biết sơ qua về Vue.\n- Tư duy logic tốt, có khả năng tự tìm kiếm giải pháp trên StackOverflow, GitHub.\n- Có sản phẩm cá nhân hoặc đồ án môn học sử dụng công nghệ web là điểm cộng.\n- Tinh thần học hỏi nghiêm túc, sẵn sàng tiếp thu góp ý từ đồng nghiệp.', 
'- Phụ cấp thực tập hấp dẫn hơn mặt bằng chung, hỗ trợ chi phí ăn trưa hàng ngày.\n- Được làm việc trong môi trường thực tế, không chỉ là thực tập mô phỏng.\n- Có lộ trình lên chính thức rõ ràng nếu hoàn thành tốt các bài test kỹ năng.\n- Tham gia các buổi giao lưu văn hóa, bida, bóng đá cùng các anh chị trong team.\n- Cấp giấy xác nhận thực tập và đánh giá chi tiết kỹ năng sau kỳ thực tập.', 
'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 'Hồ Tùng Mậu, Cầu Giấy, Hà Nội', 19, 23, '2026-05-15', '2026-08-15', '08:30:00', '17:30:00', 1, 1, 1, 1, 2, 3),

-- 50. AI Chatbot Specialist
('AI Chatbot Specialist (Prompt Engineering)', 
'- Thiết kế và tối ưu hóa các kịch bản hội thoại cho Chatbot thông minh sử dụng AI.\n- Thực hiện Prompt Engineering để điều chỉnh phong cách phản hồi của mô hình LLM.\n- Phân tích phản hồi của người dùng để cải thiện độ chính xác của kịch bản AI.\n- Hợp tác với team Tech để tích hợp Chatbot vào các kênh Facebook, Web, App.\n- Theo dõi và báo cáo hiệu quả giải quyết vấn đề tự động của hệ thống Chatbot.', 
18000000, 35000000, 
'- Tốt nghiệp Đại học, ưu tiên các ngành Ngôn ngữ, Tâm lý hoặc CNTT.\n- Có kiến thức hoặc kinh nghiệm về Generative AI và cách vận hành của Chatbot.\n- Kỹ năng viết lách tốt, tư duy logic mạch lạc và am hiểu tâm lý người dùng.\n- Khả năng sử dụng các công cụ quản lý kịch bản hội thoại như Voiceflow, Botpress.\n- Tiếng Anh tốt là một lợi thế lớn để nghiên cứu các kỹ thuật Prompt mới.', 
'- Mức lương hấp dẫn trong lĩnh vực công nghệ mới nổi, không giới hạn cơ hội.\n- Được làm việc trong dự án AI trọng điểm của công ty với quy mô đầu tư lớn.\n- Review lương định kỳ hàng năm kèm thưởng hiệu quả vận hành sản phẩm.\n- Tham gia các khóa đào tạo chuyên sâu về AI và Ngôn ngữ học tính toán.\n- Môi trường làm việc sáng tạo, khuyến khích các ý tưởng "out of the box".', 
'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, '2026-03-31 23:59:00', 'Điện Biên Phủ, Bình Thạnh, TP.HCM', 23, 35, '2026-04-15', '2026-10-15', '09:00:00', '18:00:00', 1, 2, 5, 2, 1, 1)
;
CREATE TABLE bill (
  id INT PRIMARY KEY AUTO_INCREMENT,
  created_date TIMESTAMP NOT NULL,
  status ENUM('UNPAID', 'PAID', 'REFUNDED', 'PENDING','CANCELED') NOT NULL,
  amount INT,
  txn_ref VARCHAR(255),
  transaction_no VARCHAR(255),
  transaction_date TIMESTAMP,
  job_id INT,
  FOREIGN KEY (job_id) REFERENCES job(id)
);

-- 8. Bảng CandidateSkill (nhiều-nhiều Candidate - Skill)
CREATE TABLE candidate_skill (
 id INT PRIMARY KEY AUTO_INCREMENT,
 candidate_id INT,
 skill_id INT,
 FOREIGN KEY (candidate_id) REFERENCES candidate(id) ,
 FOREIGN KEY (skill_id) REFERENCES skill(id) 
);


-- 10. Bảng ForeignLanguage (một Candidate có nhiều ngoại ngữ)
CREATE TABLE foreign_language (
  id INT PRIMARY KEY AUTO_INCREMENT,
  language_id INT,
  level_id INT,
  candidate_id INT,
  FOREIGN KEY (language_id) REFERENCES language(id) ,
  FOREIGN KEY (level_id) REFERENCES level_language(id) ,
  FOREIGN KEY (candidate_id) REFERENCES candidate(id) 
);

-- 11. Bảng Application (ứng viên apply job)
CREATE TABLE application (
 id INT PRIMARY KEY AUTO_INCREMENT,
 candidate_id INT,
 job_id INT,
 applied_date TIMESTAMP,
 message TEXT,
 cv VARCHAR(255),
 status ENUM('PENDING', 'APPROVED','CANCELED') NOT NULL,
 contacted BOOLEAN,
 FOREIGN KEY (candidate_id) REFERENCES candidate(id) ,
 FOREIGN KEY (job_id) REFERENCES job(id) 
);

-- 12. Bảng JobAlert (thông báo job cho candidate)
CREATE TABLE job_alert (
   id INT PRIMARY KEY AUTO_INCREMENT,
   candidate_id INT,
   job_id INT,
   notification_status BOOLEAN,
   FOREIGN KEY (candidate_id) REFERENCES candidate(id) ,
   FOREIGN KEY (job_id) REFERENCES job(id) 
);

-- 1. Insert LevelLanguage
INSERT INTO level_language (id, name) VALUES 
(1, 'Basic'), 
(2, 'Intermediate'), 
(3, 'Advanced');

-- 2. Insert Language
INSERT INTO language (id, name) VALUES 
(1, 'English'), 
(2, 'Japanese'), 
(3, 'French'),
(4, 'Korean'),
(5, 'Chinese');

-- 3. Insert Skill
INSERT INTO skill (id, name) VALUES 
(1, 'Java'),
(2, 'JavaScript'),
(3, 'TypeScript'),
(4, 'Python'),
(5, 'C'),
(6, 'C++'),
(7, 'C#'),
(8, 'Go'),
(9, 'Rust'),
(10, 'PHP'),
(11, 'Ruby'),
(12, 'Kotlin'),
(13, 'Swift'),
(14, 'Objective-C'),
(15, 'Scala'),
(16, 'Dart'),
(17, 'R'),
(18, 'MATLAB'),
(19, 'Groovy'),
(20, 'Bash/Shell'),

-- Databases
(21, 'MySQL'),
(22, 'PostgreSQL'),
(23, 'SQL Server'),
(24, 'Oracle'),
(25, 'MongoDB'),
(26, 'Redis'),
(27, 'Elasticsearch'),
(28, 'Cassandra'),
(29, 'MariaDB'),
(30, 'DynamoDB'),
(31, 'Firebase Realtime Database'),
(32, 'Neo4j'),

-- Backend Frameworks
(33, 'Spring Boot'),
(34, '.NET Core'),
(35, 'Express.js'),
(36, 'NestJS'),
(37, 'Django'),
(38, 'Flask'),
(39, 'Ruby on Rails'),
(40, 'Laravel'),
(41, 'FastAPI'),
(42, 'Koa.js'),

-- Frontend Frameworks
(43, 'ReactJS'),
(44, 'Angular'),
(45, 'Vue.js'),
(46, 'Svelte'),
(47, 'Next.js'),
(48, 'Nuxt.js'),

-- Mobile Frameworks
(49, 'React Native'),
(50, 'Flutter'),
(51, 'SwiftUI'),
(52, 'Xamarin'),

-- Authentication / Security
(53, 'JWT'),
(54, 'OAuth 2.0'),
(55, 'OpenID Connect'),
(56, 'SAML'),
(57, 'Basic Auth'),
(58, 'Keycloak'),
(59, 'Okta'),

-- DevOps / Cloud
(60, 'Docker'),
(61, 'Kubernetes'),
(62, 'Jenkins'),
(63, 'GitLab CI/CD'),
(64, 'GitHub Actions'),
(65, 'AWS'),
(66, 'Azure'),
(67, 'Google Cloud Platform'),
(68, 'Terraform'),
(69, 'Ansible'),

-- Architecture
(70, 'Microservices'),
(71, 'Monolithic'),
(72, 'Serverless'),
(73, 'Event-Driven Architecture'),
(74, 'MVC'),
(75, 'MVVM'),
(76, 'Hexagonal Architecture'),
(77, 'Clean Architecture'),

-- Other
(78, 'UI/UX'),
(79, 'Agile'),
(80, 'Scrum'),
(81, 'Kanban');


-- 9. Bảng JobSkill (nhiều-nhiều Job - Skill)
CREATE TABLE job_skill (
   job_id INT,
   skill_id INT,
   PRIMARY KEY (job_id, skill_id),
   FOREIGN KEY (job_id) REFERENCES job(id) ,
   FOREIGN KEY (skill_id) REFERENCES skill(id) 
);

INSERT INTO job_skill VALUES
(5, 1),   -- Java
(5, 21),  -- MySQL
(5, 33),  -- Spring Boot
(5, 53);  -- JWT

-- Frontend React Fresher
INSERT INTO job_skill (job_id, skill_id) VALUES
(6, 2),   -- JavaScript
(6, 3),   -- TypeScript
(6, 43),  -- ReactJS
(6, 78);  -- UI/UX

-- Junior PHP Developer
INSERT INTO job_skill (job_id, skill_id) VALUES
(7, 10),  -- PHP
(7, 21),  -- MySQL
(7, 40),  -- Laravel
(7, 78);  -- UI/UX

-- Middle .NET Developer
INSERT INTO job_skill (job_id, skill_id) VALUES
(8, 7),   -- C#
(8, 23),  -- SQL Server
(8, 34),  -- .NET Core
(8, 70);  -- Microservices

-- Senior Mobile Developer (Flutter)
INSERT INTO job_skill (job_id, skill_id) VALUES
(9, 16),  -- Dart
(9, 50),  -- Flutter
(9, 65),  -- AWS
(9, 78);  -- UI/UX

-- Data Analyst Intern
INSERT INTO job_skill (job_id, skill_id) VALUES
(10, 4),   -- Python
(10, 21),  -- MySQL
(10, 22),  -- PostgreSQL
(10, 17);  -- R

-- Fresher QA Engineer
INSERT INTO job_skill (job_id, skill_id) VALUES
(11, 2),   -- JavaScript
(11, 53),  -- JWT
(11, 79),  -- Agile
(11, 80);  -- Scrum

-- Junior Business Analyst
INSERT INTO job_skill (job_id, skill_id) VALUES
(12, 79),  -- Agile
(12, 80),  -- Scrum
(12, 78);  -- UI/UX

-- Middle DevOps Engineer
INSERT INTO job_skill (job_id, skill_id) VALUES
(13, 60),  -- Docker
(13, 61),  -- Kubernetes
(13, 62),  -- Jenkins
(13, 65);  -- AWS

-- Senior AI Engineer
INSERT INTO job_skill (job_id, skill_id) VALUES
(14, 4),   -- Python
(14, 17),  -- R
(14, 37),  -- Django
(14, 65),  -- AWS
(14, 67);  -- Google Cloud Platform

-- Backend NodeJS Intern
INSERT INTO job_skill (job_id, skill_id) VALUES
(15, 2),   -- JavaScript
(15, 35),  -- Express.js
(15, 36),  -- NestJS
(15, 25);  -- MongoDB

-- Fresher UI/UX Designer
INSERT INTO job_skill (job_id, skill_id) VALUES
(16, 78),  -- UI/UX
(16, 43),  -- ReactJS
(16, 44);  -- Angular

-- Junior Golang Developer
INSERT INTO job_skill (job_id, skill_id) VALUES
(17, 8),   -- Go
(17, 21),  -- MySQL
(17, 70),  -- Microservices
(17, 60);  -- Docker

-- Middle Data Engineer
INSERT INTO job_skill (job_id, skill_id) VALUES
(18, 4),   -- Python
(18, 22),  -- PostgreSQL
(18, 27),  -- Elasticsearch
(18, 60);  -- Docker

-- Senior Project Manager
INSERT INTO job_skill (job_id, skill_id) VALUES
(19, 79),  -- Agile
(19, 80),  -- Scrum
(19, 81);  -- Kanban

-- Backend Python Intern
INSERT INTO job_skill (job_id, skill_id) VALUES
(20, 4),   -- Python
(20, 37),  -- Django
(20, 38),  -- Flask
(20, 21);  -- MySQL

-- Fresher Cloud Engineer
INSERT INTO job_skill (job_id, skill_id) VALUES
(21, 65),  -- AWS
(21, 66),  -- Azure
(21, 67);  -- Google Cloud Platform

-- Junior Security Engineer
INSERT INTO job_skill (job_id, skill_id) VALUES
(22, 53),  -- JWT
(22, 54),  -- OAuth 2.0
(22, 55),  -- OpenID Connect
(22, 60);  -- Docker

-- Middle Fullstack Developer
INSERT INTO job_skill (job_id, skill_id) VALUES
(23, 2),   -- JavaScript
(23, 3),   -- TypeScript
(23, 43),  -- ReactJS
(23, 35),  -- Express.js
(23, 25);  -- MongoDB

-- Senior System Architect
INSERT INTO job_skill (job_id, skill_id) VALUES
(24, 70),  -- Microservices
(24, 72),  -- Serverless
(24, 73),  -- Event-Driven Architecture
(24, 76),  -- Hexagonal Architecture
(24, 77);  -- Clean Architecture



INSERT INTO company_image VALUES 
(1, 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 1),
(2, 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 1);


-- 10. Insert CandidateSkill
INSERT INTO candidate_skill (id, candidate_id, skill_id) VALUES 
(1, 1, 1), -- Candidate 1 - Java
(2, 1, 2), -- Candidate 1 - Spring Boot
(3, 1, 3), -- Candidate 1 - SQL
(4, 2, 4), -- Candidate 2 - JavaScript
(5, 2, 5); -- Candidate 2 - React

-- 11. Insert JobSkill
INSERT INTO job_skill VALUES 
(1, 1), -- Job 1 - Java
(1, 2), -- Job 1 - Spring Boot
(1, 3), -- Job 1 - SQL
(2, 4), -- Job 2 - JavaScript
(2, 5), -- Job 2 - React
(3, 1), -- Job 3 - Java
(3, 2), -- Job 3 - Spring Boot
(3, 5); -- Job 3 - React

-- 12. Insert ForeignLanguage
INSERT INTO foreign_language (id, language_id, level_id, candidate_id) VALUES 
(1, 1, 2, 1), -- Candidate 1 - English Intermediate
(2, 2, 1, 1), -- Candidate 1 - Japanese Basic
(3, 1, 3, 2); -- Candidate 2 - English Advanced

-- 13. Insert Application
INSERT INTO application VALUES 
(1, 1, 1, NOW(), 'I am very interested in this Java Developer position', null, 'APPROVED', 1),
(2, 1, 3, NOW(), 'I would like to apply for the Full Stack Developer role', null, 'CANCELED', 0),
(3, 2, 2, NOW(), 'I am excited about this Frontend Developer opportunity', null, 'PENDING', 0);

-- 14. Insert JobAlert
INSERT INTO job_alert (id, candidate_id, job_id, notification_status) VALUES 
(1, 1, 1, true),
(2, 1, 2, true),
(3, 2, 2, true),
(4, 2, 3, false);

CREATE TABLE it_careers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    job_title VARCHAR(255) NOT NULL,
    key_skills TEXT NOT NULL,
    character_traits TEXT NOT NULL,
    interests TEXT NOT NULL,
    work_styles TEXT NOT NULL,
    holland_code VARCHAR(10)
);

INSERT INTO it_careers (job_title, key_skills, character_traits, interests, work_styles, holland_code) VALUES
('AI/ML Engineer', 'Python, PyTorch, Math, Algorithms, TensorFlow', 'Tư duy nghiên cứu, kiên nhẫn, tò mò', 'data, AI, nghiên cứu', 'làm việc độc lập, tập trung sâu', 'IRC'),
('Backend Developer', 'Java, Spring Boot, SQL, Microservices, Hibernate', 'Tư duy logic, giải quyết vấn đề, kỷ luật', 'hệ thống, backend, công nghệ', 'làm việc độc lập, có cấu trúc', 'RIC'),
('Frontend Developer', 'React, VueJS, HTML/CSS, JavaScript, TypeScript', 'Tỉ mỉ, có gu thẩm mỹ tốt, sáng tạo', 'thiết kế giao diện, UI, trải nghiệm người dùng', 'làm việc nhóm, linh hoạt', 'RIC'),
('Mobile Developer', 'Flutter, Swift, Kotlin, React Native, Firebase', 'Thích sản phẩm thực tế, năng động, thích ứng nhanh', 'ứng dụng mobile, sản phẩm', 'nhịp độ nhanh, thích ứng nhanh', 'RIC'),
('DevOps Engineer', 'Docker, Kubernetes, AWS, CI/CD, Jenkins', 'Thích tự động hóa, chịu áp lực tốt, bình tĩnh', 'hạ tầng, automation, cloud', 'áp lực cao, có hệ thống', 'RIC'),
('Data Analyst', 'SQL, Python, PowerBI, Statistics, Excel', 'Nhạy bén số liệu, cẩn thận, trung thực', 'data, phân tích kinh doanh', 'làm việc độc lập, có cấu trúc', 'ICE'),
('Data Scientist', 'R, Python, Big Data, Deep Learning, Statistics', 'Thích khám phá, tò mò, kiên định', 'data, AI, nghiên cứu', 'tập trung sâu, làm việc độc lập', 'IRC'),
('Cybersecurity Analyst', 'Network, Pentest, SOC, Linux, Wireshark', 'Cẩn thận, tư duy phòng thủ, bảo mật', 'bảo mật, hệ thống, rủi ro', 'làm việc độc lập, cảnh giác cao', 'IRS'),
('UI/UX Designer', 'Figma, User Research, Wireframing, Adobe XD', 'Thấu cảm, sáng tạo, quan sát tỉ mỉ', 'thiết kế, người dùng, sản phẩm', 'làm việc nhóm, sáng tạo', 'AIS'),
('Business Analyst (BA)', 'Requirement Gathering, SQL, UML, Jira', 'Giao tiếp tốt, tư duy hệ thống, linh hoạt', 'kinh doanh, quy trình, con người', 'làm việc nhóm, linh hoạt', 'EIC'),
('QA/QC Engineer', 'Automation Test, Selenium, Jira, Manual Test', 'Kỹ tính, cầu toàn, kiên nhẫn', 'kiểm thử, chất lượng hệ thống', 'có cấu trúc, tỉ mỉ', 'CIR'),
('Cloud Architect', 'Azure, Google Cloud, Networking, Infrastructure', 'Tầm nhìn hệ thống, bao quát, chiến lược', 'cloud, kiến trúc hệ thống', 'chiến lược, có cấu trúc', 'RIE'),
('Game Developer', 'Unity, C++, C#, Physics, Shader', 'Sáng tạo, yêu thích trải nghiệm, kiên trì', 'game, sáng tạo, sản phẩm', 'sáng tạo, lặp lại cải tiến', 'ARI'),
('Blockchain Developer', 'Solidity, Rust, Cryptography, P2P Network', 'Tư duy thuật toán, bảo mật, đổi mới', 'blockchain, bảo mật, hệ thống', 'làm việc độc lập, thử nghiệm', 'IRC'),
('Embedded Engineer', 'C, C++, Microcontrollers, RTOS, IoT', 'Kiên trì, tư duy phần cứng, chi tiết', 'phần cứng, IoT, hệ thống', 'tập trung sâu, chính xác', 'RIC'),
('System Administrator', 'Windows Server, Linux, Networking, Security', 'Trách nhiệm, thực tế, bình tĩnh', 'hệ thống, hạ tầng vận hành', 'ổn định, phản ứng nhanh', 'RCI'),
('Database Admin (DBA)', 'Oracle, MySQL, Backup/Recovery, Performance Tuning', 'Cẩn thận, bảo mật dữ liệu, chính xác', 'data, database, hệ thống', 'có cấu trúc, cẩn thận', 'CIR'),
('Product Manager', 'Product Vision, Roadmap, Agile, User Centric', 'Lãnh đạo, quyết đoán, thấu hiểu khách hàng', 'sản phẩm, kinh doanh, người dùng', 'làm việc nhóm, chiến lược', 'ECI'),
('IT Project Manager', 'PMP, Agile/Scrum, Risk Management, Budgeting', 'Quản lý thời gian, điều phối, giao tiếp', 'quản lý, quy trình, đội nhóm', 'có cấu trúc, lãnh đạo', 'ECR'),
('Solution Architect', 'Design Patterns, System Design, Scalability', 'Kinh nghiệm dày dạn, bao quát, phân tích', 'kiến trúc hệ thống, giải pháp', 'chiến lược, phân tích', 'RIE'),
('Fullstack Developer', 'Node.js, React, PostgreSQL, API Design', 'Đa năng, ham học hỏi, linh hoạt', 'web, sản phẩm, hệ thống', 'linh hoạt, thích ứng', 'RIC'),
('Data Engineer', 'Spark, Hadoop, ETL, Data Pipeline, Airflow', 'Logic, thích xây dựng hệ thống, bền bỉ', 'data pipeline, hệ thống', 'có cấu trúc, bền bỉ', 'RIC'),
('Security Engineer', 'Encryption, Firewall, Incident Response, Python', 'Phân tích sâu, nhạy bén, trung thực', 'bảo mật, rủi ro, hệ thống', 'cảnh giác, phân tích', 'IRS'),
('Bridge SE (BrSE)', 'Japanese/English, Coding, N2, Management', 'Giao tiếp liên văn hóa, kết nối, trách nhiệm', 'giao tiếp, kinh doanh, công nghệ', 'làm việc nhóm, đa văn hóa', 'ESC'),
('Technical Architect', 'Tech Stack Selection, Scalability, High Availability', 'Tư duy chiến lược kỹ thuật, điềm đạm', 'kiến trúc, hệ thống', 'chiến lược, bình tĩnh', 'RIE'),
('Mobile Game Designer', 'Game Logic, Storytelling, UI, Concept Art', 'Sáng tạo, hiểu tâm lý người chơi, bay bổng', 'game, storytelling, thiết kế', 'sáng tạo, linh hoạt', 'ASI'),
('AI Researcher', 'Research Paper, Advanced Math, Deep Learning', 'Học thuật, kiên định, tư duy phản biện', 'AI, nghiên cứu, học thuật', 'tập trung sâu, độc lập', 'IAR'),
('IoT Developer', 'Sensors, MQTT, C/C++, Hardware, ESP32', 'Tò mò, thích vạn vật kết nối, thực hành', 'IoT, phần cứng, hệ thống', 'thực hành, thử nghiệm', 'RIC'),
('Automation Test Eng', 'Java/Python, Appium, Cucumber, Jenkins', 'Tư duy lập trình tốt, tỉ mỉ, logic', 'kiểm thử, automation', 'có cấu trúc, tỉ mỉ', 'RIC'),
('Network Engineer', 'Cisco, Routing, Switching, Firewall', 'Kỹ thuật thực tế, chính xác, cẩn trọng', 'network, hạ tầng', 'có cấu trúc, chính xác', 'RCI'),
('Information Architect', 'Content Structure, Taxonomy, UX Research', 'Tổ chức tốt, tư duy logic, hệ thống', 'nội dung, UX, cấu trúc', 'có tổ chức, phân tích', 'ICE'),
('Scrum Master', 'Coaching, Facilitation, Agile, Conflict Resolution', 'Kiên nhẫn, hỗ trợ mọi người, tinh tế', 'đội nhóm, agile, con người', 'hỗ trợ, làm việc nhóm', 'SAE'),
('ERP Consultant', 'SAP, Oracle ERP, Business Process, Finance', 'Phân tích quy trình, thực tế, tư vấn', 'ERP, quy trình, tài chính', 'tư vấn, có cấu trúc', 'ECI'),
('SEO Specialist', 'Technical SEO, Keyword Research, Analytics', 'Phân tích xu hướng, bền bỉ, thích nghi', 'SEO, marketing, data', 'thích ứng, phân tích', 'EIC'),
('Growth Hacker', 'Data-driven Marketing, Coding, Experiments', 'Đột phá, nhạy bén thị trường, quyết liệt', 'growth, marketing, data', 'thử nghiệm, nhịp độ nhanh', 'EAI'),
('Cloud Security Eng', 'IAM, Cloud Governance, Security, Compliance', 'Cẩn trọng, am hiểu đám mây, tuân thủ', 'cloud, bảo mật, compliance', 'có cấu trúc, cẩn thận', 'IRC'),
('Firmware Engineer', 'Low-level C, Assembly, Debugging, Hardware', 'Tư duy máy móc, kiên nhẫn, chính xác', 'firmware, phần cứng', 'tập trung sâu, chính xác', 'RIC'),
('Technical Writer', 'Documentation, API Guide, Copywriting', 'Diễn đạt tốt, kiên nhẫn, tỉ mỉ', 'viết tài liệu, công nghệ', 'làm việc độc lập, tỉ mỉ', 'CSI'),
('Support Engineer (L3)', 'Troubleshooting, Log analysis, Customer Service', 'Bình tĩnh, xử lý vấn đề, thấu cảm', 'support, khách hàng, hệ thống', 'phản ứng nhanh, bình tĩnh', 'RSI'),
('Site Reliability Eng', 'Python, Go, SRE Principles, Observability', 'Tư duy hệ thống ổn định, chủ động', 'SRE, hệ thống, devops', 'chủ động, có cấu trúc', 'RIC'),
('NLP Engineer', 'BERT, GPT, Tokenization, Linguistics', 'Ngôn ngữ học, thuật toán, tư duy sâu', 'NLP, AI, ngôn ngữ', 'tập trung sâu, phân tích', 'IRC'),
('Computer Vision Eng', 'OpenCV, CNN, Image Processing, YOLO', 'Quan sát hình học tốt, logic, chính xác', 'computer vision, AI, hình ảnh', 'phân tích, chính xác', 'IRC'),
('AR/VR Developer', 'C#, Unity, 3D Modeling, Blender', 'Tư duy không gian, sáng tạo, thực nghiệm', 'AR, VR, 3D', 'sáng tạo, thử nghiệm', 'ARI'),
('Hardware Designer', 'PCB Design, Altium, Electronics, Simulation', 'Kỹ thuật điện, chi tiết, kiên trì', 'phần cứng, điện tử', 'chính xác, có cấu trúc', 'RIC'),
('IT Auditor', 'Compliance, ISO 27001, Risk, Audit Process', 'Tuân thủ, liêm chính, công bằng', 'kiểm toán, compliance, rủi ro', 'có cấu trúc, khách quan', 'CEI'),
('Presales Engineer', 'Technical Demo, Presentation, Solution Selling', 'Thuyết phục, hiểu kỹ thuật, năng động', 'sales, giải pháp, công nghệ', 'năng động, thuyết phục', 'ESI'),
('Digital Forensic', 'Evidence Collection, Investigation, Law', 'Phân tích tội phạm, công tâm, kỷ luật', 'điều tra số, bảo mật, pháp luật', 'phân tích, kỷ luật', 'IRE'),
('Robotics Engineer', 'ROS, Control Theory, Mechanical, C++', 'Thích cơ khí và phần mềm, sáng tạo', 'robotics, cơ khí, AI', 'thực hành, sáng tạo', 'RIC'),
('Salesforce Dev', 'Apex, SOQL, CRM Logic, LWC', 'Tập trung quy trình khách hàng, cẩn thận', 'CRM, kinh doanh, hệ thống', 'có cấu trúc, hướng khách hàng', 'RCE'),
('Prompt Engineer', 'LLM, Prompting, Fine-tuning, Evaluation', 'Thử nghiệm, ngôn ngữ tốt, sáng tạo', 'AI, ngôn ngữ, experimentation', 'sáng tạo, thử nghiệm', 'AIR');