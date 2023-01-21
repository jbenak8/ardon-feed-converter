package cz.jbenak.tezamv.ardonFeedConverter;

import cz.jbenak.tezamv.ardonFeedConverter.mappers.ArdonMapper;
import cz.jbenak.tezamv.ardonFeedConverter.source.Shop;
import cz.jbenak.tezamv.ardonFeedConverter.luma.LumaShop;
import cz.jbenak.tezamv.ardonFeedConverter.target.TargetShop;
import cz.jbenak.tezamv.ardonFeedConverter.target.TargetShopItem;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        if (args.length != 0 && Objects.equals(args[0], "luma")) {
            loadLuma();
        } else if (args.length != 0 && Objects.equals(args[0], "img")) {
            loadImagesArdon();
        } else {
            File xml = new File("C:/TMP/ardon.xml");
            try {
                JAXBContext context = JAXBContext.newInstance(Shop.class);
                //System.out.println(context.toString());
                Unmarshaller unmarshaller = context.createUnmarshaller();
                unmarshaller.setEventHandler(new DefaultValidationEventHandler());
                Shop shop = (Shop) unmarshaller.unmarshal(xml);
                ArdonMapper mapper = new ArdonMapper(shop);
                mapper.createOutput();
                TargetShop outputShop = mapper.getTarget();
                /*HashSet<String> cats = new HashSet<>();
                outputShop.getItems().forEach(itm -> cats.add(itm.getCategories()));
                cats.forEach(System.out::println);*/
                HashSet<String> noDupl = new HashSet<>();
                outputShop.getItems().forEach(targetShopItem -> noDupl.add(targetShopItem.getCategories()));
                //noDupl.forEach(System.out::println);
                Map<String, String> catsMap = new HashMap<>();
                try (Stream<String> stream = Files.lines(Paths.get("conf/ardon-categories.txt"), StandardCharsets.UTF_8)) {
                    stream.forEach(line -> {
                        String[] keyVal = line.split("=");
                        catsMap.put(keyVal[0].trim(), keyVal[1].trim());
                    });
                }
                outputShop.getItems().forEach(itm -> itm.setCategories(catsMap.get(itm.getCategories())));
                JAXBContext targetContext = JAXBContext.newInstance(TargetShop.class);
                Marshaller marshaller = targetContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(outputShop, new File("C:/TMP/ardon_eshoprychle.xml"));
                Path of = Path.of("C:/TMP/ardon_eshoprychle.xml");
                String cdataCorrection = Files.readString(of, StandardCharsets.UTF_8);
                cdataCorrection = cdataCorrection.replaceAll("&lt;", "<");
                cdataCorrection = cdataCorrection.replaceAll("&gt;", ">");
                Files.writeString(of, cdataCorrection);
                /*InputStream in = new URL("https://img.ardon.cz/fotocache/bigorig/images/produkty/H8107_005.jpg").openStream();
                Files.copy(in, Paths.get("C:/TMP/obr.jpg"), StandardCopyOption.REPLACE_EXISTING);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadLuma() {
        try {
            File src = new File("C:/TMP/luma.xml");
            JAXBContext context = JAXBContext.newInstance(LumaShop.class);
            //System.out.println(context.toString());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new DefaultValidationEventHandler());
            LumaShop shop = (LumaShop) unmarshaller.unmarshal(src);
            /*HashSet<String> cats = new HashSet<>();
            shop.getItems().forEach(itm -> cats.add(itm.getCategory()));
            /cats.forEach(itm -> System.out.println(itm + "="));*/
            Map<String, String> catsMap = new HashMap<>();
            //System.out.println(Files.readString(Path.of("conf/luma-categories.txt")));
            try (Stream<String> stream = Files.lines(Paths.get("conf/luma-categories.txt"), StandardCharsets.UTF_8)) {
                stream.forEach(line -> {
                    String[] keyVal = line.split("=");
                    catsMap.put(keyVal[0].trim(), keyVal[1].trim());
                });
            }
            shop.getItems().forEach(itm -> itm.setCategory(catsMap.get(itm.getCategory())));
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(shop, new File("C:/TMP/luma_uprava.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadImagesArdon() {
        File xml = new File("C:/TMP/ardon_eshoprychle.xml");
        try {
            JAXBContext context = JAXBContext.newInstance(TargetShop.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new DefaultValidationEventHandler());
            TargetShop loaded = (TargetShop) unmarshaller.unmarshal(xml);
            Set<String> images = new HashSet<>();
            loaded.getItems().forEach(itm -> {
                String[] imgarray = itm.getImages().split("\\|");
                images.addAll(Arrays.asList(imgarray));
                itm.setImages(itm.getImages().replaceAll("https://img.ardon.cz/fotocache/bigorig/images/produkty/", "ardon/"));
            });
            System.out.println("Počet obrázků ke stažení: " + images.size());
            int index = 1;
            for(String img:images) {
                try {
                    System.out.println("Stahuji " + index + " z " + images.size() + ": " + img);
                    String fileName = img.substring(img.lastIndexOf("/") + 1);
                    InputStream in = new URL(img).openStream();
                    Files.copy(in, Paths.get("C:/TMP/ARDON_IMG/" + fileName), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Nemohu stáhnout: " + img);
                }
                index++;
            }
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(loaded, new File("C:/TMP/ardon_eshoprychle.xml"));
            Path of = Path.of("C:/TMP/ardon_eshoprychle.xml");
            String cdataCorrection = Files.readString(of, StandardCharsets.UTF_8);
            cdataCorrection = cdataCorrection.replaceAll("&lt;", "<");
            cdataCorrection = cdataCorrection.replaceAll("&gt;", ">");
            cdataCorrection = cdataCorrection.replaceAll("&nbsp;", " ");
            Files.writeString(of, cdataCorrection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}