# csv_sql_converter
CSV+ 계층형 쿼리로 구성된 직업 파일을 DB에 insert하기 위해 만든 프로젝트 입니다.
자바를 이용하였으며, 테이블 구조는 아래와 같습니다. 

CREATE TABLE job_categories (
    job_id NUMBER(5) PRIMARY KEY, --직업 이름
    parent_job_id NUMBER(5), -- 상위 직업 이름
    job_name VARCHAR2(50) UNIQUE NOT NULL
);
