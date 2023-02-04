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

    private Map<String, String> getCategoryMapping(String mappingConfig) throws Exception {
        Map<String, String> catsMap = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(mappingConfig), StandardCharsets.UTF_8)) {
            currentStream = stream;
            stream.forEach(line -> {
                String[] keyVal = line.split("=");
                catsMap.put(keyVal[0].trim(), keyVal[1].trim());
            });
        }
        return catsMap;
    }

    public void loadArdon() throws Exception {
        cancelled = false;
        Platform.runLater(() -> controller.getInfoMessage().setText("Zpracovávám stažený XML feed Ardonu"));
        controller.appendTextToLog("Zpracovávám stažený XML feed dodavatele Ardon.");
        ArdonMapper mapper = new ArdonMapper(unmarshallArdon());
        mapper.createOutput();
        TargetShop outputShop = mapper.getTarget();
        Map<String, String> catsMap = getCategoryMapping("conf/ardon-categories.txt");
        //TODO FTP upload
        if (!cancelled) {
            outputShop.getItems().forEach(itm -> itm.setCategories(catsMap.get(itm.getCategories())));
            controller.appendTextToLog("Bude uloženo " + outputShop.getItems().size() + " položek.");
            marshallArdon(outputShop);
            correctCData(controller.getArdonProcessedFile());
        }
    }

    public HashSet<String> getUnpairedCatsArdon() throws Exception {
        cancelled = false;
        final HashSet<String> unpaired = new HashSet<>();
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám stažený XML feed Ardonu"));
        controller.appendTextToLog("Načítám stažený XML feed dodavatele Ardon pro zjištění nespárovaných kategorií.");
        ArdonMapper mapper = new ArdonMapper(unmarshallArdon());
        mapper.createOutput();
        TargetShop outputShop = mapper.getTarget();
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám párování kategorií Ardonu"));
        controller.appendTextToLog("Načítám nastavení párování kategorií pro dodavatele ArdonSafety s.r.o.");
        Map<String, String> catsMap = getCategoryMapping("conf/ardon-categories.txt");
        Set<String> theirCats = catsMap.keySet();
        Platform.runLater(() -> controller.getInfoMessage().setText("Připravuji seznam nespárovaných kategorií Ardonu"));
        controller.appendTextToLog("Připravuji seznam nespárovaných kategorií pro dodavatele Ardon Safety s.r.o.");
        outputShop.getItems().forEach(itm -> {
            if (!theirCats.contains(itm.getCategories())) {
                unpaired.add(itm.getCategories());
            }
        });
        return unpaired;
    }

    public void loadLuma() throws Exception {
        cancelled = false;
        controller.appendTextToLog("Zahajuji zpracování XML feedu dodavatele Luma.");
        Platform.runLater(() -> controller.getInfoMessage().setText("Zpracovávám XML feed."));
        LumaShop shop = unmarshallLuma();
        Map<String, String> catsMap = getCategoryMapping("conf/luma-categories.txt");
        //TODO FTP upload
        if (!cancelled) {
            shop.getItems().forEach(itm -> itm.setCategory(catsMap.get(itm.getCategory())));
            controller.appendTextToLog("Bude uloženo " + shop.getItems().size() + " položek.");
            marshallLuma(shop);
            correctCData(controller.getLumaProcessedFile());
        }
    }

    public HashSet<String> getUnpairedCatsLuma() throws Exception {
        cancelled = false;
        final HashSet<String> unpaired = new HashSet<>();
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám stažený XML feed Lumy"));
        controller.appendTextToLog("Načítám stažený XML feed dodavatele Luma pro zjištění nespárovaných kategorií.");
        LumaShop shop = unmarshallLuma();
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám párování kategorií Lumy"));
        controller.appendTextToLog("Načítám nastavení párování kategorií pro dodavatele Luma Trading s.r.o.");
        Map<String, String> catsMap = getCategoryMapping("conf/luma-categories.txt");
        Set<String> theirCats = catsMap.keySet();
        Platform.runLater(() -> controller.getInfoMessage().setText("Připravuji seznam nespárovaných kategorií Lumy"));
        controller.appendTextToLog("Připravuji seznam nespárovaných kategorií pro dodavatele Luma Trading s.r.o.");
        shop.getItems().forEach(itm -> {
            if (!theirCats.contains(itm.getCategory())) {
                unpaired.add(itm.getCategory());
            }
        });
        return unpaired;
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
