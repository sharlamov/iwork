package com.reporting.bean;

import com.reporting.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MapRest {

    @Autowired
    private ReportService reportService;
    private String colors[] = {"#F5DEB3", "#FF7F50", "#FF4500", "#FF0000"};

    @RequestMapping("/dataMap")
    public Map<String, List<Object>> getDataMap(
            @RequestParam(value = "season") Integer season
            , @RequestParam(value = "sc") Integer sc
            , @RequestParam(value = "tip", required = false) String tip) {

        Map<String, List<Object>> result = new HashMap<>();
        List<Object> pageDataList = reportService.getContractByDistrict(season, sc);
        List<Object> data = new ArrayList<>();
        List<Object> dataPoints = new ArrayList<>();
        List<Object> dataLines = new ArrayList<>();

        for (Object obj : pageDataList) {
            Object[] row = (Object[]) obj;
            double d = Double.valueOf(row[3].toString());

            int res = -1;
            if (d > 0d && d <= 100d) {
                res = 0;
            } else if (d > 100d && d <= 300d) {
                res = 1;
            } else if (d > 300d && d <= 1000d) {
                res = 2;
            } else if (d > 1000d) {
                res = 3;
            }

            if (res != -1) {
                Map<Object, Object> map = new HashMap<>();
                map.put("id", "MD-" + row[0]);
                map.put("color", colors[res]);
                map.put("customData", d);
                //map.put("url", "regionDetail.xhtml?regionId=" + row[0] + "&regionName=" + row[1] + "&season=" + season);
                data.add(map);
            }
        }


        String[] arrParams = tip != null && tip.length() > 0 ? tip.split(",") : new String[0];
        for (String param : arrParams) {
            int i = Integer.valueOf(param);
            switch (i) {
                case 0: {
                    List<Object> ourElevators = reportService.getOurElevators();
                    for (Object obj : ourElevators) {
                        Object[] row = (Object[]) obj;
                        Map<Object, Object> map = new HashMap<>();
                        map.put("title", row[1]);
                        map.put("latitude", row[2]);
                        map.put("longitude", row[3]);
                        map.put("color", "blue");
                        dataPoints.add(map);
                    }
                }
                break;
                case 1: {
                    List<Object> anyElevators = reportService.getAnyElevators();
                    for (Object obj : anyElevators) {
                        Object[] row = (Object[]) obj;
                        Map<Object, Object> map = new HashMap<>();
                        map.put("title", row[1]);
                        map.put("latitude", row[2]);
                        map.put("longitude", row[3]);
                        map.put("color", "green");
                        dataPoints.add(map);
                    }
                }
                break;
                case 2: {
                    List<Object> buyPlaces = reportService.getBuyPlaces(season, sc);
                    for (Object obj : buyPlaces) {
                        Object[] row = (Object[]) obj;
                        Map<Object, Object> map = new HashMap<>();
                        map.put("title", row[1]);
                        map.put("latitude", row[2]);
                        map.put("longitude", row[3]);
                        map.put("color", "black");
                        dataPoints.add(map);
                    }
                }
                break;
                default: {
                    List<Object> connections = reportService.getPlacesConnections(season, sc);
                    for (Object obj : connections) {
                        Object[] row = (Object[]) obj;
                        Map<Object, Object> map = new HashMap<>();
                        map.put("latitudes", new Object[]{row[3], row[6]});
                        map.put("longitudes", new Object[]{row[4], row[7]});
                        dataLines.add(map);
                    }
                }
            }
        }

        result.put("areas", data);
        result.put("points", dataPoints);
        result.put("lines", dataLines);
        return result;
    }
}