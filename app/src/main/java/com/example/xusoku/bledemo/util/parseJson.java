package com.example.xusoku.bledemo.util;

import android.util.Log;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Davis on 2016/4/15.
 */
public class parseJson {
    public static String getString(EMMessage msg) throws JSONException {


        EMMessageBody s=msg.getBody();

//        JSONObject jsonObject=new JSONObject(msg);

//        Log.e("sss",""+s.toString());


        String ssss=s.toString();
        if(ssss.contains("ext")){
            return "输入有误";
        }else {
            String sss = ssss.substring(4, ssss.length() - 1);
            return sss;
        }
    }
}
