package de.raphaelmuesseler.financer.client.javafx.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.AttachmentWithContent;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.awt.*;
import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Executors;

class TransactionDialog extends FinancerDialog<Transaction> {

    private DoubleField amountField;
    private ComboBox<CategoryTree> categoryComboBox;
    private TextField productField;
    private TextField purposeField;
    private TextField shopField;
    private JFXDatePicker valueDateField;
    private ListView<AttachmentWithContent> attachmentListView;
    private BaseCategory categories;

    TransactionDialog(Transaction transaction, BaseCategory categories) {
        super(transaction);

        this.categories = categories;

        this.setHeaderText(I18N.get("transaction"));

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);

        this.prepareDialogContent();
    }

    @Override
    protected Node setDialogContent() {
        HBox hBox = new HBox();
        hBox.setSpacing(15);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(80);
        gridPane.setVgap(8);

        gridPane.add(new Label(I18N.get("amount")), 0, 0);
        this.amountField = new DoubleField();
        this.amountField.setId("amountTextField");
        gridPane.add(this.amountField, 1, 0);

        gridPane.add(new Label(I18N.get("category")), 0, 1);
        this.categoryComboBox = new ComboBox<>();
        this.categoryComboBox.setId("categoryComboBox");
        this.categoryComboBox.setPlaceholder(new Label(I18N.get("selectCategory")));

        gridPane.add(this.categoryComboBox, 1, 1);

        gridPane.add(new Label(I18N.get("product")), 0, 2);
        this.productField = new TextField();
        this.productField.setId("productTextField");
        gridPane.add(this.productField, 1, 2);

        gridPane.add(new Label(I18N.get("purpose")), 0, 3);
        this.purposeField = new TextField();
        this.purposeField.setId("purposeTextField");
        gridPane.add(purposeField, 1, 3);

        gridPane.add(new Label(I18N.get("shop")), 0, 4);
        this.shopField = new TextField();
        this.shopField.setId("shopTextField");
        gridPane.add(this.shopField, 1, 4);

        gridPane.add(new Label(I18N.get("product")), 0, 5);
        this.valueDateField = new JFXDatePicker();
        this.valueDateField.setId("valueDatePicker");
        gridPane.add(this.valueDateField, 1, 5);

        hBox.getChildren().add(gridPane);

        VBox attachmentsContainer = new VBox();
        attachmentsContainer.setSpacing(10);
        attachmentsContainer.setPrefHeight(200);
        attachmentsContainer.getChildren().add(new Label(I18N.get("attachments")));

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        JFXButton uploadAttachmentBtn = new JFXButton(I18N.get("upload"), fontAwesome.create(FontAwesome.Glyph.PLUS));
        JFXButton openFileBtn = new JFXButton(I18N.get("openFile"), fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN));
        JFXButton deleteAttachmentBtn = new JFXButton(I18N.get("delete"), fontAwesome.create(FontAwesome.Glyph.TRASH));

        uploadAttachmentBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(I18N.get("uploadAttachment"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(I18N.get("documents"), "*.jpg", "*.png", "*.doc", "*.docx", "*.pdf")
            );
            File attachmentFile = fileChooser.showOpenDialog(uploadAttachmentBtn.getContextMenu());
            onUploadFile(attachmentFile);
        });

        openFileBtn.setOnAction(event -> onOpenFile());

        deleteAttachmentBtn.setOnAction(event -> {

        });

        HBox toolBox = new HBox();
        toolBox.setSpacing(8);
        toolBox.getChildren().add(uploadAttachmentBtn);
        toolBox.getChildren().add(openFileBtn);
        toolBox.getChildren().add(deleteAttachmentBtn);

        this.attachmentListView = new ListView<>();
        this.attachmentListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AttachmentWithContent item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        attachmentsContainer.getChildren().add(toolBox);
        attachmentsContainer.getChildren().add(this.attachmentListView);

        hBox.getChildren().add(attachmentsContainer);

        return hBox;
    }

    @Override
    protected void prepareDialogContent() {
        categories.traverse(treeItem -> {
            if (!treeItem.isRoot() && (((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.VARIABLE_EXPENSES ||
                    ((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.VARIABLE_REVENUE)) {
                categoryComboBox.getItems().add((CategoryTree) treeItem);
            }
        });
        this.categoryComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryTree item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(Formatter.formatCategoryName(item.getValue()));
                } else {
                    setText(null);
                }
            }
        });

        if (this.getValue() != null) {
            this.amountField.setText(String.valueOf(this.getValue().getAmount()));
            this.categoryComboBox.getSelectionModel().select((CategoryTree) TreeUtil.getByValue(this.categories,
                    this.getValue().getCategoryTree(), Comparator.comparingInt(Category::getId)));
            this.productField.setText(this.getValue().getProduct());
            this.purposeField.setText(this.getValue().getPurpose());
            this.shopField.setText(this.getValue().getShop());
            this.valueDateField.setValue(this.getValue().getValueDate());

            this.attachmentListView.getItems().addAll(this.getValue().getAttachments());
        }
    }

    @Override
    protected boolean checkConsistency() {
        boolean result = true;

        if (this.categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            setErrorMessage(I18N.get("selectCategory"));
            result = false;
        }

        if (Double.valueOf(this.amountField.getText()).equals(0.0)) {
            setErrorMessage(I18N.get("selectValidAmount"));
            result = false;
        }

        return result;
    }

    @Override
    protected Transaction onConfirm() {
        if (this.getValue() == null) {
            this.setValue(new Transaction(-1, Double.valueOf(this.amountField.getText()),
                    this.categoryComboBox.getSelectionModel().getSelectedItem(), this.productField.getText(),
                    this.purposeField.getText(), this.valueDateField.getValue(), this.shopField.getText()));
        } else {
            this.getValue().setAmount(Double.valueOf(this.amountField.getText()));
            this.getValue().setCategoryTree(this.categoryComboBox.getSelectionModel().getSelectedItem());
            this.getValue().setProduct(this.productField.getText());
            this.getValue().setPurpose(this.purposeField.getText());
            this.getValue().setValueDate(this.valueDateField.getValue());
            this.getValue().setShop(this.shopField.getText());
        }

        return super.onConfirm();
    }

    private void onUploadFile(File attachmentFile) {

        if (attachmentFile != null) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("transaction", this.getValue());
            parameters.put("attachmentFile", attachmentFile);

            byte[] attachmentContent = new byte[(int) attachmentFile.length()];
            try (BufferedInputStream bufferedReader = new BufferedInputStream(new FileInputStream(attachmentFile))) {
                if (bufferedReader.read(attachmentContent, 0, attachmentContent.length) != -1) {
                    parameters.put("content", attachmentContent);
                    Executors.newCachedThreadPool().execute(new ServerRequestHandler((User) LocalStorageImpl.getInstance().readObject("user"),
                            "uploadTransactionAttachment", parameters, new JavaFXAsyncConnectionCall() {
                        @Override
                        public void onSuccess(ConnectionResult result) {
                            attachmentListView.getItems().add((AttachmentWithContent) result.getResult());
                            getValue().getAttachments().add((AttachmentWithContent) result.getResult());
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            JavaFXAsyncConnectionCall.super.onFailure(exception);
                        }
                    }));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onOpenFile() {
        File file = new File(LocalStorageImpl.LocalStorageFile.TRANSACTIONS.getFile().getParent() +
                "/transactions/" + this.getValue().getId() + "/attachments/" +
                this.attachmentListView.getSelectionModel().getSelectedItem().getName());

        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                new FinancerExceptionDialog("Financer", e).showAndWait();
            }
        } else {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("id", this.attachmentListView.getSelectionModel().getSelectedItem().getId());

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(
                    (User) LocalStorageImpl.getInstance().readObject("user"),
                    "getAttachment", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        fileOutputStream.write(((AttachmentWithContent) result.getResult()).getContent());
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        new FinancerExceptionDialog("Financer", e).showAndWait();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
            }));
        }
    }
}