package com.builtbroken.oilore.gen;

import com.builtbroken.oilore.OilOreMod;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;


public class OreGeneratorOilOre implements IWorldGenerator
{
    public int minGenerateLevel = 40;
    public int maxGenerateLevel = 120;
    public int amountPerChunk = 80;
    public int amountPerBranch = 10;

    @Override
    public final void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        chunkX = chunkX << 4;
        chunkZ = chunkZ << 4;

        // Checks to make sure this is the normal world

        if (isOreGeneratedInWorld(world, chunkGenerator))
        {
            generate(world, rand, chunkX, chunkZ);
        }
    }


    public void generate(World world, Random random, int varX, int varZ)
    {
        int blocksPlaced = 0;
        while (blocksPlaced < amountPerChunk)
        {
            int x = varX + random.nextInt(16);
            int z = varZ + random.nextInt(16);
            int y = random.nextInt(Math.max(maxGenerateLevel - minGenerateLevel, 0)) + minGenerateLevel;
            int placed = this.generateBranch(world, random, varX, varZ, x, y, z);
            if (placed <= 0)
            {
                placed = amountPerBranch; //Prevents inf loop
            }
            blocksPlaced += placed;
        }
    }

    /**
     * Picks a random location in the chunk based on a random rotation and Y value
     *
     * @param world - world
     * @param rand  - random
     * @param varX  - randomX
     * @param varY  - randomY
     * @param varZ  - randomZ
     * @return true if it placed blocks
     */
    public int generateBranch(World world, Random rand, int chunkCornerX, int chunkCornerZ, int varX, int varY, int varZ)
    {
        int blocksPlaced = 0;
        //Positions already pathed
        List<Pos> pathed = new ArrayList();
        //Positions to path next
        Queue<Pos> toPath = new LinkedList();

        //First location to path
        toPath.add(new Pos(varX, varY, varZ));

        List<ForgeDirection> directions = new ArrayList();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            directions.add(dir);
        }

        //Breadth first search
        while (!toPath.isEmpty() && blocksPlaced < amountPerBranch)
        {
            Pos next = toPath.poll();
            pathed.add(next);

            //Place block
            Block block = world.getBlock(next.x, next.y, next.z);
            if (block == Blocks.stone)
            {
                if (world.setBlock(next.x, next.y, next.z, OilOreMod.blockOre, 0, 2))
                {
                    blocksPlaced += 1;
                }
            }

            //Find new locations to place blocks
            Collections.shuffle(directions);
            for (ForgeDirection direction : directions)
            {
                //TODO randomize next path
                Pos pos = next.add(direction);
                if (!pathed.contains(pos) && world.rand.nextBoolean())
                {
                    boolean insideX = pos.x >= chunkCornerX && pos.x < (chunkCornerX + 16);
                    boolean insideZ = pos.z >= chunkCornerZ && pos.z < (chunkCornerZ + 16);
                    boolean insideY = pos.y >= minGenerateLevel && pos.y <= maxGenerateLevel;
                    if (insideX && insideZ && insideY)
                    {
                        block = world.getBlock(pos.x, pos.y, pos.z);
                        if (block == Blocks.stone)
                        {
                            toPath.add(pos);
                        }
                    }

                    if (!toPath.contains(pos))
                    {
                        pathed.add(pos);
                    }
                }
            }
        }
        return blocksPlaced;
    }

    public boolean isOreGeneratedInWorld(World world, IChunkProvider chunkGenerator)
    {
        return chunkGenerator instanceof ChunkProviderGenerate;
    }

    public static class Pos
    {
        public final int x, y, z;

        public Pos(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Pos add(ForgeDirection d)
        {
            return new Pos(x + d.offsetX, y + d.offsetY, z + d.offsetZ);
        }
    }
}
