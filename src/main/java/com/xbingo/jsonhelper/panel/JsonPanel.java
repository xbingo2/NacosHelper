package com.xbingo.jsonhelper.panel;

import com.intellij.ide.highlighter.HtmlFileHighlighter;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileHighlighter;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.json.highlighting.JsonSyntaxHighlighterFactory;
import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.xbingo.jsonhelper.common.EditorHintsNotifier;
import com.xbingo.jsonhelper.common.JsonUtils;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

public class JsonPanel implements Disposable {
    private static final IElementType TextElementType = new IElementType("TEXT", Language.ANY);


    private JPanel rootPanel;
    private JButton parseBtn;
    private JButton copyBtn;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JButton minifyBtn;
    private JButton base64EnBtn;
    private JButton base64DeBtn;
    private JButton timestampBtn;

    private Editor leftEditor;

    private Editor rightEditor;

    private Project mProject;
    public JsonPanel(Project project) {
        mProject = project;
        leftEditor = createEditor();
        leftPanel.add(leftEditor.getComponent(), BorderLayout.CENTER);

        rightEditor = createEditor();
        rightEditor.getDocument().setReadOnly(true);
        rightPanel.add(rightEditor.getComponent(), BorderLayout.CENTER);

        parseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = leftEditor.getDocument().getText();
                    String prettyJsonString;
                    if (TextUtils.isEmpty(text)) {
                        prettyJsonString = "";
                    } else {
                        prettyJsonString = JsonUtils.formatJson(text);
                    }
                    writeToEditor(prettyJsonString);
                } catch (Exception ex) {
                    EditorHintsNotifier.notifyError(leftEditor, ex.getMessage(), 0);
                }
            }
        });

        minifyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = leftEditor.getDocument().getText();
                    String prettyJsonString;
                    if (TextUtils.isEmpty(text)) {
                        prettyJsonString = "";
                    } else {
                        prettyJsonString = JsonUtils.minifyJson(text);
                    }
                    writeToEditor(prettyJsonString);
                } catch (Exception ex) {
                    EditorHintsNotifier.notifyError(leftEditor, ex.getMessage(), 0);
                }
            }
        });

        base64EnBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = leftEditor.getDocument().getText();
                    String prettyJsonString;
                    if (TextUtils.isEmpty(text)) {
                        prettyJsonString = "";
                    } else {
                        prettyJsonString = Base64.getEncoder().encodeToString(text.getBytes());
                    }
                    writeToEditor(prettyJsonString);
                } catch (Exception ex) {
                    EditorHintsNotifier.notifyError(leftEditor, ex.getMessage(), 0);
                }
            }
        });

        base64DeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = leftEditor.getDocument().getText();
                    String prettyJsonString;
                    if (TextUtils.isEmpty(text)) {
                        prettyJsonString = "";
                    } else {
                        byte[] decodedBytes = Base64.getDecoder().decode(text);
                        prettyJsonString = new String(decodedBytes);
                    }
                    writeToEditor(prettyJsonString);
                } catch (Exception ex) {
                    EditorHintsNotifier.notifyError(leftEditor, ex.getMessage(), 0);
                }
            }
        });

        timestampBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = leftEditor.getDocument().getText();
                    String prettyJsonString;
                    if (TextUtils.isEmpty(text)) {
                        prettyJsonString = "";
                    } else {
                        if (isNumeric(text) && text.length() >= 10 && text.length() <= 13) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date(Long.valueOf(text));
                            prettyJsonString = dateFormat.format(date);
                        }
                        else {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = dateFormat.parse(text);
                            long timestamp = date.getTime();
                            prettyJsonString = String.valueOf(timestamp);
                        }
                    }
                    writeToEditor(prettyJsonString);
                } catch (Exception ex) {
                    EditorHintsNotifier.notifyError(leftEditor, ex.getMessage(), 0);
                }
            }
        });

        copyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectText = rightEditor.getDocument().getText();

                if (!TextUtils.isEmpty(selectText)) {
                    Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .setContents(new StringSelection(selectText), null);
                    EditorHintsNotifier.notifyInfo(rightEditor, "Text copied");
                }
            }
        });
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    private void writeToEditor(String prettyJsonString) {
        WriteCommandAction.runWriteCommandAction(mProject, () -> {
            Document document = rightEditor.getDocument();
            document.setReadOnly(false);
            document.setText(prettyJsonString);
            document.setReadOnly(true);
        });
        LanguageFileType fileType = getFileType();
        ((EditorEx) rightEditor).setHighlighter(createHighlighter(fileType));
    }

    private Editor createEditor() {
        PsiFile myFile = null;
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document doc = myFile == null
                ? editorFactory.createDocument("")
                : PsiDocumentManager.getInstance(mProject).getDocument(myFile);
        Editor editor = editorFactory.createEditor(doc, mProject);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(false);
        editorSettings.setFoldingOutlineShown(true);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setCaretRowShown(true);
        return editor;
    }

    private LanguageFileType getFileType() {
        return JsonFileType.INSTANCE;
    }

    private EditorHighlighter createHighlighter(LanguageFileType fileType) {

        SyntaxHighlighter originalHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, null, null);
        if (originalHighlighter == null) {
            originalHighlighter = new PlainSyntaxHighlighter();
        }

        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        LayeredLexerEditorHighlighter highlighter = new LayeredLexerEditorHighlighter(getFileHighlighter(fileType), scheme);
        highlighter.registerLayer(TextElementType, new LayerDescriptor(originalHighlighter, ""));
        return highlighter;
    }

    private SyntaxHighlighter getFileHighlighter(FileType fileType) {
        if (fileType == HtmlFileType.INSTANCE) {
            return new HtmlFileHighlighter();
        } else if (fileType == XmlFileType.INSTANCE) {
            return new XmlFileHighlighter();
        } else if (fileType == JsonFileType.INSTANCE) {
            return JsonSyntaxHighlighterFactory.getSyntaxHighlighter(fileType, mProject, null);
        }
        return new PlainSyntaxHighlighter();
    }


    @Override
    public void dispose() {

    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
