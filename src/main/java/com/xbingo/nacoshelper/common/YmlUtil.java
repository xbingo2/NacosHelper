package com.xbingo.nacoshelper.common;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.xbingo.nacoshelper.nacos.dto.NacosConfigDto;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YmlUtil {
    private static final Logger LOG = Logger.getInstance("#com.xbingo.nacoshelper.common.YmlUtil");

    public static Map<String, Object> getConfigMap(@NotNull final Project project, @NotNull final VirtualFile file) {
        Map<String, Object> resutlMap = new HashMap<>();
        String name = file.getName();
        if (ArrayUtil.contains(FileUtilRt.getExtension(name), "yml")) {
            try {
                Yaml yaml = new Yaml();
                InputStream inputStream = file.getInputStream();
                Map<String, Object> configMap = yaml.load(inputStream);
                Map<String, Object> ymlMap = YmlUtil.convertYml(configMap);
                if (ymlMap.containsKey(Constants.CONFIG_NACOS_URL)
                        && ymlMap.containsKey(Constants.CONFIG_NACOS_USERNAME)
                        && ymlMap.containsKey(Constants.CONFIG_NACOS_PASSWORD)) {
                    resutlMap = ymlMap;
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        return resutlMap;
    }

    public static Map<String, Object> convertYml(Map<String, Object> mapFromYml) {
        Map<String, Object> map = mapFromYml;
        Map<String, Object> mapAfterConvert = new HashMap<>();
        map.forEach((key1, value1) -> {
            if (value1 instanceof Map) {
                mapAfterConvert.putAll(forEachYml(mapAfterConvert, key1, (Map) value1));
            } else {
                mapAfterConvert.put(key1, value1.toString());
            }
        });
        replaceStr(mapAfterConvert, mapAfterConvert);
        return mapAfterConvert;
    }

    public static Map<String, Object> forEachYml(Map<String, Object> mapAfterConvert, String key1, Map<String, Object> map) {
        map.forEach((key2, value2) -> {
            String strNew;
            if (StringUtils.isNotEmpty(key1)) {
                strNew = key1 + "." + key2;
            } else {
                strNew = key2;
            }
            if (value2 instanceof Map) {
                mapAfterConvert.putAll(forEachYml(mapAfterConvert, strNew, (Map) value2));
            } else {
                mapAfterConvert.put(strNew, value2);
            }
        });
        return mapAfterConvert;
    }

    public static void replaceStr(Map<String, Object> configMap, Map<String, Object> allMap) {
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            if (entry.getValue() instanceof String && ((String) entry.getValue()).contains("${")) {
                Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
                Matcher matcher = pattern.matcher((String) entry.getValue());
                if (matcher.find()) {
                    String result = matcher.group(1);
                    entry.setValue(((String) entry.getValue()).replaceAll("\\$\\{" + result + "}",allMap.get(result).toString()));
                }
            }
            else if (entry.getValue() instanceof Map) {
                replaceStr((Map)entry.getValue(), allMap);
            }
            else if (entry.getValue() instanceof List) {
                for (Map<String, Object> map : (List<Map<String, Object>>)entry.getValue()) {
                    replaceStr(map, allMap);
                }
            }
        }
    }

    public static List<NacosConfigDto> getAllDataId(Map<String, Object> ymlMap) {
        List<NacosConfigDto> nacosConfigDtoList = new ArrayList<>();
        NacosConfigDto nacosConfigDto = new NacosConfigDto();
        nacosConfigDto.setDataId("" + ymlMap.get(Constants.APPLICATION_NAME) + "-" + ymlMap.get(Constants.PROFILES_ACTIVE) + "." + ymlMap.get(Constants.CONFIG_NACOS_EXTENSION));
        nacosConfigDto.setGroup("" + ymlMap.get(Constants.CONFIG_NACOS_GROUP));
        nacosConfigDtoList.add(nacosConfigDto);

        List<Map<String, Object>> dataIdList = (List)ymlMap.get(Constants.CONFIG_NACOS_EXTENSION_CONFIGS);
        for (Map<String, Object> map : dataIdList) {
            NacosConfigDto dto = new NacosConfigDto();
            dto.setDataId(map.get("dataId").toString());
            dto.setGroup(map.get("group").toString());
            nacosConfigDtoList.add(dto);
        }
        return nacosConfigDtoList;
    }

}
