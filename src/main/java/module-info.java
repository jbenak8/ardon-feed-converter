module ardon.feed.converter {
    requires jakarta.xml.bind;
    requires com.google.common;
    requires com.sun.xml.bind;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires transitive javafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fluentui;
    requires MaterialFX;
    requires jasypt;
    requires org.apache.commons.net;

    exports cz.jbenak.tezamv.ardonFeedConverter;
    exports cz.jbenak.tezamv.ardonFeedConverter.dialogs;
    exports cz.jbenak.tezamv.ardonFeedConverter.source;
    exports cz.jbenak.tezamv.ardonFeedConverter.target;
    exports cz.jbenak.tezamv.ardonFeedConverter.luma;
    exports cz.jbenak.tezamv.ardonFeedConverter.engine;
    exports cz.jbenak.tezamv.ardonFeedConverter.mappers;

    opens cz.jbenak.tezamv.ardonFeedConverter to javafx.fxml;
    opens cz.jbenak.tezamv.ardonFeedConverter.dialogs to javafx.fxml;
    opens cz.jbenak.tezamv.ardonFeedConverter.source to jakarta.xml.bind;
    opens cz.jbenak.tezamv.ardonFeedConverter.luma to jakarta.xml.bind;
    opens cz.jbenak.tezamv.ardonFeedConverter.target to jakarta.xml.bind;
    opens cz.jbenak.tezamv.ardonFeedConverter.engine to com.sun.xml.bind.core;
}