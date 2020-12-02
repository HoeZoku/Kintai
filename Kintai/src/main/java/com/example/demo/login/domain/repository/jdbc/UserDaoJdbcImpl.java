package com.example.demo.login.domain.repository.jdbc;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.demo.login.domain.model.DaySheet;
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




    // Userテーブルの件数を取得./////////////////////////////////////////////////////////////////////////////////////////////////
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

   //ログイン時に今月勤怠のひな形がDBにあるかチェックなければ作成する//////////////////////////////////////////////////

    @Override
	public String checkAndMake(String userId) throws DataAccessException {

    	//自分用メモ
    	//勤怠テーブルに接続して、引数のユーザIDで月末の行があるか確認。
    	//ある…＝初日から月末まで行自体はできているはずなので何もしない|ない…初日～月末までの日付とユーザIDが埋まった行を作る
    	//↑出勤ボタン等で情報をインサートしたり、今月の一覧表示するとき予めにないと作成とか面倒だから？
    	//確認と作成はメソッド分けるべき？通常はリザルトセット複数いる・・・
    	//該当のユーザIDかつ今月末のレコード検索
    	//SELECT * FROM work_schedule WHERE user_id='test@test' AND work_date = LAST_DAY(CURDATE());
    	//↑でデータ取れるけど空だと何が帰るのかわからないNILL? NULLならレコード作ってそれ以外は何もしないながれにしたい。
    	//日付の計算や条件まわりはsqlでやるのは難しそうなのでJavaで

    	//NULLの場合の処理
    	//月末取得
    	LocalDate now = LocalDate.now();
    	LocalDate first = now.with(TemporalAdjusters.firstDayOfMonth()); // 月初
    	LocalDate last = now.with(TemporalAdjusters.lastDayOfMonth()); // 月末
    	//デバック
    	System.out.println("月初:"+ first +"  月末:" +last);

    	//対象ユーザの今月初から月末までのレコードをinsertするsql文の作成

    	String sql = "INSERT INTO work_schedule (user_id,work_date,start_time,end_time,note)VALUES";
    	//月初から月末までループ（月末までカウントしないので+1だけどもっと方法ある・・・)
    	while (first.isBefore(last.plusDays(1))) {
    		// ’で囲まないとエラー。直したい・・・
    	   sql = sql + "("+"'"+userId +"'"+ "," +"'"+ first +"'"+ "," + null + "," + null + "," + null + "),";
    	   first = first.plusDays(1);
    	}

    	//今のところsqlは,で終わってるので;に直す（もっとやり方ある?）
    	StringBuilder sb = new StringBuilder(sql);
        sb.setLength(sb.length()-1);
        sb=sb.append(";");
        sql=sb.toString();

    	//結果をretrnで返したいが結果が何型で帰るかわからない・・・

      //デバック
    	System.out.println(sql);
    	//SQL実行
    	jdbc.update(sql);

		return null;
	}


    //勤怠情報全件取得/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public List<DaySheet> selectManySheet(String userId) throws DataAccessException {
		List<Map<String, Object>> getList = jdbc.queryForList("SELECT * FROM work_schedule WHERE user_id = '" + userId+"'");

		System.out.println(userId);

        // 結果返却用の変数
		List<DaySheet> daysheeList = new ArrayList<>();

        for (Map<String, Object> map : getList) {

        	DaySheet daysheet = new DaySheet();

        	daysheet.setUserId((String) map.get("user_id")); //ユーザーID いらんかも？修正とかでいる？
        	daysheet.setWork_date((Date) map.get("work_date")); //日
        	daysheet.setStart_time((Time) map.get("start_time")); //出勤時間
        	daysheet.setEnd_time((Time) map.get("end_time")); //退勤時間
        	daysheet.setNote((String) map.get("note")); //備考
        	daysheeList.add(daysheet);//これで1行分。あとはforで全件取得
        }

        return daysheeList ;
    }



}