# csv_sql_converter
CSV+ 계층형 쿼리로 구성된 직업 파일을 DB에 insert하기 위해 만든 프로젝트 입니다.
자바를 이용하였으며, 테이블 구조는 아래와 같습니다. 

CREATE TABLE job_categories (
    job_id NUMBER(5) PRIMARY KEY, --직업 이름
    parent_job_id NUMBER(5), -- 상위 직업 이름
    job_name VARCHAR2(50) UNIQUE NOT NULL
);

사용 방법은 아래 링크 참조
https://velog.io/@flyjunu/%ED%95%9C%EA%B5%AD%EC%9D%98-%EC%A7%81%EC%97%85-%EC%A2%85%EB%A5%98%ED%8C%8C%EC%9D%BC-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%A0%80%EC%9E%A5%ED%95%98%EA%B8%B0
