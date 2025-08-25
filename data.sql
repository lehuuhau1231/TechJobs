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
DROP TABLE IF EXISTS contract_type;
DROP TABLE IF EXISTS company_image;
DROP TABLE IF EXISTS employer;
DROP TABLE IF EXISTS candidate;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS language;
DROP TABLE IF EXISTS level_language;

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
(1, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'candidate1@email.com', '0123456789','phường 13', 'Quận 1', 'Ho Chi Minh','CANDIDATE'),
(2, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'candidate2@email.com', '0123456790','phường 15', 'Quận 5', 'Ho Chi Minh','CANDIDATE'),
(3, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'employer1@email.com', '0987654321','phường 13', 'Hanoi', 'Cau Giay','EMPLOYER'),
(4, '$2a$10$l1OgCBN.SZJR0YdI0NW4EuMJdBWoaDxVsv.gZazqUKyoPh4JjgSJ.', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'admin@email.com', '0987654322','phường 13', 'District 3', 'Cau Giay','ADMIN');

-- 5. Bảng Candidate
CREATE TABLE candidate (
   id INT PRIMARY KEY AUTO_INCREMENT,
   full_name VARCHAR(50),
   self_description TEXT,
   birth_date DATE,
   cv VARCHAR(255),
   user_id INT UNIQUE,
   CONSTRAINT fk_candidate_user FOREIGN KEY (user_id) REFERENCES user(id) 
);

-- 7. Insert Candidate
INSERT INTO candidate VALUES 
(1, 'Le Huu Hau', 'I am a Java developer with 3 years of experience', '1995-01-01', 'https://res.cloudinary.com/dndsrbf9s/image/upload/v1755098353/v8zoo3pariinuwrmrvfv.pdf', 1),
(2, 'Dang Van Binh', 'I am a frontend developer passionate about React', '1995-01-01', null, 2);

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
(1, 'Cong ty ABC', 'AB198293CB', 'APPROVED', 3);

CREATE TABLE company_image (
  id INT PRIMARY KEY AUTO_INCREMENT,
  image VARCHAR(255),
  employer_id INT,
  FOREIGN KEY (employer_id) REFERENCES user(id) 
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

-- 7. Bảng Job
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


-- 9. Insert Job
INSERT INTO job VALUES 
(1, 'Java Developer', 'Develop backend services using Spring Boot', 1000, 2000, 'Java, Spring Boot, SQL', 'Đi chơi 1 năm 2 lần', 'APPROVED', NOW(), NOW(), 'Hồ Chí minh', 18, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00',1, 1, 1, 1, 1,1),
(2, 'Frontend Developer', 'Develop user interface using React', 800, 1500, 'JavaScript, React, HTML, CSS', 'Đi chơi 1 năm 2 lần', 'CANCELED', NOW(), NOW(), 'Hà Nội', 18, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00',1, 2, 40, 1, 1,1),
(3, 'Full Stack Developer', 'Develop both frontend and backend', 1200, 2500, 'Java, Spring Boot, React, SQL', 'Đi chơi 1 năm 2 lần', 'PENDING', NOW(), NOW(), 'Đà Nẵng', 18, 40, '2025-08-10', '2025-09-10', '08:00:00', '17:00:00',1, 3, 54, 1, 1,1);

CREATE TABLE bill (
  id INT PRIMARY KEY AUTO_INCREMENT,
  created_date TIMESTAMP NOT NULL,
  status ENUM('UNPAID', 'PAID', 'REFUNDED','CANCELED') NOT NULL,
  amount INT,
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

-- 9. Bảng JobSkill (nhiều-nhiều Job - Skill)
CREATE TABLE job_skill (
   job_id INT,
   skill_id INT,
   PRIMARY KEY (job_id, skill_id),
   FOREIGN KEY (job_id) REFERENCES job(id) ,
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
(1, 1, 1, NOW(), 'I am very interested in this Java Developer position', null, 'APPROVED'),
(2, 1, 3, NOW(), 'I would like to apply for the Full Stack Developer role', null, 'CANCELED'),
(3, 2, 2, NOW(), 'I am excited about this Frontend Developer opportunity', null, 'PENDING');

-- 14. Insert JobAlert
INSERT INTO job_alert (id, candidate_id, job_id, notification_status) VALUES 
(1, 1, 1, true),
(2, 1, 2, true),
(3, 2, 2, true),
(4, 2, 3, false);