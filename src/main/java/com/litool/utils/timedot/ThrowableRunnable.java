package com.litool.utils.timedot;


/**
 * 处理异常的函数接口   功能与{@link Runnable} 类似
 *
 * @see java.util.function.Supplier
 * @author : KangNing Hu
 */
@FunctionalInterface
public interface ThrowableRunnable {
	
	void run() throws Throwable;
}
