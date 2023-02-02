package cz.jbenak.tezamv.ardonFeedConverter.engine;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class CDataAdapter extends XmlAdapter<String, String> {

    @Override
    public String marshal(String s) throws Exception {
        return "<![CDATA[" + s + "]]>";
    }

    @Override
    public String unmarshal(String s) throws Exception {
        return s;
    }
}
