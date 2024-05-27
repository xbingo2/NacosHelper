package com.xbingo.jsonhelper.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.ui.TextTransferable;
import com.xbingo.jsonhelper.common.EditorHintsNotifier;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonLineMarker implements LineMarkerProvider {


    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiIdentifier && element.getParent() instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element.getParent();
            if (isModel(psiClass)) {
                return new LineMarkerInfo<>(
                        element,
                        element.getTextRange(),
                        getIcon(),
                        (Function<PsiElement, String>) psiElement -> "Get JsonString",
                        (MouseEvent e, PsiElement psiElement) -> {
                            String jsonStr = convertPsiClassToJson(PsiTreeUtil.getParentOfType(psiElement, PsiClass.class));
                            jsonStr = jsonStr.replaceAll("\\\\u003c", "<").replaceAll("\\\\u003e", ">");



                            Toolkit.getDefaultToolkit()
                                    .getSystemClipboard()
                                    .setContents(new TextTransferable(jsonStr), null);

                            EditorHintsNotifier.notificationAll("Get JsonString Success", NotificationType.INFORMATION);

                        },
                        GutterIconRenderer.Alignment.RIGHT,
                        ()->"Get JsonString"
                );
            }
        }
        return null;
    }

    private boolean isModel(PsiClass psiClass) {
        return psiClass.getQualifiedName().contains(".model") || psiClass.getQualifiedName().contains(".dto") || psiClass.getQualifiedName().contains(".vo");
    }

    private Icon getIcon() {
        // 返回你想在标记中显示的图标
        return IconLoader.getIcon("/META-INF/toolIcon.svg", JsonLineMarker.class);
    }

    public String convertPsiClassToJson(PsiClass psiClass) {
        Map<String, Object> classMap = new HashMap<>();
        for (PsiField field : psiClass.getAllFields()) {
            String fieldName = field.getName();
            String fieldType = field.getType().getPresentableText();
            classMap.put(fieldName, fieldType);

            // 如果字段类型是类，则递归处理
            /*if (field.getType() instanceof PsiClassType) {
                PsiClass fieldTypeClass = ((PsiClassType) field.getType()).resolve();
                if (fieldTypeClass != null  && !fieldTypeClass.getQualifiedName().startsWith("java")) {
                    classMap.put(fieldName, convertPsiClassToJson(fieldTypeClass));
                }
            }*/
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(classMap);
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        LineMarkerProvider.super.collectSlowLineMarkers(elements, result);
    }

}
