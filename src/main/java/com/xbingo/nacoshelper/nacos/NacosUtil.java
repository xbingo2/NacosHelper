package com.xbingo.nacoshelper.nacos;

import com.fasterxml.jackson.jr.ob.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xbingo.nacoshelper.common.Constants;
import com.xbingo.nacoshelper.common.HttpUtil;
import com.xbingo.nacoshelper.nacos.dto.NacosConfigDto;
import com.xbingo.nacoshelper.nacos.dto.NacosTokenDto;
import com.xbingo.nacoshelper.nacos.dto.NacosTreeDto;
import net.minidev.json.JSONUtil;
import org.apache.http.client.HttpClient;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NacosUtil {

    public static final ConcurrentHashMap<String, NacosTokenDto> TOKEN_MAP = new ConcurrentHashMap();

    public static NacosConfigDto EDIT_CONFIG = new NacosConfigDto();

    public static NacosTokenDto loginNacos(Map<String, Object> ymlMap) {
        try {
            String username = ymlMap.get(Constants.CONFIG_NACOS_USERNAME).toString();
            String password = ymlMap.get(Constants.CONFIG_NACOS_PASSWORD).toString();
            String nacosUrl = ymlMap.get(Constants.CONFIG_NACOS_URL).toString();
            if (!TOKEN_MAP.contains(nacosUrl + "-" + username)) {
                loginNacos(nacosUrl, username, password);
            }
            else {
                NacosTokenDto nacosTokenDto = TOKEN_MAP.get(nacosUrl + "-" + username);
                if ((System.currentTimeMillis() - nacosTokenDto.getLoginTime() - 60) < nacosTokenDto.getTokenTtl()) {
                    loginNacos(nacosUrl, username, password);
                }
            }
            return TOKEN_MAP.get(nacosUrl + "-" + username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void loginNacos(String nacosUrl, String username, String password) {
        try {

            long loginTime = System.currentTimeMillis();
            String url = "http://" + nacosUrl + "/nacos/v1/auth/users/login";

            String data = "username="+username+ "&password="+ password;
            String result = HttpUtil.builder().post(url, data);

            Gson gson = new Gson();
            NacosTokenDto nacosTokenDto = gson.fromJson(result, NacosTokenDto.class);
            nacosTokenDto.setNacosUrl(nacosUrl);
            nacosTokenDto.setLoginTime(loginTime);
            TOKEN_MAP.put(nacosUrl + "-" + username, nacosTokenDto);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static NacosConfigDto editConfigStr(NacosTreeDto nacosTreeDto, NacosTokenDto nacosTokenDto) {
        try {
            if (null != nacosTokenDto) {
                String nacosUrl = nacosTokenDto.getNacosUrl();

                String url = "http://"+ nacosUrl + "/nacos/v1/cs/configs?"
                        + "username=" + nacosTokenDto.getUsername()
                        + "&accessToken="+ nacosTokenDto.getAccessToken()
                        + "&dataId="+ nacosTreeDto.getDataId()
                        + "&group="+ nacosTreeDto.getGroup()
                        + "&namespaceId="+ nacosTreeDto.getTenant()
                        + "&tenant="+ nacosTreeDto.getTenant()
                        + "&show=all";
                String result = HttpUtil.builder().get(url);

                Gson gson = new Gson();
                EDIT_CONFIG = gson.fromJson(result, NacosConfigDto.class);
                EDIT_CONFIG.setInitContent(EDIT_CONFIG.getContent());
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return EDIT_CONFIG;
    }

    public static boolean commit(String content, NacosConfigDto nacosConfigDto, NacosTreeDto nacosTreeDto, NacosTokenDto nacosTokenDto) {
        try {
            if (null != nacosTokenDto) {
                String nacosUrl = nacosTokenDto.getNacosUrl();

                String url = "http://"+ nacosUrl + "/nacos/v1/cs/configs";

                String data = "username="+ nacosTokenDto.getUsername()
                        + "&accessToken="+ nacosTokenDto.getAccessToken()
                        + "&dataId="+ nacosTreeDto.getDataId()
                        + "&group="+ nacosTreeDto.getGroup()
                        + "&content="+ content
                        + "&type="+ nacosConfigDto.getType()
                        + "&id="+ nacosConfigDto.getId()
                        + "&md5="+ nacosConfigDto.getMd5()
                        + "&tenant="+ nacosTreeDto.getTenant()
                        + "&createTime="+ System.currentTimeMillis()
                        + "&modifyTime="+ System.currentTimeMillis()
                        + "&createIp="+ InetAddress.getLocalHost();

                String result = HttpUtil.builder().post(url, data);
                return "true".equals(result);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }



}
