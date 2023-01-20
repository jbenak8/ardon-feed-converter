package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "IMAGES")
@XmlAccessorType(XmlAccessType.FIELD)
public class Images {

    @XmlElement(name = "IMAGE", required = true)
    private List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Images images1 = (Images) o;
        return Objects.equal(images, images1.images);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(images);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("images", images)
                .toString();
    }
}
