package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FILES")
public class FileWrapper {

    @XmlElement(name = "FILE", required = true)
    private List<String> files;

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileWrapper that = (FileWrapper) o;
        return Objects.equal(files, that.files);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("files", files)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(files);
    }
}
