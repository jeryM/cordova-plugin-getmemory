package cordova.plugin.getmemory;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */
public class Getmemory extends CordovaPlugin {
  private static final String MEM_INFO_PATH = "/proc/meminfo";
  public static final String MEMTOTAL = "MemTotal";
  public static final String MEMFREE = "MemFree";

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("coolMethod")) {
      String message = args.getString(0);
      this.coolMethod(message, callbackContext);
      return true;
    }
    return false;
  }

  private void coolMethod(String message, CallbackContext callbackContext) {
//        if (message != null && message.length() > 0) {
//
//            callbackContext.success(message);
//        } else {
//            callbackContext.error("Expected one non-empty string argument.");
//        }


    JSONObject js = new JSONObject();
    Context context = this.cordova.getActivity().getApplicationContext();
    try {
      /*单位 kb */
      js.put("totalMemory",getTotalMemory());
      js.put("freeMemory",getMemInfoIype(context, MEMFREE));

    } catch (JSONException e) {
      e.printStackTrace();
    }

    callbackContext.success(js);
  }



  //获取总内存的大小
  private long getTotalMemory() {
//        MemTotal:         341780 kB
    try {
      FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
      //包装一个一行行读取的流
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
      //取到所有的内存信息
      String memTotal = bufferedReader.readLine();

      StringBuffer sb = new StringBuffer();

      for (char c : memTotal.toCharArray()) {

        if (c >= '0' && c <= '9') {
          sb.append(c);
        }
      }
      //为了方便格式化 所以乘以1024
      long totalMemory = Long.parseLong(sb.toString()) * 1024;

      return totalMemory;

    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  public static String getMemInfoIype(Context context, String type) {
    try {
      FileReader fileReader = new FileReader(MEM_INFO_PATH);
      BufferedReader bufferedReader = new BufferedReader(fileReader, 4 * 1024);
      String str = null;
      while ((str = bufferedReader.readLine()) != null) {
        if (str.contains(type)) {
          break;
        }
      }
      bufferedReader.close();
            /* \\s表示   空格,回车,换行等空白符,
            +号表示一个或多个的意思     */
      String[] array = str.split("\\s+");
      // 获得系统总内存，单位是KB，乘以1024转换为Byte
      int length = Integer.valueOf(array[1]).intValue() * 1024;
      return android.text.format.Formatter.formatFileSize(context, length);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }



}
