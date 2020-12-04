package com.example.demo.login.domain.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.demo.login.domain.model.DayRecode;
import com.example.demo.login.domain.model.User;

//多分いらない、直でuserdaojdbcに飛んでいいかも
//多すぎる。分けねば。。。
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

  //SQL取得結果をサーバーにCSVで保存する
    public void userCsvOut() throws DataAccessException;

    //勤怠処理

    //勤怠テーブルの情報を取得する(テスト用全件）
    public  List<DayRecode> selectManySheet(String userId) throws DataAccessException;

    //今月分チェック。なければ作成する（とりあえずStringで実行結果返す)
    public String checkAndMake(String userId) throws DataAccessException;

    //勤怠テーブル1件取得(日付型どれがいいかわからない、とりあえずsql.Date）
    public DayRecode selectDay(String userId,Date date) throws DataAccessException;

    //本日出勤
    public int attendance(String userId) throws DataAccessException;

  //本日退勤
    public int leave(String userId) throws DataAccessException;



}
