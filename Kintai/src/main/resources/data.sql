use testdb;
/* ユーザーマスタのデータ（ADMIN権限） */
INSERT INTO m_user (user_id,password,user_name,role)
VALUES('test1@test', '$2a$10$xRTXvpMWly0oGiu65WZlm.3YL95LGVV2ASFjDhe6WF4.Qji1huIPa', '山田太郎', 'ROLE_ADMIN');

/* ユーザーマスタのデータ（一般権限） */
INSERT INTO m_user (user_id, password, user_name, role)
VALUES('test2@test', '$2a$10$xRTXvpMWly0oGiu65WZlm.3YL95LGVV2ASFjDhe6WF4.Qji1huIPa', '山田次郎', 'ROLE_GENERAL');

/* 勤務表テーブルのデータ*/
INSERT INTO work_schedule (user_id,work_date,start_time,end_time,note)
VALUES('test1@test', '2020-11-01', '09:00:00', '18:00:00','ここに備考');
VALUES('test1@test', '2020-12-01', '09:00:00', '19:00:00');
