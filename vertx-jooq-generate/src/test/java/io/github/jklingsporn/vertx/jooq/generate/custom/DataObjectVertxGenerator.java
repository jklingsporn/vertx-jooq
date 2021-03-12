package io.github.jklingsporn.vertx.jooq.generate.custom;

import io.github.jklingsporn.vertx.jooq.generate.builder.BuildOptions;
import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

public class DataObjectVertxGenerator extends DelegatingVertxGenerator {

    public DataObjectVertxGenerator() {
        super(VertxGeneratorBuilder.init().withClassicAPI().withPostgresReactiveDriver().build(new BuildOptions().addBuildFlags(BuildOptions.BuildFlag.GENERATE_DATA_OBJECT_ANNOTATION)));
    }

}
