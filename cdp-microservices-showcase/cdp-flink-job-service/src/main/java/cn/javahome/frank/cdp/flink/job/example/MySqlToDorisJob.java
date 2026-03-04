package cn.javahome.frank.cdp.flink.job.example;

/**
 * 示例任务：MySQL -> Doris。
 * 该类用于展示“任务代码化”的落地形式，风格类似：
 * C:\Users\Administrator\IdeaProjects\realtime-job\src\main\java\com\liaoliao\car\rose\MySqlToDorisJob.java
 */
public class MySqlToDorisJob {

    public static void main(String[] args) {
        // 这里只做模板展示，真实项目可替换为 StreamExecutionEnvironment + TableEnvironment 代码。
        String sourceDdl = """
                CREATE TABLE ods_user_mysql (
                  id BIGINT,
                  mobile STRING,
                  level STRING,
                  update_time TIMESTAMP(3),
                  PRIMARY KEY (id) NOT ENFORCED
                ) WITH (
                  'connector' = 'mysql-cdc',
                  'hostname' = 'mysql-host',
                  'port' = '3306',
                  'username' = 'cdc_user',
                  'password' = '***',
                  'database-name' = 'crm',
                  'table-name' = 'user'
                )
                """;

        String sinkDdl = """
                CREATE TABLE ads_user_profile (
                  id BIGINT,
                  mobile STRING,
                  level STRING,
                  update_time TIMESTAMP(3),
                  PRIMARY KEY (id) NOT ENFORCED
                ) WITH (
                  'connector' = 'doris',
                  'fenodes' = 'doris-fe:8030',
                  'table.identifier' = 'cdp.ads_user_profile',
                  'username' = 'root',
                  'password' = '***'
                )
                """;

        String insertSql = """
                INSERT INTO ads_user_profile
                SELECT id, mobile, level, update_time
                FROM ods_user_mysql
                """;

        System.out.println("=== Flink SQL Job Template ===");
        System.out.println(sourceDdl);
        System.out.println(sinkDdl);
        System.out.println(insertSql);
        System.out.println("Submit this job to Flink on K8s by your deployment service.");
    }
}
