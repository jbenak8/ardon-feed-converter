package cz.jbenak.tezamv.ardonFeedConverter.source;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import cz.jbenak.tezamv.ardonFeedConverter.CDataAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.List;

@XmlRootElement(name = "SHOPITEM")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShopItem {


    @XmlElement(name = "ITEM_CODE", required = true)
    private String itemCode;
    @XmlElement(name = "EAN", required = true, nillable = true)
    private String ean;
    @XmlElement(name = "ITEMGROUP_CODE", required = true)
    private String itemGroupCode;
    @XmlElement(name = "PRODUCTNAME", required = true)
    private String productName;
    @XmlElement(name = "IN_CATEGORIES", required = true)
    private List<CategoryPath> inCategories;
    @XmlElement(name = "DESCRIPTION", required = true, nillable = true)
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String description;
    @XmlElement(name = "ADITIONAL_DESCRIPTION", nillable = true)
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String AdditionalDescription;
    @XmlElement(name = "SIZE_TABLE")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String sizeTable;
    @XmlElement(name = "URL")
    private String url;
    @XmlElement(name = "IMAGES")
    private Images images;
    @XmlElement(name = "PRICE_VAT", required = true)
    private Double priceVat;
    @XmlElement(name = "CURRENCY")
    private String currency;
    @XmlElement(name = "VAT", required = true)
    private String vat;
    @XmlElement(name = "PARAMS")
    private ParamWrapper params;
    @XmlElement(name = "SIZE")
    private String size;
    @XmlElement(name = "COLOR")
    private String color;
    @XmlElement(name = "FILES")
    private FileWrapper files;
    @XmlElement(name = "QUANTITY_IN_STOCK")
    private String quantityInStock;
    @XmlElement(name = "AMOUNT_IN_STOCK")
    private Double amountInStock;
    @XmlElement(name = "CONNECT_AND_GO", nillable = true)
    private ProductWrapper connectAndGo;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getItemGroupCode() {
        return itemGroupCode;
    }

    public void setItemGroupCode(String itemGroupCode) {
        this.itemGroupCode = itemGroupCode;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<CategoryPath> getInCategories() {
        return inCategories;
    }

    public void setInCategories(List<CategoryPath> inCategories) {
        this.inCategories = inCategories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalDescription() {
        return AdditionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        AdditionalDescription = additionalDescription;
    }

    public String getSizeTable() {
        return sizeTable;
    }

    public void setSizeTable(String sizeTable) {
        this.sizeTable = sizeTable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public Double getPriceVat() {
        return priceVat;
    }

    public void setPriceVat(Double priceVat) {
        this.priceVat = priceVat;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public ParamWrapper getParams() {
        return params;
    }

    public void setParams(ParamWrapper params) {
        this.params = params;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public FileWrapper getFiles() {
        return files;
    }

    public void setFiles(FileWrapper files) {
        this.files = files;
    }


    public String getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(String quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Double getAmountInStock() {
        return amountInStock;
    }

    public void setAmountInStock(Double amountInStock) {
        this.amountInStock = amountInStock;
    }

    public ProductWrapper getConnectAndGo() {
        return connectAndGo;
    }

    public void setConnectAndGo(ProductWrapper connectAndGo) {
        this.connectAndGo = connectAndGo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopItem shopItem = (ShopItem) o;
        //return Objects.equal(itemCode, shopItem.itemCode) && Objects.equal(ean, shopItem.ean) && Objects.equal(productName, shopItem.productName);
        return Objects.equal(ean, shopItem.ean);
    }

    @Override
    public int hashCode() {
        //return Objects.hashCode(itemCode, ean, productName);
        return Objects.hashCode(ean);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("itemCode", itemCode)
                .add("ean", ean)
                .add("itemGroupCode", itemGroupCode)
                .add("productName", productName)
                .add("inCategories", inCategories)
                .add("description", description)
                .add("AdditionalDescription", AdditionalDescription)
                .add("sizeTable", sizeTable)
                .add("url", url)
                .add("images", images)
                .add("priceVat", priceVat)
                .add("currency", currency)
                .add("vat", vat)
                .add("params", params)
                .add("size", size)
                .add("color", color)
                .add("files", files)
                .add("quantityInStock", quantityInStock)
                .add("amountInStock", amountInStock)
                .add("connectAndGo", connectAndGo)
                .toString();
    }
}
