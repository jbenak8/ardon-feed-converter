package cz.jbenak.tezamv.ardonFeedConverter.luma;

import com.google.common.base.MoreObjects;
import cz.jbenak.tezamv.ardonFeedConverter.source.ShopItem;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "SHOP")
public class LumaShop {

    private List<LumaShopItem> items;
    @XmlAttribute(name = "CUSTOMER")
    private String attribute;

    @XmlElement(name = "SHOPITEM")
    public List<LumaShopItem> getItems() {
        return items;
    }

    public void setItems(List<LumaShopItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("items", items)
                .add("attribute", attribute)
                .toString();
    }
}
