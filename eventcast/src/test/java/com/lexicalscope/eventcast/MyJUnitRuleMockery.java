package com.lexicalscope.eventcast;

import org.jmock.integration.junit4.JUnitRuleMockery;

import com.google.inject.TypeLiteral;

public class MyJUnitRuleMockery extends JUnitRuleMockery {

	@SuppressWarnings("unchecked")
	public <T> T mock(final TypeLiteral<T> typeToMock)
	{
		return (T) super.mock(typeToMock.getRawType(), typeToMock.toString());
	}

}
