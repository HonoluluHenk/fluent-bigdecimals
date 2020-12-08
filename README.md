# fluent-bigdecimals

![Maven Central](https://img.shields.io/maven-central/v/com.github.honoluluhenk.fluent-bigdecimals/fluent-bigdecimals)
![GitHub contributors](https://img.shields.io/github/contributors/HonoluluHenk/fluent-bigdecimals)
![GitHub stars](https://img.shields.io/github/stars/HonoluluHenk/fluent-bigdecimals?style=social)
![GitHub forks](https://img.shields.io/github/forks/HonoluluHenk/fluent-bigdecimals?style=social)

fluent-bigdecimals is a library that lets Java programmers forget about BigDecimal rounding/scaling!

## Prerequisites

This project requires Java >= 11

## Installing

Use this maven dependency:

```xml

<dependency>
  <groupId>com.github.honoluluhenk.fluent-bigdecimals</groupId>
  <artifactId>fluent-bigdecimals</artifactId>
  <version>x.y.z</version>
</dependency>
```

Current version: see [GitHub releases](./releases)
or [Maven Central](https://search.maven.org/search?q=g:com.github.honoluluhenk.fluent-bigdecimals%20a:fluent-bigdecimals)

## Basic Usage

FluentBigDecimals requires you to only setup precision, rounding and scaling (a.k.a.: the "configuration") once. It will
then re-use this configuration on all BigDecimal operations.

### Step 1: Define your configurations for common use-cases

```java
public class MyMathUtil {
  // some custom configuration
  public static final BigDecimalFactory DEFAULT = BigDecimalConfiguration.factory(DEFAULT_MATH_CONTEXT, new MaxPrecisionScaler());
  // predefined: round/scale in a databse compatible way (See FIXME: link to method).
  public static final BigDecimalFactory DATABASE = BigDecimalConfiguration.jpaBigDecimal();
}

```

### Step 2: use these configurations

```java
public class MyBusiness {
  public BigDecimal usingBigDecimals() {
    BigDecimal result = DEFAULT.of("12.3456789")
      .add(new BigDecimal("54.555555"))
      .roundInto(DATABASE) // round/scale using the DATABASE configuration and use it for future operations
      .multiply(new BigDecimal("123.99999"))
      .getValue();

    return result;
  }

}
```

## Advanced usage

### compareTo/equals/hashCode

`compareTo` is implemented by directly delegating
to [BigDecimal::compareTo](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html#compareTo-java.math.BigDecimal-)
(i.e.: it does not take any `Configuration` into account).

This allows putting a `FluentBigDecimal` into anything that needs sorting.

equals/hashCode take both value and configuration into account. This breaks the contract on compareTo in regard to
equals/hashcode... just like BigDecimal does (so: nothing new here).

### Switching to other configurations

While rounding: use the `roundInto` method

```java
class Foo {
  private void roundIntoDemo() {
    FluentBigDecimal result = DEFAULT.of("12345678.90")
      .roundInto(DATABASE);
  }
}
```

Without rounding: use the `withConfiguration` method:

```java
class Foo {
  private void roundIntoDemo() {
    FluentBigDecimal result = DEFAULT.of("12345678.90")
      .withConfiguration(DATABASE);


  }
}
```

If you need this rounded, you might call the `round()` method afterwards.

### Custom operators

Custom operators can be applied using the various `apply()` methods.

Operation with one argument:

```java
class Foo {
  public void customOperationsWithOneArgument() {
    FluentBigDecimal result = DATABASE.of("12345678.90")
      .apply(BigDecimal::divideToIntegralValue, new BigDecimal("42"))
      .apply(this::myFancyOperation, 42);
  }

  public BigDecimal myFancyOperation(BigDecimal value, int argument, MathContext mc) {
    // just a simple simulation
    BigDecimal result = value.add(BigDecimal.valueOf(argument));

    return result;
  }

}
```

Operation with arguments of your choice:

```java
class Foo {
  public void customOperationsWithAnyArgument(Object someParam) {
    FluentBigDecimal result = DATABASE.of("12345678.90")
      .apply((value, mathContext) -> doStuffWith(value, someParam));
  }

  private BigDecimal doStuffWith(BigDecimal value, Object whatever) {
    return value.add(new BigDecimal(whatever.hashCode()));
  }
}
```

### mapping to other types

There is the `map` method:

```java
class Foo {
  public void mapping() {
    double result = DATABASE.of("12345678.90")
      .map(BigDecimal::doubleValue);
  }
}
```

## Contributing/Contact

Just create an issue.

## License

This project uses the following license: [LGPL-3.0](https://www.gnu.org/licenses/lgpl-3.0-standalone.html)
