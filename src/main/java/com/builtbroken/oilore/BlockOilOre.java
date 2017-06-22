package com.builtbroken.oilore;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/11/2017.
 */
public class BlockOilOre extends Block
{
    private Random rand = new Random();

    public BlockOilOre()
    {
        super(Material.ROCK);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setUnlocalizedName(OilOreMod.DOMAIN + ":oilore");
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune)
    {
        return MathHelper.getRandomIntegerInRange(rand, 0, 2);
    }
}
