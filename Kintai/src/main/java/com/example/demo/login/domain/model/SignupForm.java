package com.example.demo.login.domain.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

/*
 * 新規登録画面で入力された内容を格納しておくクラス
 * //@Dataを付けているのでgetterやsetterは自動生成
 */

@Data
public class SignupForm {

    @NotBlank(message = "{require_check}")
    @Email(message = "{email_check}")
    private String userId; // ユーザーID

    @NotBlank(message = "{require_check}")
    @Length(min=4,max=20,message = "{length_check}")
    @Pattern(regexp = "^[a-zA-Z0-9]+$",message = "{pattern_check}")
    private String password; // パスワード

    @NotBlank(message = "{require_check}")
    private String userName; // ユーザー名

}
