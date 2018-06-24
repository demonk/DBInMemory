package cn.demonk.test3;

import android.util.Log;

import java.math.BigInteger;

/**
 * 大数进制转换
 * Created by guosen.lgs@alibaba-inc.com on 6/20/18.
 */
public class MultiNumberation2 {

    //符号集，最大可转换进制为表的大小
    private final static char[] SYMBOLS_MAP = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    //            '!', '#', '%', '&', '$', '*', '(', ')', '[', ']', '{', '}', '^', '~', '?', '@', '>',
    //            '<', '=' };

    public static String radixConvert(BigInteger decimal, int deep) {
        if (deep < 1 || deep > SYMBOLS_MAP.length || deep == 10) {
            return "";
        }

        // 余数
        BigInteger d = BigInteger.valueOf(deep);
        BigInteger[] bigDivide = decimal.divideAndRemainder(d);
        int remainder = bigDivide[1].intValue();
        String result = "" + SYMBOLS_MAP[remainder];

        // 商
        BigInteger quotient = bigDivide[0];
        if (quotient.compareTo(d) >= 0) {
            result = radixConvert(quotient, deep) + result;
        } else {
            result = "" + SYMBOLS_MAP[quotient.intValue()] + result;
        }

        return result;
    }

    public static void test() {
        int radix = 62;
        String num = "1fffffffffffff";
        BigInteger big = new BigInteger(num, 16);

        String ret = radixConvert(big, radix);
        Log.e("demonk", num + ">>" + ret);
    }
}
