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
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS conversations;
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
   experience TEXT,
   preferred_location TEXT,
   preferred_salary TEXT,
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
CREATE TABLE conversations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    candidate_id INT NOT NULL, -- ID của user từ hệ thống Auth
    title VARCHAR(255),
    status ENUM('OPEN', 'CLOSED','ARCHIVED'),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (candidate_id) REFERENCES candidate(id) 
);

-- 3. Tạo Bảng Messages (Chi tiết tin nhắn)
CREATE TABLE messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    conversation_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP,
    
    -- Khóa ngoại liên kết tới bảng conversations
        FOREIGN KEY(conversation_id) 
        REFERENCES conversations(id) 
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

 salary_min DECIMAL(10,2) NOT NULL,

 salary_max DECIMAL(10,2) NOT NULL,

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
    id, title, description, salary_min, salary_max, job_require, benefits, 
    status, created_date, updated_at, vector_updated_at, posted_date, 
    address, age_from, age_to, start_date, end_date, start_time, 
    end_time, employer_id, city_id, district_id, job_level_id, job_type_id, contract_type_id
) VALUES 
(1, 'Java Developer', 'Develop backend services using Spring Boot', 1000, 2000, 'Java, Spring Boot, SQL', 'Đi chơi 1 năm 2 lần', 'APPROVED', NOW(), NOW(), NOW(), NOW(), 'Hồ Chí Minh', 18, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 1, 1, 1, 1),

(2, 'Frontend Developer', 'Develop user interface using React', 800, 1500, 'JavaScript, React, HTML, CSS', 'Đi chơi 1 năm 2 lần', 'CANCELED', NOW(), NOW(), NOW(), NOW(), 'Hà Nội', 18, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 2, 40, 1, 1, 1),

(3, 'Full Stack Developer', 'Develop both frontend and backend', 1200, 2500, 'Java, Spring Boot, React, SQL', 'Đi chơi 1 năm 2 lần', 'PENDING', NOW(), NOW(), NOW(), NOW(), 'Đà Nẵng', 18, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 3, 54, 1, 1, 1),

(4, 'Senior Frontend Developer (Angular, ReactJS)', '<ol><li>Xây dựng bản thảo UI/UX</li><li>Thiết kế kiến trúc Frontend</li><li>Tích hợp API</li></ol>', 1200, 2500, '≥3 năm kinh nghiệm FE, Angular, React', 'Phúc lợi hấp dẫn, bảo hiểm sức khỏe', 'APPROVED', NOW(), NOW(), NOW(), NOW(), 'Tầng 1, Vincom Cộng Hòa, Tân Bình, HCM', 18, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 3, 54, 1, 1, 1),

(5, 'Backend Java Intern', '<ol><li>Học cách xây dựng API bằng Spring Boot</li><li>Hỗ trợ fix bug</li></ol>', 200, 400, 'Sinh viên CNTT năm 3, 4, hiểu Java', 'Cơ hội lên chính thức', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '12 Nguyễn Văn Bảo, Gò Vấp, HCM', 20, 25, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 10, 1, 1, 1),

(6, 'Frontend React Fresher', '<ol><li>Phát triển giao diện web</li></ol>', 400, 800, 'Tốt nghiệp CNTT, biết React', 'BHYT, BHXH', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '25 Lê Lợi, Quận 1, HCM', 21, 26, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 5, 2, 1, 1),

(7, 'Junior PHP Developer', '<ol><li>Xây dựng website với Laravel</li></ol>', 600, 1200, '1 năm kinh nghiệm PHP', 'Thưởng theo dự án', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '78 Trần Phú, Hải Châu, Đà Nẵng', 22, 28, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 3, 22, 3, 1, 1),

(8, 'Middle .NET Developer', '<ol><li>Phát triển hệ thống ERP</li></ol>', 1000, 1800, '≥3 năm .NET', 'Thưởng hiệu suất', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '21 Nguyễn Văn Linh, Đà Nẵng', 25, 35, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 3, 30, 4, 1, 1),

(9, 'Senior Mobile Developer (Flutter)', '<ol><li>Phát triển app iOS/Android</li></ol>', 1500, 2800, '≥5 năm Mobile, Flutter', 'Lương cạnh tranh', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '102 Hoàng Hoa Thám, Tân Bình, HCM', 27, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 18, 5, 1, 1),

(10, 'Data Analyst Intern', '<ol><li>Xử lý dữ liệu</li></ol>', 200, 500, 'Sinh viên năm cuối, biết SQL', 'Tham gia workshop', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '35 Nguyễn Huệ, Quận 1, HCM', 20, 25, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 3, 1, 1, 1),

(11, 'Fresher QA Engineer', 'Kiểm thử chức năng', 400, 700, 'Kiến thức kiểm thử', 'Ăn trưa miễn phí', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '89 Xã Đàn, Hà Nội', 21, 26, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 2, 45, 2, 1, 1),

(12, 'Junior Business Analyst', 'Viết tài liệu nghiệp vụ', 600, 1200, '1 năm BA', 'Lương tháng 13', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '12 Láng Hạ, Ba Đình, Hà Nội', 23, 30, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 2, 50, 3, 1, 1),

(13, 'Middle DevOps Engineer', 'Quản lý CI/CD', 1200, 2000, '≥3 năm DevOps, Docker', 'Hỗ trợ chứng chỉ', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '42 Nguyễn Hữu Cảnh, HCM', 25, 35, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 14, 4, 1, 1),

(14, 'Senior AI Engineer', 'Xây dựng mô hình ML', 2000, 3500, '≥5 năm AI, Python', 'Đào tạo nâng cao', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '10 Duy Tân, Cầu Giấy, Hà Nội', 28, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 2, 36, 5, 1, 1),

(15, 'Backend NodeJS Intern', 'Học phát triển API NodeJS', 200, 400, 'Sinh viên CNTT', 'Team trẻ trung', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '15 Võ Văn Ngân, Thủ Đức, HCM', 20, 24, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 8, 1, 1, 1),

(16, 'Fresher UI/UX Designer', 'Thiết kế giao diện', 400, 700, 'Biết Figma', 'Môi trường sáng tạo', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '68 Nguyễn Trãi, Hà Nội', 21, 26, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 2, 44, 2, 1, 1),

(17, 'Junior Golang Developer', 'Phát triển API tốc độ cao', 700, 1300, '1 năm Go', 'Thưởng dự án', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '33 Điện Biên Phủ, HCM', 23, 30, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 11, 3, 1, 1),

(18, 'Middle Data Engineer', 'Xây dựng pipeline dữ liệu', 1200, 2000, '≥3 năm Data Engineer', 'Lương thưởng hấp dẫn', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '27 Nguyễn Văn Cừ, Quận 5, HCM', 25, 34, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 7, 4, 1, 1),

(19, 'Senior Project Manager', 'Quản lý dự án phần mềm', 1800, 3200, '≥5 năm PM', 'Làm việc linh hoạt', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '123 Phạm Văn Đồng, Hà Nội', 28, 42, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 2, 55, 5, 1, 1),

(20, 'Backend Python Intern', 'Học Flask/Django', 200, 450, 'Biết Python cơ bản', 'Mentor hỗ trợ', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '45 Nguyễn Thị Minh Khai, HCM', 20, 24, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 1, 6, 1, 1, 1),

(21, 'Fresher Cloud Engineer', 'Triển khai dịch vụ cloud', 500, 900, 'Kiến thức cơ bản Cloud', 'Đào tạo chứng chỉ', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '19 Nguyễn Văn Cừ, Hà Nội', 21, 26, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 2, 47, 2, 1, 1),

(22, 'Junior Security Engineer', 'Phân tích log hệ thống', 700, 1400, '1 năm security', 'Bảo hiểm sức khỏe', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '66 Nguyễn Văn Linh, Đà Nẵng', 23, 30, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 3, 27, 3, 1, 1),

(23, 'Middle Fullstack Developer', 'Phát triển FE và BE', 1200, 2100, '≥3 năm JavaScript', 'Chế độ đầy đủ', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '22 Hoàng Diệu, Đà Nẵng', 25, 35, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 3, 33, 4, 1, 1),

(24, 'Senior System Architect', 'Thiết kế kiến trúc hệ thống', 2500, 4000, '≥8 năm kinh nghiệm', 'Lương cao ngất', 'APPROVED', NOW(), NOW(), NOW(), NOW(), '11 Trần Duy Hưng, Hà Nội', 30, 45, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00', 1, 2, 39, 5, 1, 1);
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
    holland_code VARCHAR(10)
);

INSERT INTO it_careers (job_title, key_skills, character_traits, holland_code) VALUES
('AI/ML Engineer', 'Python, PyTorch, Math, Algorithms, TensorFlow', 'Tư duy nghiên cứu, kiên nhẫn, tò mò', 'IRC'),
('Backend Developer', 'Java, Spring Boot, SQL, Microservices, Hibernate', 'Tư duy logic, giải quyết vấn đề, kỷ luật', 'RIC'),
('Frontend Developer', 'React, VueJS, HTML/CSS, JavaScript, TypeScript', 'Tỉ mỉ, có gu thẩm mỹ tốt, sáng tạo', 'RIC'),
('Mobile Developer', 'Flutter, Swift, Kotlin, React Native, Firebase', 'Thích sản phẩm thực tế, năng động, thích ứng nhanh', 'RIC'),
('DevOps Engineer', 'Docker, Kubernetes, AWS, CI/CD, Jenkins', 'Thích tự động hóa, chịu áp lực tốt, bình tĩnh', 'RIC'),
('Data Analyst', 'SQL, Python, PowerBI, Statistics, Excel', 'Nhạy bén số liệu, cẩn thận, trung thực', 'ICE'),
('Data Scientist', 'R, Python, Big Data, Deep Learning, Statistics', 'Thích khám phá, tò mò, kiên định', 'IRC'),
('Cybersecurity Analyst', 'Network, Pentest, SOC, Linux, Wireshark', 'Cẩn thận, tư duy phòng thủ, bảo mật', 'IRS'),
('UI/UX Designer', 'Figma, User Research, Wireframing, Adobe XD', 'Thấu cảm, sáng tạo, quan sát tỉ mỉ', 'AIS'),
('Business Analyst (BA)', 'Requirement Gathering, SQL, UML, Jira', 'Giao tiếp tốt, tư duy hệ thống, linh hoạt', 'EIC'),
('QA/QC Engineer', 'Automation Test, Selenium, Jira, Manual Test', 'Kỹ tính, cầu toàn, kiên nhẫn', 'CIR'),
('Cloud Architect', 'Azure, Google Cloud, Networking, Infrastructure', 'Tầm nhìn hệ thống, bao quát, chiến lược', 'RIE'),
('Game Developer', 'Unity, C++, C#, Physics, Shader', 'Sáng tạo, yêu thích trải nghiệm, kiên trì', 'ARI'),
('Blockchain Developer', 'Solidity, Rust, Cryptography, P2P Network', 'Tư duy thuật toán, bảo mật, đổi mới', 'IRC'),
('Embedded Engineer', 'C, C++, Microcontrollers, RTOS, IoT', 'Kiên trì, tư duy phần cứng, chi tiết', 'RIC'),
('System Administrator', 'Windows Server, Linux, Networking, Security', 'Trách nhiệm, thực tế, bình tĩnh', 'RCI'),
('Database Admin (DBA)', 'Oracle, MySQL, Backup/Recovery, Performance Tuning', 'Cẩn thận, bảo mật dữ liệu, chính xác', 'CIR'),
('Product Manager', 'Product Vision, Roadmap, Agile, User Centric', 'Lãnh đạo, quyết đoán, thấu hiểu khách hàng', 'ECI'),
('IT Project Manager', 'PMP, Agile/Scrum, Risk Management, Budgeting', 'Quản lý thời gian, điều phối, giao tiếp', 'ECR'),
('Solution Architect', 'Design Patterns, System Design, Scalability', 'Kinh nghiệm dày dạn, bao quát, phân tích', 'RIE'),
('Fullstack Developer', 'Node.js, React, PostgreSQL, API Design', 'Đa năng, ham học hỏi, linh hoạt', 'RIC'),
('Data Engineer', 'Spark, Hadoop, ETL, Data Pipeline, Airflow', 'Logic, thích xây dựng hệ thống, bền bỉ', 'RIC'),
('Security Engineer', 'Encryption, Firewall, Incident Response, Python', 'Phân tích sâu, nhạy bén, trung thực', 'IRS'),
('Bridge SE (BrSE)', 'Japanese/English, Coding, N2, Management', 'Giao tiếp liên văn hóa, kết nối, trách nhiệm', 'ESC'),
('Technical Architect', 'Tech Stack Selection, Scalability, High Availability', 'Tư duy chiến lược kỹ thuật, điềm đạm', 'RIE'),
('Mobile Game Designer', 'Game Logic, Storytelling, UI, Concept Art', 'Sáng tạo, hiểu tâm lý người chơi, bay bổng', 'ASI'),
('AI Researcher', 'Research Paper, Advanced Math, Deep Learning', 'Học thuật, kiên định, tư duy phản biện', 'IAR'),
('IoT Developer', 'Sensors, MQTT, C/C++, Hardware, ESP32', 'Tò mò, thích vạn vật kết nối, thực hành', 'RIC'),
('Automation Test Eng', 'Java/Python, Appium, Cucumber, Jenkins', 'Tư duy lập trình tốt, tỉ mỉ, logic', 'RIC'),
('Network Engineer', 'Cisco, Routing, Switching, Firewall', 'Kỹ thuật thực tế, chính xác, cẩn trọng', 'RCI'),
('Information Architect', 'Content Structure, Taxonomy, UX Research', 'Tổ chức tốt, tư duy logic, hệ thống', 'ICE'),
('Scrum Master', 'Coaching, Facilitation, Agile, Conflict Resolution', 'Kiên nhẫn, hỗ trợ mọi người, tinh tế', 'SAE'),
('ERP Consultant', 'SAP, Oracle ERP, Business Process, Finance', 'Phân tích quy trình, thực tế, tư vấn', 'ECI'),
('SEO Specialist', 'Technical SEO, Keyword Research, Analytics', 'Phân tích xu hướng, bền bỉ, thích nghi', 'EIC'),
('Growth Hacker', 'Data-driven Marketing, Coding, Experiments', 'Đột phá, nhạy bén thị trường, quyết liệt', 'EAI'),
('Cloud Security Eng', 'IAM, Cloud Governance, Security, Compliance', 'Cẩn trọng, am hiểu đám mây, tuân thủ', 'IRC'),
('Firmware Engineer', 'Low-level C, Assembly, Debugging, Hardware', 'Tư duy máy móc, kiên nhẫn, chính xác', 'RIC'),
('Technical Writer', 'Documentation, API Guide, Copywriting', 'Diễn đạt tốt, kiên nhẫn, tỉ mỉ', 'CSI'),
('Support Engineer (L3)', 'Troubleshooting, Log analysis, Customer Service', 'Bình tĩnh, xử lý vấn đề, thấu cảm', 'RSI'),
('Site Reliability Eng', 'Python, Go, SRE Principles, Observability', 'Tư duy hệ thống ổn định, chủ động', 'RIC'),
('NLP Engineer', 'BERT, GPT, Tokenization, Linguistics', 'Ngôn ngữ học, thuật toán, tư duy sâu', 'IRC'),
('Computer Vision Eng', 'OpenCV, CNN, Image Processing, YOLO', 'Quan sát hình học tốt, logic, chính xác', 'IRC'),
('AR/VR Developer', 'C#, Unity, 3D Modeling, Blender', 'Tư duy không gian, sáng tạo, thực nghiệm', 'ARI'),
('Hardware Designer', 'PCB Design, Altium, Electronics, Simulation', 'Kỹ thuật điện, chi tiết, kiên trì', 'RIC'),
('IT Auditor', 'Compliance, ISO 27001, Risk, Audit Process', 'Tuân thủ, liêm chính, công bằng', 'CEI'),
('Presales Engineer', 'Technical Demo, Presentation, Solution Selling', 'Thuyết phục, hiểu kỹ thuật, năng động', 'ESI'),
('Digital Forensic', 'Evidence Collection, Investigation, Law', 'Phân tích tội phạm, công tâm, kỷ luật', 'IRE'),
('Robotics Engineer', 'ROS, Control Theory, Mechanical, C++', 'Thích cơ khí và phần mềm, sáng tạo', 'RIC'),
('Salesforce Dev', 'Apex, SOQL, CRM Logic, LWC', 'Tập trung quy trình khách hàng, cẩn thận', 'RCE'),
('Prompt Engineer', 'LLM, Prompting, Fine-tuning, Evaluation', 'Thử nghiệm, ngôn ngữ tốt, sáng tạo', 'AIR');