/* applicationpropertyで初期化する方法が分からず。。。 */
drop table employee;
drop table m_user;

/* 従業員テーブル */
CREATE TABLE IF NOT EXISTS employee (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(50)
);
/* ユーザーマスタ */
CREATE TABLE IF NOT EXISTS m_user (
    user_id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100),
    user_name VARCHAR(50),
    role VARCHAR(50)
);