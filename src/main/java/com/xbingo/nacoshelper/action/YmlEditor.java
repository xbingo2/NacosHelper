package com.xbingo.nacoshelper.action;

import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.actions.IncrementalFindAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.EditorTextField;

import static com.intellij.openapi.editor.ex.EditorEx.VERTICAL_SCROLLBAR_RIGHT;

public class YmlEditor extends EditorTextField  {

    public YmlEditor() {
        super("");
    }

    public YmlEditor(String text, Project project) {
        super(EditorFactory.getInstance().createDocument(StringUtil.convertLineSeparators(text)), project,
                FileTypeManager.getInstance().getStdFileType("YAML"));
    }

    @Override
    protected EditorEx createEditor() {
        EditorEx editor = super.createEditor();
        editor.setInsertMode(true);
        editor.setVerticalScrollbarOrientation(VERTICAL_SCROLLBAR_RIGHT);
        editor.setHorizontalScrollbarVisible(true);
        editor.setVerticalScrollbarVisible(true);
        editor.setOneLineMode(false);

        EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setAutoCodeFoldingEnabled(true);
        settings.setWheelFontChangeEnabled(true);
        settings.setDndEnabled(true);
        settings.setIndentGuidesShown(true);
        settings.setUseSoftWraps(true);
        settings.setUseCustomSoftWrapIndent(true);
        settings.setCustomSoftWrapIndent(0);
        settings.setWhitespacesShown(true);
        settings.setGutterIconsShown(true);
        editor.putUserData(IncrementalFindAction.SEARCH_DISABLED, Boolean.FALSE);
        return editor;
    }
}
