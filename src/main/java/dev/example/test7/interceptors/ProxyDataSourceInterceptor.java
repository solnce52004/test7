package dev.example.test7.interceptors;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;

public class ProxyDataSourceInterceptor implements MethodInterceptor {
    private final DataSource dataSource;

    public ProxyDataSourceInterceptor(final DataSource dataSource) {
        this.dataSource = net.ttddyy.dsproxy.support.ProxyDataSourceBuilder.create(dataSource)
                .name("Batch-Insert-Logger")
                .asJson()
                .countQuery()
                .logQueryToSysOut()
                .build();
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Method proxyMethod = ReflectionUtils.findMethod(dataSource.getClass(), invocation.getMethod().getName());
        if (proxyMethod != null) {
            return proxyMethod.invoke(dataSource, invocation.getArguments());
        }
        return invocation.proceed();
    }
}
