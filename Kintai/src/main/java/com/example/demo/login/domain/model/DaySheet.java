package com.example.demo.login.domain.model;

import java.sql.Date;
import java.sql.Time;

import lombok.Data;

//gettersetter自動生成
@Data
public class DaySheet {
//データベースから取得 した値 を、コントローラークラスやサービスクラスなどの間でやり取りするためのクラス

    private String userId; //ユーザーID
    private Date work_date; //日 java.sql.DateがmysqlのDateに対応しているらしい？
    private Time start_time; //出勤時間 java.sql.timeがmysqlのDateに対応しているらしい？
    private Time end_time;//退勤時間
    private String note;//備考

}
