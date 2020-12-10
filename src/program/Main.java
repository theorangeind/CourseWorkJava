package program;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import program.classes.CPU;
import program.classes.ClockGenerator;
import program.classes.Resource;
import program.classes.TaskScheduler;

import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application
{
    public static Controller guiController;

    private static ClockGenerator systemClock;
    private static CPU cpu;
    private static TaskScheduler taskScheduler;
    private static ArrayList<Resource> resources;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Course Work");
        primaryStage.setScene(new Scene(root, 800, 600));

        guiController = loader.getController();
        guiController.initBaseTabs();
        guiController.initResourcesBar(resources);

        primaryStage.show();
    }


    public static void main(String[] args)
    {
        setupSystem();

        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                Scanner input = new Scanner(System.in);
                while(true)
                {
                    int command = input.nextInt();
                    if(command == 1)
                    {
                        System.out.println("New Task has been added to queue!");
                        taskScheduler.scheduleRandom();
                    }
                    else if(command == 0)
                    {
                        System.out.println("Finishing work!");
                        finishWork();
                        break;
                    }
                    else System.out.println("Unknown command!");
                }
            }
        };

        Thread userInputThread = new Thread(r);
        userInputThread.start();

        launch(args);
    }

    private static void setupSystem()
    {
        System.out.println("Starting system!");

        cpu = new CPU(4);
        taskScheduler = new TaskScheduler(cpu, Configuration.MEMORY_VOLUME);
        systemClock = new ClockGenerator(cpu, taskScheduler);
        resources = new ArrayList<>();
        for (int i = 0; i < Configuration.RESOURCES_COUNT; i++)
        {
            Resource r = new Resource("R" + (i + 1));
            resources.add(r);
            systemClock.attachSystemComponent(r);
        }

        systemClock.start();

        System.out.println("System setup completed!");
    }

    private static void finishWork()
    {
        taskScheduler.finishWork();
        systemClock.finishWork();
    }

    public static int getSystemTime()
    {
        return systemClock.getTime();
    }

    public static TaskScheduler getTaskScheduler()
    {
        return taskScheduler;
    }

    public static ArrayList<Resource> getSystemResources()
    {
        return resources;
    }
}
