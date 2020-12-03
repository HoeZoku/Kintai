package com.example.demo.login.controller;

import java.io.IOException;
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

import com.example.demo.login.domain.model.DaySheet;
import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.UserService;

//ホーム画面用コントローラークラス
@Controller
public class HomeController {


	@Autowired
	UserService userService;

	///////////////////////////////////GET////////////////////////////////////////////////////////////////

	/**
	 * ログイン後、画面表示前に今月分の勤怠データが作られているかチェック
	 */
	@GetMapping("/home")
	public String getHome(Model model) {
		model.addAttribute("contents", "login/home::home_contents");

		//ログインしているユーザのID取得(ココじゃなくてもいいような・・・どっかに共通化したい）
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId;
		if (principal instanceof UserDetails) {
			userId = ((UserDetails)principal).getUsername();
		} else {
			userId = principal.toString();
		}
		//デバック
		System.out.println(userId);

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			String result = userService.checkAndMake(userId);
			//とりあえず結果確認
			System.out.println(result);
		}
		return "login/homeLayout";
	}

	/**
	 * ユーザー一覧画面
	 */
	@GetMapping("/userList")
	//Modelから値を取得して表示するためカウント結果と複数検索結果をModelクラスに登録（addAttribute）
	public String getUserList(Model model) {

		//コンテンツ部分にユーザー一覧を表示するための文字列を登録
		model.addAttribute("contents", "login/userList :: userList_contents");

		//ログインしているユーザのID取得(ココじゃなくてもいいような・・・どっかに共通化したい）
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId;
		if (principal instanceof UserDetails) {
			userId = ((UserDetails)principal).getUsername();
		} else {
			userId = principal.toString();
		}
		//デ
		System.out.println(userId);

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
	 * ユーザー詳細画面
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



	/**
	 * 今月の勤怠シート画面（＊＊＊作成中＊＊＊）
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
	@GetMapping("/workSheet")
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
		model.addAttribute("contents", "login/workSheet :: workSheet_contents");

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			//勤怠テーブルからリスト取得
			List<DaySheet> daySheetList = userService.selectManySheet(userId);

			//Modelに登録
			model.addAttribute("daySheetList", daySheetList);
		}
		return "login/homeLayout";
	}

	/*
	 * 出勤ボタン処理/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 *
	 */
	@PostMapping(value = "/home", params = "attendance")
	public String postAttendance(Model model) {

		model.addAttribute("contents", "login/home::home_contents");

		//デ
		System.out.println("出勤ボタンの処理");

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


				//更新実行
				boolean result = userService.attendance(userId);
				//デ
				System.out.println(result);

				if (result == true) {
					model.addAttribute("result", "更新成功");
				} else {
					model.addAttribute("result", "更新失敗");
				}

		}
		return "login/homeLayout";
	}

	/*
	 * 退勤ボタン処理/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 *
	 */
	@PostMapping(value = "/home", params = "leave")
	public String postLeave(Model model) {

		model.addAttribute("contents", "login/home::home_contents");

		//デ
		System.out.println("退勤ボタンの処理");

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


				//更新実行
				boolean result = userService.leave(userId);
				//デ
				System.out.println(result);

				if (result == true) {
					model.addAttribute("result", "更新成功");
				} else {
					model.addAttribute("result", "更新失敗");
				}

		}
		return "login/homeLayout";
	}


		/**
		 * ユーザー更新用処理////////////////////////////////////////////////////////////
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
		 * ユーザー削除用処理
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
		 * ユーザー一覧のCSV出力用処理
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



		//ログアウト用メソッド.
		@PostMapping("/logout")
		public String postLogout() {

			//ログイン画面にリダイレクト
			return "redirect:/login";
		}

		/**
		 * アドミン権限専用画面のGET用メソッド
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
