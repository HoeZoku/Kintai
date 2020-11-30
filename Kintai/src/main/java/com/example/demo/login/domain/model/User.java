package com.example.demo.login.domain.model;

import lombok.Data;

@Data
public class User {

	//データベースから取得 した値 を、コントローラークラスやサービスクラスなどの間でやり取りするためのクラス
	//@Dataを付けているのでgetterやsetterは自動生成

    private String userId; //ユーザーID
    private String password; //パスワード
    private String userName; //ユーザー名
    private String role; //ロール

}
