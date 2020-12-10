package program;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import program.classes.Process;
import program.classes.Queue;
import program.classes.Resource;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Controller
{
    @FXML
    TabPane queuesPane;
    @FXML
    BorderPane runningPane;
    @FXML
    Tab tabRejected;
    @FXML
    Tab tabFinished;
    @FXML
    ToolBar barResources;

    TableView tblRunning;
    TableView tblRejected;
    TableView tblFinished;

    ArrayList<ToggleButton> resourceButtons = new ArrayList<>();

    ObservableList<Process> tblDataRunning = FXCollections.observableArrayList();
    ObservableList<Process> tblDataRejected = FXCollections.observableArrayList();
    ObservableList<Process> tblDataFinished = FXCollections.observableArrayList();

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

    public void initResourcesBar(ArrayList<Resource> resources)
    {
        resourceButtons.add(new ToggleButton("CPU"));
        resourceButtons.get(0).setOnAction(handleResourceTableSwitch);
        for (Resource item : resources)
        {
            resourceButtons.add(new ToggleButton(item.getName()));
            resourceButtons.get(resourceButtons.size() - 1).setOnAction(handleResourceTableSwitch);
        }

        barResources.getItems().addAll(resourceButtons);
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

                printTasks(tblDataRunning);
                System.out.println("C");
            }
            else
            {
                //requesting current resource task list
                for (int i = 1; i <= Configuration.RESOURCES_COUNT; i++)
                {
                    if(currentResourceTableIndex == i)
                    {
                        tblDataRunning.setAll(Main.getTaskScheduler().getResourceTaskList(i-1));

                        printTasks(tblDataRunning);
                        System.out.println("R");
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

    public void printTasks(ObservableList list)
    {
        System.out.println(list.toString());
    }
}
