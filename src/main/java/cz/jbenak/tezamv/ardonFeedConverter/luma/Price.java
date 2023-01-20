package cz.jbenak.tezamv.ardonFeedConverter.luma;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "PRICE")
@XmlAccessorType(XmlAccessType.FIELD)
public class Price {

    @XmlValue
    private Double price;
    @XmlAttribute(name = "DISCOUNT")
    private String discount;
    @XmlAttribute(name = "ACTION")
    private String action;
    @XmlAttribute(name = "VAT")
    private String vat;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price1 = (Price) o;
        return Objects.equal(price, price1.price) && Objects.equal(discount, price1.discount) && Objects.equal(action, price1.action) && Objects.equal(vat, price1.vat);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(price, discount, action, vat);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("price", price)
                .add("discount", discount)
                .add("action", action)
                .add("vat", vat)
                .toString();
    }
}
