package com.example.demo.login.domain.repository.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.repository.UserDao;


//Bean名をセットすることで@Autowiredする際にどのクラスを使用するか指定できる
@Repository("UserDaoJdbcImpl")
public class UserDaoJdbcImpl implements UserDao {
/*
 * UserDaoインターフェイスを実装するクラス
 */
    @Autowired
    JdbcTemplate jdbc;
   @Autowired
    PasswordEncoder passwordEncoder;

    // Userテーブルの件数を取得.///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int count() throws DataAccessException {
        // 全件取得してカウント
        int count = jdbc.queryForObject("SELECT COUNT(*) FROM m_user", Integer.class);

        return count;
    }

    // Userテーブルにデータを1件insert.//////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int insertOne(User user) throws DataAccessException {
    
    	//暗号化
    	String password = passwordEncoder.encode(user.getPassword());

        int rowNumber = jdbc.update("INSERT INTO m_user(user_id,"
                + " password,"
                + " user_name,"
                + " role)"
                + " VALUES(?, ?, ?, ?, ?, ?, ?)",
                user.getUserId(),	
                password,
                user.getUserName(),
                user.getRole());

        return rowNumber;
    }

    // Userテーブルのデータを１件取得////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public User selectOne(String userId) throws DataAccessException {
        Map<String, Object> map = jdbc.queryForMap("SELECT * FROM m_user"
                + " WHERE user_id = ?", userId);
      
        User user = new User();

        user.setUserId((String) map.get("user_id")); //ユーザーID
        user.setPassword((String) map.get("password")); //パスワード
        user.setUserName((String) map.get("user_name")); //ユーザー名
        user.setRole((String) map.get("role")); //ロール
        return user;
    }

    // Userテーブルの全データを取得.///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<User> selectMany() throws DataAccessException {
        List<Map<String, Object>> getList = jdbc.queryForList("SELECT * FROM m_user");

        // 結果返却用の変数
        List<User> userList = new ArrayList<>();

        for (Map<String, Object> map : getList) {

            User user = new User();

            user.setUserId((String) map.get("user_id")); //ユーザーID
            user.setPassword((String) map.get("password")); //パスワード
            user.setUserName((String) map.get("user_name")); //ユーザー名
            user.setRole((String) map.get("role")); //ロール

            userList.add(user);
        }

        return userList;
    }

    // Userテーブルを１件更新.insert同じ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int updateOne(User user) throws DataAccessException {

    	String password = passwordEncoder.encode(user.getPassword());

        int rowNumber = jdbc.update("UPDATE M_USER"
                + " SET"
                + " password = ?,"
                + " user_name = ?,"
                + " WHERE user_id = ?",
                password,
                user.getUserName(),
                user.getUserId());
        
        return rowNumber;
    }

    // Userテーブルを１件削除./////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int deleteOne(String userId) throws DataAccessException {
    	int rowNumber = jdbc.update("DELETE FROM m_user WHERE user_id = ?", userId);
        return rowNumber;
    }

    // SQL取得結果をサーバーにCSVで保存する//////////////////////////////////////////////////////////////////////////////
    @Override
    public void userCsvOut() throws DataAccessException {

         String sql = "SELECT * FROM m_user";

         // ResultSetExtractorの生成
         UserRowCallbackHandler handler = new UserRowCallbackHandler();

         //SQL実行＆CSV出力
         jdbc.query(sql, handler);
    }
}