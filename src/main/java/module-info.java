@SuppressWarnings({"requires-automatic", "requires-transitive-automatic"})
open module com.github.honoluluhenk.fluentbigdecimals {
    exports com.github.honoluluhenk.fluentbigdecimals;
    exports com.github.honoluluhenk.fluentbigdecimals.scaler;

    requires transitive org.checkerframework.checker.qual;

    requires static transitive lombok;
    requires static transitive com.github.spotbugs.annotations;
}
