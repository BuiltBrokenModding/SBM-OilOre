package com.builtbroken.oilore.recipe;

import com.builtbroken.oilore.OilOreMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/24/2017.
 */
public class FluidContainerRecipe extends ShapelessOreRecipe
{
    public final Item bucket;

    public FluidContainerRecipe(Item result, Item bucket)
    {
        super(result, bucket, new ItemStack(OilOreMod.itemOil));
        this.bucket = bucket;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(InventoryCrafting var1, World world)
    {
        boolean foundBucket = false;
        int oilCount = 0;
        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            ItemStack slot = var1.getStackInSlot(x);

            if (slot != null)
            {
                if (slot.getItem() == OilOreMod.itemOil && slot.getItemDamage() == 0)
                {
                    oilCount++;
                }
                else if (getBucket(slot) != null && !foundBucket)
                {
                    foundBucket = true;
                }
                else
                {
                    return false;
                }
            }
        }

        return foundBucket && oilCount == 1;
    }

    protected ItemStack getBucket(ItemStack slot)
    {
        if(slot != null && slot.getItem() == bucket)
        {
            ItemStack stack = slot.copy();
            if (stack.getItem() instanceof IFluidContainerItem)
            {
                //Assumes the meta is used for texture
                IFluidContainerItem containerItem = (IFluidContainerItem) slot.getItem();
                if (containerItem.drain(slot, Integer.MAX_VALUE, false) == null)
                {
                    stack.stackSize = 1;
                    if (containerItem.fill(stack, new FluidStack(FluidRegistry.getFluid("fuel"), Fluid.BUCKET_VOLUME), true) >= Fluid.BUCKET_VOLUME)
                    {
                        return stack;
                    }
                }
            }
            else if(stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
            {
                IFluidHandler handler = slot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if(handler != null)
                {
                    if (handler.fill(new FluidStack(FluidRegistry.getFluid("fuel"), Fluid.BUCKET_VOLUME), true) >= Fluid.BUCKET_VOLUME)
                    {
                        return stack;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1)
    {
        ItemStack bucket = null;
        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            ItemStack stack = getBucket(var1.getStackInSlot(x));
            if (stack != null)
            {
                if (bucket == null)
                {
                    bucket = stack;
                }
                else
                {
                    return null; //invalid recipe
                }
            }
        }
        return bucket;
    }
}
