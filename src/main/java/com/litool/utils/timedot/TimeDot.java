package com.litool.utils.timedot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* 执行埋点注解  
* @author : KangNing Hu
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimeDot {

	/**
	 * 执行主题名称
	 * @return
	 */
	String value();

	/**
	 * 是否支持spring el表达式
	 * @return
	 */
	boolean supportSpEl() default false;
}
