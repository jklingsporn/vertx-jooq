package io.github.jklingsporn.vertx.jooq.generate.converter;

import io.github.jklingsporn.vertx.jooq.shared.postgres.IdentityRowConverter;
import io.github.jklingsporn.vertx.jooq.shared.postgres.PgConverter;
import io.github.jklingsporn.vertx.jooq.shared.postgres.RowConverter;
import io.vertx.pgclient.data.Interval;
import org.jooq.Converter;
import org.jooq.impl.IdentityConverter;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;

import java.util.concurrent.TimeUnit;

public class YearToSecondIntervalConverter implements
        PgConverter<Interval, YearToSecond, Interval> {

    private static final IdentityConverter<Interval> intervalIdentityConverter = new IdentityConverter<>(Interval.class);
    private static final IdentityRowConverter<Interval> intervalIdentityRowConverter = new IdentityRowConverter<Interval>(Interval.class);

    @Override
    public Interval from(YearToSecond yearToSecond) {
        return Interval.of(
                yearToSecond.getYears(),
                yearToSecond.getMonths(),
                yearToSecond.getDays(),
                yearToSecond.getHours(),
                yearToSecond.getMinutes(),
                yearToSecond.getSeconds(),
                (int) TimeUnit.NANOSECONDS.toMicros(yearToSecond.getNano())
        );
    }

    @Override
    public YearToSecond to(Interval interval) {
        return new YearToSecond(
                new YearToMonth(interval.getYears(), interval.getMonths()),
                new DayToSecond(
                        interval.getDays(),
                        interval.getHours(),
                        interval.getMinutes(),
                        interval.getSeconds(),
                        (int) TimeUnit.MICROSECONDS.toNanos(interval.getMicroseconds())
                )
        );

    }

    @Override
    public Class<YearToSecond> fromType() {
        return YearToSecond.class;
    }

    @Override
    public Class<Interval> toType() {
        return Interval.class;
    }

    @Override
    public Converter<Interval, Interval> pgConverter() {
        return intervalIdentityConverter;
    }

    @Override
    public RowConverter<Interval, Interval> rowConverter() {
        return intervalIdentityRowConverter;
    }
}
