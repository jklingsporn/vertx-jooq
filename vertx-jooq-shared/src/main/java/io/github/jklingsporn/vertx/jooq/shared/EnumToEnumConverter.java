package io.github.jklingsporn.vertx.jooq.shared;

import org.jooq.EnumType;
import org.jooq.impl.AbstractConverter;
import org.jooq.tools.Convert;
import org.jooq.tools.JooqLogger;

import java.util.EnumMap;
import java.util.function.Function;

/**
 * In case a user decides to use a converter for enums, he cannot use the ordinary {@code EnumConverter} because this
 * only converts from a enum's name or it's ordinal to the user type. Postgres for example requires the database type to be
 * handled as enum too. In cases like this, you should use this converter.
 * @author jensklingsporn
 */
public abstract class EnumToEnumConverter<T extends Enum<T> & EnumType,U extends Enum<U>> extends AbstractConverter<T,U> {

    private static final JooqLogger logger = JooqLogger.getLogger(EnumToEnumConverter.class);

    private final Function<U,String> nameRepresentationLookup;
    private final EnumMap<T, U> lookup;

    /**
     * This is the same as calling {@code new EnumToEnumConverter(fromType,toType,Enum::name)}
     * @param fromType the database type
     * @param toType your enum type
     */
    public EnumToEnumConverter(Class<T> fromType, Class<U> toType) {
        this(fromType,toType,Enum::name);
    }

    /**
     * @param fromType the database enum type
     * @param toType your enum type
     * @param nameRepresentationLookup the function to lookup the enum by name
     */
    public EnumToEnumConverter(Class<T> fromType, Class<U> toType, Function<U, String> nameRepresentationLookup) {
        super(fromType,toType);
        this.nameRepresentationLookup = nameRepresentationLookup;
        this.lookup = new EnumMap<T, U>(fromType);
        for (U u : toType.getEnumConstants()) {
            T to = to(u);
            if(to == null){
                logger.warn(String.format("No mapping found for type %s and name %s", toType.getName(), u.name()));
            }else{
                this.lookup.put(to, u);
            }
        }
    }

    @Override
    public U from(T databaseObject) {
        return lookup.get(databaseObject);
    }

    @Override
    public T to(U userObject) {
        if(userObject == null){
            return null;
        }
        return Convert.convert(nameRepresentationLookup.apply(userObject), fromType());
    }
}
