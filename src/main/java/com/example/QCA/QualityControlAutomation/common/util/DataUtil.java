package com.example.QCA.QualityControlAutomation.common.util;

import com.example.QCA.QualityControlAutomation.common.InitInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public List<InitInfo> getLabelAndHomepage() throws Exception {
        List<InitInfo> list = new ArrayList<>();
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader("/Users/bsu/Desktop/2022OpenSW/file.json"));

        JSONArray jsonArray = (JSONArray) jsonObject.get("websites");

        for (Object object : jsonArray) {
            JSONObject js = (JSONObject) object;
            StringBuilder homepage = new StringBuilder(js.get("homepage").toString());
            // 제일 뒤에 있는 / 제거
            if (homepage.charAt(homepage.length() - 1) == '/')
                homepage.deleteCharAt(homepage.length() - 1);
            list.add(new InitInfo((String) js.get("label"), homepage.toString()));
        }

        return list;
    }
}
