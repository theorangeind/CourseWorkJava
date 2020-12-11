package program;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import program.classes.Process;
import program.classes.Resource;

import java.util.ArrayList;

public class Controller
{
    /*--Panes--*/
    @FXML
    TabPane queuesPane;
    @FXML
    BorderPane runningPane;

    /*--Tabs--*/
    @FXML
    Tab tabRejected;
    @FXML
    Tab tabFinished;

    /*--ToolBars--*/
    @FXML
    ToolBar barResources;

    /*--Control Buttons--*/
    @FXML
    ToggleButton btnPause;
    @FXML
    Button btnNext;
    @FXML
    Button btnRun;
    @FXML
    Button btnStop;
    @FXML
    Button btnCreate;

    /*--Initial Values settings fields--*/
    @FXML
    TextField txtMemory;
    @FXML
    TextField txtResources;

    /*--Sliders--*/
    @FXML
    Slider sldTps;
    @FXML
    Slider sldErrors;

    /*--Labels--*/
    @FXML
    Label lblTps;
    @FXML
    Label lblErrors;

    /*--Checkboxes--*/
    @FXML
    CheckBox chkErrors;
    @FXML
    CheckBox chkGeneration;

    /*--Tables--*/
    TableView tblRunning;
    TableView tblRejected;
    TableView tblFinished;

    ObservableList<Process> tblDataRunning = FXCollections.observableArrayList();
    ObservableList<Process> tblDataRejected = FXCollections.observableArrayList();
    ObservableList<Process> tblDataFinished = FXCollections.observableArrayList();

    ArrayList<ToggleButton> resourceButtons = new ArrayList<>();

    EventHandler handleResourceTableSwitch = new EventHandler()
    {
        @Override
        public void handle(Event event)
        {
            updateTable(Tables.RUNNING);
        }
    };

    @FXML
    private void initialize()
    {
        queuesPane.setTabMinWidth(64);
    }

    public void initControlButtons()
    {
        btnRun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!Main.isRunning())
                {
                    initSystemResources();
                    Main.setupSystem();
                    btnPause.setSelected(false);
                    Main.setPause(false);
                    updateTable(Tables.RUNNING);
                    updateTable(Tables.REJECTED);
                    updateTable(Tables.FINISHED);
                    btnRun.setDisable(true);
                    btnPause.setDisable(false);
                    btnStop.setDisable(false);
                    btnCreate.setDisable(false);
                }
            }
        });

        btnPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!Main.isFirstRun())
                {
                    Main.setPause(btnPause.isSelected());
                    btnNext.setDisable(!btnPause.isSelected());
                }
            }
        });

        btnNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.getSystemClock().nextTick();
            }
        });

        btnStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(Main.isRunning()) Main.finishWork();
                btnPause.setSelected(false);
                btnRun.setDisable(false);
                btnCreate.setDisable(true);
                btnPause.setDisable(true);
                btnNext.setDisable(true);
                btnStop.setDisable(true);
                updateTable(Tables.RUNNING);
            }
        });

        btnCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openCreateProcessWindow();
            }
        });

        btnPause.setDisable(true);
        btnStop.setDisable(true);
        btnCreate.setDisable(true);
        btnNext.setDisable(true);
    }

    public void initTextFields()
    {
        txtMemory.setTextFormatter(new TextFormatter<Number>(change -> numberFilter(change)));
        txtMemory.setOnAction(event -> validateNumberField(txtMemory, 1024, 16384, 2048));
        txtMemory.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                validateNumberField(txtMemory, 1024, 16384, 2048);
            }
        });

        txtResources.setTextFormatter(new TextFormatter<Number>(change -> numberFilter(change)));
        txtResources.setOnAction(event -> validateNumberField(txtResources, 3, 5, 3));
        txtResources.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                validateNumberField(txtResources, 3, 5, 3);
            }
        });
    }

    public void initSliders()
    {
        sldTps.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                Configuration.setClockTps(newValue.intValue());
                lblTps.setText(String.valueOf(Configuration.getClockTps()));
            }
        });

        sldErrors.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                Configuration.setProcessTerminationChance(newValue.intValue());
                lblErrors.setText(String.valueOf(Configuration.getProcessTerminationChance()));
            }
        });
    }

    public void initCheckBoxes()
    {
        chkGeneration.setSelected(true);
        chkErrors.setSelected(true);
        chkGeneration.setOnAction(event -> Configuration.setGenerateRandomProcesses(chkGeneration.isSelected()));
        chkErrors.setOnAction(event -> Configuration.setGenerateErrors(chkErrors.isSelected()));
    }

    private void initSystemResources()
    {
        try
        {
            Configuration.setMemoryVolume(Integer.parseInt(txtMemory.getText()));
            Configuration.setResourcesCount(Integer.parseInt(txtResources.getText()));
        }
        catch (NumberFormatException e)
        {
            Configuration.setDefaultResources();
        }
    }

    private void openCreateProcessWindow()
    {
        TextInputDialog dialog = new TextInputDialog("New Process");
        dialog.setTitle("Creating New Process");
        dialog.setHeaderText("Enter the name of a new process.\nUse only letters, numbers, '-' and '_'");
        dialog.setContentText("Process name:");

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        Button btnCancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

        dialog.setOnCloseRequest(event -> {
            event.consume();
        });

        dialog.show();

        btnOk.addEventFilter(ActionEvent.ACTION, event -> {
            String name = dialog.getEditor().getText();
            if (!name.isEmpty())
            {
                boolean correct = true;
                char[] arr = name.toCharArray();
                for (char c : arr)
                {
                    if (!Character.isLetter(c) && !Character.isDigit(c) && c != '-' && c != '_' && c != ' ')
                    {
                        correct = false;
                        break;
                    }
                }
                if (correct)
                {
                    dialog.setOnCloseRequest(e -> { });
                    dialog.close();
                    Main.getTaskScheduler().scheduleTask(name);
                }
                else
                {
                    dialog.setHeaderText("You should use only\nletters, numbers, '-' and '_'");
                }
            }
            else
            {
                dialog.setHeaderText("You should use only\nletters, numbers, '-' and '_'");
            }
        });

        btnCancel.addEventFilter(ActionEvent.ACTION, event -> {
            dialog.setOnCloseRequest(e -> { });
            dialog.close();
        });
    }

    private TextFormatter.Change numberFilter(TextFormatter.Change change)
    {
        if (change.isDeleted())
        {
            return change;
        }

        String text = change.getControlNewText();
        if (!text.matches("[0-9]+"))
        {
            return null;
        }

        return change;
    }

    private void validateNumberField(TextField field, int minVal, int maxVal, int defVal)
    {
        try
        {
            String oldText = field.getText();
            int old = Integer.parseInt(oldText);
            if(old > maxVal) field.setText(String.valueOf(maxVal));
            else if(old < minVal) field.setText(String.valueOf(minVal));
            else field.setText(oldText);
        }
        catch (Exception e) {field.setText(String.valueOf(defVal));}
    }

    public void initResourcesBar(ArrayList<Resource> resources)
    {
        resourceButtons.clear();
        resourceButtons.add(new ToggleButton("CPU"));
        resourceButtons.get(0).setOnAction(handleResourceTableSwitch);
        for (Resource item : resources)
        {
            resourceButtons.add(new ToggleButton(item.getName()));
            resourceButtons.get(resourceButtons.size() - 1).setOnAction(handleResourceTableSwitch);
        }

        barResources.getItems().setAll(resourceButtons);
        ToggleGroup group = new ToggleGroup();
        barResources.getItems().forEach(item ->
        {
            ((ToggleButton) item).setToggleGroup(group);
            ((ToggleButton) item).setMinWidth(64);
        });

        if(resourceButtons.size() > 0) resourceButtons.get(0).setSelected(true);
    }

    public void initBaseTabs()
    {
        tblRunning = generateProcessTable();
        runningPane.setCenter(tblRunning);

        tblRejected = generateTableRejected();
        tabRejected.setContent(tblRejected);

        tblFinished = generateTableFinished();
        tabFinished.setContent(tblFinished);
    }

    public int getCurrentResourceTableIndex()
    {
        for (int i = 0; i < resourceButtons.size(); i++)
        {
            if(resourceButtons.get(i).isSelected())
            {
                return i;
            }
        }

        return 0;
    }

    public void updateTable(Tables table)
    {
        if(table == Tables.RUNNING)
        {
            int currentResourceTableIndex = getCurrentResourceTableIndex();

            if(currentResourceTableIndex == 0)
            {
                //requesting CPU task list
                tblDataRunning.setAll(Main.getTaskScheduler().getCPUTaskList());
                tblRunning.setItems(tblDataRunning);
            }
            else
            {
                //requesting current resource task list
                for (int i = 1; i <= Configuration.getResourcesCount(); i++)
                {
                    if(currentResourceTableIndex == i)
                    {
                        tblDataRunning.setAll(Main.getTaskScheduler().getResourceTaskList(i-1));
                    }
                }
            }
        }
        else if(table == Tables.REJECTED)
        {
            tblDataRejected.setAll(Main.getTaskScheduler().getRejectsList());
            tblRejected.setItems(tblDataRejected);
        }
        else if(table == Tables.FINISHED)
        {
            tblDataFinished.setAll(Main.getTaskScheduler().getCompletedList());
            tblFinished.setItems(tblDataFinished);

        }
    }

    private static TableView<Process> generateProcessTable()
    {
        TableColumn<Process, Integer> tblColId;
        TableColumn<Process, String> tblColName;
        TableColumn<Process, Integer> tblColPriority;
        TableColumn<Process, String> tblColStatus;
        TableColumn<Process, Integer> tblColMemory;

        TableView<Process> tbl = new TableView<>();

        tblColId = new TableColumn<>("ID");
        tblColId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl.getColumns().add(tblColId);

        tblColName = new TableColumn<>("Name");
        tblColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbl.getColumns().add(tblColName);

        tblColPriority = new TableColumn<>("Priority");
        tblColPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        tbl.getColumns().add(tblColPriority);

        tblColStatus = new TableColumn<>("Status");
        tblColStatus.setCellValueFactory(new PropertyValueFactory<>("state"));
        tbl.getColumns().add(tblColStatus);

        tblColMemory = new TableColumn<>("Memory Usage");
        tblColMemory.setCellValueFactory(new PropertyValueFactory<>("memoryUsage"));
        tbl.getColumns().add(tblColMemory);

        return tbl;
    }

    private static TableView<Process> generateTableRejected()
    {
        TableColumn<Process, Integer> tblColId;
        TableColumn<Process, String> tblColName;
        TableColumn<Process, Integer> tblColMemory;

        TableView<Process> tbl = new TableView<>();

        tblColId = new TableColumn<>("ID");
        tblColId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl.getColumns().add(tblColId);

        tblColName = new TableColumn<>("Name");
        tblColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbl.getColumns().add(tblColName);

        tblColMemory = new TableColumn<>("Memory Usage");
        tblColMemory.setCellValueFactory(new PropertyValueFactory<>("memoryUsage"));
        tbl.getColumns().add(tblColMemory);

        return tbl;
    }

    private static TableView<Process> generateTableFinished()
    {
        TableColumn<Process, Integer> tblColId;
        TableColumn<Process, String> tblColName;
        TableColumn<Process, Integer> tblColMemory;
        TableColumn<Process, Integer> tblColTime;
        TableColumn<Process, String> tblColStatus;

        TableView<Process> tbl = new TableView<>();

        tblColId = new TableColumn<>("ID");
        tblColId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl.getColumns().add(tblColId);

        tblColName = new TableColumn<>("Name");
        tblColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbl.getColumns().add(tblColName);

        tblColMemory = new TableColumn<>("Memory Usage");
        tblColMemory.setCellValueFactory(new PropertyValueFactory<>("memoryUsage"));
        tbl.getColumns().add(tblColMemory);

        tblColTime = new TableColumn<>("Run Time");
        tblColTime.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        tbl.getColumns().add(tblColTime);

        tblColStatus = new TableColumn<>("Status");
        tblColStatus.setCellValueFactory(new PropertyValueFactory<>("state"));
        tbl.getColumns().add(tblColStatus);

        return tbl;
    }

    public enum Tables
    {
        RUNNING,
        REJECTED,
        FINISHED
    }
}
