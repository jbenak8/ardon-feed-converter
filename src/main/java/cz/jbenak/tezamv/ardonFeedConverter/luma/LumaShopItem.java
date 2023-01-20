package cz.jbenak.tezamv.ardonFeedConverter.luma;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SHOPITEM")
@XmlAccessorType(XmlAccessType.FIELD)
public class LumaShopItem {

    @XmlElement(name = "ITEM_ID", required = true)
    private String itemId;
    @XmlElement(name = "PRODUCT", required = true)
    private String product;
    @XmlElement(name = "PACKAGE", required = true)
    private Integer packageAmount;
    @XmlElement(name = "MIN_AMOUNT", required = true)
    private Integer minAmount;
    @XmlElement(name = "EAN", required = true)
    private String ean;
    @XmlElement(name = "AMOUNT", required = true)
    private String amount;
    @XmlElement(name = "DESCRIPTION", required = true)
    private String description;
    @XmlElement(name = "CATEGORYTEXT", required = true)
    private String category;
    @XmlElement(name = "PRODUCERTEXT", required = true)
    private String producer;
    @XmlElement(name = "URL", required = true)
    private String url;
    @XmlElement(name = "DELIVERY_DATE", required = true)
    private String deliveryDate;
    @XmlElement(name = "IMGURL", required = true)
    private String img;
    @XmlElement(name = "PRICE", required = true)
    private Price price;
    @XmlElement(name = "PRICE_VAT", required = true)
    private Double priceVat;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(Integer packageAmount) {
        this.packageAmount = packageAmount;
    }

    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Double getPriceVat() {
        return priceVat;
    }

    public void setPriceVat(Double priceVat) {
        this.priceVat = priceVat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LumaShopItem that = (LumaShopItem) o;
        return Objects.equal(itemId, that.itemId) && Objects.equal(product, that.product) && Objects.equal(packageAmount, that.packageAmount) && Objects.equal(minAmount, that.minAmount) && Objects.equal(ean, that.ean) && Objects.equal(amount, that.amount) && Objects.equal(description, that.description) && Objects.equal(category, that.category) && Objects.equal(producer, that.producer) && Objects.equal(url, that.url) && Objects.equal(deliveryDate, that.deliveryDate) && Objects.equal(img, that.img) && Objects.equal(price, that.price) && Objects.equal(priceVat, that.priceVat);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemId, product, packageAmount, minAmount, ean, amount, description, category, producer, url, deliveryDate, img, price, priceVat);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("itemId", itemId)
                .add("product", product)
                .add("packageAmount", packageAmount)
                .add("minAmount", minAmount)
                .add("ean", ean)
                .add("amount", amount)
                .add("description", description)
                .add("category", category)
                .add("producer", producer)
                .add("url", url)
                .add("deliveryDate", deliveryDate)
                .add("img", img)
                .add("price", price)
                .add("priceVat", priceVat)
                .toString();
    }
}
