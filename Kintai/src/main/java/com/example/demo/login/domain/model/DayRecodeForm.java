package com.example.demo.login.domain.model;

import java.sql.Date;
import java.sql.Time;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

//バリデーションかける用の修正フォームだけどいらんかも・・・？
//IDは入力も表示もしないのでいらない？

@Data
public class DayRecodeForm {

	//入力を受け付けない所はバリデーションチェックしなくていいのか？

	    private Date workDate; //日

	    //:入力は面倒だが、表示にも関係するのでつけとく・・・
	    @NotBlank(message = "{require_check}")
//	    @DateTimeFormat(pattern="hh:mm:ss")
	    private Time startTime; //出勤時間

//	    @DateTimeFormat(pattern="hh:mm")
	    private Time endTime;//退勤時間
	    @Length(max=20,message = "{length_check_note}")
	    private String note;//備考

}
