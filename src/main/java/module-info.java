module ardon.feed.converter {
    requires jakarta.xml.bind;
    requires com.google.common;
    requires com.sun.xml.bind;

    opens cz.jbenak.tezamv.ardonFeedConverter.source to jakarta.xml.bind;
    opens cz.jbenak.tezamv.ardonFeedConverter.luma to jakarta.xml.bind;
    opens cz.jbenak.tezamv.ardonFeedConverter.target to jakarta.xml.bind;
    opens cz.jbenak.tezamv.ardonFeedConverter to com.sun.xml.bind.core;
}