package io.github.jklingsporn.vertx.jooq.generate.classic;

import generated.classic.guice.vertx.tables.daos.SomethingDao;
import generated.classic.guice.vertx.tables.daos.SomethingcompositeDao;
import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import java.lang.reflect.Method;

/**
 * Created by jensklingsporn on 15.05.17.
 */
public class VertxGuiceDaoTest {


    @Test
    public void injectAnnotationsShouldBeGenerated() throws NoSuchMethodException {
        assertAnnotationPresent(SomethingDao.class,"setConfiguration", Configuration.class);
        assertAnnotationPresent(SomethingDao.class,"setVertx",Vertx.class);
        assertAnnotationPresent(SomethingcompositeDao.class,"setConfiguration",Configuration.class);
        assertAnnotationPresent(SomethingcompositeDao.class,"setVertx", Vertx.class);
    }

    private void assertAnnotationPresent(Class<?> clazz, String methodName, Class<?> arg) throws NoSuchMethodException {
        Method method = clazz.getMethod(methodName, arg);
        Inject annotation = method.getDeclaredAnnotation(Inject.class);
        Assert.assertNotNull(annotation);
    }

}
