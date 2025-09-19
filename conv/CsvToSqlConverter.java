import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CsvToSqlConverter {

    public static void main(String[] args) {
        // --- 사용자가 수정할 부분 ---
        String csvFilePath = "korean_jobs.csv"; // 원본 CSV 파일 경로
        String outputSqlFilePath = "insert_jobs.sql"; // 생성될 SQL 파일 경로
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
        // 각 들여쓰기 레벨별 마지막 부모 ID를 저장하는 맵
        // Key: 들여쓰기 공백 수(Integer), Value: 해당 레벨의 마지막 ID(String)
        Map<Integer, String> levelParentMap = new HashMap<>();

        // try-with-resources 구문으로 파일 스트림을 안전하게 자동 종료
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(sqlPath))) {

            String line = reader.readLine(); // 헤더(첫 줄) 읽기 (건너뛰기)

            while ((line = reader.readLine()) != null) {
                // 1. 줄을 콤마 기준으로 분리 (직업명에 콤마가 있을 수 있으므로 2개로만 나눔)
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue; // 비정상적인 데이터는 건너뛰기

                String codeRaw = parts[0];
                String jobNameRaw = parts[1];

                // 2. 현재 줄의 들여쓰기 수준(공백 수) 계산
                int indentLevel = codeRaw.length() - codeRaw.replaceAll("^\\s+", "").length();

                // 3. 코드와 직업명에서 앞뒤 공백 제거
                String jobId = codeRaw.trim();
                String jobName = jobNameRaw.trim().replace("'", "''"); // SQL Injection 방지를 위해 작은따옴표 이스케이프

                // 4. 부모 ID 찾기
                String parentId = "NULL";
                if (indentLevel > 0) {
                    int parentLevel = -1;
                    // 현재 레벨보다 낮은 레벨 중 가장 가까운(큰) 레벨을 찾음
                    for (int level : levelParentMap.keySet()) {
                        if (level < indentLevel) {
                            parentLevel = Math.max(parentLevel, level);
                        }
                    }
                    if (parentLevel != -1) {
                        parentId = levelParentMap.get(parentLevel);
                    }
                }

                // 5. 현재 ID를 다음 자식들이 참조할 수 있도록 맵에 기록
                levelParentMap.put(indentLevel, jobId);

                // 6. SQL 구문 생성 및 파일에 쓰기
                String sql = String.format(
                        "INSERT INTO job_categories (job_id, parent_job_id, job_name) VALUES (%s, %s, '%s');\n",
                        jobId, parentId, jobName
                );
                writer.write(sql);
            }
        }
    }
}