package io.github.jklingsporn.vertx.jooq.generate.builder;

import java.util.function.UnaryOperator;

public enum ConverterInstantiationMethod implements UnaryOperator<String> {

    NEW(converter -> String.format("new %s()",converter)),
    SINGLETON(converter -> converter.replaceAll("\\.", "_").toUpperCase()+"_INSTANCE");

    private final UnaryOperator<String> instanceResolver;

    ConverterInstantiationMethod(UnaryOperator<String> instanceResolver) {
        this.instanceResolver = instanceResolver;
    }


    @Override
    public String apply(String s) {
        return instanceResolver.apply(s);
    }
}
