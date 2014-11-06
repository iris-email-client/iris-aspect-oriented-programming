package br.unb.cic.iris.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class CoreLoggingAspect {
	
/*	@Pointcut("call(* br.unb.cic.iris.core.aspect.*.teste())")
	public void saudacao() {
	}
	@After("saudacao()")
	public void finalizaSaudacao(){
		System.out.println(" >>>>>>>   <<<<<<< World !!!");
	}
	*/
	
	@Before("execution(public * br.unb.cic.iris..*(..))")
	public void logBefore(JoinPoint joinPoint) {
		System.out.println("CoreLoggingAspect: logBefore() is running!");
		System.out.println("CoreLoggingAspect : " + joinPoint.getSignature().getName());
		System.out.println("******\n");
	}
	
}