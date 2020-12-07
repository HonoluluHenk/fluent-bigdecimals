# fluent-bigdecimals

Forget all about rounding/scaling in your day-to-day programming tasks!

#

# Demo

```java
public class MyMathUtil {
  public static final BigDecimalFactory DEFAULT = BigDecimalFactory.factory(DEFAULT_MATH_CONTEXT, new MaxPrecisionScaler());
  public static final BigDecimalFactory DATABASE = BigDecimalFactory.jpaBigDecimal();
}

public class MyBusiness {
  public BigDecimal usingBigDecimals() {

    BigDecimal a = new BigDecimal("12.3456789");
    assertThat(a).isEqualTo("12.3456789");

    // explicit rounding
    BigDecimal b = a.round(DEFAULT_MATH_CONTEXT);
    assertThat(b).isEqualTo("12.34568");

    // some math operation
    BigDecimal c = b.add(new BigDecimal("54.555555"), DEFAULT_MATH_CONTEXT);
    // intermediate result: 66.901235 and then rounded
    assertThat(c).isEqualTo("66.90124");

    // continue with different scaler
    BigDecimal x = c.round(DATABASE_MATH_CONTEXT) // remember: needs scaling
      .setScale(DATABASE_MAX_SCALE, DATABASE_MATH_CONTEXT.getRoundingMode());

    assertThat(x).isEqualTo("66.90");

    BigDecimal y = x.multiply(new BigDecimal("123.99999"), DATABASE_MATH_CONTEXT)
      .setScale(DATABASE_MAX_SCALE, DATABASE_MATH_CONTEXT.getRoundingMode());
    // intermediate result: 8295.599331
    assertThat(y).isEqualTo("8295.60");

    // finally...
    BigDecimal result = y;
    assertThat(result).isEqualTo("8295.60");

    return result;
  }

}
```
