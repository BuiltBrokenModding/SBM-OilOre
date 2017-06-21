package com.builtbroken.oilore;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.IFuelHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/12/2017.
 */
public class FurnaceFuelHandler implements IFuelHandler
{
    private final int fuelBurnTime;

    public FurnaceFuelHandler(Configuration configuration)
    {
        fuelBurnTime = configuration.getInt("burn_time", "Furnace", 10000, 0, 1000000, "How long in ticks (20 ticks a second) should oil burn in a furnace");
    }

    @Override
    public int getBurnTime(ItemStack fuel)
    {
        if (fuel != null && fuel.getItem() == OilOreMod.itemOil)
        {
            return fuelBurnTime;
        }
        return 0;
    }
}
