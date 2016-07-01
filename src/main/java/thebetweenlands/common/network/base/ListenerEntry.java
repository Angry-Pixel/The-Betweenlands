package thebetweenlands.common.network.base;

import java.lang.reflect.Method;

public class ListenerEntry {
    private Method method;
    private Class clazz;
    private Class paramType;

    public ListenerEntry(Method method, Class clazz, Class paramType) {
        this.method = method;
        this.clazz = clazz;
        this.paramType = paramType;
    }

    public Method getListenerMethod() {
        return this.method;
    }

    public Class getListenerClass() {
        return this.clazz;
    }

    public Class getParamType() {
        return this.paramType;
    }
}
