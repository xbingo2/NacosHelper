package com.xbingo.nacoshelper.action;

import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyTree extends Tree implements DataProvider {
	private Project project;
	public MyTree() {
		setOpaque(false);
	}

	public void setProject(Project project) {
		this.project = project;
	}
	@Override
	public @Nullable Object getData(@NotNull @NonNls String s) {
		return null;
	}
}
