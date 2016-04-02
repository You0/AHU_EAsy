package krelve.app.Easy.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImagePreProcess {
    public String Cookies;
    private String ImageUrl = null;
	private static Map<Bitmap, String> trainMap = null;
    private static Context context = null;
	private static int index = 0;
	public static String srcPath = "data/data/krelve.app.kuaihu/";
	public static String trainPath = null;
	//public static String tempPath = "temp\\";

    public ImagePreProcess(Context context,String ImageUrl){
        this.ImageUrl = ImageUrl;
        this.context = context;
    }


	public int isBlue(int colorInt) {
        int redValue = Color.red(colorInt);
        int blueValue = Color.blue(colorInt);
        int greenValue = Color.green(colorInt);
		int rgb = redValue + blueValue + greenValue;
		if (rgb == 153) {
			return 1;
		}
		return 0;
	}

	public int isBlack(int colorInt) {
        int redValue = Color.red(colorInt);
        int blueValue = Color.blue(colorInt);
        int greenValue = Color.green(colorInt);
        int rgb = redValue + blueValue + greenValue;
		if (rgb <= 100) {
			return 1;
		}
		return 0;
	}

	public int isWhite(int colorInt) {
        int redValue = Color.red(colorInt);
        int blueValue = Color.blue(colorInt);
        int greenValue = Color.green(colorInt);
        int rgb = redValue + blueValue + greenValue;
		if (rgb > 600) {
			return 1;
		}
		return 0;
	}

	/**
	 * 去除背景，二值化
	 *
	 * @param picFile
	 * @return
	 * @throws Exception
	 */
	public Bitmap removeBackgroud(String picFile) throws Exception {
        Bitmap bt = BitmapFactory.decodeFile(picFile);
        bt =  bt.createBitmap(bt,5,1,bt.getWidth()-5,bt.getHeight()-2);
        bt = bt.createBitmap(bt,0,0,50,bt.getHeight());
		int width = bt.getWidth();
		int height = bt.getHeight();
//        Color cl = Bitmap.GetPixel(30, 30);
//        if (cl.ToArgb() == Color.Black.ToArgb())
//        {
//            //是黑色
//        }
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (isBlue(bt.getPixel(x,y)) == 1) {
                    bt.setPixel(x, y, Color.BLACK);
				} else {
                    bt.setPixel(x, y, Color.WHITE);
				}
			}
		}
		return bt;
	}

	/**
	 * 按自己的规则分割验证码
	 *
	 * @param img
	 * @return
	 * @throws Exception
	 */
	public List<Bitmap> splitImage(Bitmap img) throws Exception {
		List<Bitmap> subImgs = new ArrayList<Bitmap>();
		int width = img.getWidth() / 4;
		int height = img.getHeight();
		subImgs.add(img.createBitmap(img, 0, 0, width, height));
		subImgs.add(img.createBitmap(img, width, 0, width, height));
		subImgs.add(img.createBitmap(img, width * 2, 0, width, height));
		subImgs.add(img.createBitmap(img, width * 3, 0, width, height));
		return subImgs;
	}

	/**
	 * 载入训练好的字摸
	 *
	 * @return
	 * @throws Exception
	 */
	public Map<Bitmap, String> loadTrainData() throws Exception {
		if (trainMap == null) {
			Map<Bitmap, String> map = new HashMap<Bitmap, String>();
			String[] files = context.getResources().getAssets().list("train");
			for (String file : files) {
				map.put(BitmapFactory.decodeStream(context.getAssets().open("train/" + file)), file.charAt(0) + "");
			}
			trainMap = map;
		}
		return trainMap;
	}

	/**
	 * 识别分割的单个字符
	 *
	 * @param img
	 * @param map
	 * @return
	 */
	public String getSingleCharOcr(Bitmap img, Map<Bitmap, String> map) {
		String result = "#";
		int width = img.getWidth();
		int height = img.getHeight();
		int min = width * height;
		for (Bitmap bi : map.keySet()) {
			int count = 0;
			if (Math.abs(bi.getWidth() - width) > 2)
				continue;
			int widthmin = width < bi.getWidth() ? width : bi.getWidth();
			int heightmin = height < bi.getHeight() ? height : bi.getHeight();
			Label1: for (int x = 0; x < widthmin; ++x) {
				for (int y = 0; y < heightmin; ++y) {
					if (isBlack(img.getPixel(x, y)) != isBlack(bi.getPixel(x, y))) {
						count++;
						if (count >= min)
							break Label1;
					}
				}
			}
			if (count < min) {
				min = count;
				result = map.get(bi);
			}
		}
		return result;
	}

	/**
	 * 验证码识别
	 *
	 * @param file
	 * 要验证的验证码本地路径
	 * @return
	 * @throws Exception
	 */
	public String getAllOcr(String file) throws Exception {
		Bitmap img = removeBackgroud(file);
		List<Bitmap> listImg = splitImage(img);
		Map<Bitmap, String> map = loadTrainData();
		String result = "";
		for (Bitmap bi : listImg) {
			result += getSingleCharOcr(bi, map);
		}
		//ImageIO.write(img, "PNG", new File("result\\" + result + ".png"));
        System.out.println(result);
		return result;
	}


	public void downloadImage() {
            HttpURLConnection httpURLConnection = null;
            String responseCookie = null;
            FileOutputStream fos = null;
            InputStream in = null;
        try {
            URL ImageUrl = new URL(ImagePreProcess.this.ImageUrl);
                  //URL ImageUrl = "http://xk2.ahu.cn/CheckCode.aspx");
            httpURLConnection = (HttpURLConnection) ImageUrl.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);// 允许连接提交信息
            httpURLConnection.setRequestMethod("GET");// 网页默认“GET”提交方式
            httpURLConnection.connect();
            in = httpURLConnection.getInputStream();
            // 取Cookie
            responseCookie = httpURLConnection.getHeaderField("Set-Cookie");
            responseCookie = responseCookie.substring(0, 42);
            File file = new File(srcPath + '0' + ".png");
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int length = 0;
            while ((length = in.read(b)) != -1) {
                fos.write(b, 0, length);
            }
            //System.out.println("cookie:" + responseCookie);
            ImagePreProcess.this.Cookies = responseCookie;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
	}
}
