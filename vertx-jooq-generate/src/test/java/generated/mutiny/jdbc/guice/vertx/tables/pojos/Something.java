/*
 * This file is generated by jOOQ.
 */
package generated.mutiny.jdbc.guice.vertx.tables.pojos;


import generated.mutiny.jdbc.guice.vertx.tables.interfaces.ISomething;

import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import static io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo.*;
/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Something implements VertxPojo, ISomething {

    private static final long serialVersionUID = 1L;

    private Integer       someid;
    private String        somestring;
    private Long          somehugenumber;
    private Short         somesmallnumber;
    private Integer       someregularnumber;
    private BigDecimal    somedecimal;
    private Boolean       someboolean;
    private Double        somedouble;
    private JsonObject    somejsonobject;
    private JsonArray     somejsonarray;
    private LocalDateTime sometimestamp;

    public Something() {}

    public Something(ISomething value) {
        this.someid = value.getSomeid();
        this.somestring = value.getSomestring();
        this.somehugenumber = value.getSomehugenumber();
        this.somesmallnumber = value.getSomesmallnumber();
        this.someregularnumber = value.getSomeregularnumber();
        this.somedecimal = value.getSomedecimal();
        this.someboolean = value.getSomeboolean();
        this.somedouble = value.getSomedouble();
        this.somejsonobject = value.getSomejsonobject();
        this.somejsonarray = value.getSomejsonarray();
        this.sometimestamp = value.getSometimestamp();
    }

    public Something(
        Integer       someid,
        String        somestring,
        Long          somehugenumber,
        Short         somesmallnumber,
        Integer       someregularnumber,
        BigDecimal    somedecimal,
        Boolean       someboolean,
        Double        somedouble,
        JsonObject    somejsonobject,
        JsonArray     somejsonarray,
        LocalDateTime sometimestamp
    ) {
        this.someid = someid;
        this.somestring = somestring;
        this.somehugenumber = somehugenumber;
        this.somesmallnumber = somesmallnumber;
        this.someregularnumber = someregularnumber;
        this.somedecimal = somedecimal;
        this.someboolean = someboolean;
        this.somedouble = somedouble;
        this.somejsonobject = somejsonobject;
        this.somejsonarray = somejsonarray;
        this.sometimestamp = sometimestamp;
    }

        public Something(io.vertx.core.json.JsonObject json) {
                this();
                fromJson(json);
        }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMEID</code>.
     */
    @Override
    public Integer getSomeid() {
        return this.someid;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMEID</code>.
     */
    @Override
    public Something setSomeid(Integer someid) {
        this.someid = someid;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMESTRING</code>.
     */
    @Override
    public String getSomestring() {
        return this.somestring;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMESTRING</code>.
     */
    @Override
    public Something setSomestring(String somestring) {
        this.somestring = somestring;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMEHUGENUMBER</code>.
     */
    @Override
    public Long getSomehugenumber() {
        return this.somehugenumber;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMEHUGENUMBER</code>.
     */
    @Override
    public Something setSomehugenumber(Long somehugenumber) {
        this.somehugenumber = somehugenumber;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMESMALLNUMBER</code>.
     */
    @Override
    public Short getSomesmallnumber() {
        return this.somesmallnumber;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMESMALLNUMBER</code>.
     */
    @Override
    public Something setSomesmallnumber(Short somesmallnumber) {
        this.somesmallnumber = somesmallnumber;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMEREGULARNUMBER</code>.
     */
    @Override
    public Integer getSomeregularnumber() {
        return this.someregularnumber;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMEREGULARNUMBER</code>.
     */
    @Override
    public Something setSomeregularnumber(Integer someregularnumber) {
        this.someregularnumber = someregularnumber;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMEDECIMAL</code>.
     */
    @Override
    public BigDecimal getSomedecimal() {
        return this.somedecimal;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMEDECIMAL</code>.
     */
    @Override
    public Something setSomedecimal(BigDecimal somedecimal) {
        this.somedecimal = somedecimal;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMEBOOLEAN</code>.
     */
    @Override
    public Boolean getSomeboolean() {
        return this.someboolean;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMEBOOLEAN</code>.
     */
    @Override
    public Something setSomeboolean(Boolean someboolean) {
        this.someboolean = someboolean;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMEDOUBLE</code>.
     */
    @Override
    public Double getSomedouble() {
        return this.somedouble;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMEDOUBLE</code>.
     */
    @Override
    public Something setSomedouble(Double somedouble) {
        this.somedouble = somedouble;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMEJSONOBJECT</code>.
     */
    @Override
    public JsonObject getSomejsonobject() {
        return this.somejsonobject;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMEJSONOBJECT</code>.
     */
    @Override
    public Something setSomejsonobject(JsonObject somejsonobject) {
        this.somejsonobject = somejsonobject;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMEJSONARRAY</code>.
     */
    @Override
    public JsonArray getSomejsonarray() {
        return this.somejsonarray;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMEJSONARRAY</code>.
     */
    @Override
    public Something setSomejsonarray(JsonArray somejsonarray) {
        this.somejsonarray = somejsonarray;
        return this;
    }

    /**
     * Getter for <code>VERTX.SOMETHING.SOMETIMESTAMP</code>.
     */
    @Override
    public LocalDateTime getSometimestamp() {
        return this.sometimestamp;
    }

    /**
     * Setter for <code>VERTX.SOMETHING.SOMETIMESTAMP</code>.
     */
    @Override
    public Something setSometimestamp(LocalDateTime sometimestamp) {
        this.sometimestamp = sometimestamp;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Something other = (Something) obj;
        if (this.someid == null) {
            if (other.someid != null)
                return false;
        }
        else if (!this.someid.equals(other.someid))
            return false;
        if (this.somestring == null) {
            if (other.somestring != null)
                return false;
        }
        else if (!this.somestring.equals(other.somestring))
            return false;
        if (this.somehugenumber == null) {
            if (other.somehugenumber != null)
                return false;
        }
        else if (!this.somehugenumber.equals(other.somehugenumber))
            return false;
        if (this.somesmallnumber == null) {
            if (other.somesmallnumber != null)
                return false;
        }
        else if (!this.somesmallnumber.equals(other.somesmallnumber))
            return false;
        if (this.someregularnumber == null) {
            if (other.someregularnumber != null)
                return false;
        }
        else if (!this.someregularnumber.equals(other.someregularnumber))
            return false;
        if (this.somedecimal == null) {
            if (other.somedecimal != null)
                return false;
        }
        else if (!this.somedecimal.equals(other.somedecimal))
            return false;
        if (this.someboolean == null) {
            if (other.someboolean != null)
                return false;
        }
        else if (!this.someboolean.equals(other.someboolean))
            return false;
        if (this.somedouble == null) {
            if (other.somedouble != null)
                return false;
        }
        else if (!this.somedouble.equals(other.somedouble))
            return false;
        if (this.somejsonobject == null) {
            if (other.somejsonobject != null)
                return false;
        }
        else if (!this.somejsonobject.equals(other.somejsonobject))
            return false;
        if (this.somejsonarray == null) {
            if (other.somejsonarray != null)
                return false;
        }
        else if (!this.somejsonarray.equals(other.somejsonarray))
            return false;
        if (this.sometimestamp == null) {
            if (other.sometimestamp != null)
                return false;
        }
        else if (!this.sometimestamp.equals(other.sometimestamp))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.someid == null) ? 0 : this.someid.hashCode());
        result = prime * result + ((this.somestring == null) ? 0 : this.somestring.hashCode());
        result = prime * result + ((this.somehugenumber == null) ? 0 : this.somehugenumber.hashCode());
        result = prime * result + ((this.somesmallnumber == null) ? 0 : this.somesmallnumber.hashCode());
        result = prime * result + ((this.someregularnumber == null) ? 0 : this.someregularnumber.hashCode());
        result = prime * result + ((this.somedecimal == null) ? 0 : this.somedecimal.hashCode());
        result = prime * result + ((this.someboolean == null) ? 0 : this.someboolean.hashCode());
        result = prime * result + ((this.somedouble == null) ? 0 : this.somedouble.hashCode());
        result = prime * result + ((this.somejsonobject == null) ? 0 : this.somejsonobject.hashCode());
        result = prime * result + ((this.somejsonarray == null) ? 0 : this.somejsonarray.hashCode());
        result = prime * result + ((this.sometimestamp == null) ? 0 : this.sometimestamp.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Something (");

        sb.append(someid);
        sb.append(", ").append(somestring);
        sb.append(", ").append(somehugenumber);
        sb.append(", ").append(somesmallnumber);
        sb.append(", ").append(someregularnumber);
        sb.append(", ").append(somedecimal);
        sb.append(", ").append(someboolean);
        sb.append(", ").append(somedouble);
        sb.append(", ").append(somejsonobject);
        sb.append(", ").append(somejsonarray);
        sb.append(", ").append(sometimestamp);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ISomething from) {
        setSomeid(from.getSomeid());
        setSomestring(from.getSomestring());
        setSomehugenumber(from.getSomehugenumber());
        setSomesmallnumber(from.getSomesmallnumber());
        setSomeregularnumber(from.getSomeregularnumber());
        setSomedecimal(from.getSomedecimal());
        setSomeboolean(from.getSomeboolean());
        setSomedouble(from.getSomedouble());
        setSomejsonobject(from.getSomejsonobject());
        setSomejsonarray(from.getSomejsonarray());
        setSometimestamp(from.getSometimestamp());
    }

    @Override
    public <E extends ISomething> E into(E into) {
        into.from(this);
        return into;
    }
}