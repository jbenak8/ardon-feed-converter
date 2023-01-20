package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PARAM")
@XmlAccessorType(XmlAccessType.FIELD)
public class Param {

    @XmlElement(name = "PARAM_NAME", required = true)
    private String paramName;
    @XmlElement(name = "PARAM_VALUE", required = true)
    private String paramValue;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Param param = (Param) o;
        return Objects.equal(paramName, param.paramName) && Objects.equal(paramValue, param.paramValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(paramName, paramValue);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("paramName", paramName)
                .add("paramValue", paramValue)
                .toString();
    }
}
