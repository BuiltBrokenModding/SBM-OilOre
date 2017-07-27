package com.builtbroken.oilore;

import com.builtbroken.oilore.debug.ItemInstantHole;
import com.builtbroken.oilore.gen.OreGeneratorOilOre;
import com.builtbroken.oilore.recipe.FluidContainerRecipe;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Main mod class
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/11/2017.
 */
@Mod(modid = OilOreMod.DOMAIN, name = "Oil Ore", version = OilOreMod.VERSION)
@Mod.EventBusSubscriber(modid = OilOreMod.DOMAIN)
public class OilOreMod
{
    public static final boolean runningAsDev = System.getProperty("development") != null && System.getProperty("development").equalsIgnoreCase("true");

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    public static final String DOMAIN = "sbmoilore";

    @SidedProxy(modId = OilOreMod.DOMAIN, serverSide = "com.builtbroken.oilore.CommonProxy", clientSide = "com.builtbroken.oilore.client.ClientProxy")
    public static CommonProxy proxy;

    /** Information output thing */
    public static final Logger logger = LogManager.getLogger("SBM-OilOre");

    private static Configuration configuration;

    public static Block blockOre;
    public static Item itemOil;
    public static Item instantHole;

    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event)
    {
        logger.info("Registering Models");
        proxy.doLoadModels();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        logger.info("Registering Items");
        event.getRegistry().register(
                new ItemBlock(blockOre)
                        .setRegistryName(blockOre.getRegistryName()));

        event.getRegistry().register(
                itemOil = new Item()
                        .setRegistryName("oilItem")
                        .setUnlocalizedName(DOMAIN + ":oilore")
                        .setCreativeTab(CreativeTabs.MATERIALS));

        if (runningAsDev)
        {
            event.getRegistry().register(instantHole = new ItemInstantHole());
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        logger.info("Registering Blocks");
        event.getRegistry().register(
                blockOre = new BlockOilOre()
                        .setRegistryName("oilore"));

        int harvestLevel = configuration.getInt("harvest_level", Configuration.CATEGORY_GENERAL, 1, 0, 4, "Tool level to use for breaking the ore.");
        blockOre.setHarvestLevel("pickaxe", harvestLevel, blockOre.getDefaultState());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger.info("PreInit");
        configuration = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/OilOreMod.cfg"));
        configuration.load();

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (configuration.getBoolean("generate_ore", Configuration.CATEGORY_GENERAL, true, "Set to false to disable generate of this ore in the world."))
        {
            OreGeneratorOilOre generatorOilOre = new OreGeneratorOilOre();
            //TODO register ore generator
            generatorOilOre.amountPerBranch = configuration.getInt("amount_per_branch", "Ore_Gen_Settings", generatorOilOre.amountPerBranch, 1, 30, "Amount of ore blocks per vein/branch of ore.");
            generatorOilOre.amountPerChunk = configuration.getInt("amount_per_chunk", "Ore_Gen_Settings", generatorOilOre.amountPerChunk, 1, 200, "Amount of ore blocks per chunk (16x16 square on the map).");
            generatorOilOre.minGenerateLevel = configuration.getInt("min_y", "Ore_Gen_Settings", generatorOilOre.minGenerateLevel, 1, 255, "Starting point to generate ore y level (world height)");
            generatorOilOre.maxGenerateLevel = configuration.getInt("max_y", "Ore_Gen_Settings", generatorOilOre.maxGenerateLevel, 1, 255, "End point to generate ore y level (world height)");

            GameRegistry.registerWorldGenerator(generatorOilOre, 1);
        }

        if (configuration.getBoolean("enable", "Furnace_Recipe", true, "Allows the oil ore block to oil clump item recipe to work as a furnace recipe."))
        {
            int outputStackSize = configuration.getInt("output_stack_size", "Furnace_Recipe", 1, 1, 64, "Number of oil clumps to output per block in a furnace.");
            GameRegistry.addSmelting(blockOre, new ItemStack(itemOil, outputStackSize, 0), 0.1f);
        }

        if (configuration.getBoolean("furn_fuel", "Furnace", true, "Allows oil clump to be used in a furnace as fuel."))
        {
            GameRegistry.registerFuelHandler(new FurnaceFuelHandler(configuration));
        }

        configuration.save();
    }

    @SubscribeEvent
    public static void registerRecipe(RegistryEvent.Register<IRecipe> event)
    {
        Fluid fluid = FluidRegistry.getFluid("oil");
        if(fluid != null)
        {
            Item item = Item.REGISTRY.getObject(new ResourceLocation("vefluids:veBucket"));
            if (item != null)
            {
                event.getRegistry().register(new FluidContainerRecipe(item, item, fluid).setRegistryName(new ResourceLocation(DOMAIN, "fluidBucket_" + item)));
            }
        }
    }
}
