package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ParamWrapper {

    @XmlElement(name = "PARAM", required = true)
    private List<Param> param;

    public List<Param> getParam() {
        return param;
    }

    public void setParam(List<Param> param) {
        this.param = param;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamWrapper that = (ParamWrapper) o;
        return Objects.equal(param, that.param);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(param);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("param", param)
                .toString();
    }
}
