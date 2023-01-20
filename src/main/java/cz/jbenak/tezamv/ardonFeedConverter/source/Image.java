package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import jakarta.xml.bind.annotation.XmlElement;

public class Image {

    private String imagePath;

    @XmlElement(name = "IMAGE", required = true)
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equal(imagePath, image.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(imagePath);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("imagePath", imagePath)
                .toString();
    }
}
