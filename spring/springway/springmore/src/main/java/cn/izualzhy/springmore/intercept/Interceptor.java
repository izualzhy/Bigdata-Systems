package cn.izualzhy.springmore.intercept;

import cn.izualzhy.springmore.invoke.Invocation;

import java.lang.reflect.InvocationTargetException;

/**
* @Description:
 * before: 事前
 * after: 事后
 * afterReturning: 事件没有发生异常
 * afterThrowing: 事件发生异常
 * useAround: 是否使用 around 方法取代原有方法
*/
public interface Interceptor {
    public boolean before();

    public void after();

    public Object around(Invocation invocation) throws InvocationTargetException, IllegalAccessException;

    public void afterReturning();
    public void afterThrowing();

    boolean useAround();
}
