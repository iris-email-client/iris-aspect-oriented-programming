package br.unb.cic.iris.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggingAspect {
	
	@After("execution(public * br.unb.cic.iris..*(..))")
	public void logAfter(JoinPoint joinPoint) {
		System.out.println("CoreLoggingAspect: logAfter() is running!");
		System.out.println("CoreLoggingAspect : " + joinPoint.getSignature().getName());
		System.out.println("******\n");
	}
	
}