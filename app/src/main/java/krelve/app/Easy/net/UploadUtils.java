package krelve.app.Easy.net;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 11092 on 2016/2/24.
 */
public class UploadUtils {

    public static String BOUNDARY = UUID.randomUUID().toString(); //边界标识
    public static String PREFIX = "--", LINE_END = "\r\n", CONTENT_TYPE = "multipart/form-data"; // 内容类型;


    public static void writeStringParams(HashMap<String, String> textParams,
                                         DataOutputStream ds) throws Exception {
        Set<String> keySet = textParams.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
            String name = it.next();
            String value = textParams.get(name);
            ds.writeBytes("--" + BOUNDARY + "\r\n");
            ds.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n");
            ds.writeBytes("\r\n");
            value = value + "\r\n";
            ds.write(value.getBytes());

        }
    }

    public static void writeFileParams(HashMap<String, Bitmap> fileparams,
                                       DataOutputStream ds) throws Exception {
        Set<String> keySet = fileparams.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
            String temp = it.next();
            String name = temp.substring(0, 5);
            Bitmap value = fileparams.get(temp);
            //System.out.println("theName---->" + value.getName());
            ds.writeBytes(PREFIX + BOUNDARY + LINE_END);
            ds.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\""
                    + URLEncoder.encode(UUID.randomUUID().toString().substring(0, 32)+".jpg", "UTF-8") + "\"\r\n");
            ds.writeBytes("Content-Type:application/octet-stream \r\n");
            ds.writeBytes("\r\n");
            ds.write(getBytes(value));
            ds.writeBytes("\r\n");
        }
    }

    // 把文件转换成字节数组
    private static byte[] getBytes(Bitmap bitmap) {
        FileInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out.toByteArray();
    }

    public static void paramsEnd(DataOutputStream ds) throws Exception {
        ds.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
        ds.writeBytes(LINE_END);
    }




}
