package com.example.demo.login.domain.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.demo.login.domain.model.DaySheet;
import com.example.demo.login.domain.model.User;

//多分いらない、直でuserdaojdbcに飛んでいいかも
//Springではデータベース操作例外はSpring提供のDataAccessExceptionを投げる。

public interface UserDao {

    // Userテーブルの件数を取得.
    public int count() throws DataAccessException;

    // Userテーブルにデータを1件insert.
    public int insertOne(User user) throws DataAccessException;

    // Userテーブルのデータを１件取得
    public User selectOne(String userId) throws DataAccessException;

    // Userテーブルの全データを取得.
    public List<User> selectMany() throws DataAccessException;

    // Userテーブルを１件更新.
    public int updateOne(User user) throws DataAccessException;

    // Userテーブルを１件削除.
    public int deleteOne(String userId) throws DataAccessException;

    //勤怠テーブルの情報を取得する(テスト用全件）
    public  List<DaySheet> selectManySheet(String userId) throws DataAccessException;

    //今月分の勤怠ひな形があるかチェックしてなければ作成する（とりあえずStringで実行結果返す)
    public String checkAndMake(String userId) throws DataAccessException;


    //SQL取得結果をサーバーにCSVで保存する
    public void userCsvOut() throws DataAccessException;
}
