package com.h2v.java.comparator.reflect;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class ComparisonResult {

	private String elementName;
	private TargetType elementType;
	private String targetName;
	private TargetType targetType;
	private String leftVal;
	private String rightVal;
	private ComparisonStatus status;

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public TargetType getElementType() {
		return elementType;
	}

	public void setElementType(TargetType elementType) {
		this.elementType = elementType;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public String getLeftVal() {
		return leftVal;
	}

	public void setLeftVal(String leftVal) {
		this.leftVal = leftVal;
	}

	public String getRightVal() {
		return rightVal;
	}

	public void setRightVal(String rightVal) {
		this.rightVal = rightVal;
	}

	public ComparisonStatus getStatus() {
		return status;
	}

	public void setStatus(ComparisonStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (elementName != null)
			append("Element", elementName, builder);
		if (elementType != null)
			append("Element Type", elementType.toString(), builder);
		if (targetName != null)
			append("Target", targetName, builder);
		if (targetType != null)
			append("Target Type", targetType.toString(), builder);
		if (leftVal != null)
			append("Left Value", leftVal, builder);
		if (rightVal != null)
			append("Right Value", rightVal, builder);
		if (status != null)
			append("Status", status.toString(), builder);
		return builder.toString();
	}

	private void append(String field, String val, StringBuilder builder) {
		final char SEP = ':';
		final char LIMIT = ';';
		builder.append(field).append(SEP).append(val).append(LIMIT);
	}

	public static enum ComparisonStatus {
		ADDED, DELETED, MODIFIED;
	}

	public static enum TargetType {
		CLASS, METHOD, ANNOTATION;
	}

}
