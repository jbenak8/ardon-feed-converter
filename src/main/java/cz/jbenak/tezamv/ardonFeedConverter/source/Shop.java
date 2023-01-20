package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import cz.jbenak.tezamv.ardonFeedConverter.source.ShopItem;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "SHOP")
public class Shop {

    private List<ShopItem> items;

    @XmlElement(name = "SHOPITEM")
    public List<ShopItem> getItems() {
        return items;
    }

    public void setItems(List<ShopItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("items", items)
                .toString();
    }
}
