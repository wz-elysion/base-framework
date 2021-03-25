package wz_ling1991.base.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class FileCopyTest {


    @Test
    public void test() {
        String source = "C:\\Users\\weizheng\\Downloads\\pycharm-community-2020.3.3.exe";
        String target = "C:\\Users\\weizheng\\Downloads\\pycharm-community-2020.3.3";
        oldFileCopy(source, target+"-oldFileCopy");
        mMapFileCopy(source, target+"-mMapFileCopy");
        sendFileCopy(source, target+"sendFileCopy");
    }

    private void oldFileCopy(String source, String target) {
        try {
            FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(target);
            long start = System.currentTimeMillis();
            byte[] buff = new byte[4096];
            long read = 0, total = 0;
            while ((read = inputStream.read(buff)) >= 0) {
                total += read;
                outputStream.write(buff);
            }
            outputStream.flush();
            log.info("耗时：{}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mMapFileCopy(String source, String target) {
        try {
            FileChannel sourceChannel = new RandomAccessFile(source, "rw").getChannel();
            FileChannel targetChannel = new RandomAccessFile(target, "rw").getChannel();
            long start = System.currentTimeMillis();
            MappedByteBuffer map = targetChannel.map(FileChannel.MapMode.READ_WRITE, 0, sourceChannel.size());
            sourceChannel.write(map);
            map.flip();
            log.info("耗时：{}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFileCopy(String source, String target) {
        try {
            FileChannel sourceChannel = new RandomAccessFile(source, "rw").getChannel();
            FileChannel targetChannel = new RandomAccessFile(target, "rw").getChannel();
            long start = System.currentTimeMillis();
            sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
            log.info("耗时：{}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}