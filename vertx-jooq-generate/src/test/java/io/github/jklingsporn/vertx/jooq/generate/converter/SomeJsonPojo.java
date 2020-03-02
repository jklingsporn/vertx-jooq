package io.github.jklingsporn.vertx.jooq.generate.converter;

/**
 * @author jensklingsporn
 */
public class SomeJsonPojo {

    public String foo;
    public Integer bar;

    public String getFoo() {
        return foo;
    }

    public SomeJsonPojo setFoo(String foo) {
        this.foo = foo;
        return this;
    }

    public Integer getBar() {
        return bar;
    }

    public SomeJsonPojo setBar(Integer bar) {
        this.bar = bar;
        return this;
    }
}
