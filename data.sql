-- Xóa bảng nếu tồn tại (theo thứ tự tránh lỗi ràng buộc FK)
DROP TABLE IF EXISTS job_alert;
DROP TABLE IF EXISTS application;
DROP TABLE IF EXISTS foreign_language;
DROP TABLE IF EXISTS job_skill;
DROP TABLE IF EXISTS candidate_skill;
DROP TABLE IF EXISTS job;
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
	username VARCHAR(100) UNIQUE NOT NULL,
	password VARCHAR(255) NOT NULL,
	avatar VARCHAR(255),
	email VARCHAR(100) UNIQUE NOT NULL,
	phone VARCHAR(20),
	full_name VARCHAR(100),
	full_address VARCHAR(255),
	district VARCHAR(100),
	city VARCHAR(100),
    role ENUM('CANDIDATE', 'EMPLOYER','ADMIN') NOT NULL
);

-- 5. Bảng Candidate
CREATE TABLE candidate (
   id INT PRIMARY KEY AUTO_INCREMENT,
   self_description TEXT,
   phone VARCHAR(20),
   avatar VARCHAR(255),
   birth_date DATE,
   user_id INT UNIQUE,
   CONSTRAINT fk_candidate_user FOREIGN KEY (user_id) REFERENCES user(id) 
);

-- 6. Bảng Employer
CREATE TABLE employer (
  id INT PRIMARY KEY AUTO_INCREMENT,
  company_name VARCHAR(255) NOT NULL,
  image VARCHAR(255),
  status ENUM('PENDING', 'APPROVED','CANCELED') NOT NULL,
  user_id INT UNIQUE,
  FOREIGN KEY (user_id) REFERENCES user(id) 
);

CREATE TABLE company_image (
  id INT PRIMARY KEY AUTO_INCREMENT,
  image VARCHAR(255),
  employer_id INT,
  FOREIGN KEY (employer_id) REFERENCES user(id) 
);

-- 7. Bảng Job
CREATE TABLE job (
 id INT PRIMARY KEY AUTO_INCREMENT,
 title VARCHAR(255) NOT NULL,
 description TEXT,
 salary_min DECIMAL(10,2),
 salary_max DECIMAL(10,2),
 job_require TEXT,
 status ENUM('PENDING', 'APPROVED','CANCELED') NOT NULL,
 created_date TIMESTAMP,
 posted_date TIMESTAMP,
 employer_id INT,
 FOREIGN KEY (employer_id) REFERENCES employer(id) 
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
   id INT PRIMARY KEY AUTO_INCREMENT,
   job_id INT,
   skill_id INT,
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
(2, 'Spring Boot'), 
(3, 'SQL'),
(4, 'JavaScript'),
(5, 'React'),
(6, 'Python'),
(7, 'Docker'),
(8, 'AWS');

-- 4. Insert User (CANDIDATE)
INSERT INTO user (id, username, password, avatar, email, phone, full_name, full_address, city, district) VALUES 
(1, 'candidate1', '$2a$10$rDmFN6ZqJdcQKzKzKzKzK.zKzKzKzKzKzKzKzKzKzKzKzKzKzKzK', NULL, 'candidate1@email.com', '0123456789', 'Nguyen Van A','phường 13', 'Quận 1', 'Ho Chi Minh'),
(2, 'candidate2', '$2a$10$rDmFN6ZqJdcQKzKzKzKzK.zKzKzKzKzKzKzKzKzKzKzKzKzKzKzK', NULL, 'candidate2@email.com', '0123456790', 'Tran Thi B','phường 15', 'Quận 5', 'Ho Chi Minh'),
(3, 'employer', '$2a$10$rDmFN6ZqJdcQKzKzKzKzK.zKzKzKzKzKzKzKzKzKzKzKzKzKzKzK', NULL, 'employer1@email.com', '0987654321', 'Nguyen Van C','phường 13', 'Hanoi', 'Cau Giay'),
(4, 'admin', '$2a$10$rDmFN6ZqJdcQKzKzKzKzK.zKzKzKzKzKzKzKzKzKzKzKzKzKzKzK', NULL, 'admin@email.com', '0987654322', 'Nguyen Van D','phường 13', 'District 3', 'Cau Giay');

-- 7. Insert Candidate
INSERT INTO candidate (id, self_description, avatar, birth_date, user_id) VALUES 
(1, 'I am a Java developer with 3 years of experience', 'https://deviet.vn/wp-content/uploads/2019/04/vuong-quoc-anh.jpg', '1995-01-01', 1),
(2, 'I am a frontend developer passionate about React', 'https://deviet.vn/wp-content/uploads/2019/04/vuong-quoc-anh.jpg', '1995-01-01', 2);

-- 8. Insert Employer
INSERT INTO employer (id, company_name, image, status, user_id) VALUES 
(1, 'Cong ty ABC', 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 'APPROVED', 3);


INSERT INTO company_image VALUES 
(1, 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 1),
(2, 'https://png.pngtree.com/element_pic/16/11/03/dda587d35b48fd01947cf38931323161.jpg', 1);

-- 9. Insert Job
INSERT INTO job (id, title, description, salary_min, salary_max, job_require, status, created_date, posted_date, employer_id) VALUES 
(1, 'Java Developer', 'Develop backend services using Spring Boot', 1000, 2000, 'Java, Spring Boot, SQL', 'APPROVED', NOW(), NOW(), 1),
(2, 'Frontend Developer', 'Develop user interface using React', 800, 1500, 'JavaScript, React, HTML, CSS', 'CANCELED', NOW(), NOW(), 1),
(3, 'Full Stack Developer', 'Develop both frontend and backend', 1200, 2500, 'Java, Spring Boot, React, SQL', 'PENDING', NOW(), NOW(), 1);

-- 10. Insert CandidateSkill
INSERT INTO candidate_skill (id, candidate_id, skill_id) VALUES 
(1, 1, 1), -- Candidate 1 - Java
(2, 1, 2), -- Candidate 1 - Spring Boot
(3, 1, 3), -- Candidate 1 - SQL
(4, 2, 4), -- Candidate 2 - JavaScript
(5, 2, 5); -- Candidate 2 - React

-- 11. Insert JobSkill
INSERT INTO job_skill (id, job_id, skill_id) VALUES 
(1, 1, 1), -- Job 1 - Java
(2, 1, 2), -- Job 1 - Spring Boot
(3, 1, 3), -- Job 1 - SQL
(4, 2, 4), -- Job 2 - JavaScript
(5, 2, 5), -- Job 2 - React
(6, 3, 1), -- Job 3 - Java
(7, 3, 2), -- Job 3 - Spring Boot
(8, 3, 5); -- Job 3 - React

-- 12. Insert ForeignLanguage
INSERT INTO foreign_language (id, language_id, level_id, candidate_id) VALUES 
(1, 1, 2, 1), -- Candidate 1 - English Intermediate
(2, 2, 1, 1), -- Candidate 1 - Japanese Basic
(3, 1, 3, 2); -- Candidate 2 - English Advanced

-- 13. Insert Application
INSERT INTO application (id, candidate_id, job_id, applied_date, message, status) VALUES 
(1, 1, 1, NOW(), 'I am very interested in this Java Developer position', 'APPROVED'),
(2, 1, 3, NOW(), 'I would like to apply for the Full Stack Developer role', 'CANCELED'),
(3, 2, 2, NOW(), 'I am excited about this Frontend Developer opportunity', 'PENDING');

-- 14. Insert JobAlert
INSERT INTO job_alert (id, candidate_id, job_id, notification_status) VALUES 
(1, 1, 1, true),
(2, 1, 2, true),
(3, 2, 2, true),
(4, 2, 3, false);