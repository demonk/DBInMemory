package cn.demonk.test3;

import android.util.Log;

import java.math.BigInteger;

/**
 * Created by guosen.lgs@alibaba-inc.com on 6/20/18.
 */
public class Test {


    public static void test() {
        int a = 16;
        int b = 62;
        String aa = "1fffffffffffff";

        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < aa.length(); i++) {
            sum = sum.multiply(BigInteger.valueOf(a)).add(BigInteger.valueOf(getnum(aa.charAt(i))));
        }

        String bb = "";

        while (!sum.equals(BigInteger.ZERO)) {
            bb = retchar(sum.mod(BigInteger.valueOf(b)).intValue()) + bb;
            sum = sum.divide(BigInteger.valueOf(b));
        }
        if (bb.equals("")) {
            bb = "0";
        }
        Log.e("demonk","bb="+bb);
    }

    static int getnum(char m) {
        if (m >= '0' && m <= '9') {
            return m - '0';
        } else if (m >= 'A' && m <= 'Z') {
            return m - 'A' + 10;
        } else {
            return m - 'a' + 36;
        }
    }

    static char retchar(int i) {
        if (i <= 9) {
            return (char) (i + '0');
        } else if (i >= 10 && i <= 35) {
            return (char) (i - 10 + 'A');
        } else {
            return (char) (i - 36 + 'a');
        }
    }
}
