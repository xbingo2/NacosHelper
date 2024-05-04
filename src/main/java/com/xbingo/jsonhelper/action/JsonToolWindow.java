package com.xbingo.jsonhelper.action;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactoryImpl;
import com.xbingo.jsonhelper.panel.JsonPanel;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Steinberg
 */
public class JsonToolWindow implements ToolWindowFactory, DumbAware {
    private JsonPanel jsonPanel;

    public JsonToolWindow(){
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactoryImpl contentFactory = new ContentFactoryImpl();
        jsonPanel = new JsonPanel(project);
        Content content = contentFactory.createContent(jsonPanel.getRootPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
