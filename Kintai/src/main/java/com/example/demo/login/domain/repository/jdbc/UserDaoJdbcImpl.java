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

import com.example.demo.login.domain.model.DayRecode;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.repository.UserDao;

//近いうちにBeanPropertyRowMapper使ってスマートにしたい

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


	// Userテーブルの件数を取得.//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int count() throws DataAccessException {
		// 全件取得してカウント
		int count = jdbc.queryForObject("SELECT COUNT(*) FROM m_user", Integer.class);

		return count;
	}

	// Userテーブルにデータを1件insert.///////////////////////////////////////////////////////////////////////////////////////
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

	// Userテーブルのデータを１件取得////////////////////////////////////////////////////////////////////////////////////////
	//ここは継承先のBeanPropertyRowMapperで処理される
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
	//ここは継承先のBeanPropertyRowMapperで処理される
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
	//自分用メモ
	//1カ月以上ログインしないと終わる・・・後に修正
	//勤怠テーブルに接続して、引数のユーザIDで月末の行があるか確認。
	//ある…＝初日から月末まで行自体はできているはずなので何もしない|ない…初日～月末までの日付とユーザIDが埋まった行を作る
	//↑出勤ボタン等で情報をインサートしたり、今月の一覧表示するとき予めにないと作成とか面倒だから？
	//確認と作成はメソッド分けるべき？通常はリザルトセット複数いる・・・
	//springJDBCの取得操作のqueryForMapは戻り値Mapだが今回はMapの必要性はない・・・ほかにやり方ある？
	//resultには[0]か「1]のobject型が帰るので苦肉のtoString。なんとかcastする？後に修正・・・
	//日付の計算や条件まわりはsqlでやるのは難しそうなのでJavaで
	//（isBeforeだと月末までカウントしないので+1だけどもっと方法ある・・・)
	// sql中’で囲まないとエラー。直したい・・・
	//BeanPropertyRowMapperを使えばもっとスマート？null入れる場合どうなるのかはわからない・・・

	@Override
	public String checkAndMake(String userId) throws DataAccessException {

		//結果格納用
		String result;

		//今月分レコードチェック(月末レコードの有無を基準)  ?使った書き方にかえる
		String sql ="SELECT EXISTS"
				+ "(SELECT * FROM work_schedule "
				+ " WHERE user_id=" + "'"+userId+"'"
				+ " AND work_date = LAST_DAY(CURDATE()))";

		Map<String, Object> recordResult = ( jdbc.queryForMap(sql));

		if(recordResult.values().toString().equals("[0]")) {

			//今月レコード作成

			//月初末
			LocalDate now = LocalDate.now();
			LocalDate first = now.with(TemporalAdjusters.firstDayOfMonth());
			LocalDate last = now.with(TemporalAdjusters.lastDayOfMonth());

			//クエリ作成 ?つかった方がすまーと？
			sql = "INSERT INTO work_schedule (user_id,work_date,start_time,end_time,note)VALUES";
			while (first.isBefore(last.plusDays(1))) {
				sql = sql + "("+"'"+userId +"'"+ "," +"'"+ first +"'"+ "," + null + "," + null + "," + null + "),";
				first = first.plusDays(1);
			}
			//今のところsqlは,で終わってるので;に直す（もっとやり方ある?）
			StringBuilder sb = new StringBuilder(sql);
			sb.setLength(sb.length()-1);
			sb=sb.append(";");
			sql=sb.toString();

			//SQL実行
			jdbc.update(sql);
			result = "今月レコード新規作成";
		}
		else {
			result = "今月レコード作成済み";
		}
		return result;
	}

	//出勤ボタン処理/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//打刻時間と打刻日付はそのうち引数でもらうようにしないと今のは厳密ではない

	@Override
	public int attendance(String userId) throws DataAccessException {

		int rowNumber = jdbc.update("UPDATE work_schedule"
				+ " SET start_time= NOW() "
				+ " WHERE user_id =" + "'" + userId + "'"
				+ " AND work_date = CURDATE();");

		return rowNumber;
	}

	//退勤ボタン処理/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//打刻時間と打刻日付はそのうち引数でもらうようにしないと今のは厳密ではない
	@Override
	public int leave(String userId) throws DataAccessException {

		int rowNumber = jdbc.update("UPDATE work_schedule"
				+ " SET end_time= NOW() "
				+ " WHERE user_id =" + "'" + userId + "'"
				+ " AND work_date = CURDATE();");

		return rowNumber;

	}

	//勤怠テーブル一件(1日分)取得/////////////////////////////////////////////////////////////////////////////////////

	@Override
	public DayRecode selectDay(String userId,Date date) throws DataAccessException {
		Map<String, Object> map = jdbc.queryForMap("SELECT * FROM work_schedule"
				+ " WHERE user_id = ?"
				+ " AND work_date = ?",
				userId,
				date);

		//結果格納用
		DayRecode deyRecode = new DayRecode();

		deyRecode.setUserId((String) map.get("user_id")); //ユーザーID
		deyRecode.setWorkDate((Date) map.get("work_date")); //年月日
		deyRecode.setStartTime((Time) map.get("start_time")); //出勤時間
		deyRecode.setEndTime((Time) map.get("end_time")); //退勤時間
		deyRecode.setNote((String) map.get("note"));

		return deyRecode;
	}

	//勤怠情報全件取得/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//試しに作った。使ってない
	@Override
	public List<DayRecode> selectManySheet(String userId) throws DataAccessException {
		List<Map<String, Object>> getList = jdbc.queryForList("SELECT * FROM work_schedule WHERE user_id = '" + userId+"'");

		//デ
		System.out.println(userId);

		// 結果返却用の変数
		List<DayRecode> daysheeList = new ArrayList<>();

		for (Map<String, Object> map : getList) {

			DayRecode dayRecode = new DayRecode();

			dayRecode.setUserId((String) map.get("user_id")); //ユーザーID いらんかも？修正とかでいる？
			dayRecode.setWorkDate((Date) map.get("work_date")); //日
			dayRecode.setStartTime((Time) map.get("start_time")); //出勤時間
			dayRecode.setEndTime((Time) map.get("end_time")); //退勤時間
			dayRecode.setNote((String) map.get("note")); //備考
			daysheeList.add(dayRecode);//これで1行分。あとはforで全件取得
		}

		return daysheeList ;
	}



}