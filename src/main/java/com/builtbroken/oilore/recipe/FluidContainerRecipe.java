package com.builtbroken.oilore.recipe;

import com.builtbroken.oilore.OilOreMod;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
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
    public final Fluid fluid;

    public FluidContainerRecipe(Item result, Item bucket, Fluid fluid)
    {
        super(new ResourceLocation(OilOreMod.DOMAIN, "fuelBucket"), getBucketForStack(fluid, new ItemStack(result)), bucket, new ItemStack(OilOreMod.itemOil));
        this.bucket = bucket;
        this.fluid = fluid;
    }

    @Override
    public boolean matches(InventoryCrafting var1, World world)
    {
        boolean foundBucket = false;
        int oilCount = 0;
        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            ItemStack slotStack = var1.getStackInSlot(x);

            if (slotStack != null)
            {
                if (slotStack.getItem() == OilOreMod.itemOil && slotStack.getItemDamage() == 0)
                {
                    oilCount++;
                }
                else if (isSupportedBucket(slotStack))
                {
                    foundBucket = true;
                }
                else if (slotStack.getItem() != Item.getItemFromBlock(Blocks.AIR))
                {
                    return false;
                }
            }
        }

        return foundBucket && oilCount == 1;
    }

    protected boolean isSupportedBucket(ItemStack slotStack)
    {
        if (slotStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP))
        {
            IFluidHandler handler = slotStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
            if (handler != null)
            {
                if (handler.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), false) >= Fluid.BUCKET_VOLUME)
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected static ItemStack getBucketForStack(Fluid fluid, ItemStack slot)
    {
        if (slot.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP))
        {
            IFluidHandler handler = slot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
            if (handler != null)
            {
                if (handler.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true) >= Fluid.BUCKET_VOLUME)
                {
                    return slot;
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid)
    {
        ItemStack bucket = null;
        for (int x = 0; x < grid.getSizeInventory(); x++)
        {
            ItemStack stack = grid.getStackInSlot(x);
            if (isSupportedBucket(stack))
            {
                bucket = getBucketForStack(fluid, stack.copy());
            }
        }
        return bucket;
    }
}
