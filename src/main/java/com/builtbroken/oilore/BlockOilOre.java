package com.builtbroken.oilore;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/11/2017.
 */
public class BlockOilOre extends Block
{
    public BlockOilOre()
    {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setBlockTextureName(OilOreMod.DOMAIN + ":oil_ore");
        this.setBlockName(OilOreMod.DOMAIN + ":oilore");
    }

    private Random rand = new Random();

    @Override
    public int getExpDrop(IBlockAccess p_149690_1_, int p_149690_5_, int p_149690_7_)
    {
        return MathHelper.getRandomIntegerInRange(rand, 0, 2);
    }
}
