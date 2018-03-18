package com.zchi.common.utils;
import org.apache.commons.lang3.StringUtils;

public class StringBuilderPlus {

	private StringBuilder builder;

	public StringBuilderPlus() {
		builder = new StringBuilder();
	}

	public StringBuilder getStringBuilder() {
		return builder;
	}

	public StringBuilderPlus append(String add) {
		if (StringUtils.isNotEmpty(add)) {
			builder.append(add);
		}
		return this;
	}

	@Override
	public String toString() {
		return builder.toString();
	}

}
