/*
 * This file is generated by jOOQ.
 */
package generated.rx.reactive.regular.tables.pojos;


import generated.rx.reactive.regular.enums.Someenum;
import generated.rx.reactive.regular.tables.interfaces.ISomething;

import io.github.jklingsporn.vertx.jooq.generate.converter.SomeJsonPojo;
import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.data.Interval;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;


import static io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo.*;
/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Something implements VertxPojo, ISomething {

    private static final long serialVersionUID = 1L;

    private Integer someid;
    private String somestring;
    private Long somehugenumber;
    private Short somesmallnumber;
    private Integer someregularnumber;
    private BigDecimal somedecimal;
    private Double somedouble;
    private Someenum someenum;
    private JsonObject somejsonobject;
    private SomeJsonPojo somecustomjsonobject;
    private JsonArray somejsonarray;
    private JsonObject somevertxjsonobject;
    private LocalTime sometime;
    private LocalDate somedate;
    private LocalDateTime sometimestamp;
    private OffsetDateTime sometimestampwithtz;
    private Interval someinterval;
    private byte[] somebytea;
    private List<String> somestringaslist;

    public Something() {}

    public Something(ISomething value) {
        this.someid = value.getSomeid();
        this.somestring = value.getSomestring();
        this.somehugenumber = value.getSomehugenumber();
        this.somesmallnumber = value.getSomesmallnumber();
        this.someregularnumber = value.getSomeregularnumber();
        this.somedecimal = value.getSomedecimal();
        this.somedouble = value.getSomedouble();
        this.someenum = value.getSomeenum();
        this.somejsonobject = value.getSomejsonobject();
        this.somecustomjsonobject = value.getSomecustomjsonobject();
        this.somejsonarray = value.getSomejsonarray();
        this.somevertxjsonobject = value.getSomevertxjsonobject();
        this.sometime = value.getSometime();
        this.somedate = value.getSomedate();
        this.sometimestamp = value.getSometimestamp();
        this.sometimestampwithtz = value.getSometimestampwithtz();
        this.someinterval = value.getSomeinterval();
        this.somebytea = value.getSomebytea();
        this.somestringaslist = value.getSomestringaslist();
    }

    public Something(
        Integer someid,
        String somestring,
        Long somehugenumber,
        Short somesmallnumber,
        Integer someregularnumber,
        BigDecimal somedecimal,
        Double somedouble,
        Someenum someenum,
        JsonObject somejsonobject,
        SomeJsonPojo somecustomjsonobject,
        JsonArray somejsonarray,
        JsonObject somevertxjsonobject,
        LocalTime sometime,
        LocalDate somedate,
        LocalDateTime sometimestamp,
        OffsetDateTime sometimestampwithtz,
        Interval someinterval,
        byte[] somebytea,
        List<String> somestringaslist
    ) {
        this.someid = someid;
        this.somestring = somestring;
        this.somehugenumber = somehugenumber;
        this.somesmallnumber = somesmallnumber;
        this.someregularnumber = someregularnumber;
        this.somedecimal = somedecimal;
        this.somedouble = somedouble;
        this.someenum = someenum;
        this.somejsonobject = somejsonobject;
        this.somecustomjsonobject = somecustomjsonobject;
        this.somejsonarray = somejsonarray;
        this.somevertxjsonobject = somevertxjsonobject;
        this.sometime = sometime;
        this.somedate = somedate;
        this.sometimestamp = sometimestamp;
        this.sometimestampwithtz = sometimestampwithtz;
        this.someinterval = someinterval;
        this.somebytea = somebytea;
        this.somestringaslist = somestringaslist;
    }

        public Something(io.vertx.core.json.JsonObject json) {
                this();
                fromJson(json);
        }

    /**
     * Getter for <code>vertx.something.someId</code>.
     */
    @Override
    public Integer getSomeid() {
        return this.someid;
    }

    /**
     * Setter for <code>vertx.something.someId</code>.
     */
    @Override
    public Something setSomeid(Integer someid) {
        this.someid = someid;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someString</code>.
     */
    @Override
    public String getSomestring() {
        return this.somestring;
    }

    /**
     * Setter for <code>vertx.something.someString</code>.
     */
    @Override
    public Something setSomestring(String somestring) {
        this.somestring = somestring;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someHugeNumber</code>.
     */
    @Override
    public Long getSomehugenumber() {
        return this.somehugenumber;
    }

    /**
     * Setter for <code>vertx.something.someHugeNumber</code>.
     */
    @Override
    public Something setSomehugenumber(Long somehugenumber) {
        this.somehugenumber = somehugenumber;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someSmallNumber</code>.
     */
    @Override
    public Short getSomesmallnumber() {
        return this.somesmallnumber;
    }

    /**
     * Setter for <code>vertx.something.someSmallNumber</code>.
     */
    @Override
    public Something setSomesmallnumber(Short somesmallnumber) {
        this.somesmallnumber = somesmallnumber;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someRegularNumber</code>.
     */
    @Override
    public Integer getSomeregularnumber() {
        return this.someregularnumber;
    }

    /**
     * Setter for <code>vertx.something.someRegularNumber</code>.
     */
    @Override
    public Something setSomeregularnumber(Integer someregularnumber) {
        this.someregularnumber = someregularnumber;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someDecimal</code>.
     */
    @Override
    public BigDecimal getSomedecimal() {
        return this.somedecimal;
    }

    /**
     * Setter for <code>vertx.something.someDecimal</code>.
     */
    @Override
    public Something setSomedecimal(BigDecimal somedecimal) {
        this.somedecimal = somedecimal;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someDouble</code>.
     */
    @Override
    public Double getSomedouble() {
        return this.somedouble;
    }

    /**
     * Setter for <code>vertx.something.someDouble</code>.
     */
    @Override
    public Something setSomedouble(Double somedouble) {
        this.somedouble = somedouble;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someEnum</code>.
     */
    @Override
    public Someenum getSomeenum() {
        return this.someenum;
    }

    /**
     * Setter for <code>vertx.something.someEnum</code>.
     */
    @Override
    public Something setSomeenum(Someenum someenum) {
        this.someenum = someenum;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someJsonObject</code>.
     */
    @Override
    public JsonObject getSomejsonobject() {
        return this.somejsonobject;
    }

    /**
     * Setter for <code>vertx.something.someJsonObject</code>.
     */
    @Override
    public Something setSomejsonobject(JsonObject somejsonobject) {
        this.somejsonobject = somejsonobject;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someCustomJsonObject</code>.
     */
    @Override
    public SomeJsonPojo getSomecustomjsonobject() {
        return this.somecustomjsonobject;
    }

    /**
     * Setter for <code>vertx.something.someCustomJsonObject</code>.
     */
    @Override
    public Something setSomecustomjsonobject(SomeJsonPojo somecustomjsonobject) {
        this.somecustomjsonobject = somecustomjsonobject;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someJsonArray</code>.
     */
    @Override
    public JsonArray getSomejsonarray() {
        return this.somejsonarray;
    }

    /**
     * Setter for <code>vertx.something.someJsonArray</code>.
     */
    @Override
    public Something setSomejsonarray(JsonArray somejsonarray) {
        this.somejsonarray = somejsonarray;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someVertxJsonObject</code>.
     */
    @Override
    public JsonObject getSomevertxjsonobject() {
        return this.somevertxjsonobject;
    }

    /**
     * Setter for <code>vertx.something.someVertxJsonObject</code>.
     */
    @Override
    public Something setSomevertxjsonobject(JsonObject somevertxjsonobject) {
        this.somevertxjsonobject = somevertxjsonobject;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someTime</code>.
     */
    @Override
    public LocalTime getSometime() {
        return this.sometime;
    }

    /**
     * Setter for <code>vertx.something.someTime</code>.
     */
    @Override
    public Something setSometime(LocalTime sometime) {
        this.sometime = sometime;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someDate</code>.
     */
    @Override
    public LocalDate getSomedate() {
        return this.somedate;
    }

    /**
     * Setter for <code>vertx.something.someDate</code>.
     */
    @Override
    public Something setSomedate(LocalDate somedate) {
        this.somedate = somedate;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someTimestamp</code>.
     */
    @Override
    public LocalDateTime getSometimestamp() {
        return this.sometimestamp;
    }

    /**
     * Setter for <code>vertx.something.someTimestamp</code>.
     */
    @Override
    public Something setSometimestamp(LocalDateTime sometimestamp) {
        this.sometimestamp = sometimestamp;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someTimestampWithTZ</code>.
     */
    @Override
    public OffsetDateTime getSometimestampwithtz() {
        return this.sometimestampwithtz;
    }

    /**
     * Setter for <code>vertx.something.someTimestampWithTZ</code>.
     */
    @Override
    public Something setSometimestampwithtz(OffsetDateTime sometimestampwithtz) {
        this.sometimestampwithtz = sometimestampwithtz;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someInterval</code>.
     */
    @Override
    public Interval getSomeinterval() {
        return this.someinterval;
    }

    /**
     * Setter for <code>vertx.something.someInterval</code>.
     */
    @Override
    public Something setSomeinterval(Interval someinterval) {
        this.someinterval = someinterval;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someByteA</code>.
     */
    @Override
    public byte[] getSomebytea() {
        return this.somebytea;
    }

    /**
     * Setter for <code>vertx.something.someByteA</code>.
     */
    @Override
    public Something setSomebytea(byte[] somebytea) {
        this.somebytea = somebytea;
        return this;
    }

    /**
     * Getter for <code>vertx.something.someStringAsList</code>.
     */
    @Override
    public List<String> getSomestringaslist() {
        return this.somestringaslist;
    }

    /**
     * Setter for <code>vertx.something.someStringAsList</code>.
     */
    @Override
    public Something setSomestringaslist(List<String> somestringaslist) {
        this.somestringaslist = somestringaslist;
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
        if (this.somedouble == null) {
            if (other.somedouble != null)
                return false;
        }
        else if (!this.somedouble.equals(other.somedouble))
            return false;
        if (this.someenum == null) {
            if (other.someenum != null)
                return false;
        }
        else if (!this.someenum.equals(other.someenum))
            return false;
        if (this.somejsonobject == null) {
            if (other.somejsonobject != null)
                return false;
        }
        else if (!this.somejsonobject.equals(other.somejsonobject))
            return false;
        if (this.somecustomjsonobject == null) {
            if (other.somecustomjsonobject != null)
                return false;
        }
        else if (!this.somecustomjsonobject.equals(other.somecustomjsonobject))
            return false;
        if (this.somejsonarray == null) {
            if (other.somejsonarray != null)
                return false;
        }
        else if (!this.somejsonarray.equals(other.somejsonarray))
            return false;
        if (this.somevertxjsonobject == null) {
            if (other.somevertxjsonobject != null)
                return false;
        }
        else if (!this.somevertxjsonobject.equals(other.somevertxjsonobject))
            return false;
        if (this.sometime == null) {
            if (other.sometime != null)
                return false;
        }
        else if (!this.sometime.equals(other.sometime))
            return false;
        if (this.somedate == null) {
            if (other.somedate != null)
                return false;
        }
        else if (!this.somedate.equals(other.somedate))
            return false;
        if (this.sometimestamp == null) {
            if (other.sometimestamp != null)
                return false;
        }
        else if (!this.sometimestamp.equals(other.sometimestamp))
            return false;
        if (this.sometimestampwithtz == null) {
            if (other.sometimestampwithtz != null)
                return false;
        }
        else if (!this.sometimestampwithtz.equals(other.sometimestampwithtz))
            return false;
        if (this.someinterval == null) {
            if (other.someinterval != null)
                return false;
        }
        else if (!this.someinterval.equals(other.someinterval))
            return false;
        if (this.somebytea == null) {
            if (other.somebytea != null)
                return false;
        }
        else if (!Arrays.equals(this.somebytea, other.somebytea))
            return false;
        if (this.somestringaslist == null) {
            if (other.somestringaslist != null)
                return false;
        }
        else if (!this.somestringaslist.equals(other.somestringaslist))
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
        result = prime * result + ((this.somedouble == null) ? 0 : this.somedouble.hashCode());
        result = prime * result + ((this.someenum == null) ? 0 : this.someenum.hashCode());
        result = prime * result + ((this.somejsonobject == null) ? 0 : this.somejsonobject.hashCode());
        result = prime * result + ((this.somecustomjsonobject == null) ? 0 : this.somecustomjsonobject.hashCode());
        result = prime * result + ((this.somejsonarray == null) ? 0 : this.somejsonarray.hashCode());
        result = prime * result + ((this.somevertxjsonobject == null) ? 0 : this.somevertxjsonobject.hashCode());
        result = prime * result + ((this.sometime == null) ? 0 : this.sometime.hashCode());
        result = prime * result + ((this.somedate == null) ? 0 : this.somedate.hashCode());
        result = prime * result + ((this.sometimestamp == null) ? 0 : this.sometimestamp.hashCode());
        result = prime * result + ((this.sometimestampwithtz == null) ? 0 : this.sometimestampwithtz.hashCode());
        result = prime * result + ((this.someinterval == null) ? 0 : this.someinterval.hashCode());
        result = prime * result + ((this.somebytea == null) ? 0 : Arrays.hashCode(this.somebytea));
        result = prime * result + ((this.somestringaslist == null) ? 0 : this.somestringaslist.hashCode());
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
        sb.append(", ").append(somedouble);
        sb.append(", ").append(someenum);
        sb.append(", ").append(somejsonobject);
        sb.append(", ").append(somecustomjsonobject);
        sb.append(", ").append(somejsonarray);
        sb.append(", ").append(somevertxjsonobject);
        sb.append(", ").append(sometime);
        sb.append(", ").append(somedate);
        sb.append(", ").append(sometimestamp);
        sb.append(", ").append(sometimestampwithtz);
        sb.append(", ").append(someinterval);
        sb.append(", ").append("[binary...]");
        sb.append(", ").append(somestringaslist);

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
        setSomedouble(from.getSomedouble());
        setSomeenum(from.getSomeenum());
        setSomejsonobject(from.getSomejsonobject());
        setSomecustomjsonobject(from.getSomecustomjsonobject());
        setSomejsonarray(from.getSomejsonarray());
        setSomevertxjsonobject(from.getSomevertxjsonobject());
        setSometime(from.getSometime());
        setSomedate(from.getSomedate());
        setSometimestamp(from.getSometimestamp());
        setSometimestampwithtz(from.getSometimestampwithtz());
        setSomeinterval(from.getSomeinterval());
        setSomebytea(from.getSomebytea());
        setSomestringaslist(from.getSomestringaslist());
    }

    @Override
    public <E extends ISomething> E into(E into) {
        into.from(this);
        return into;
    }
}
