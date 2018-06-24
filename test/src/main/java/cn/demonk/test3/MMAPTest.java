package cn.demonk.test3;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by guosen.lgs@alibaba-inc.com on 6/22/18.
 */
public class MMAPTest {

    public static void test(Context ctx) throws FileNotFoundException {


        File file = new File(ctx.getCacheDir(), "abc.dat");
        RandomAccessFile rfile = new RandomAccessFile(file, "rw");
        try {
            MappedByteBuffer buf = rfile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 0x8FFFFFF);
            buf.putLong(111111111);
            buf.putChar('s');
            buf.putChar('b');
            buf.putChar(0, 'b');
            buf.force();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                rfile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
