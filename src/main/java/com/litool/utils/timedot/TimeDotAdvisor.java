package com.litool.utils.timedot;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
* 执行时间埋点  
* @author : KangNing Hu
*/
@Component
public class TimeDotAdvisor  extends StaticMethodMatcherPointcutAdvisor {


	private Advice advice = new TimeDotAdvice();


	@Override
	public boolean matches(Method method, Class<?> aClass) {
		return method.isAnnotationPresent(TimeDot.class);
	}


	@Override
	public Advice getAdvice() {
		return advice;
	}


	/**
	 * 通知执行器
	 */
	private class TimeDotAdvice implements MethodInterceptor{

		@Override
		public Object invoke(MethodInvocation methodInvocation) throws Throwable {
			Method method = methodInvocation.getMethod();
			//获取埋点标识
			TimeDot timeDot = method.getAnnotation(TimeDot.class);

			//解析主题名称
			String subjectName;
			//支持el
			if (timeDot.supportSpEl()){
				subjectName = "default";//SpringELUtils.parse(timeDot.value() , method , methodInvocation.getArguments());
			}
			//不支持el
			else {
				subjectName = timeDot.value();
			}


			//初始化时间计算器
			TimeRecordUtils.init(subjectName);
			try {
				return methodInvocation.proceed();
			}finally {
				//打印时间
				TimeRecordUtils.print();
				TimeRecordUtils.remove();
			}
		}
	}
}
