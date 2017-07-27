package com.builtbroken.oilore.gen;

import com.builtbroken.oilore.OilOreMod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.*;


public class OreGeneratorOilOre implements IWorldGenerator
{
    public int minGenerateLevel = 40;
    public int maxGenerateLevel = 120;
    public int amountPerChunk = 80;
    public int amountPerBranch = 10;

    @Override
    public void generate(Random random, int cx, int cz, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        // Checks to make sure this is the normal world
        if (isOreGeneratedInWorld(world, chunkGenerator, chunkProvider))
        {
            int blocksPlaced = 0;

            int chunkX = cx << 4;
            int chunkZ = cz << 4;

            while (blocksPlaced < amountPerChunk)
            {
                int x = chunkX + random.nextInt(16);
                int z = chunkZ + random.nextInt(16);

                int y = random.nextInt(Math.max(maxGenerateLevel - minGenerateLevel, 0)) + minGenerateLevel;

                int placed = this.generateBranch(world, random, cx, cz, x, y, z);
                if (placed <= 0)
                {
                    placed = amountPerBranch; //Prevents inf loop
                }
                blocksPlaced += placed;
            }
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
    public int generateBranch(World world, Random rand, int cx, int cz, int varX, int varY, int varZ)
    {
        int blocksPlaced = 0;
        //Positions already pathed
        List<BlockPos> pathed = new ArrayList();
        //Positions to path next
        Queue<BlockPos> toPath = new LinkedList();

        //First location to path
        toPath.add(new BlockPos(varX, varY, varZ));

        List<EnumFacing> directions = new ArrayList();
        for (EnumFacing dir : EnumFacing.values())
        {
            directions.add(dir);
        }

        //Breadth first search
        while (!toPath.isEmpty() && blocksPlaced < amountPerBranch)
        {
            BlockPos next = toPath.poll();
            pathed.add(next);

            //Place block
            Block block = world.getBlockState(next).getBlock();
            if (block == Blocks.STONE)
            {
                if (world.setBlockState(next, OilOreMod.blockOre.getDefaultState()))
                {
                    blocksPlaced += 1;
                }
            }

            //Find new locations to place blocks
            Collections.shuffle(directions);
            for (EnumFacing direction : directions)
            {
                //TODO randomize next path
                BlockPos pos = next.add(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());
                if (!pathed.contains(pos) && world.rand.nextBoolean())
                {
                    int px = pos.getX() >> 4;
                    int pz = pos.getZ() >> 4;
                    boolean insideX = px == cx;
                    boolean insideZ = pz == cz;
                    boolean insideY = pos.getY() >= minGenerateLevel && pos.getY() <= maxGenerateLevel;
                    if (insideX && insideZ && insideY)
                    {
                        block = world.getBlockState(pos).getBlock();
                        if (block == Blocks.STONE)
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

    public boolean isOreGeneratedInWorld(World world, IChunkGenerator generator, IChunkProvider provider)
    {
        return generator instanceof ChunkGeneratorOverworld;
    }
}
