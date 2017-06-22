package com.builtbroken.oilore.debug;

import com.builtbroken.oilore.OilOreMod;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2017.
 */
public class ItemInstantHole extends Item
{
    public ItemInstantHole()
    {
        setCreativeTab(CreativeTabs.TOOLS);
        setRegistryName("instantHole");
        setUnlocalizedName(OilOreMod.DOMAIN + ":instantHole");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            Chunk chunk = world.getChunkFromBlockCoords(pos);
            int chunkX = chunk.x << 4;
            int chunkZ = chunk.z << 4;
            for (int y = 1; y < 256; y++)
            {
                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        BlockPos blockPos = new BlockPos(x + chunkX, y, z + chunkZ);
                        Block block = world.getBlockState(blockPos).getBlock();
                        if (block == Blocks.STONE || block == Blocks.SAND || block == Blocks.SANDSTONE || block == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.GRAVEL)
                        {
                            world.setBlockToAir(blockPos);
                        }
                        else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)
                        {
                            world.setBlockState(blockPos, Blocks.STAINED_GLASS.getStateFromMeta(4), 2);
                        }
                        else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
                        {
                            world.setBlockState(blockPos, Blocks.STAINED_GLASS.getStateFromMeta(1), 2);
                        }
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
