package io.github.jklingsporn.vertx.jooq.generate.builder;

public class BuildOptions {

    private ConverterInstantiationMethod converterInstantiationMethod;

    public BuildOptions() {
        this(ConverterInstantiationMethod.SINGLETON);
    }

    public BuildOptions(ConverterInstantiationMethod converterInstantiationMethod) {
        this.converterInstantiationMethod = converterInstantiationMethod;
    }

    public ConverterInstantiationMethod getConverterInstantiationMethod() {
        return converterInstantiationMethod;
    }

    public BuildOptions withConverterInstantiationMethod(ConverterInstantiationMethod converterInstantiationMethod) {
        this.converterInstantiationMethod = converterInstantiationMethod;
        return this;
    }
}
