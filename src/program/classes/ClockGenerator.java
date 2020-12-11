package program.classes;

import program.Configuration;
import program.Main;
import program.util.ITickable;

import java.util.ArrayList;

public class ClockGenerator extends Thread
{
    ArrayList<ITickable> attachedComponents;

    private int currentTick = 0;

    private boolean running = false;

    public ClockGenerator(ITickable... attachedComponents)
    {
        this.attachedComponents = new ArrayList<>();

        for (ITickable item : attachedComponents)
        {
            this.attachedComponents.add(item);
        }
    }

    public void attachSystemComponent(ITickable component)
    {
        attachedComponents.add(component);
    }

    @Override
    public void run()
    {
        running = true;

        System.out.println("System clock is running.");

        while(running)
        {
            if(!Main.pauseActive())
            {
                try
                {
                    Thread.sleep(Math.floorDiv(1000, Configuration.getClockTps()));
                    for (ITickable item : attachedComponents)
                    {
                        item.tick(currentTick);
                    }
                    currentTick++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("System clock is stopped.");
    }

    public int getTime()
    {
        return currentTick;
    }

    public String getInfo()
    {
        return "";
    }

    /*--external control--*/

    public void nextTick()
    {
        for (ITickable item : attachedComponents)
        {
            item.tick(currentTick);
        }
        currentTick++;
    }

    public void finishWork() { running = false; }
}
