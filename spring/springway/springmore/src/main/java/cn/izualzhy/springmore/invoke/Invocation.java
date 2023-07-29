package cn.izualzhy.springmore.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invocation {
    private Object[] params;
    private Method method;
    private Object target;

    public Invocation(Object target, Method method, Object[] params) {
        this.target = target;
        this.method = method;
        this.params = params;
    }

    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, params);
    }
}
