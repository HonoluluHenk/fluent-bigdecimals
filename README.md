# fluent-bigdecimals

![Maven Central](https://img.shields.io/maven-central/v/com.github.honoluluhenk.fluent-bigdecimals/fluent-bigdecimals)
[![pipeline status](https://gitlab.com/HonoluluHenk/fluent-bigdecimals/badges/main/pipeline.svg)](https://gitlab.com/HonoluluHenk/fluent-bigdecimals/-/commits/main)
![GitHub contributors](https://img.shields.io/github/contributors/HonoluluHenk/fluent-bigdecimals)
![GitHub stars](https://img.shields.io/github/stars/HonoluluHenk/fluent-bigdecimals?style=social)
![GitHub forks](https://img.shields.io/github/forks/HonoluluHenk/fluent-bigdecimals?style=social)

fluent-bigdecimals is a library that lets Java programmers forget about BigDecimal rounding/scaling.

It does this by wrapping Javas [BigDecimal](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html#compareTo-java.math.BigDecimal-)
into a class with fancy fluent API.

Very few and low-impact transitive dependencies.

Java 9 modules supported.

## Prerequisites/Dependencies

This project requires Java >= 11.

## Installing

Maven dependency:

```xml

<dependency>
  <groupId>com.github.honoluluhenk.fluent-bigdecimals</groupId>
  <artifactId>fluent-bigdecimals</artifactId>
  <version>x.y.z</version>
</dependency>
```

Current version: see [GitHub releases](https://github.com/HonoluluHenk/fluent-bigdecimals/releases)
or [Maven Central](https://search.maven.org/search?q=g:com.github.honoluluhenk.fluent-bigdecimals%20a:fluent-bigdecimals)

## Basic Usage

fluent-bigdecimals requires you to only setup precision, rounding and scaling (a.k.a.: the `Configuration`) once. You can then re-use this configuration on all BigDecimal operations.

### Step 1: Define your rounding/scaling configurations globally

```java
public class MyMathUtil {
  private static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(7, HALF_UP);

  // some custom configuration to your liking (here: just rounding when calling the operation but no scaling)
  public static final Configuration<FluentBigDecimal> DEFAULT = ConfigurationFactory
    .create(DEFAULT_MATH_CONTEXT, new NopScaler());

  // predefined: round/scale in a database compatible way.
  public static final BigDecimalFactory DATABASE = ConfigurationFactory.jpaBigDecimal();
}

```

### Step 2: use these configurations

```java
public class MyBusinessTest {

  @Test
  public void showcase() {

    // after each step: automatic rounding/scaling according to current configuration
    BigDecimal result = DEFAULT.of("12.3456789")
      .add("54.555555")
      // currently supports BigDecimal, String and other FluentBigDecimals on all implemented operations
      .multiply("42.23")
      .divide(new BigDecimal("555.5"))
      .subtract(DEFAULT.of("99"))
      // continue with other configuration
      .roundInto(DATABASE)
      .multiply("123.99999")
      .getValue();

    // return result;
    assertThat(result)
      .isEqualTo("-11644.84");
  }

  @Test
  public void usingFancyOperators() {
    // call your own operators (or BigDecimal operations not yet directly implemented by fluent-bigdecimals)
    FluentBigDecimal result = DEFAULT.of("12.3456789")
      .apply(this::myFancyOperation, 42);

    // operators returning other values thant BigDecimal
    int signum = DEFAULT.of("12.3456789")
      .map(BigDecimal::signum);
  }
}
```

## Basics

### Semantic versioning

In short: no breaking changes on minor or patch version upgrades.

See [Semantic versioning](https://semver.org/)

### Nullability

All method arguments and return values are @NonNull if not explicitly stated otherwise (see param/method annotations).

Passing an *illegal* null parameter will immediately result in a `NullPointerException`.

### Immutability, thread safety

All classes are immutable and - as such - thread-safe

### Configuration

Class: [Configuration.java](src/main/java/com/github/honoluluhenk/fluentbigdecimals/Configuration.java)

A Configuration is a combination of a
[MathContext](https://docs.oracle.com/javase/9/docs/api/java/math/MathContext.html) (i.e.: precision and rounding)
and some [Scaler](#Scaler).

The Configuration class is as factory and creates the main workhorse: [FluentBigDecimal](#FluentBigDecimal). As such, Configuration is the main starting-point of all that is to come.

To help setup your configurations, see [ConfigurationFactory](#ConfigurationFactory).

Basic setups may be modified by various `with()` methods.

### ConfigurationFactory

Classs: [ConfigurationFactory.java](src/main/java/com/github/honoluluhenk/fluentbigdecimals/ConfigurationFactory.java).

Helps building a [Configuration](#Configuration) by providing some useful defaults.

The most basic factory method is `ConfigurationFactory::create(MathContext, Scaler) ` (all the others build on that).

Some more convenient factory-methods in `ConfigurationFactory`:

| method | description |
|--------|-------------|
| ::monetary | The most common usecase: some precision with a maximum scale of 2 and HALF_UP rounding. |
| ::jpaBigDecimal | compatible with JPA/Hibernate defaults: @Column(precision = 16, scale = 2). See [JPA/Database](#JPA/Database-precision-and-scale) |
| ::databaseJavaNotation | Scale in a database compatible way and use Java notation for precision/scale. See [JPA/Database](#JPA/Database-precision-and-scale) |
| ::databaseDBNotation | Scale in a database compatible way and use database notation for precision/scale. See [JPA/Database](#JPA/Database-precision-and-scale) |
| ::cashRounding | User Cash Rouding/Scaling. See [Cash Rounding](#Cash-Rounding) |

### FluentBigDecimal

Class: [FluentBigDecimal.java](src/main/java/com/github/honoluluhenk/fluentbigdecimals/FluentBigDecimal.java) ans [AbstractFluentBigDecimal.java](src/main/java/com/github/honoluluhenk/fluentbigdecimals/AbstractFluentBigDecimal.java)

The main workhorse: apply operations on BigDecimals and keep track of precision, RoundingMode and scale (i.e.: the [Configuration](#Configuration)).

Provides the operations (add/subtract/multiply/divide/...) and a multitude of helper methods.

Basic flow for each operation:

1. apply operation using the configured MathContext (i.e. apply precision/rounding).
2. scale using the [Scaler](#Scaler)
3. return a new instance of FluentBigDecimal (that's where the "Fluent" comes from :) )

### Scaler

Interface: [Scaler.java](src/main/java/com/github/honoluluhenk/fluentbigdecimals/scaler/Scaler.java).

A Scaler takes care of scaling the rounded value after applying an operation.

There exist some predefined Scalers:

| scaler | description |
|--------|-------------|
| [CashRoundingScaler](src/main/java/com/github/honoluluhenk/fluentbigdecimals/scaler/CashRoundingScaler.java) | See [Cash Rounding](#Cash-Rounding) |
| [FixedScaleScaler](src/main/java/com/github/honoluluhenk/fluentbigdecimals/scaler/FixedScaleScaler.java)     | reduces/expands to the desired scale |
| [IntegerScaler](src/main/java/com/github/honoluluhenk/fluentbigdecimals/scaler/IntegerScaler.java)           | Scales to scale 0 (i.e.: effectively convert to integer) |
| [MaxScaleScaler](src/main/java/com/github/honoluluhenk/fluentbigdecimals/scaler/MaxScaleScaler.java)         | Reduces scale to a given maximum |
| [NopScaler](src/main/java/com/github/honoluluhenk/fluentbigdecimals/scaler/NopScaler.java)                   | Does not scale at all. Use this if you just care about precision |

## Common Usecases

### Creating your own Configuration

See methods in [ConfigurationFactory](src/main/java/com/github/honoluluhenk/fluentbigdecimals/ConfigurationFactory.java)
for some predefined configurations.

Some examples:

* `ConfigurationFactory::monetary` (configurable precision/scale, HALF_UP rounding and stick to the given scale)
* `ConfigurationFactory::cashRounding` (same as monetary plus apply [Cash Rounding](https://en.wikipedia.org/wiki/Cash_rounding) on each step, See also enum `CashRoundingUnits` for some predefined values, see also: Advanced Usage)
* `ConfigurationFactory::jpaBigDecimal` (precision/scale taken from [JPA/Hibernate](https://de.wikipedia.org/wiki/Java_Persistence_API) defaults for BigDecimal, see also: Advanced Usage)

See chapter [Scaler](#Scaler) for more.

### Builders/with

All relevant classes (Configuration, Scaler, FluentBigDecimal, ...) support various `with` methods. This means you can always start with some defaults from `ConfigurationFactory` and then adjust to your liking.

Example:

```java
class Foo {
  private final MonetaryConfiguration<FluentBigDecimal> SWISS_CASH = ConfigurationFactory
    .monetary(20)
    .withScale(10);
}
```

### Cash Rounding (predefined configuration)

```java
class Foo {
  private final Configuration<FluentBigDecimal> SWISS_CASH = ConfigurationFactory
    .cashRounding(20, CashRoundingUnits.ROUND_DOT05);

  private final Configuration<FluentBigDecimal> HIGH_PRECISION = ConfigurationFactory
    .create(20, HALF_UP, MaxScaleScaler.of(10));

  @Test
  void roundIntoCash() {
    // start off with some high precision calculations
    FluentBigDecimal cash = HIGH_PRECISION.of("12345.67890")
      .multiply("3")
      // intermediate result: 37037.03670
      .roundInto(SWISS_CASH);

    assertThat(cash.getValue())
      .isEqualTo("37037.05");
  }
}
```

You might also give a custom rounding unit using the `CashRoundingScaler`.

Rouding units represent the smallest amount representable in the cash system.

Example: Switzerland. The Swiss Rappen is avaiable only 5/100 units (0.05, 0.10, 0.15, ...). As such, the unit parameter would be "0.05"
(but for most units you should find constants in `CashRoundingUnits`)

### JPA/Database precision and scale

In contrast to Java BigDecimals, databases usually only allow a definable *maximum* scale for numeric values.

Also, Java usually treat precision and scale differently:
Java: `precision` is total number of relevant digits (integer + decimal part together), allowing a maximum of `scale`
digits after the decimal point. Database: `precision` is the total number of digits including `scale`. There are `precision - scale` digits available for the integer part.

A configuration matching the above behavior can be obtained by Using database notation:
[ConfigurationFactory.database(precision, scale)](src/main/java/com/github/honoluluhenk/fluentbigdecimals/ConfigurationFactory.java)
.

Also there is a shortcut to obtain a database configuration using JPA/Hibernate default values for precision/scale:
Using JPA/Hibernate notation:
[ConfigurationFactory.jpaBigDecimal(precision, scale)](src/main/java/com/github/honoluluhenk/fluentbigdecimals/ConfigurationFactory.java)
.

Just the scaling part is implemented by the [MaxScaleScaler](src/main/java/com/github/honoluluhenk/fluentbigdecimals/scaler/MaxScaleScaler.java).

###

## Advanced usage

### compareTo/equals/hashCode

`compareTo`, `equals` and `hashCode` are implemented by directly delegating to their BigDecimal equivalents and thus could be treated as a drop-in replacement for mose usecases.

### Cash Rounding

See [Wikipedia](https://en.wikipedia.org/wiki/Cash_rounding)

Some countries round to some custom fraction (e.g.: Switzerland rounds to 0.05 Rappen).

### rounding/scaling using configurations

Maybe you do some high-precision calculations. Then, at some point, you need to continue calculating using the (e.g.)
database precision.

This can be achieved using the `roundInto` method:

```java
class Foo {
  public void roundIntoDemo() {
    FluentBigDecimal result = DEFAULT.of("12345678.90")
      .add("999.999999")
      .roundInto(DATABASE) // exact result: 124456789.899999, gets rounded to (124456789.90).
      // now we continue with DATABASE precision/scaling
      .multiply("12.3456");

    assertThat(result.getValue())
      .isEqualTo("152427172.61");
  }
}
```

Rare usecase: If immediate rounding is not wanted: use the `withConfiguration` method instead:

```java
class Foo {
  public void withConfiguration() {
    FluentBigDecimal result = DEFAULT.of("12345678.90")
      .add("999.999999")
      .withConfiguration(DATABASE) // exact result: 124456789.899999, does *not* get rounded here
      // ... but on the next step:
      .multiply("12.3456");

    assertThat(result.getValue())
      .isEqualTo("152427172.61");
  }
}
```

If you need this rounded, you might call the `round()` method afterwards.

### Mapping to other types

There is the `map` method:

```java
class Foo {
  public void mapping() {
    double result = DATABASE.of("12345678.90")
      .map(BigDecimal::doubleValue);
  }
}
```

### Custom one-off operators

If you need re-usable operators, consider extension (see below) instead!

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

### Extension

If you regularly need some custom operators, you do have the option to extend your custom FluentBigDecimal from `AbstractFluentBigDecimal`.

`Configuration` supports the factory pattern for instantiating your subclass on each operation:

```java
public class MyConfig {
  public static final Configuration<MyMath> MY_MATH = ConfigurationFactory.monetary(20)
    .withFactory(MyMath::new);

  public static final Configuration<MyMath> SWISS_CASH = ConfigurationFactory
    .cashRounding(20, CashRoundingUnits.ROUND_DOT05)
    .withFactory(MyMath::new);

}

public class MyMath extends AbstractFluentBigDecimal<MyMath> {
  private static final long serialVersionUID = -1828369497254888980L;

  protected MyMath(@NonNull BigDecimal value, @NonNull Configuration<MyMath> configuration) {
    super(value, configuration);
  }

  public String toJson() {
    return "{ value: \"" + getValue().toPlainString() + "\" }";
  }

  public MyMath roundIntoSwissRappen() {
    return roundInto(SWISS_CASH);
  }
}

class MyBusiness {

  void doStuff() {
    var json = MY_MATH.of("42.04") // of() creates an instance of MyMath
      .roundIntoSwissRappen()
      .add(new BigDecimal("23"))
      .toJson();

    assertThat(json)
      .isEqualTo("{ value: \"65.05\" }");
  }

}
```

## Contributing/Contact

Just create an issue.

## License

This project uses the following license: [LGPL-3.0](https://www.gnu.org/licenses/lgpl-3.0-standalone.html)

## Changelog

See [releases](https://github.com/HonoluluHenk/fluent-bigdecimals/releases)
