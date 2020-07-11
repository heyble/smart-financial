package com.smart.financial.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smart.financial.common.SmartException;
import com.smart.financial.configuration.AppProperties;
import com.smart.financial.model.StockBaseMO;
import com.smart.financial.model.StockListMO;
import com.smart.financial.model.StockWeekMO;
import com.smart.financial.model.TransactionCalendarMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Stock定制代理类
 */
@Component
public class StockProxy {

    private RestTemplate restTemplate;
    private AppProperties appProperties;
    /**
     * 用于将接口返回的数据转换为StockListMO
     */
    private static final Method[] STOCK_LIST_MO_METHODS = StockListMO.class.getMethods();
    private static final Logger LOGGER = LoggerFactory.getLogger(StockProxy.class);
    /**
     * 用于将接口返回的数据转换为StockBaseMO
     */
    private static final Method[] STOCK_BASE_MO_METHODS = StockBaseMO.class.getMethods();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public StockProxy(RestTemplate restTemplate, AppProperties appProperties) {
        this.restTemplate = restTemplate;
        this.appProperties = appProperties;
    }

    public String get(String url) {
        return restTemplate.getForObject(url, String.class);
    }

    public String post(String url) {

        String request = "{ \t\"api_name\": \"stock_basic\", \t\"token\": \"a6fbbe5c78c74d284b72cfaa463143eb3f8b2ea3e21be2154823c882\", \t\"params\": { \t\t\"list_status\": \"L\" \t}, \t\"fields\": \"ts_code,symbol,name,exchange,market\" }";
        return restTemplate.postForObject(url, request, String.class);
    }


    public StockBaseMO getStockBase(String tsCode) throws SmartException {
        final Date today = new Date();
        return getStockBaseByDate(tsCode, DATE_FORMAT.format(today));
    }

    public StockBaseMO getStockBaseByDate(String tsCode, String dateStr) throws SmartException {
        String request = "{\"api_name\": \"daily\",\"token\": \"2bfd2e2344838353a793b3195d2faeb49e412c72cf75599aa4efe5e3\",\"params\": {\"ts_code\": \""+tsCode+"\",\"trade_date\": \""+dateStr+"\"},\"fields\": \"ts_code,trade_date,open,high,low,close,vol\"}";
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(appProperties.getTushareUrl(), request, String.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new SmartException("请求代理接口失败，URL：" + appProperties.getTushareUrl() + ", " + responseEntity.getBody());
        }
        final String body = responseEntity.getBody();
        final List<StockBaseMO> stockBaseMOList = carverStockXList(body, StockBaseMO.class);
        if (CollectionUtils.isEmpty(stockBaseMOList)) {
            return null;
        }
        return stockBaseMOList.get(0);
    }

    public List<StockBaseMO> getStockBaseList(String tsCode) throws SmartException {
        String request = "{\"api_name\": \"daily\",\"token\": \"2bfd2e2344838353a793b3195d2faeb49e412c72cf75599aa4efe5e3\",\"params\": {\"ts_code\": \""+tsCode+"\",\"start_date\": \"20190620\"},\"fields\": \"ts_code,trade_date,open,high,low,close,vol\"}";
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(appProperties.getTushareUrl(), request, String.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new SmartException("请求代理接口失败，URL：" + appProperties.getTushareUrl() + ", " + responseEntity.getBody());
        }
        final String body = responseEntity.getBody();
        return carverStockXList(body, StockBaseMO.class);
    }

    private <T> List<T> carverStockXList(String body, Class<T> clazz) {
        final JSONObject responseBody = JSONObject.parseObject(body);
        final JSONObject data = responseBody.getJSONObject("data");
        final JSONArray fields = data.getJSONArray("fields");
        final JSONArray items = data.getJSONArray("items");

        // 没有数据
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> stockXList = new ArrayList<>(items.size());

        for (Object item : items) {
            final T stockXMO = carverStockXMO(fields, item, clazz);
            if (null != stockXMO) {
                stockXList.add(stockXMO);
            }
        }
        return stockXList;
    }

    private <T> T carverStockXMO(JSONArray fields, Object item, Class<T> clazz) {
        final Method[] methods = clazz.getMethods();
        JSONArray detail = (JSONArray) item;
        T stockXMO = null;
        try {
            stockXMO = clazz.newInstance();
            for (int i = 0; i < fields.size(); i++) {
                for (Method method : methods) {
                    if (method.getName().contains("set") && method.getName().toLowerCase(Locale.ENGLISH).contains(fields.get(i).toString().replace("_", ""))) {
                        final Class<?> parameterType = method.getParameterTypes()[0];
                        Object param = null;
                        if (parameterType == String.class) {
                            param = detail.get(i) == null ? "" : detail.get(i).toString();
                        }else if(parameterType == Date.class){
                            param = DATE_FORMAT.parse(detail.get(i) == null ? "" : detail.get(i).toString());
                        }else if(parameterType ==  Double.class){
                            param = Double.valueOf(detail.get(i) == null ? "0" : detail.get(i).toString());
                        }else if(parameterType == Long.class){
                            param = Long.valueOf(detail.get(i) == null ? "0" : detail.get(i).toString().split("\\.")[0]);
                        }else if(parameterType == Integer.class){
                            param = Integer.valueOf(detail.get(i) == null ? "0" : detail.get(i).toString().split("\\.")[0]);
                        }
                        method.invoke(stockXMO, param);
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ParseException | InstantiationException e) {
            LOGGER.error("carver response to MO error",e);
        }
        return stockXMO;
    }

    /**
     * 获取StockList，该接口为定制接口，url，入参和StockListMO转换都是定制的。
     *
     * @return List<StockListMO>
     * @throws SmartException 异常
     */
    public List<StockListMO> getStockList() throws SmartException {
        String request = "{\"api_name\": \"stock_basic\",\"token\": \"a6fbbe5c78c74d284b72cfaa463143eb3f8b2ea3e21be2154823c882\",\"params\": {},\"fields\": \"ts_code, symbol, name, area, industry, fullname, enname, market, exchange, curr_type, list_status, list_date, delist_date, is_hs\" }";
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(appProperties.getTushareUrl(), request, String.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new SmartException("请求代理接口失败，URL：" + appProperties.getTushareUrl());
        }
        final String body = responseEntity.getBody();

        return carverStockListBody(body);
    }

    private List<StockListMO> carverStockListBody(String body) {
        final JSONObject responseBody = JSONObject.parseObject(body);
        final JSONObject data = responseBody.getJSONObject("data");
        final JSONArray fields = data.getJSONArray("fields");
        final JSONArray items = data.getJSONArray("items");

        List<StockListMO> stockListMOList = new ArrayList<>(items.size());

        for (Object item : items) {
            stockListMOList.add(carverStockListMO(fields, item));
        }
        return stockListMOList;
    }

    private StockListMO carverStockListMO(JSONArray fields, Object item) {
        StockListMO stockListMO = new StockListMO();
        JSONArray detail = (JSONArray) item;
        try {
            for (int i = 0; i < fields.size(); i++) {
                for (Method method : STOCK_LIST_MO_METHODS) {
                    if (method.getName().contains("set") && method.getName().toLowerCase(Locale.ENGLISH).contains(fields.get(i).toString().replace("_", ""))) {
                        method.invoke(stockListMO, detail.get(i) == null ? "" : detail.get(i).toString());
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return stockListMO;
    }

    /**
     * 初始化周线行情专用
     *
     * @param tsCode
     * @return
     * @throws SmartException
     */
    public List<StockWeekMO> getStockWeekList(String tsCode) throws SmartException {
        String request = "{\"api_name\": \"weekly\",\"token\": \"2bfd2e2344838353a793b3195d2faeb49e412c72cf75599aa4efe5e3\",\"params\": {\"ts_code\": \""+tsCode+"\",\"start_date\": \"20170623\"},\"fields\": \"ts_code,trade_date,open,high,low,close,vol\"}";
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(appProperties.getTushareUrl(), request, String.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new SmartException("请求代理接口失败，URL：" + appProperties.getTushareUrl() + ", " + responseEntity.getBody());
        }
        final String body = responseEntity.getBody();
        return carverStockXList(body, StockWeekMO.class);
    }

    public List<TransactionCalendarMO> getTransactionCalendar() throws SmartException {
        String request = "{\"api_name\": \"trade_cal\",\"token\": \"2bfd2e2344838353a793b3195d2faeb49e412c72cf75599aa4efe5e3\",\"params\": {\"start_date\": \"20170623\"},\"fields\": \"exchange,cal_date,is_open,pretrade_date\"}";
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(appProperties.getTushareUrl(), request, String.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new SmartException("请求代理接口失败，URL：" + appProperties.getTushareUrl() + ", " + responseEntity.getBody());
        }
        final String body = responseEntity.getBody();
        return carverStockXList(body, TransactionCalendarMO.class);
    }
}
