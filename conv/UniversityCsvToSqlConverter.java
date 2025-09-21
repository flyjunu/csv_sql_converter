import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UniversityCsvToSqlConverter {

    public static void main(String[] args) {
        // --- 사용자가 수정할 부분 ---
        String csvFilePath = "학교개황(20250507 기준).csv";    // 대학교 정보 원본 CSV 파일 경로
        String outputSqlFilePath = "insert_universities.sql"; // 생성될 SQL 파일 경로
        // -------------------------

        try {
            convertCsvToSql(csvFilePath, outputSqlFilePath);
            System.out.println("✅ '" + outputSqlFilePath + "' 파일이 성공적으로 생성되었습니다.");
        } catch (IOException e) {
            System.err.println("❌ 파일 처리 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void convertCsvToSql(String csvPath, String sqlPath) throws IOException {
        // try-with-resources 구문으로 파일 스트림을 안전하게 자동 종료
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(sqlPath))) {

            String line = reader.readLine(); // 헤더(첫 줄)는 건너뜁니다.

            while ((line = reader.readLine()) != null) {
                // 1. 줄을 콤마 기준으로 분리
                String[] parts = line.split(",", -1); // -1 옵션으로 마지막 빈 문자열도 포함
                if (parts.length < 7) continue; // 컬럼이 7개 미만인 비정상 데이터는 건너뛰기

                // 2. 각 컬럼 데이터를 변수에 할당하고 공백 제거
                String universityId      = parts[0].trim();
                String universityName    = parts[1].trim();
                String campusType        = parts[2].trim();
                String academicSystem    = parts[3].trim();
                String region            = parts[4].trim();
                String establishmentType = parts[5].trim();
                String universityLevel   = parts[6].trim();

                // 3. SQL Injection 방지를 위해 작은따옴표(')를 두 개('')로 이스케이프 처리
                universityName = universityName.replace("'", "''");
                campusType = campusType.replace("'", "''");
                academicSystem = academicSystem.replace("'", "''");
                region = region.replace("'", "''");
                establishmentType = establishmentType.replace("'", "''");

                // 4. university_level 값이 비어있으면 SQL의 NULL로 처리
                String levelValue = universityLevel.isEmpty() ? "NULL" : universityLevel;

                // 5. SQL INSERT 구문 생성
                // 문자열 데이터는 작은따옴표로 감싸고, 숫자 데이터는 그대로 둡니다.
                String sql = String.format(
                        "INSERT INTO universities (university_id, university_name, campus_type, academic_system, region, establishment_type, university_level) VALUES (%s, '%s', '%s', '%s', '%s', '%s', %s);\n",
                        universityId, universityName, campusType, academicSystem, region, establishmentType, levelValue
                );

                // 6. 생성된 SQL 구문을 파일에 쓰기
                writer.write(sql);
            }
        }
    }
}