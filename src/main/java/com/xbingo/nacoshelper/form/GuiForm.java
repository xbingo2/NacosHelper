package com.xbingo.nacoshelper.form;

import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManagerImpl;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.diff.requests.ContentDiffRequest;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.Consumer;
import com.xbingo.nacoshelper.action.MyTree;
import com.xbingo.nacoshelper.action.YmlEditor;
import com.xbingo.nacoshelper.common.Constants;
import com.xbingo.nacoshelper.common.TreeUtil;
import com.xbingo.nacoshelper.common.YmlUtil;
import com.xbingo.nacoshelper.nacos.NacosUtil;
import com.xbingo.nacoshelper.nacos.dto.NacosConfigDto;
import com.xbingo.nacoshelper.nacos.dto.NacosTokenDto;
import com.xbingo.nacoshelper.nacos.dto.NacosTreeDto;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xbingo
 */
public class GuiForm implements Disposable {
	private static final Logger LOG = Logger.getInstance("#com.xbingo.nacoshelper.form.GuiForm");

	private final Project project;
	private final VirtualFile file;
	private JPanel rootPanel;
	private JButton commitBtn;
	private JButton refreshBtn;
	private JLabel namespace;
	private MyTree groupTree;
	private JBPanel editPanel;
	private JSplitPane splitPane;
	private JBScrollPane treeScollPane;
	private JBScrollPane editScrollPane;
	private YmlEditor ymlEditor;

	private List<NacosConfigDto> nacosConfigDtoList = new ArrayList<>();

	public GuiForm(@NotNull Project project, VirtualFile file, Map<String, Object> ymlConfigMap, NacosTokenDto nacosTokenDto) {
		this.project = project;
		this.file = file;
		editPanel.setLayout(new GridLayout(1,1));
		ymlEditor = new YmlEditor("", project);
		editPanel.add(ymlEditor);

		DefaultTreeSelectionModel defaultTreeSelectionModel = new DefaultTreeSelectionModel();
		defaultTreeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		groupTree.setSelectionModel(defaultTreeSelectionModel);
		TreeUtil.setTreeData(groupTree, ymlConfigMap);

		namespace.setText(ymlConfigMap.get(Constants.CONFIG_NACOS_NAMESPACE).toString());
		groupTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode note = (DefaultMutableTreeNode) groupTree.getLastSelectedPathComponent();
				if (null != note && null != note.getUserObject() && 0 == note.getChildCount()) {
					NacosTreeDto nacosTreeDto = (NacosTreeDto)note.getUserObject();
					NacosConfigDto nacosConfigDto = NacosUtil.editConfigStr(nacosTreeDto, nacosTokenDto);
					ymlEditor.setText(nacosConfigDto.getContent());
				}
				else {
					ymlEditor.setText("");
				}
			}
		});

		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				file.refresh(false,true);
				Map<String, Object> ymlConfigMap = YmlUtil.getConfigMap(project, file);
				TreeUtil.setTreeData(groupTree, ymlConfigMap);
			}
		});

		commitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode note = (DefaultMutableTreeNode) groupTree.getLastSelectedPathComponent();
				if (null == note || null == note.getUserObject() || note.getChildCount() > 0) {
					return;
				}


				DiffManagerImpl diffManager = new DiffManagerImpl();
				ContentDiffRequest diffRequest = new ContentDiffRequest() {
					@Override
					public @NlsContexts.DialogTitle @Nullable String getTitle() {
						return "different";
					}

					@Override
					public @NotNull List<DiffContent> getContents() {
						List<DiffContent> virtualFiles = new ArrayList<>();

						Document document1 = new DocumentImpl(NacosUtil.EDIT_CONFIG.getInitContent());
						Document document2 = new DocumentImpl(ymlEditor.getText());
						DocumentContentImpl content1 = new DocumentContentImpl(document1);
						DocumentContentImpl content2 = new DocumentContentImpl(document2);

						virtualFiles.add(content1);
						virtualFiles.add(content2);

						return virtualFiles;
					}

					@Override
					public @NotNull List<@Nls String> getContentTitles() {
						List<String> strings = new ArrayList<>();
						strings.add("before");
						strings.add("after");
						return  strings;
					}
				};
				diffManager.showDiff(project, diffRequest, new DiffDialogHints(WindowWrapper.Mode.MODAL, null, new Consumer<WindowWrapper>() {
					@Override
					public void consume(WindowWrapper windowWrapper) {
						Window window = windowWrapper.getWindow();

						JPanel buttonPanel = new JPanel();
						buttonPanel.setLayout(new FlowLayout());
						// 创建两个按钮并添加到buttonPanel中
						JButton commitBtn = new JButton("Commit");
						commitBtn.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								String content = ymlEditor.getText();
								DefaultMutableTreeNode note = (DefaultMutableTreeNode) groupTree.getLastSelectedPathComponent();
								if (null != note.getUserObject() && 0 == note.getChildCount()) {
									NacosTreeDto nacosTreeDto = (NacosTreeDto)note.getUserObject();
									boolean result = NacosUtil.commit(content, NacosUtil.EDIT_CONFIG, nacosTreeDto, nacosTokenDto);
									if (result) {
										NacosUtil.EDIT_CONFIG.setInitContent(content);
										window.dispose();
										Messages.showMessageDialog(project, "success", "NacosHelp", Messages.getInformationIcon());
									}
								}
							}
						});
						buttonPanel.add(commitBtn);


						JButton cancelBtn = new JButton("Cancel");
						buttonPanel.add(cancelBtn);
						cancelBtn.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								window.dispose();
							}
						});
						window.add(buttonPanel, BorderLayout.SOUTH);
					}
				}));
			}
		});
	}

	public JComponent getRootComponent() {
		return rootPanel;
	}

	public JComponent getPreferredFocusedComponent() {
		return rootPanel;
	}

	public void selectNotify() {
	}

	@Override
	public void dispose() {
		//myProjectService.unregister(myEventListener);
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}
}
