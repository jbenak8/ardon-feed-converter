package cz.jbenak.tezamv.ardonFeedConverter.dialogs;

import cz.jbenak.tezamv.ardonFeedConverter.Main;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class CatsMappingDialogController implements Initializable {

    public enum CatsType {
        LUMA, ARDON
    }

    private Stage dialogStage;
    private Path catFile;
    private ObservableList<CategoryMappingItem> tableData = FXCollections.emptyObservableList();
    @FXML
    private MFXTableView<CategoryMappingItem> table;
    @FXML
    private Label title;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTypeAndLoad(CatsType type) {
        switch (type) {
            case LUMA -> {
                title.setText("Upravit mapování kategorií pro dodavatele Luma Trading s.r.o");
                catFile = Paths.get("conf/luma-categories.txt");
            }
            case ARDON -> {
                title.setText("Upravit mapování kategorií pro dodavatele ARDON Safety s.r.o");
                catFile = Paths.get("conf/ardon-categories.txt");
            }
        }
        loadData();
    }

    private void loadData() {
        try {
            final List<CategoryMappingItem> loadedData = new ArrayList<>();
            try (Stream<String> stream = Files.lines(catFile, StandardCharsets.UTF_8)) {
                stream.forEach(line -> {
                    String[] keyVal = line.split("=");
                    CategoryMappingItem item = new CategoryMappingItem();
                    item.setTheirCategory(keyVal[0]);
                    item.setOurCategory(keyVal[1]);
                    loadedData.add(item);
                });
            }
            tableData = FXCollections.observableArrayList(loadedData);
            prepareTable();
        } catch (Exception e) {
            Main.getInstance().showErrorDialog("Interní chyba", "Nelze načíst soubor s kategoriemi", e);
        }
    }

    @FXML
    private void addRow() {
        EditDialog dialog = new EditDialog(dialogStage);
        CategoryMappingItem item = dialog.showDialog();
        if (item != null) {
            tableData.add(tableData.size(), item);
            table.scrollToLast();
        }
    }

    @FXML
    private void updateRow() {
        if (!table.getSelectionModel().getSelectedValues().isEmpty()) {
            CategoryMappingItem selectedItem = table.getSelectionModel().getSelectedValues().get(table.getSelectionModel().getSelectedValues().size() - 1);
            int selectedItemIndex = tableData.indexOf(selectedItem);
            EditDialog dialog = new EditDialog(dialogStage, selectedItem);
            CategoryMappingItem updatedItem = dialog.showDialog();
            if (updatedItem != null && !updatedItem.equals(selectedItem)) {
                tableData.remove(selectedItem);
                tableData.add(selectedItemIndex, updatedItem);
            }
            table.scrollTo(selectedItemIndex);
        }
    }

    @FXML
    private void deleteRow() {
        if (!table.getSelectionModel().getSelectedValues().isEmpty()) {
            table.getSelectionModel().getSelectedValues().forEach(itm -> tableData.remove(itm));
        }
    }

    @FXML
    private void save() {
        if (!tableData.isEmpty()) {
            StringBuilder content = new StringBuilder();
            tableData.forEach(itm -> {
                content.append(itm.getTheirCategory()).append("=").append(itm.getOurCategory()).append("\n");
            });
            try {
                Files.writeString(catFile, content.toString(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                Main.getInstance().showErrorDialog("Interní chyba", "Nelze uložit soubor s kategoriemi", e);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @SuppressWarnings("unchecked")
    private void prepareTable() {
        ObservableList<MFXTableColumn<CategoryMappingItem>> columns = table.getTableColumns();

        if (columns.isEmpty()) {
            MFXTableColumn<CategoryMappingItem> theirCatColumn = new MFXTableColumn<>("Jejich kategorie", true, Comparator.comparing(CategoryMappingItem::getTheirCategory));
            MFXTableColumn<CategoryMappingItem> ourCatColumn = new MFXTableColumn<>("Naše kategorie", true, Comparator.comparing(CategoryMappingItem::getOurCategory));

            ourCatColumn.setRowCellFactory(country -> new MFXTableRowCell<>(CategoryMappingItem::getOurCategory));
            theirCatColumn.setRowCellFactory(country -> new MFXTableRowCell<>(CategoryMappingItem::getTheirCategory));

            table.autosizeColumnsOnInitialization();
            columns.add(theirCatColumn);
            columns.add(ourCatColumn);

            table.setFooterVisible(true);
            table.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            table.getStyleClass().add("content-panel");
            table.getSelectionModel().setAllowsMultipleSelection(true);

            table.getFilters().addAll(
                    new StringFilter<>("Jejich kategorie", CategoryMappingItem::getOurCategory),
                    new StringFilter<>("Naše kategorie", CategoryMappingItem::getTheirCategory)
            );
        }
        table.setItems(tableData);
    }
}
