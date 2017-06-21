package com.builtbroken.oilore.client;

import com.builtbroken.oilore.CommonProxy;
import com.builtbroken.oilore.OilOreMod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2017.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(OilOreMod.blockOre), 0, new ModelResourceLocation(OilOreMod.blockOre.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(OilOreMod.itemOil, 0, new ModelResourceLocation(OilOreMod.itemOil.getRegistryName(), "inventory"));

        if(OilOreMod.instantHole != null)
        {
            ModelLoader.setCustomModelResourceLocation(OilOreMod.instantHole, 0, new ModelResourceLocation(OilOreMod.instantHole.getRegistryName(), "inventory"));
        }
    }
}
