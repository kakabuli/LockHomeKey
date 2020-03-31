package com.kaadas.lock;

import com.kaadas.lock.utils.Rsa;

import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void test1(){
        byte[] data = Rsa.hex2byte2("02cb28c6 7b41c467 60dfa626 108f4bfb 1802571a ddda986f 6e017911 84d676a9 55505330 32303038 31303031 380a");
        String adminPassword = "1470258";
        parseWifiData(adminPassword,data);
    }

    public static void parseWifiData(String adminPassword, byte[] data) {

        String temp = "";
        for (int i = 0; i < adminPassword.length(); i++) {
            temp += "0" + adminPassword.charAt(i);
        }
        byte[] t = Rsa.hex2byte(temp);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("SHA256 出错");
        }
        messageDigest.update(t);
        byte[] adminKey = messageDigest.digest();
        System.out.println("管理密码管理  Sha256之后是 " + Rsa.bytesToHexString(adminKey));

        byte[] content = new byte[32];
        byte[] sn = new byte[13];
        //Object src,  int  srcPos,    Object dest, int destPos,    int length
        System.arraycopy(data, 0, content, 0, content.length);
        System.arraycopy(data, 32, sn, 0, sn.length);

        byte[] decrypt = Rsa.decrypt(content, adminKey);
        System.out.println("解密之后的数据是  " + Rsa.bytesToHexString(adminKey));

        byte[] pwd = new byte[28];
        byte[] crc = new byte[4];

        System.arraycopy(decrypt, 0, pwd, 0, pwd.length);
        System.arraycopy(decrypt, 28, crc, 0, crc.length);

        System.out.println("随机数明文是  " + Rsa.bytesToHexString(pwd));
        System.out.println("RCR明文是  " + Rsa.bytesToHexString(crc));
        CRC32 crc32 = new CRC32();
        crc32.update(pwd);
        long localCrc = crc32.getValue();
        byte[] bytes = Rsa.int2BytesArray((int) localCrc);
        System.out.println("校验和 本地CRC  " + Rsa.bytesToHexString(bytes) + "   锁端CRC  " + Rsa.bytesToHexString(crc));
        if (bytes[0] != crc[0] || bytes[1] != crc[1] || bytes[2] != crc[2] || bytes[3] != crc[3]) { //校验失败
            System.out.println("校验出错   ");
            return;
        }
        System.out.println("校验成功   ");
        return;
    }



    @Test
    public void socketTest(){


    }

    public void socket(String ip,int port){


    }


}