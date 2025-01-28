package com.litool.utils.timedot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
* 执行时间记录工具类  
* @author : KangNing Hu
*/
public class TimeRecordUtils {


	private static final Logger logger = LoggerFactory.getLogger(TimeRecordUtils.class);


	private static ThreadLocal<StopWatch> stopWatch = new ThreadLocal<>();


	/**
	 * 释放当前上下资源
	 * @param name 记录主题名称
	 */
	public static void init(String name){
		StopWatch stopWatch = new StopWatch(name);
		TimeRecordUtils.stopWatch.set(stopWatch);
	}


	/**
	 * 方法执行打点
	 * @param call 业务执行的回调 有返回值
	 * @param runnable 业务执行的回调 无返回值
	 * @param taskName 任务名称
	 * @param <T> 返回类型
	 * @return 返回业务执行的结果
	 */
	private static <T>T dot(ThrowableSupplier<T> call , ThrowableRunnable runnable, String taskName){
		StopWatch stopWatch = TimeRecordUtils.stopWatch.get();
		if (stopWatch == null){
			if (logger.isDebugEnabled()) {
				logger.debug("执行时间记录主题未初始化");
			}
			//执行需要计算的业务
			return invoke(call , runnable);
		}

		String previousTaskName = null;
		//如果上一个任务还没完成
		if (stopWatch.isRunning()){
			//获取上一个任务
			previousTaskName = stopWatch.currentTaskName();
			//先结束上一个任务
			stopWatch.stop();
		}
		//开始计时
		stopWatch.start(taskName);
		try {
			//执行需要计算的业务
			return invoke(call , runnable);
		} finally {
			//结束计时
			stopWatch.stop();
			//如果上一个任务没有执行完成继续上一个任务
			if (previousTaskName != null){
				stopWatch.start(previousTaskName);
			}
		}
	}


	/**
	 * 执行业务方法
	 * @param call 业务执行的回调 有返回值
	 * @param runnable 业务执行的回调 无返回值
	 * @param <T>
	 * @return 返回执行结果
	 * @throws Throwable
	 */
	private static  <T>T invoke(ThrowableSupplier<T> call , ThrowableRunnable runnable){
		try {
			if (call != null){
				return call.get();
			}else {
				runnable.run();
				return null;
			}
		}catch (Throwable e ){
			if (e instanceof RuntimeException){
				throw (RuntimeException)e;
			}
			throw new RuntimeException(e.getMessage()  ,e);
		}

	}


	/**
	 * 释放当前上下资源
	 */
	public static void remove(){
		stopWatch.remove();
	}


	/**
	 * 方法执行打点
	 * @param runnable 业务执行的回调 无返回值
	 * @param taskName 任务名称
	 * @return 返回业务执行的结果
	 */
	public static void dot(ThrowableRunnable runnable, String taskName){
		dot(null , runnable , taskName);
	}



	/**
	 * 方法执行打点
	 * @param call 业务执行的回调 有返回值
	 * @param taskName 任务名称
	 * @param <T> 返回类型
	 * @return 返回业务执行的结果
	 */
	public static <T>T dot(ThrowableSupplier<T> call , String taskName){
		return dot(call , null , taskName);
	}




	/**
	 * 打印计算结果
	 */
	public static void print(){
		StopWatch stopWatch = TimeRecordUtils.stopWatch.get();
		if (Objects.isNull(stopWatch)){
			System.out.println("执行时间记录主题未初始化");
			throw new RuntimeException("执行时间记录主题未初始化");
		}
		logger.info(stopWatch.prettyPrint());
	}


	public static void main(String[] args) {
		init("测试任务");
		dot(() -> TimeUnit.MILLISECONDS.sleep(100) , "任务1");
		System.out.println(dot(() -> {TimeUnit.MILLISECONDS.sleep(500); return "我要睡觉";} , "任务2"));
		dot(() -> TimeUnit.MILLISECONDS.sleep(200) , "任务3");
		print();
	}


}
