/* 従業員テーブルのデータ */
INSERT INTO employee (employee_id, employee_name)
VALUES(1, '山田太郎');

/* ユーザーマスタのデータ（ADMIN権限） */
INSERT INTO m_user (user_id,password,user_name,role)
VALUES('test1@test', '$2a$10$xRTXvpMWly0oGiu65WZlm.3YL95LGVV2ASFjDhe6WF4.Qji1huIPa', '山田太郎', 'ROLE_ADMIN');

/* ユーザーマスタのデータ（一般権限） */
INSERT INTO m_user (user_id, password, user_name, role)
VALUES('test2@test', '$2a$10$xRTXvpMWly0oGiu65WZlm.3YL95LGVV2ASFjDhe6WF4.Qji1huIPa', '山田次郎', 'ROLE_GENERAL');