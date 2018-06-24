package cn.demonk.test3;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by guosen.lgs@alibaba-inc.com on 6/20/18.
 */
public class MultiNumberation {

    private final static char[] flag = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
//            '!', '#', '%', '&', '$', '*', '(', ')', '[', ']', '{', '}', '^', '~', '?', '@', '>',
//            '<', '=' };

    public static int getDeep() {
        return flag.length;
    }

    public static int findInFlag(String f) {
        char c = (char) f.getBytes()[0];
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == c)
                return i;
        }
        return -1;
    }

    public static BigInteger toDecimal(String multi, int deep) {
        BigInteger result = new BigInteger("0");
        if (deep < 1 || deep > flag.length || deep == 10)
            return result;

        BigInteger d = BigInteger.valueOf(deep);
        for (int i = 0; i < multi.length(); i++) {
            int pos = findInFlag(multi.substring(i, i + 1));
            // Arrays.binarySearch(flag, (char) (multi.substring(i, i +
            // 1).getBytes()[0]));

            result = result.add(d.pow(multi.length() - i - 1).multiply(BigInteger.valueOf(pos)));
        }
        return result;
    }

    /**
     *
     * @Title:
     * @Description:将十进制整数转为指定进制的数
     * @param decimal
     *            --十进制整数
     * @param deep
     *            --选择进制，从2～81
     * @return
     */
    public static String toMulti(BigInteger decimal, int deep) {
        if (deep < 1 || deep > flag.length || deep == 10)
            return "";
        // 取余数
        BigInteger d = BigInteger.valueOf(deep);
        BigInteger[] bigDivide = decimal.divideAndRemainder(d);
        int remainder = bigDivide[1].intValue();
        String result = "" + flag[(int) remainder];

        // 取商
        BigInteger quotient = bigDivide[0];
        // 商数下雨指定的进制数则继续
        if (quotient.compareTo(d) >= 0) {
            result = toMulti(quotient, deep) + result;
        } else {
            result = "" + flag[(int) quotient.intValue()] + result;
        }

        return result;
    }

    public static void test(){
        int deep = MultiNumberation.getDeep();
        // System.out.println("deep=" + deep);
        long s = new Date().getTime();
        // MultiNumeration.radixConvert(238328L, deep);
//        for (long i = 1000000; i < 1000002; i++) {
//            String code = String.valueOf(i);
        String code= "1fffffffffffff";
            // String code =
            // CreateCodeHelper.createCode(SeqObjectName.ServiceGroup_Category);
            // System.out.println("code=" + code);
            BigInteger big = new BigInteger(code,16);

            String ret = MultiNumberation.toMulti(big, deep);
            System.out.println(code + ">>" + ret + "===>" + MultiNumberation.toDecimal(ret, deep));
//        }
        System.out.println("deep=" + deep + ",timeout:" + (new Date().getTime() - s));
    }
}
