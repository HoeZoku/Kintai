package com.example.demo.login.domain.model;

import java.sql.Date;
import java.sql.Time;

import lombok.Data;

//gettersetter自動生成
@Data
public class DayRecode {
//データベースから取得 した値 を、コントローラークラスやサービスクラスなどの間でやり取りするためのクラス

    private String userId; //ユーザーID
    private Date workDate; //日 java.sql.DateがmysqlのDateに対応しているらしい？
    private Time startTime; //出勤時間 java.sql.timeがmysqlのDateに対応しているらしい？
    private Time endTime;//退勤時間
    private String note;//備考

}