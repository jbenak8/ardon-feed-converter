package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import jakarta.xml.bind.annotation.XmlElement;

public class CategoryPath {

    private String categoryPath;

    @XmlElement(name = "CATEGORY_PATH", required = true)
    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryPath that = (CategoryPath) o;
        return Objects.equal(categoryPath, that.categoryPath);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(categoryPath);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("categoryPath", categoryPath)
                .toString();
    }
}
