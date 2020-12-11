package com.example.demo.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.UserService;

@Controller
public class SignupController {

	@Autowired
	private UserService userServise;

	/**
	 * ユーザー登録画面のGETメソッド用処理.
	 */
	@GetMapping("/signup")
	public String getSignUp(@ModelAttribute SignupForm form, Model model) {
		// signup.htmlに画面遷移
		return "login/signup";
	}

	/**
	 * ユーザー登録画面のPOSTメソッド用処理.
	 */
	@PostMapping("/signup")
	public String postSignUp(@ModelAttribute SignupForm form,
			BindingResult bindingResult,
			Model model) {



		//でバック用 formの中身をコンソールに出して確認します
		System.out.println(form);

		// サービスに渡すinsert用変数
		User user = new User();
		//formのデータをわたす
		user.setUserId(form.getUserId()); //ユーザーID
		user.setPassword(form.getPassword()); //パスワード
		user.setUserName(form.getUserName()); //ユーザー名
		user.setRole("ROLE_GENERAL"); //ロール（一般）

		// サービスの処理へuserインスタンスを渡す
		boolean result = userServise.insert(user);

		// ユーザー登録結果の判定
		if (result == true) {
			System.out.println("insert成功");
		} else {
			System.out.println("insert失敗");
		}

		// login.htmlにリダイレクト
		return "redirect:/login";
	}


	/*＠ExceptionHandlerでException毎の例外処理を実装できる。引数に例外クラスを指定すると例外毎の処理を実行できる。
		メソッドは複数用意することもでき、下記では共通エラーページに遷移する。その際にエラーメッセージをModelクラスに登録。*/

	/**
     * DataAccessException発生時の処理メソッド.
     */
    @ExceptionHandler(DataAccessException.class)
    public String dataAccessExceptionHandler(DataAccessException e, Model model) {

        // 例外クラスのメッセージをModelに登録
        model.addAttribute("error", "内部サーバーエラー（DB）：ExceptionHandler");

        // 例外クラスのメッセージをModelに登録
        model.addAttribute("message", "SignupControllerでDataAccessExceptionが発生しました");

        // HTTPのエラーコード（500）をModelに登録
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);

        return "error";
    }

    /**
     * Exception発生時の処理メソッド.
     */
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e, Model model) {

        // 例外クラスのメッセージをModelに登録
        model.addAttribute("error", "内部サーバーエラー：ExceptionHandler");

        // 例外クラスのメッセージをModelに登録
        model.addAttribute("message", "SignupControllerでExceptionが発生しました");

        // HTTPのエラーコード（500）をModelに登録
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);

        return "error";
    }
}