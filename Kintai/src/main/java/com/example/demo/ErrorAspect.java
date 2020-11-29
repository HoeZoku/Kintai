package com.example.demo;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorAspect {

    //全てのクラスを対象
	//＠AfterThrowingはメソッドが異常終了した場合だけAOPの処理（ Advice） を実行。
	//上記の＠AfterThrowing内のポイントカット(処理実行するクラスやメソッド)は全てのコントローラサービスリポジトリを対象。
    @AfterThrowing(value="execution(* *..*..*(..))"
            + " && (bean(*Controller) || bean(*Service) || bean(*Repository))"
            , throwing="ex")
    public void throwingNull(DataAccessException ex) {

        //例外処理の内容（ログ出力）
        System.out.println("===========================================");
        System.out.println("DataAccessExceptionが発生しました。 : " + ex);
        System.out.println("===========================================");

    }

}