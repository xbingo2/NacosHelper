package com.xbingo.cronhelper.annotator;

import com.intellij.lang.properties.psi.impl.PropertyValueImpl;

public class PropertiesCronExpressionAnnotator extends BaseCronExpressionAnnotator<PropertyValueImpl> {

	public PropertiesCronExpressionAnnotator() {
		super(PropertyValueImpl.class);
	}

	@Override
	protected String extractText(PropertyValueImpl element) {
		return element.getText();
	}
}
