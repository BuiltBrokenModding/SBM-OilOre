package com.builtbroken.oilore.debug;

import com.builtbroken.oilore.OilOreMod;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
        setCreativeTab(CreativeTabs.tabTools);
        setRegistryName("instantHole");
        setUnlocalizedName(OilOreMod.DOMAIN + ":instantHole");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            Chunk chunk = world.getChunkFromBlockCoords(pos);
            int chunkX = chunk.xPosition << 4;
            int chunkZ = chunk.zPosition << 4;
            for (int y = 1; y < 256; y++)
            {
                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        BlockPos blockPos = new BlockPos(x + chunkX, y, z + chunkZ);
                        Block block = world.getBlockState(blockPos).getBlock();
                        if (block == Blocks.stone || block == Blocks.sand || block == Blocks.sandstone || block == Blocks.dirt || block == Blocks.grass || block == Blocks.gravel)
                        {
                            world.setBlockToAir(blockPos);
                        }
                        else if (block == Blocks.water || block == Blocks.flowing_water)
                        {
                            world.setBlockState(blockPos, Blocks.stained_glass.getStateFromMeta(4), 2);
                        }
                        else if (block == Blocks.lava || block == Blocks.flowing_lava)
                        {
                            world.setBlockState(blockPos, Blocks.stained_glass.getStateFromMeta(1), 2);
                        }
                    }
                }
            }
        }
        return true;
    }
}
