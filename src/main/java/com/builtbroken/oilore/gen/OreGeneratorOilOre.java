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

/**
 * Ore generator for oil ore
 */
public class OreGeneratorOilOre implements IWorldGenerator
{
    //Generation settings
    public int minGenerateLevel = 40;
    public int maxGenerateLevel = 120;
    public int amountPerChunk = 80;
    public int amountPerBranch = 10;

    //Used for randomized direction in pathfinder
    private List<EnumFacing> directions = new ArrayList();

    @Override
    public void generate(Random random, int cx, int cz, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        //Init directions list
        if (directions.isEmpty())
        {
            for (EnumFacing dir : EnumFacing.values())
            {
                directions.add(dir);
            }
        }

        // Checks to make sure this is the normal world
        if (isOreGeneratedInWorld(world, chunkGenerator, chunkProvider))
        {
            //Track blocks placed
            int blocksPlaced = 0;

            //Convert chunk position to block position
            int chunkX = cx << 4;
            int chunkZ = cz << 4;

            //TODO add config option to check timing of world gen, exit if time is too high
            while (blocksPlaced < amountPerChunk)
            {
                //Random position
                int x = chunkX + random.nextInt(15);
                int z = chunkZ + random.nextInt(15);
                int y = random.nextInt(Math.max(maxGenerateLevel - minGenerateLevel, 0)) + minGenerateLevel;

                //Checks to ensure location is still inside chunk
                int px = x >> 4;
                int pz = y >> 4;
                if (px == cx && pz == cz)
                {
                    //Generate blocks
                    int placed = this.generateBranch(world, cx, cz, x, y, z);

                    //Ensures exit condition is met, prevents inf loop
                    if (placed <= 0)
                    {
                        placed = amountPerBranch;
                    }

                    //Increase block count based on placement
                    blocksPlaced += placed;
                }
                //Ensures exit condition of loop
                else
                {
                    blocksPlaced += amountPerBranch;
                }
            }
        }
    }

    /**
     * Generated a branch at location using a breadth first pathfinder
     *
     * @param world        - world generating the ore branch inside
     * @param branchStartX - random X to start world generate of branch
     * @param branchStartY - random Y to start world generate of branch
     * @param branchStartZ - random Z to start world generate of branch
     * @return number of blocks placed
     */
    public int generateBranch(World world, int cx, int cz, int branchStartX, int branchStartY, int branchStartZ)
    {
        //Track blocks placed
        int blocksPlaced = 0;

        //Ore generation is a breadth first pathfinder. This allows for more randomized layouts over the vanilla version.

        //Positions already pathed
        List<BlockPos> pathed = new ArrayList();
        //Positions to path next
        Queue<BlockPos> toPath = new LinkedList();

        //First location to path
        toPath.add(new BlockPos(branchStartX, branchStartY, branchStartZ));

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

            //randomize directions
            Collections.shuffle(directions);

            //Find new blocks to path
            for (EnumFacing direction : directions)
            {
                //Get position based on direction
                BlockPos pos = next.add(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());

                //Ensure we have not pathed, randomize chance of path TODO add randomize settings
                if (!pathed.contains(pos) && world.rand.nextBoolean())
                {
                    //Get chunk position
                    int px = pos.getX() >> 4;
                    int pz = pos.getZ() >> 4;

                    //Validate position is inside chunk
                    boolean insideX = px == cx;
                    boolean insideZ = pz == cz;
                    boolean insideY = pos.getY() >= minGenerateLevel && pos.getY() <= maxGenerateLevel;

                    //Valid chunk exists, prevents new chunk creation due to mistakes
                    boolean chunkLoaded = world.isChunkGeneratedAt(px, pz);

                    //Validate based on checks
                    if (chunkLoaded && insideX && insideZ && insideY)
                    {
                        //Generate in stone
                        block = world.getBlockState(pos).getBlock();
                        if (block == Blocks.STONE)
                        {
                            toPath.add(pos);
                        }
                    }

                    //Add position to cache to prevent re-path
                    if (!toPath.contains(pos))
                    {
                        pathed.add(pos);
                    }
                }
            }
        }
        return blocksPlaced;
    }

    /**
     * Checks if the world generator can run for the world
     *
     * @param world
     * @param generator
     * @param provider
     * @return
     */
    public boolean isOreGeneratedInWorld(World world, IChunkGenerator generator, IChunkProvider provider)
    {
        return generator instanceof ChunkGeneratorOverworld;
    }
}
