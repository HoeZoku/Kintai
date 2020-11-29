package com.example.demo.login.domain.repository.jdbc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowCallbackHandler;

/*csv書き出し用クラス
 * RowCallbackHandlerを使用したコールバック処理を実装
 */

public class UserRowCallbackHandler implements RowCallbackHandler {

	//FileWriterクラスのオブジェクトを作成する場合はIOExceptionが発生する可能性があるため例外の処理
    @Override
    public void processRow(ResultSet rs) throws SQLException {

        try {

            //ファイル書き込みの準備
        	//書き込みたいファイルを対象にFileクラストを作成。そのオブジェクトを引数としてFileWriterクラストを作成。
        	//FileWriterは一文字ずつ入行わなくてはならないためある程度データを溜めてから出力できるBufferedWriterにしたい
            //BufferdWriterは連結ストリームと呼ばれ単独使用できないためFileWriterのストリームにつなげることで使用可能
            File file = new File("sample.csv");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            //取得件数分loop
            do {

                //ResultSetから値を取得してStringにセット
                String str = rs.getString("user_id") + ","
                        + rs.getString("password") + ","
                        + rs.getString("user_name") + ","
                        + rs.getDate("birthday") + ","
                        + rs.getInt("age") + ","
                        + rs.getBoolean("marriage") + ","
                        + rs.getString("role");

                //ファイルに書き込み＆改行
                bw.write(str);
                bw.newLine();

            } while(rs.next());

            //強制的に書き込み＆ファイルクローズ
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }
}