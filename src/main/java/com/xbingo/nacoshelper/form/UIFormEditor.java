package com.xbingo.nacoshelper.form;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.xbingo.nacoshelper.nacos.dto.NacosTokenDto;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.Map;

public final class UIFormEditor extends UserDataHolderBase implements FileEditor {
	public static final FileEditorState MY_EDITOR_STATE = new FileEditorState() {
		@Override
		public boolean canBeMergedWith(FileEditorState otherState, FileEditorStateLevel level) {
			return false;
		}
	};
	private final VirtualFile file;
	private GuiForm myEditor;

	public UIFormEditor(@NotNull Project project, final VirtualFile file, Map<String, Object> ymlConfigMap, NacosTokenDto nacosTokenDto) {
		this.file = file;
		myEditor = new GuiForm(project, file, ymlConfigMap, nacosTokenDto);
	}

	@Override
	@NotNull
	public JComponent getComponent() {
		return myEditor.getRootComponent();
	}

	@Override
	public void dispose() {
		if (myEditor != null) {
			myEditor.dispose();
		}
	}

	@Override
	public VirtualFile getFile() {
		return file;
	}

	@Override
	public JComponent getPreferredFocusedComponent() {
		return myEditor.getPreferredFocusedComponent();
	}

	@Override
	@NotNull
	public String getName() {
		return "NacosYml";
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void selectNotify() {
		myEditor.selectNotify();
	}

	@Override
	public void deselectNotify() {
	}

	@Override
	public void addPropertyChangeListener(@NotNull final PropertyChangeListener listener) {
	}

	@Override
	public void removePropertyChangeListener(@NotNull final PropertyChangeListener listener) {
	}

	@Override
	public BackgroundEditorHighlighter getBackgroundHighlighter() {
		return null;
	}

	@Override
	public FileEditorLocation getCurrentLocation() {
		return null;
	}

	@Override
	@NotNull
	public FileEditorState getState(@NotNull final FileEditorStateLevel ignored) {
		return MY_EDITOR_STATE;
	}

	@Override
	public void setState(@NotNull final FileEditorState state) {
	}

	@Override
	public StructureViewBuilder getStructureViewBuilder() {
		return null;
	}
}
