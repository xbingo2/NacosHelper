package com.xbingo.cronhelper.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.xbingo.jsonhelper.common.CronUtil;
import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.intellij.lang.annotation.HighlightSeverity.INFORMATION;

public abstract class BaseCronExpressionAnnotator<T> implements Annotator {

	protected static final String DOUBLE_QUOTES = "\"";
	protected static final String QUOTES = "'";

	private final Class<T> valueClass;

	private final Options parserOptions;

	public BaseCronExpressionAnnotator(Class<T> valueClass) {
		this.valueClass = valueClass;
		this.parserOptions = Options.twentyFourHour();
		this.parserOptions.setZeroBasedDayOfWeek(true);
		this.parserOptions.setThrowExceptionOnParseError(false);
	}

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {

		if (!canHandleType(element)) {
			return;
		}

		String cronExpression = extractText(valueClass.cast(element));

		if (!CronUtil.isCronExpression(cronExpression)) {
			return;
		}

		TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());

		try {
			String description = CronExpressionDescriptor.getDescription(cronExpression, parserOptions, Locale.getDefault());
			holder.newAnnotation(INFORMATION, description)
					.range(range)
					.textAttributes(DefaultLanguageHighlighterColors.HIGHLIGHTED_REFERENCE)
					.create();
		} catch (Throwable e) {
//			throw new RuntimeException(e);
		}
	}



	protected boolean canHandleType(@NotNull PsiElement element) {
		return valueClass.isAssignableFrom(element.getClass());
	}

	protected abstract String extractText(T element);
}
