package program.classes;

import program.Configuration;
import program.util.ITickable;

import java.util.ArrayList;

public class ClockGenerator extends Thread
{
    ArrayList<ITickable> attachedComponents;

    private int currentTick = 0;
    private int tps;

    private boolean running = false;

    public ClockGenerator(ITickable... attachedComponents)
    {
        this.attachedComponents = new ArrayList<>();

        tps = Configuration.DEFAULT_CLOCK_TPS;

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

        while(running)
        {
            try
            {
                Thread.sleep(Math.floorDiv(1000, tps));
                for (ITickable item : attachedComponents)
                {
                    item.tick(currentTick);
                }
                nextTick();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public int getTime()
    {
        return currentTick;
    }

    public void nextTick()
    {
        currentTick++;
    }

    public void addTicks(int count)
    {
        currentTick += count;
    }

    public String getInfo()
    {
        return "";
    }

    public boolean setTPS(int value)
    {
        if(value < 1 || value > 10) return false;

        tps = value;
        return true;
    }

    public void finishWork()
    {
        running = false;
    }
}
