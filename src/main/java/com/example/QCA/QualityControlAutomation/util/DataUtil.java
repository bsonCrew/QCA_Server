package com.example.QCA.QualityControlAutomation.util;

import com.example.QCA.QualityControlAutomation.domain.InitInfo;
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
            list.add(new InitInfo((String) js.get("label"), (String) js.get("homepage")));
        }

        return list;
    }
}
