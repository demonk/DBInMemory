package cn.demonk.test3;

import android.app.Activity;
import android.os.Bundle;

import java.io.FileNotFoundException;

/**
 * Created by guosen.lgs@alibaba-inc.com on 6/20/18.
 */
public class MainActivity extends Activity {

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);


        Test.test();
        MultiNumberation2.test();

        try {
            MMAPTest.test(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SqliteTest.test(this);

    }
}
