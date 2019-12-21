package org.obridge.coconut.json;

import org.junit.Assert;
import org.junit.Test;
import org.obridge.coconut.converter.Converter;
import org.obridge.coconut.converter.Converters;
import org.obridge.coconut.converter.converters.StringPatternToLocalDateConverter;
import org.obridge.coconut.converter.exception.ConverterNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class JsonObjectTest {

    @Test
    public void t1() throws ConverterNotFoundException {

        Converters.register(new StringPatternToLocalDateConverter("yyyy-MM-dd"));
        Converters.register(new Converter<Double, BigDecimal>() {
            @Override
            public BigDecimal convert(Double val) {
                return BigDecimal.valueOf(val);
            }
        });

        Teszt t = JsonObject.create(Teszt.class,
                "{\n" +
                        "    \"teszt\": \"Hello\",\n" +
                        "    \"integ\": 12345,\n" +
                        "    \"dt\": \"2019-11-03\",\n" +
                        "    \"decimal\": 3.4567,\n" +
                        "    \"otst\": {\n" +
                        "        \"name\": \"Ferenc\",\n" +
                        "        \"country\": \"Hungary\"\n" +
                        "    },\n" +
                        "    \"arr\": [\n" +
                        "        {\"name\": \"Lo\", \"country\": \"Bela\"},\n" +
                        "        {\"name\": \"Hejj\", \"country\": \"Hajj\"}\n" +
                        "    ]\n" +
                        "}");

        Assert.assertEquals("Hello", t.teszt());
        Assert.assertEquals(Integer.valueOf(12345), t.getInteg());
        Assert.assertEquals(LocalDate.parse("2019-11-03"), t.dt());
        Assert.assertEquals(BigDecimal.valueOf(3.4567), t.decimal());

        Assert.assertEquals(2,
                t.arr()
                        .size());

        Assert.assertEquals("Ferenc",
                t.otst()
                        .name());

        Assert.assertEquals("Bela",
                t.arr()
                        .get(0)
                        .country());

    }

    public interface Teszt {

        String teszt();

        Integer getInteg();

        LocalDate dt();

        BigDecimal decimal();

        Person otst();

        List<Person> arr();

    }

    public interface Person {

        String name();

        String country();

    }
}