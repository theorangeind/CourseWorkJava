package program.classes;

import java.util.ArrayList;

public class MemoryScheduler
{
    private final int accessibleMemory;
    private ArrayList<MemoryBlock> memoryBlocks;

    public MemoryScheduler(int accessibleMemory)
    {
        this.accessibleMemory = accessibleMemory;
        memoryBlocks = new ArrayList<>();
    }

    /**
     * Returns the start of free block with size equal or greater than given size.
     */
    private int findFreeBlockStart(int size)
    {
        if(size > accessibleMemory) return -1;

        if(memoryBlocks.isEmpty())
        {
            return 0;
        }

        memoryBlocks.sort(MemoryBlock.byEnd);

        //finding block with required size using the 'first suitable' strategy
        for (int i = 0; i < memoryBlocks.size(); i++)
        {
            int emptySpaceSize;
            if(i + 1 >= memoryBlocks.size()) emptySpaceSize = accessibleMemory;
            else emptySpaceSize = memoryBlocks.get(i+1).start;
            emptySpaceSize -= memoryBlocks.get(i).end;

            if(emptySpaceSize > size)
                return memoryBlocks.get(i).end + 1;
        }

        if(accessibleMemory - memoryBlocks.get(memoryBlocks.size() - 1).end > size)
        {
            return memoryBlocks.get(memoryBlocks.size() - 1).end + 1;
        }

        return -1;
    }

    public MemoryBlock fillMemoryBlock(int size)
    {
        if(size > accessibleMemory) return null;

        int start = findFreeBlockStart(size);
        if(start == -1) return null;

        MemoryBlock result = new MemoryBlock(start, start + size - 1);
        if(addBlock(result)) return result;

        return null;
    }

    public void releaseMemoryBlock(MemoryBlock block)
    {
        if(block == null) return;
        memoryBlocks.remove(block);
    }

    public boolean addBlock(MemoryBlock block)
    {
        if(block == null) return false;
        return memoryBlocks.add(block);
    }

    public int getAccessibleMemory()
    {
        return accessibleMemory;
    }
}
