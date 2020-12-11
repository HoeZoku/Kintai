package com.example.demo.login.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.domain.model.DayRecode;
import com.example.demo.login.domain.model.DayRecodeForm;
import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.UserService;

//ホーム画面用コントローラークラス
@Controller
public class HomeController {

	@Autowired
	UserService userService;

	/**
	 * ログイン後、画面表示前に今月分の勤怠データが作られているかチェック////////////////////////////////////////////////////
	 */
	@GetMapping("/home")
	public String getHome(Model model) {
		model.addAttribute("contents", "login/home::home_contents");

		//ユーザID取得(どっかで共通化したい）
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId;
		if (principal instanceof UserDetails) {
			userId = ((UserDetails)principal).getUsername();
		} else {
			userId = principal.toString();
		}

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			//今月分のレコードチェック（とりあえず戻り値Stringだがどうするか・・・
			String result = userService.checkAndMake(userId);

			//本日分レコード取得
			Date now = new Date(System.currentTimeMillis());
			DayRecode dayRecode =userService.selectDay(userId,now);

				//デ
			System.out.println(dayRecode.getWorkDate());

			//画面に渡すためDayRecodeクラスmodelに登録（Formクラスがいるかもしれないがとりあえず作らず）
			model.addAttribute("dayRecode", dayRecode);

		}
		return "login/homeLayout";
	}


	 //ユーザー一覧画面//////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/userList")

	public String getUserList(Model model) {

		//htmlのコンテンツ部分に登録
		model.addAttribute("contents", "login/userList::userList_contents");

		//ユーザー一覧の生成
		List<User> userList = userService.selectMany();

		//Modelにユーザーリストを登録
		model.addAttribute("userList", userList);

		//データ件数を取得
		int count = userService.count();
		model.addAttribute("userListCount", count);

		return "login/homeLayout";
	}

	/**
	 * ユーザー詳細画面////////////////////////////////////////////////////////////////////////////////////////////////////
	 */

	@GetMapping("/userDetail/{id:.+}")
	public String getUserDetail(@ModelAttribute SignupForm form,
			Model model,
			@PathVariable("id") String userId) {
		System.out.println(userId);
		// コンテンツ部分にユーザー詳細を表示するための文字列を登録
		model.addAttribute("contents", "login/userDetail :: userDetail_contents");

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			// ユーザー情報を取得
			User user = userService.selectOne(userId);

			// Userクラスをフォームクラスに変換
			form.setUserId(user.getUserId()); //ユーザーID
			form.setUserName(user.getUserName()); //ユーザー名

			// Modelに登録
			model.addAttribute("signupForm", form);
		}

		return "login/homeLayout";
	}

	/*
	 * 勤怠一覧の修正ボタン処理//////////////////////////////////////////////////////////////////////////////////////////////
	 */
	@GetMapping("/correct/{recode.workDate}")

	public String getCorrect(@ModelAttribute DayRecodeForm form,
			Model model, @PathVariable("recode.workDate") Date date) {

		model.addAttribute("contents", "login/correct::correct_contents");

		//ログインしているユーザのID取得(どっかで共通化すべき？）
				Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				String userId;
				if (principal instanceof UserDetails) {
					userId = ((UserDetails)principal).getUsername();
				} else {
					userId = principal.toString();
				}

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			//レコード取得
			DayRecode dayRecode =userService.selectDay(userId,date);

			// DayRecodeクラスをフォームクラスに変換（バリデーション用？）
			form.setWorkDate(dayRecode.getWorkDate()); //日付
			form.setStartTime(dayRecode.getStartTime()); //出勤時間
			form.setEndTime(dayRecode.getEndTime()); //退勤時間
			form.setNote(dayRecode.getNote()); //備考

			// Modelに登録
			model.addAttribute("dayRecodeForm", form);
		}
			return "login/homeLayout";
	}



	/**
	 * 勤怠一覧画面

	 * 今はとりあえず全件取得
	 * 動的 な URL に 対応 し た メソッド を 作る ため には、@ GetMapping や@ PostMapping の 値 に/{< 変数 名 >} を 付け ます。
	 * 例えば、 ユーザー ID を 受け取る 場合 は、@ GetMapping(/userDetail/{ id}) と し ます。idがemailの場合は正規表現で{id:.+}
	 * ＠PathVariableでURLに含まれる情報を変数に渡せる。下記ではURLに含まれるidをString型の変数userIdにぶちこむ
	 * ↑
	 * URLにIDが出るのでよくない？のでSpringSecrityのセッションからログインIDを取得する方法に変更
	 * Authenticationクラスには認証されたユーザーの情報（ユーザー名や付与されている権限の一覧など）が格納されている。
	 * 現在のリクエストに紐づく Authentication を取得するには SecurityContextHolder.getContext().getAuthentication() とする。
	 *SecurityContextHolder.getContext() は、現在のリクエストに紐づく SecurityContext を返している。
	 *Context.getAuthentication()は認証情報を取得
	 *Authentication.getPrincipal() で、ログインユーザーの UserDetails を取得←要キャスト？
	 * まとめ SecuritiContext→認証情報→ユーザー情報で取得してる？よくわからん
	 */
	@GetMapping("/attendanceRecord")
	public String getWorkSheet(Model model) {

		//ログインしているユーザのID取得(どっかで共通化すべき？）
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId;
		if (principal instanceof UserDetails) {
			userId = ((UserDetails)principal).getUsername();
		} else {
			userId = principal.toString();
		}

		//htmlコンテンツ部分に表示するための文字列を登録
		model.addAttribute("contents", "login/attendanceRecord :: attendanceRecord_contents");

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			//勤怠テーブルからリスト取得
			List<DayRecode> dayRecodeList = userService.selectManySheet(userId);

			//Modelに登録
			model.addAttribute("dayRecodeList", dayRecodeList);
		}
		return "login/homeLayout";
	}

	/*
	 * 打刻ボタン処理/////////////////////////////////////////////////////////////////////////////////////////////////////////
	 */
	@PostMapping(value = "/home", params = "stamping")
	public String postAttendance(Model model) {

		model.addAttribute("contents", "login/home::home_contents");

		//ログインしているユーザのID取得(どっかで共通化すべき？）
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId;
		if (principal instanceof UserDetails) {
			userId = ((UserDetails)principal).getUsername();
		} else {
			userId = principal.toString();
		}

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			//今日分レコード取得
			Date now = new Date(System.currentTimeMillis());
			DayRecode dayRecode =userService.selectDay(userId,now);

			//とりあえず格納用(戻り値Stringにして画面出力してもいいかも)
			boolean result;

			String str;

			//出勤してなければ出勤
			if(dayRecode.getStartTime()==null) {

				result = userService.attendance(userId);
				str="【 出勤 】しました。";

			//出勤してかつ退勤してなければ退勤
			}else if(dayRecode.getEndTime()==null){
					result = userService.leave(userId);
					str="【 退勤 】しました。";

			//出勤も退勤もしていれば修正を促す
			}else {
				str = "出退勤済みです。修正は【勤怠一覧】を参照してください";
			}

			model.addAttribute("str", str);
		}
		return "login/homeLayout";
	}

	/*
	 * home画面の修正ボタン処理//////////////////////////////////////////////////////////////////////////////////////////////
	 */
	@PostMapping(value = "/home", params = "todayCorrect")

	public String postTodayCorrect(@ModelAttribute DayRecodeForm form,Model model) {

		model.addAttribute("contents", "login/correct::correct_contents");

		//ログインしているユーザのID取得(どっかで共通化すべき？）
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId;
		if (principal instanceof UserDetails) {
			userId = ((UserDetails)principal).getUsername();
		} else {
			userId = principal.toString();
		}

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			//今日分レコード取得
			Date now = new Date(System.currentTimeMillis());
			DayRecode dayRecode =userService.selectDay(userId,now);

			// DayRecodeクラスをフォームクラスに変換（バリデーション用？）
			form.setWorkDate(dayRecode.getWorkDate()); //日付
			form.setStartTime(dayRecode.getStartTime()); //出勤時間
			form.setEndTime(dayRecode.getEndTime()); //退勤時間
			form.setNote(dayRecode.getNote()); //備考

			// Modelに登録
			model.addAttribute("dayRecodeForm", form);
		}
			return "login/homeLayout";
	}


	/**
	 * 打刻修正用処理//////////////////////////////////////////////////////////////////////////////////////////////////
	 */

	//バリデーションまだ
	@PostMapping(value = "/correct", params = "update")
	public String postStampingUpdate(@ModelAttribute  DayRecodeForm form,
			Model model) {


		model.addAttribute("contents", "login/correct::correct_contents");

			System.out.println("打刻修正開始");

		//ログインしているユーザのID取得(どっかで共通化すべき？）
				Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				String userId;
				if (principal instanceof UserDetails) {
					userId = ((UserDetails)principal).getUsername();
				} else {
					userId = principal.toString();
				}

		//DayRecodeインスタンスの生成
		DayRecode dayRecode = new DayRecode();

		//フォームクラスをDayRecodeクラスに変換
		dayRecode.setUserId(userId);
		dayRecode.setWorkDate(form.getWorkDate());
		dayRecode.setStartTime(form.getStartTime());
		dayRecode.setEndTime(form.getEndTime());
		dayRecode.setNote(form.getNote());

		//更新実行
		try {

			//更新実行
			boolean result = userService.updateStamping(dayRecode);

			if (result == true) {
				model.addAttribute("result", "更新成功");
			} else {
				model.addAttribute("result", "更新失敗");
			}

		} catch (DataAccessException e) {

			model.addAttribute("result", "更新失敗(トランザクションテスト)");

		}

		//ユーザー一覧画面を表示
		return getWorkSheet(model);
	}



		/**
		 * ユーザー更新用処理//////////////////////////////////////////////////////////////////////////////////////////////////
		 */

		@PostMapping(value = "/userDetail", params = "update")
		public String postUserDetailUpdate(@ModelAttribute SignupForm form,
				Model model) {

			System.out.println("更新ボタンの処理");

			//Userインスタンスの生成
			User user = new User();

			//フォームクラスをUserクラスに変換
			user.setUserId(form.getUserId());
			user.setPassword(form.getPassword());
			user.setUserName(form.getUserName());
			//更新実行
			try {

				//更新実行
				boolean result = userService.updateOne(user);

				if (result == true) {
					model.addAttribute("result", "更新成功");
				} else {
					model.addAttribute("result", "更新失敗");
				}

			} catch (DataAccessException e) {

				model.addAttribute("result", "更新失敗(トランザクションテスト)");

			}

			//ユーザー一覧画面を表示
			return getUserList(model);
		}


		/**
		 * ユーザー削除用処理/////////////////////////////////////////////////////////////////////////////////////////////////
		 */

		@PostMapping(value = "/userDetail", params = "delete")
		public String postUserDetailDelete(@ModelAttribute SignupForm form,
				Model model) {

			System.out.println("削除ボタンの処理");

			//削除実行
			boolean result = userService.deleteOne(form.getUserId());

			if (result == true) {
				model.addAttribute("result", "削除成功");
			} else {
				model.addAttribute("result", "削除失敗");
			}

			//ユーザー一覧画面を表示
			return getUserList(model);
		}

		/**
		 * ユーザー一覧のCSV出力用処理////////////////////////////////////////////////////////////////////////////////
		 */
		@GetMapping("/userList/csv")
		public ResponseEntity<byte[]> getUserListCsv(Model model) {

			//ユーザーを全件取得して、CSVをサーバーに保存する
			userService.userCsvOut();

			byte[] bytes = null;

			try {

				//サーバーに保存されているsample.csvファイルをbyteで取得する
				bytes = userService.getFile("sample.csv");

			} catch (IOException e) {
				e.printStackTrace();
			}

			HttpHeaders header = new HttpHeaders();
			header.add("Content-Type", "text/csv; charset=UTF-8");
			header.setContentDispositionFormData("filename", "sample.csv");

			//sample.csvを戻すをResponseEntity型にするとタイムリーフのテンプレート(html)ではなく、ファイル（byte型の配列）を返却できる
			return new ResponseEntity<>(bytes, header, HttpStatus.OK);
		}

		//ログアウト用メソッド./////////////////////////////////////////////////////////////////////////////////////////
		@PostMapping("/logout")
		public String postLogout() {

			//ログイン画面にリダイレクト
			return "redirect:/login";
		}

		/**
		 * アドミン権限専用画面のGET用メソッド////////////////////////////////////////////////////////////////////////
		 * @param model Modelクラス
		 * @return 画面のテンプレート名
		 */
		@GetMapping("/admin")
		public String getAdmin(Model model) {

			//コンテンツ部分にユーザー詳細を表示するための文字列を登録
			model.addAttribute("contents", "login/admin :: admin_contents");

			//レイアウト用テンプレート
			return "login/homeLayout";
		}


	}
