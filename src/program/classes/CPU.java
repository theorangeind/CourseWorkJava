package program.classes;

import program.util.ITickable;

import java.util.ArrayList;

public class CPU implements ITickable
{
    private Core[] cores;

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
        for (Core core : cores)
        {
            core.tick(currentTime);
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
}
