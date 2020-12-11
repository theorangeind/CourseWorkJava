package program.classes;

import program.Main;
import program.util.ITickable;

import java.util.ArrayList;

public class CPU implements ITickable
{
    private Core[] cores;

    private int inactivityTicks = 0;

    public CPU(final int coresNumber)
    {
        cores = new Core[coresNumber];
        for(int i = 0; i < coresNumber; i++)
        {
            cores[i] = new Core(this);
        }
    }

    @Override
    public void tick(int currentTime)
    {
        int freeCores = 0;
        for (Core core : cores)
        {
            core.tick(currentTime);
            if(!core.isBusy()) freeCores++;
        }
        if(freeCores == cores.length)
        {
            inactivityTicks++;
            Main.guiController.updateCPUInactivity();
        }
    }

    public boolean runProcess(Process process)
    {
        Core core = getFirstFreeCore();
        if(core != null)
        {
            //System.out.println(process.toString() + " running on " + core);

            core.runProcess(process);
            return true;
        }
        return false;
    }

    public boolean finishProcess(Process process, String reason)
    {
        for (Core core : cores)
        {
            if(core.isBusy())
            {
                if(core.getRunningProcess().equals(process))
                {
                    core.finishProcess(reason);
                    return true;
                }
            }
        }

        return false;
    }

    public void finishWork()
    {
        for (Core core : cores)
        {
            if(core.isBusy())
            {
                core.finishProcess("CPU shutdown.");
            }
        }
    }

    public Core getFirstFreeCore()
    {
        for (Core core : cores)
        {
            if(!core.isBusy()) return core;
        }
        return null;
    }

    public int getLowestPriorityIndex()
    {
        int lowestPriorityIndex = 0;
        for (int i = 1; i < cores.length; i++)
        {
            if(cores[i].getRunningProcess().getPriority() > cores[lowestPriorityIndex].getRunningProcess().getPriority())
            {
                lowestPriorityIndex = i;
            }
        }
        return lowestPriorityIndex;
    }

    public Core getCore(int index)
    {
        if(index > cores.length || index < 0) return null;
        return cores[index];
    }

    public boolean hasFreeCore()
    {
        return getFirstFreeCore() != null;
    }

    public int getCoresCount()
    {
        return cores.length;
    }

    public ArrayList<Process> getCoresContent()
    {
        ArrayList<Process> result = new ArrayList<>();
        for (Core core : cores)
        {
            if(core.getRunningProcess() != null)
                result.add(core.getRunningProcess());
        }

        return result;
    }

    public int getInactivityTicks()
    {
        return inactivityTicks;
    }
}
