@SuppressWarnings({"requires-automatic", "requires-transitive-automatic"})
open module com.github.honoluluhenk.fluentbigdecimals {
    exports com.github.honoluluhenk.fluentbigdecimals;
    exports com.github.honoluluhenk.fluentbigdecimals.scaler;

    // JSpecify annotations appear in exported signatures: -Xlint:exports demands 'transitive'
    requires transitive org.jspecify;

    // lombok is compile-time only and does not appear in exported signatures
    requires static lombok;
}
