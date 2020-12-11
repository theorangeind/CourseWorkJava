package program.classes;

import program.Configuration;
import program.Main;
import program.util.ITickable;

import java.util.ArrayList;
import java.util.Random;

public class Resource implements ITickable
{
    private final String name;
    private Queue queue;
    private Status status;

    private int processTime;
    private Process currentTask;
    private int timer;

    private Random random = new Random();

    public Resource(String name)
    {
        this.name = name;
        queue = new Queue();
        status = Status.READY;
    }

    public void addProcess(Process process)
    {
        process.setState(Process.State.WAITING);
        queue.addProcess(process);
    }

    private boolean setProcess(Process process)
    {
        if(status == Status.BUSY) return false;

        timer = 0;
        currentTask = process;
        process.setState(Process.State.WAITING);
        processTime = Math.floorDiv(process.getTimeRequired(), 100) * random.nextInt(20) + 5;

        return true;
    }

    public String getName()
    {
        return name;
    }

    public String getInfo()
    {
        return "";
    }

    public void setStatus(Status value)
    {
        this.status = value;
    }

    public Status getStatus()
    {
        return status;
    }

    public void sendTaskToCPU()
    {
        Main.getTaskScheduler().scheduleTask(currentTask);
        status = Status.READY;
    }

    @Override
    public void tick(int currentTime)
    {
        if(queue.isEmpty()) return;
        if(status == Status.READY)
        {
            setProcess(queue.getFirstProcess());
            setStatus(Status.BUSY);

            //System.out.println("Process " + currentTask.getName() + " is running on a resource " + getName());
        }
        else if(currentTask != null)
        {
            //System.out.println("Process " + currentTask.getName() + " has running on a resource " + getName() + " for " + timer + " ticks");

            if (timer < processTime)
            {
                if(Configuration.runtimeErrorsEnabled() && random.nextInt(Configuration.getProcessTerminationChance()) == 0)
                    simulateException();

                timer++;
            }
            else
            {
                sendTaskToCPU();
                queue.removeProcess(currentTask);
                setStatus(Status.READY);
            }
        }
    }

    public void simulateException()
    {
        queue.removeProcess(currentTask);
        currentTask.setState(Process.State.TERMINATED);
        currentTask.setInterruptionReason("Runtime Error (" + name + ")");
        setStatus(Status.READY);
    }

    public ArrayList<Process> getTaskList()
    {
        return queue.getList();
    }

    public enum Status
    {
        READY,
        BUSY
    }
}


