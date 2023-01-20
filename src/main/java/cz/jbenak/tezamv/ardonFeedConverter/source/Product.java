package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PRODUCT")
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {

    @XmlElement(name = "NAME", required = true)
    private String name;
    @XmlElement(name = "PRODUCT_URL", required = true)
    private String productUrl;
    @XmlElement(name = "IMAGE_URL", required = true)
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equal(name, product.name) && Objects.equal(productUrl, product.productUrl) && Objects.equal(imageUrl, product.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, productUrl, imageUrl);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("productUrl", productUrl)
                .add("imageUrl", imageUrl)
                .toString();
    }
}
