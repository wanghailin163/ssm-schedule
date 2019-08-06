package com.demo.util;

import org.springframework.context.ApplicationContext;

public class SpringUtils {

	private static ApplicationContext context;

	public static void setContext(ApplicationContext context) {
		SpringUtils.context = context;
	}

	public static ApplicationContext getContext() {
		return SpringUtils.context;
	}

	public static <T> T getBean(Class<T> clazz) {

		if (context == null)
			return null;
		else
			return context.getBean(clazz);
	}

}
