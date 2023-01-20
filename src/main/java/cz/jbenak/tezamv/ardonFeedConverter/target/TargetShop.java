package cz.jbenak.tezamv.ardonFeedConverter.target;

import com.google.common.base.MoreObjects;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "SHOP")
public class TargetShop {

    private List<TargetShopItem> items;

    @XmlElement(name = "SHOPITEM")
    public List<TargetShopItem> getItems() {
        return items;
    }

    public void setItems(List<TargetShopItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("items", items)
                .toString();
    }
}
