package com.xbingo.nacoshelper.common;

import com.xbingo.nacoshelper.action.MyTree;
import com.xbingo.nacoshelper.nacos.dto.NacosTreeDto;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtil {

    public static void setTreeData(MyTree groupTree, Map<String, Object> ymlConfigMap) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        String applicationName = ymlConfigMap.get(Constants.APPLICATION_NAME).toString();
        String group = ymlConfigMap.get(Constants.CONFIG_NACOS_GROUP).toString();
        String extension = ymlConfigMap.get(Constants.CONFIG_NACOS_EXTENSION).toString();
        String namespace = ymlConfigMap.get(Constants.CONFIG_NACOS_NAMESPACE).toString();
        String profilesActive = ymlConfigMap.get(Constants.PROFILES_ACTIVE).toString();


        DefaultMutableTreeNode configGroup = new DefaultMutableTreeNode(group);
        root.add(configGroup);

        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(applicationName + "-" + profilesActive + "." + extension );
        NacosTreeDto configDto = new NacosTreeDto();
        configDto.setDataId(applicationName + "-" + profilesActive + "." + extension);
        configDto.setTenant(namespace);
        configDto.setGroup(group);
        treeNode.setUserObject(configDto);
        configGroup.add(treeNode);

        Map<String, DefaultMutableTreeNode> groupMap = new HashMap<>();
        groupMap.put(group, configGroup);

        List<Map<String, Object>> dataIdList = (List)ymlConfigMap.get(Constants.CONFIG_NACOS_EXTENSION_CONFIGS);
        if (null != dataIdList && !dataIdList.isEmpty()) {
            for (Map<String, Object> map : dataIdList) {
                if (!groupMap.containsKey(map.get("group").toString())) {
                    DefaultMutableTreeNode extensionGroup = new DefaultMutableTreeNode(map.get("group").toString());
                    root.add(extensionGroup);
                    groupMap.put(map.get("group").toString(), extensionGroup);
                }
                DefaultMutableTreeNode extensionTreeNode = new DefaultMutableTreeNode(map.get("dataId").toString());
                NacosTreeDto extConfigDto = new NacosTreeDto();
                extConfigDto.setDataId(map.get("dataId").toString());
                extConfigDto.setTenant(namespace);
                extConfigDto.setGroup(map.get("group").toString());
                extensionTreeNode.setUserObject(extConfigDto);
                groupMap.get(map.get("group").toString()).add(extensionTreeNode);
            }
        }
        DefaultTreeModel rightTreeModel = new DefaultTreeModel(root);
        groupTree.setModel(rightTreeModel);

        groupTree.expandPath(new TreePath(configGroup.getPath()));
    }
}
