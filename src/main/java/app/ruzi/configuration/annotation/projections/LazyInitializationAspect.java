package app.ruzi.configuration.annotation.projections;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.LazyInitializationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;

@Aspect
@Component
public class LazyInitializationAspect {

    @Pointcut("@annotation(lazyCorrector)")
    public void methodAnnotatedWithLazyCorrector(LazyCorrector lazyCorrector) {
    }

    @AfterThrowing(value = "methodAnnotatedWithLazyCorrector(lazyCorrector)", throwing = "ex")
    public void handleLazyInitializationException(JoinPoint joinPoint, LazyCorrector lazyCorrector, LazyInitializationException ex) {
        Class<?> targetClass = lazyCorrector.className();
        if (targetClass != null) {
            Object[] methodArgs = joinPoint.getArgs();
            if (methodArgs.length > 0 && targetClass.isAssignableFrom(methodArgs[0].getClass())) {
                Object targetObject = methodArgs[0];
                for (Field field : targetClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        if (Arrays.stream(field.getAnnotations()).anyMatch(annotation -> annotation instanceof OneToMany || annotation instanceof ManyToOne)) {
                            field.set(targetObject, null);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}


