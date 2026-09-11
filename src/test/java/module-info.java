module tests {
    requires transitive com.github.honoluluhenk.fluentbigdecimals;
    requires org.assertj.core;
    requires org.junit.jupiter;
    requires org.mockito;

    // @UtilityClass in TypeFixtures
    requires static lombok;

    opens com.github.honoluluhenk.fluentbigdecimals.tests to org.junit.platform.commons;
    opens com.github.honoluluhenk.fluentbigdecimals.tests.customtypes to org.junit.platform.commons;
    opens com.github.honoluluhenk.fluentbigdecimals.tests.scaler to org.junit.platform.commons;
    opens com.github.honoluluhenk.fluentbigdecimals.tests.demo to org.junit.platform.commons;
}
