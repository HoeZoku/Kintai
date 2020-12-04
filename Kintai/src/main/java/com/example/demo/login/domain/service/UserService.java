package com.example.demo.login.domain.service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.login.domain.model.DayRecode;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.repository.UserDao;

@Transactional
@Service
public class UserService {

	@Autowired
	//↓二つ以上なら必須
	@Qualifier("UserDaoJdbcImpl3")
	UserDao dao;

	/**
	 * insert用メソッド.////////////////////////////////////////////////////////////////////
	 */
	public boolean insert(User user) {

		// insert実行
		//リポジトリークラスのinsertOneメソッド 呼出し
		//呼び出し先はinserfaceだけど勝手に実装クラスのUserDaoJdbcImplになるの？？
		int rowNumber = dao.insertOne(user);

		// 判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			// insert成功
			result = true;
		}
		return result;
	}

	/**
	 * カウント用メソッド.//////////////////////////////////////////////////
	 */
	public int count() {
		return dao.count();
	}

	/**
	 * 全件取得用メソッド./////////////////////////////////////////////////
	 */
	public List<User> selectMany() {
		// 全件取得
		return dao.selectMany();
	}

	/**
	 * １件取得用メソッド.///////////////////////////////////////////////////
	 */
	public User selectOne(String userId) {
		System.out.println(userId);
		// selectOne実行
		return dao.selectOne(userId);
	}

	/**
	 * １件更新用メソッド.//////////////////////////////////////////////////
	 */
	public boolean updateOne(User user) {

		// 判定用変数
		boolean result = false;

		// １件更新
		int rowNumber = dao.updateOne(user);

		if (rowNumber > 0) {
			// update成功
			result = true;
		}

		return result;
	}

	/**
	 * １件削除用メソッド./////////////////////////////////////////////////////
	 */
	public boolean deleteOne(String userId) {
		// １件削除
		int rowNumber = dao.deleteOne(userId);
		// 判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			// delete成功
			result = true;
		}
		return result;
	}

	/**
	 * ログイン時に当月勤務データのひな形が作成されているか確認するメソッド//////////////////////////////////////////////////////////////
	 * 1か月ログインしないと終わる・・・要修正
	 */
	public String checkAndMake(String userId) {

		//daoクラス処理呼び出し
		String result = dao.checkAndMake(userId);

		return result;
	}

	/**
	 * 出勤ボタンの処理/////////////////////////////////////////////////////////////////////////////////////////////////////////
	 */
	public boolean attendance(String userId) {

		// 判定用変数
		boolean result = false;
		//daoクラス処理呼び出し
		int rowNumber  = dao.attendance(userId);

		if (rowNumber > 0) {
			// update成功
			result = true;
		}
		return result;
	}

	/**
	 * 退勤ボタンの処理/////////////////////////////////////////////////////////////////////////////////////////////////////////
	 */
	public boolean leave(String userId) {

		// 判定用変数
		boolean result = false;
		//daoクラス処理呼び出し
		int rowNumber  = dao.leave(userId);

		if (rowNumber > 0) {
			// update成功
			result = true;
		}
		return result;
	}

	/**
	 * 勤怠テーブル複数取得メソッド.//////////////////////////////////////////////////////////////////////////////////////////////////////
	 *とりあえず今は全件取得のちに
	 *そのうち画面から年月指定させて指定された年月を表示する流れに
	 */
	public List<DayRecode> selectManySheet(String userId) {

		return dao.selectManySheet(userId);

	}

	/**
	 * 勤怠データ１日取得用メソッド.///////////////////////////////////////////////////
	 * 日付は何型が一番いいのか・・・とりあえずsql.dateで
	 */
	public DayRecode selectDay(String userId,Date date) {

		System.out.println(userId+date);

		// 一日分取得実行
		return dao.selectDay(userId,date);

	}

	// ユーザー一覧をCSV出力する.///////////////////////////////////
	/**
	 * @throws DataAccessException
	 */
	public void userCsvOut() throws DataAccessException {
		// CSV出力
		dao.userCsvOut();
	}

	/**
	 * サーバーに保存されているファイルを取得して、byte配列に変換する.
	 */
	public byte[] getFile(String fileName) throws IOException {

		// ファイルシステム（デフォルト）の取得
		FileSystem fs = FileSystems.getDefault();

		// ファイル取得
		Path p = fs.getPath(fileName);

		// ファイルをbyte配列に変換
		byte[] bytes = Files.readAllBytes(p);

		return bytes;
	}

}