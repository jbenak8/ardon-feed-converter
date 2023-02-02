package cz.jbenak.tezamv.ardonFeedConverter.target;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import cz.jbenak.tezamv.ardonFeedConverter.engine.CDataAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "SHOPITEM")
@XmlAccessorType(XmlAccessType.FIELD)
public class TargetShopItem {

    @XmlElement(name = "ITEM_CODE", required = true)
    private String itemCode;
    @XmlElement(name = "ITEM_GROUP_CODE", required = true)
    private String itemGroupCode;
    @XmlElement(name = "EAN", required = true, nillable = true)
    private String ean;
    @XmlElement(name = "ITEM_NAME", required = true)
    private String productName;
    @XmlElement(name = "CATEGORIES", required = true)
    private String categories;
    @XmlElement(name = "DESCRIPTION", required = true, nillable = true)
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String description;
    @XmlElement(name = "ADDITIONAL_DESCRIPTION", nillable = true)
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String AdditionalDescription;
    @XmlElement(name = "IMAGES", required = true, nillable = true)
    private String images;
    @XmlElement(name = "PRICE_VAT", required = true)
    private Double priceVat;
    @XmlElement(name = "VAT", required = true)
    private String vat;
    @XmlElement(name = "SIZE", required = true)
    private String size;
    @XmlElement(name = "COLOR", required = true)
    private String color;
    @XmlElement(name = "PARAMS", required = true, nillable = true)
    private String params;
    @XmlElement(name = "FILE1", required = true, nillable = true)
    private String file1;
    @XmlElement(name = "FILE1_DESC", required = true, nillable = true)
    private String file1Description;
    @XmlElement(name = "FILE2", required = true, nillable = true)
    private String file2;
    @XmlElement(name = "FILE2_DESC", required = true, nillable = true)
    private String file2Description;
    @XmlElement(name = "CONNECTED_PROD_IDS", required = true, nillable = true)
    private String connectedProductIds;
    @XmlElement(name = "MANUFACTURER", required = true, nillable = true)
    private String manufacturer;
    @XmlElement(name = "IN_STOCK", required = true, nillable = true)
    private Boolean inStock;
    @XmlElement(name = "IN_STOCK_TYPE", required = true, nillable = true)
    private String inStockType;
    @XmlElement(name = "IN_STOCK_AMOUNT", required = true, nillable = true)
    private Double inStockAmount;
    @XmlElement(name = "MEASURE_UNIT", required = true, nillable = true)
    private String measureUnit;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemGroupCode() {
        return itemGroupCode;
    }

    public void setItemGroupCode(String itemGroupCode) {
        this.itemGroupCode = itemGroupCode;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
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

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Double getPriceVat() {
        return priceVat;
    }

    public void setPriceVat(Double priceVat) {
        this.priceVat = priceVat;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public String getFile1Description() {
        return file1Description;
    }

    public void setFile1Description(String file1Description) {
        this.file1Description = file1Description;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }

    public String getFile2Description() {
        return file2Description;
    }

    public void setFile2Description(String file2Description) {
        this.file2Description = file2Description;
    }

    public String getConnectedProductIds() {
        return connectedProductIds;
    }

    public void setConnectedProductIds(String connectedProductIds) {
        this.connectedProductIds = connectedProductIds;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public String getInStockType() {
        return inStockType;
    }

    public void setInStockType(String inStockType) {
        this.inStockType = inStockType;
    }

    public Double getInStockAmount() {
        return inStockAmount;
    }

    public void setInStockAmount(Double inStockAmount) {
        this.inStockAmount = inStockAmount;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetShopItem that = (TargetShopItem) o;
        return Objects.equal(itemCode, that.itemCode) && Objects.equal(itemGroupCode, that.itemGroupCode) && Objects.equal(ean, that.ean) && Objects.equal(productName, that.productName) && Objects.equal(categories, that.categories) && Objects.equal(description, that.description) && Objects.equal(AdditionalDescription, that.AdditionalDescription) && Objects.equal(images, that.images) && Objects.equal(priceVat, that.priceVat) && Objects.equal(vat, that.vat) && Objects.equal(size, that.size) && Objects.equal(color, that.color) && Objects.equal(params, that.params) && Objects.equal(file1, that.file1) && Objects.equal(file1Description, that.file1Description) && Objects.equal(file2, that.file2) && Objects.equal(file2Description, that.file2Description) && Objects.equal(connectedProductIds, that.connectedProductIds) && Objects.equal(manufacturer, that.manufacturer) && Objects.equal(inStock, that.inStock) && Objects.equal(inStockType, that.inStockType) && Objects.equal(inStockAmount, that.inStockAmount) && Objects.equal(measureUnit, that.measureUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemCode, itemGroupCode, ean, productName, categories, description, AdditionalDescription, images, priceVat, vat, size, color, params, file1, file1Description, file2, file2Description, connectedProductIds, manufacturer, inStock, inStockType, inStockAmount, measureUnit);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("itemCode", itemCode)
                .add("itemGroupCode", itemGroupCode)
                .add("ean", ean)
                .add("productName", productName)
                .add("categories", categories)
                .add("description", description)
                .add("AdditionalDescription", AdditionalDescription)
                .add("images", images)
                .add("priceVat", priceVat)
                .add("vat", vat)
                .add("size", size)
                .add("color", color)
                .add("params", params)
                .add("file1", file1)
                .add("file1Description", file1Description)
                .add("file2", file2)
                .add("file2Description", file2Description)
                .add("connectedProductIds", connectedProductIds)
                .add("manufacturer", manufacturer)
                .add("inStock", inStock)
                .add("inStockType", inStockType)
                .add("inStockAmount", inStockAmount)
                .add("measureUnit", measureUnit)
                .toString();
    }
}
