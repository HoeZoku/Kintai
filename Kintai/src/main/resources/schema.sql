/* ユーザーマスタ */
CREATE TABLE IF NOT EXISTS m_user (
    user_id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100),
    user_name VARCHAR(50),
    role VARCHAR(50)
);

/*勤務データ（とりあえず一つのテーブルで・・・）*/
CREATE TABLE IF NOT EXISTS work_schedule(
    user_id VARCHAR(50) ,
    work_date DATE,
    start_time TIME,
    end_time TIME,
    note VARCHAR(50)
);

