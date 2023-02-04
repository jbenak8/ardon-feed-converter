package cz.jbenak.tezamv.ardonFeedConverter.mappers;

import cz.jbenak.tezamv.ardonFeedConverter.source.*;
import cz.jbenak.tezamv.ardonFeedConverter.target.TargetShop;
import cz.jbenak.tezamv.ardonFeedConverter.target.TargetShopItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

public class ArdonMapper {

    private final Shop input;
    private final TargetShop output = new TargetShop();

    public ArdonMapper(Shop input) {
        this.input = input;
    }

    public void createOutput() {
        deleteDuplicates();
        mapItems();
    }

    public TargetShop getTarget() {
        return output;
    }

    private void deleteDuplicates() {
        HashSet<ShopItem> clean = new HashSet<>(input.getItems());
        input.setItems(clean.stream().toList());
    }

    private void mapItems() {
        List<TargetShopItem> items = new ArrayList<>();
        input.getItems().forEach(source -> {
            TargetShopItem itm = new TargetShopItem();
            itm.setItemCode(source.getItemCode());
            itm.setItemGroupCode(source.getItemGroupCode());
            itm.setProductName(source.getProductName());
            itm.setEan(source.getEan());
            itm.setCategories(getCategories(source.getInCategories()));
            itm.setColor(source.getColor());
            itm.setSize(source.getSize());
            itm.setDescription(source.getDescription());
            itm.setAdditionalDescription(source.getAdditionalDescription());
            itm.setVat(source.getVat());
            itm.setPriceVat(source.getPriceVat());
            itm.setImages(getImageLink(source.getImages().getImages()));
            itm.setParams(getParams(source.getParams().getParam()));
            getFiles(source, itm);
            itm.setConnectedProductIds(getConnectedProducts(source.getConnectAndGo()));
            itm.setManufacturer("ARDON SAFETY s.r.o.");
            double amountInStock = 0.0;
            if (source.getAmountInStock() != null && source.getAmountInStock().matches("\\d+\\.\\d+")) {
                amountInStock = Double.parseDouble(source.getAmountInStock());
            }
            itm.setInStock(amountInStock > 0);
            itm.setInStockAmount(amountInStock);
            itm.setInStockType(amountInStock > 0 ? "Skladem u dodavatele" : "na cestě");
            itm.setMeasureUnit(source.getQuantityInStock().substring(source.getQuantityInStock().length() - 2).trim().toLowerCase());
            items.add(itm);
        });
        output.setItems(items);
    }

    private String getCategories(List<CategoryPath> inCategories) {
        if (inCategories.isEmpty()) {
            return "";
        } else {
            List<CategoryPath> clean = inCategories.stream().filter(itm -> !itm.getCategoryPath().toLowerCase().contains("kolekce")).toList();
            return clean.isEmpty() ? "ARDON | " + inCategories.get(0).getCategoryPath() : "ARDON | " + clean.get(0).getCategoryPath();
        }
    }

    private String getImageLink(List<String> images) {
        StringJoiner sj = new StringJoiner("|");
        if (images != null) {
            images.forEach(sj::add);
        }
        return sj.toString();
    }

    private String getParams(List<Param> params) {
        if (params == null) {
            return "";
        } else {
            StringJoiner paramJoiner = new StringJoiner(" | ");
            params.forEach(param -> {
                if (param.getParamName() != null) {
                    paramJoiner.add(param.getParamName() + "=" + param.getParamValue());
                }
            });
            return paramJoiner.toString();
        }
    }

    private void getFiles(ShopItem source, TargetShopItem target) {
        if (source.getFiles().getFiles() != null && !source.getFiles().getFiles().isEmpty()) {
            List<String> files = source.getFiles().getFiles();
            if (files.size() == 1 && files.get(0) != null) {
                target.setFile1(files.get(0));
                target.setFile1Description(target.getFile1().substring(target.getFile1().lastIndexOf("/") + 1));
            }
            if (files.size() > 1 && files.get(0) != null && files.get(1) != null) {
                target.setFile1(files.get(0));
                target.setFile1Description(target.getFile1().substring(target.getFile1().lastIndexOf("/") + 1));
                target.setFile2(files.get(1));
                target.setFile2Description(target.getFile2().substring(target.getFile2().lastIndexOf("/") + 1));
            }
        }
    }

    private String getConnectedProducts(ProductWrapper products) {
        if (products == null) {
            return "";
        } else {
            StringJoiner conn = new StringJoiner("|");
            products.getProducts().forEach(itm -> conn.add(itm.getName()));
            return conn.toString();
        }
    }
}
