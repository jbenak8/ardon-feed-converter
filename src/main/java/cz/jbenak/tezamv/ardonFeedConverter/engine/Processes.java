package cz.jbenak.tezamv.ardonFeedConverter.engine;

import cz.jbenak.tezamv.ardonFeedConverter.MainController;
import cz.jbenak.tezamv.ardonFeedConverter.luma.LumaShop;
import cz.jbenak.tezamv.ardonFeedConverter.mappers.ArdonMapper;
import cz.jbenak.tezamv.ardonFeedConverter.source.Shop;
import cz.jbenak.tezamv.ardonFeedConverter.target.TargetShop;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class Processes {

    private final MainController controller;
    private InputStream currentInputStream;
    private Stream<String> currentStream;
    private boolean cancelled;

    public Processes(MainController controller) {
        this.controller = controller;
    }

    public void cancelProcess() throws Exception {
        if (currentStream != null) {
            currentStream.close();
        }
        if (currentInputStream != null) {
            currentInputStream.close();
        }
        cancelled = true;
    }

    public void downloadFile(String fileUrl, String destinationFile) throws Exception {
        currentInputStream = new URL(fileUrl).openStream();
        Files.copy(currentInputStream, Paths.get(destinationFile), StandardCopyOption.REPLACE_EXISTING);
    }

    private Shop unmarshallArdon() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Shop.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        return (Shop) unmarshaller.unmarshal(new File(controller.getArdonDownloadedFile()));
    }

    private TargetShop unmarshallProcessedArdon() throws Exception {
        JAXBContext context = JAXBContext.newInstance(TargetShop.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        return (TargetShop) unmarshaller.unmarshal(new File(controller.getArdonProcessedFile()));
    }

    private void marshallArdon(TargetShop toSave) throws Exception {
        JAXBContext context = JAXBContext.newInstance(TargetShop.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(toSave, new File(controller.getArdonProcessedFile()));
    }

    private LumaShop unmarshallLuma() throws Exception {
        JAXBContext context = JAXBContext.newInstance(LumaShop.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        return (LumaShop) unmarshaller.unmarshal(new File(controller.getLumaDownloadedFile()));
    }

    private void marshallLuma(LumaShop toSave) throws Exception {
        JAXBContext context = JAXBContext.newInstance(LumaShop.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(toSave, new File(controller.getLumaProcessedFile()));
    }

    private void correctCData(String xmlFile) throws Exception {
        Path of = Path.of(xmlFile);
        String cdataCorrection = Files.readString(of, StandardCharsets.UTF_8);
        cdataCorrection = cdataCorrection.replaceAll("&lt;", "<");
        cdataCorrection = cdataCorrection.replaceAll("&gt;", ">");
        Files.writeString(of, cdataCorrection);
    }

    public void loadArdon() throws Exception {
        cancelled = false;
        Platform.runLater(() -> controller.getInfoMessage().setText("Zpracovávám stažený XML feed Ardonu"));
        controller.appendTextToLog("Zpracovávám stažený XML feed dodavatele Ardon.");
        ArdonMapper mapper = new ArdonMapper(unmarshallArdon());
        mapper.createOutput();
        TargetShop outputShop = mapper.getTarget();
        Map<String, String> catsMap = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get("conf/ardon-categories.txt"), StandardCharsets.UTF_8)) {
            currentStream = stream;
            stream.forEach(line -> {
                String[] keyVal = line.split("=");
                catsMap.put(keyVal[0].trim(), keyVal[1].trim());
            });
        }
        if (!cancelled) {
            outputShop.getItems().forEach(itm -> itm.setCategories(catsMap.get(itm.getCategories())));
            controller.appendTextToLog("Bude uloženo " + outputShop.getItems().size() + " položek.");
            marshallArdon(outputShop);
            correctCData(controller.getArdonProcessedFile());
        }
    }

    public void loadLuma() throws Exception {
        cancelled = false;
        controller.appendTextToLog("Zahajuji zpracování XML feedu dodavatele Luma.");
        Platform.runLater(() -> controller.getInfoMessage().setText("Zpracovávám XML feed."));
        LumaShop shop = unmarshallLuma();
        Map<String, String> catsMap = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get("conf/luma-categories.txt"), StandardCharsets.UTF_8)) {
            currentStream = stream;
            stream.forEach(line -> {
                String[] keyVal = line.split("=");
                catsMap.put(keyVal[0].trim(), keyVal[1].trim());
            });
        }
        if (!cancelled) {
            shop.getItems().forEach(itm -> itm.setCategory(catsMap.get(itm.getCategory())));
            controller.appendTextToLog("Bude uloženo " + shop.getItems().size() + " položek.");
            marshallLuma(shop);
            correctCData(controller.getLumaProcessedFile());
        }
    }

    public void loadImagesArdon(boolean processOnly) throws Exception {
        cancelled = false;
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám seznam obrázků"));
        controller.appendTextToLog("Načítám seznam obrázků.");
        TargetShop loaded = unmarshallProcessedArdon();
        Set<String> images = new HashSet<>();
        loaded.getItems().forEach(itm -> {
            String[] imgarray = itm.getImages().split("\\|");
            images.addAll(Arrays.asList(imgarray));
            itm.setImages(itm.getImages().replaceAll(controller.getArdonImagesUrlPath(), controller.getArdonImagesUrlPathReplaceTo()));
        });
        if (processOnly) {
            controller.appendTextToLog("Obrázky nebudou staženy. Pouze se přepíše adresa ve feedu.");
            controller.appendTextToLog("!!! PRO STAŽENÍ OBRÁZKŮ JE NUTNO ZNOVU STÁHNOUT CELÝ FEED !!!");
        } else {
            controller.appendTextToLog("Počet obrázků ke stažení: " + images.size());
            int index = 1;
            for (String img : images) {
                if (!cancelled) {
                    try {
                        int finalIndex = index;
                        Platform.runLater(() -> controller.getInfoMessage().setText("Stahuji " + finalIndex + " z " + images.size() + ": " + img));
                        controller.appendTextToLog("Stahuji " + index + " z " + images.size() + ": " + img);
                        String fileName = img.substring(img.lastIndexOf("/") + 1);
                        downloadFile(img, controller.getArdonImagesFolder() + "/" + fileName);
                        Platform.runLater(() -> controller.getProgressBar().setProgress((double) finalIndex / (double) images.size()));
                    } catch (IOException e) {
                        //Optional - by ardon are most common malformed URL. e.printStackTrace();
                        controller.appendTextToLog("Nemohu stáhnout: " + img + ":\n" + e.getLocalizedMessage());
                    }
                    index++;
                } else {
                    break;
                }
            }
        }
        if (!cancelled) {
            controller.getProgressBar().setProgress(-1);
            Platform.runLater(() -> controller.getInfoMessage().setText("Zapisuji nové adresy obrázků do feedu"));
            controller.appendTextToLog("Zapisuji nové adresy obrázků do feedu.");
            marshallArdon(loaded);
            correctCData(controller.getArdonProcessedFile());
        }
    }
}
