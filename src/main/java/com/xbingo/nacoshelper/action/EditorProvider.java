package com.xbingo.nacoshelper.action;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.xbingo.nacoshelper.common.YmlUtil;
import com.xbingo.nacoshelper.form.UIFormEditor;
import com.xbingo.nacoshelper.nacos.NacosUtil;
import com.xbingo.nacoshelper.nacos.dto.NacosTokenDto;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xbingo
 */
public class EditorProvider implements FileEditorProvider {
	private static final Logger LOG = Logger.getInstance("#com.xbingo.nacoshelper.action.EditorProvider");

	private Map<String, Object> ymlConfigMap = new HashMap<>();

	private NacosTokenDto nacosTokenDto;


	@Override
	public boolean accept(@NotNull final Project project, @NotNull final VirtualFile file) {
		ymlConfigMap = YmlUtil.getConfigMap(project, file);
		if (ymlConfigMap.isEmpty()) {
			return false;
		}
		nacosTokenDto = NacosUtil.loginNacos(ymlConfigMap);
		return true;
	}

	@Override
	@NotNull
	public FileEditor createEditor(@NotNull final Project project, @NotNull final VirtualFile file) {
		LOG.assertTrue(accept(project, file));
		return new UIFormEditor(project, file, ymlConfigMap, nacosTokenDto);
	}

	@Override
	public void disposeEditor(@NotNull final FileEditor editor) {
		Disposer.dispose(editor);
	}

	@Override
	@NotNull
	public FileEditorState readState(@NotNull final Element element, @NotNull final Project project,
									 @NotNull final VirtualFile file) {
		return UIFormEditor.MY_EDITOR_STATE;
	}

	@Override
	public void writeState(@NotNull final FileEditorState state, @NotNull final Project project,
						   @NotNull final Element element) {
	}

	@Override
	@NotNull
	public String getEditorTypeId() {
		return "NacosYml";
	}

	@Override
	@NotNull
	public FileEditorPolicy getPolicy() {
		return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
	}

}
