package com.cmat.supertubtweaks;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.slurry.EmptySlurry;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.UnaryOperator;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.stream.Collectors;
import it.zerono.mods.extremereactors.api.coolant.FluidMappingsRegistry;
import it.zerono.mods.extremereactors.api.coolant.FluidsRegistry;
import it.zerono.mods.extremereactors.api.coolant.TransitionsRegistry;
import it.zerono.mods.extremereactors.api.reactor.*;

import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;

import javax.annotation.Resource;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("supertubtweaks")
public class SuperTubTweaks
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<Item> itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, "supertubtweaks");
    private static final DeferredRegister<Fluid> fluidRegister = DeferredRegister.create(ForgeRegistries.FLUIDS, "supertubtweaks");
    private static final DeferredRegister<Block> blockRegister = DeferredRegister.create(ForgeRegistries.BLOCKS, "supertubtweaks");

    public static final LinkedHashMap<String, RegistryObject<Item>> customItems = new LinkedHashMap<String, RegistryObject<Item>>();
    public static final LinkedHashMap<String, FluidRegisterHandle> customFluids = new LinkedHashMap<String, FluidRegisterHandle>();

    public SuperTubTweaks() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        itemRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
        fluidRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
        blockRegister.register(FMLJavaModLoadingContext.get().getModEventBus());

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(Slurry.class, this::registerSlurries);

        ArrayList<String> itemNames = new ArrayList<String>();

        ArrayList<String> ores0 = new ArrayList<String>();
        Collections.addAll(ores0, "gold", "iron", "copper", "lead", "tin", "osmium", "uranium");

        ArrayList<String> ores1 = new ArrayList<String>();
        Collections.addAll(ores1, "starmetal", "zinc", "silver", "bauxite", "monazite", "oratchalcum", "netherite_scrap", "nickel");

        ArrayList<String> ores2 = new ArrayList<String>();
        Collections.addAll(ores2, "coal", "lapis", "redstone", "fluorite", "inferium", "soulium", "cheese", "apatite", "niter", "sulfur", "biotite", "bitumen");

        ArrayList<String> ores3 = new ArrayList<String>();
        Collections.addAll(ores3, "diamond", "emerald", "quartz", "certus", "chargedcertus", "arcanegem", "sapphire", "amethyst", "prosperity","dimensionalshard", "ratlanteangem", "cinnabar", "aquamarine");

        ArrayList<Tuple<String, Integer>> fluids = new ArrayList<>();
        Collections.addAll(fluids,
                new Tuple<>("diamond", -11866662),
                new Tuple<>("emerald", -15213213),
                new Tuple<>("quartz", -2239289),
                new Tuple<>("certus", -5648942),
                new Tuple<>("chargedcertus", -2101764),
                new Tuple<>("sapphire", -10781202),
                new Tuple<>("arcanegem", -2913351),
                new Tuple<>("amethyst", -3169047),
                new Tuple<>("prosperity", -2031873),
                new Tuple<>("dimensionalshard", -8072752),
                new Tuple<>("ratlanteangem", -6423701),
                new Tuple<>("cinnabar", -5627604),
                new Tuple<>("aquamarine", -16733478));

        for (String ore : ores0) {
            itemNames.add("crushed/crushed_" + ore);
        }

        for (String ore : ores1) {
            itemNames.add("crushed/crushed_" + ore);
            itemNames.add("crystal/crystal_" + ore);
            itemNames.add("shard/shard_" + ore);
            itemNames.add("clump/clump_" + ore);
            itemNames.add("dirtydust/dirtydust_" + ore);
            itemNames.add("dust/dust_" + ore);
        }

        for (String ore : ores2) {
            itemNames.add("crushed/crushed_" + ore);
            itemNames.add("blazeinfused/blazeinfused_" + ore);
            itemNames.add("charredpowder/charredpowder_" + ore);
            itemNames.add("revivedpowder/revivedpowder_" + ore);
        }

        for (String ore : ores3) {
            itemNames.add("crushed/crushed_" + ore);
            itemNames.add("smallgem/smallgem_" + ore);
        }

        itemNames.add("refined_uranium");

        for (String name: itemNames) {
            customItems.put(name, itemRegister.register(
                    name,
                    () -> new Item(new Item.Properties().group(ItemGroup.MISC))
            ));
        }

        for (Tuple<String, Integer> fluid: fluids) {
            String name = "starliquid_" + fluid.getA();
            FluidAttributes.Builder builder = FluidAttributes.builder(new ResourceLocation("supertubtweaks", "liquid/liquid"), new ResourceLocation("supertubtweaks", "liquid/liquid_flow"));
            builder.color(fluid.getB());
            builder.overlay(new ResourceLocation("minecraft", "block/water_overlay"));
            FluidRegisterHandle<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, FlowingFluidBlock, BucketItem> fluidregisterhandle = new FluidRegisterHandle<>("supertubtweaks", name);
            ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(
                    fluidregisterhandle::getStillFluid,
                    fluidregisterhandle::getFlowingFluid,
                    builder).bucket(fluidregisterhandle::getBucket).block(fluidregisterhandle::getBlock);
            fluidregisterhandle.updateStill(fluidRegister.register(name, () -> new ForgeFlowingFluid.Source(properties)));
            fluidregisterhandle.updateFlowing(fluidRegister.register(name + "_flowing", () -> new ForgeFlowingFluid.Flowing(properties)));
            fluidregisterhandle.updateBucket(itemRegister.register(name + "_bucket", () -> new BucketItem(fluidregisterhandle::getStillFluid, new Item.Properties().group(ItemGroup.MISC).maxStackSize(1).containerItem(Items.BUCKET))));
            fluidregisterhandle.updateBlock(blockRegister.register(name, () -> new FlowingFluidBlock(
                    fluidregisterhandle::getStillFluid,
                    AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())));
            customFluids.put(name, fluidregisterhandle);
        }
    }

    public void registerSlurries(RegistryEvent.Register<Slurry> event) {
        ResourceLocation clean = new ResourceLocation("mekanism", "slurry/clean");
        ResourceLocation dirty = new ResourceLocation("mekanism", "slurry/dirty");

        Slurry starmetalclean = new Slurry(new SpecialSlurryBuilder(clean, 0x030891));
        Slurry zincclean = new Slurry(new SpecialSlurryBuilder(clean, -5271945));
        Slurry bauxiteclean = new Slurry(new SpecialSlurryBuilder(clean, 0x61420a));
        Slurry nickelclean = new Slurry(new SpecialSlurryBuilder(clean, 0x9e9d9d));
        Slurry monaziteclean = new Slurry(new SpecialSlurryBuilder(clean, 0x5aadc4));
        Slurry oratchalcumclean = new Slurry(new SpecialSlurryBuilder(clean, 0xa88013));
        Slurry netheriteclean = new Slurry(new SpecialSlurryBuilder(clean, 0x61429a));
        Slurry silverclean = new Slurry(new SpecialSlurryBuilder(clean, 0x7e7d7d));

        Slurry starmetaldirty = new Slurry(new SpecialSlurryBuilder(dirty, 0x030891));
        Slurry zincdirty = new Slurry(new SpecialSlurryBuilder(dirty, -5271945));
        Slurry bauxitedirty = new Slurry(new SpecialSlurryBuilder(dirty, 0x61420a));
        Slurry nickeldirty = new Slurry(new SpecialSlurryBuilder(dirty, 0x9e9d9d));
        Slurry monazitedirty = new Slurry(new SpecialSlurryBuilder(dirty, 0x5aadc4));
        Slurry oratchalcumdirty = new Slurry(new SpecialSlurryBuilder(dirty, 0xa88013));
        Slurry netheritedirty = new Slurry(new SpecialSlurryBuilder(dirty, 0x61429a));
        Slurry silverdirty = new Slurry(new SpecialSlurryBuilder(dirty, 0x7e7d7d));

        event.getRegistry().register(starmetalclean.setRegistryName(new ResourceLocation("supertubtweaks", "starmetal_slurry_clean")));
        event.getRegistry().register(zincclean.setRegistryName(new ResourceLocation("supertubtweaks", "zinc_slurry_clean")));
        event.getRegistry().register(bauxiteclean.setRegistryName(new ResourceLocation("supertubtweaks", "bauxite_slurry_clean")));
        event.getRegistry().register(nickelclean.setRegistryName(new ResourceLocation("supertubtweaks", "nickel_slurry_clean")));
        event.getRegistry().register(monaziteclean.setRegistryName(new ResourceLocation("supertubtweaks", "monazite_slurry_clean")));
        event.getRegistry().register(oratchalcumclean.setRegistryName(new ResourceLocation("supertubtweaks", "oratchalcum_slurry_clean")));
        event.getRegistry().register(netheriteclean.setRegistryName(new ResourceLocation("supertubtweaks", "netherite_slurry_clean")));
        event.getRegistry().register(silverclean.setRegistryName(new ResourceLocation("supertubtweaks", "silver_slurry_clean")));

        event.getRegistry().register(zincdirty.setRegistryName(new ResourceLocation("supertubtweaks", "zinc_slurry_dirty")));
        event.getRegistry().register(bauxitedirty.setRegistryName(new ResourceLocation("supertubtweaks", "bauxite_slurry_dirty")));
        event.getRegistry().register(nickeldirty.setRegistryName(new ResourceLocation("supertubtweaks", "nickel_slurry_dirty")));
        event.getRegistry().register(monazitedirty.setRegistryName(new ResourceLocation("supertubtweaks", "monazite_slurry_dirty")));
        event.getRegistry().register(oratchalcumdirty.setRegistryName(new ResourceLocation("supertubtweaks", "oratchalcum_slurry_dirty")));
        event.getRegistry().register(starmetaldirty.setRegistryName(new ResourceLocation("supertubtweaks", "starmetal_slurry_dirty")));
        event.getRegistry().register(netheritedirty.setRegistryName(new ResourceLocation("supertubtweaks", "netherite_slurry_dirty")));
        event.getRegistry().register(silverdirty.setRegistryName(new ResourceLocation("supertubtweaks", "silver_slurry_dirty")));
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        ReactantMappingsRegistry.removeSolid(new ResourceLocation("forge", "ingots/yellorium"));
        ReactantMappingsRegistry.removeSolid(new ResourceLocation("forge", "storage_blocks/yellorium"));
        ReactantMappingsRegistry.removeSolid(new ResourceLocation("forge", "ingots/cyanite"));
        ReactantMappingsRegistry.removeSolid(new ResourceLocation("forge", "storage_blocks/cyanite"));
        ReactantMappingsRegistry.removeSolid((String)"forge:ingots/uranium");
        ReactantMappingsRegistry.removeSolid((String)"forge:storage_blocks/uranium");
        ReactantsRegistry.remove("yellorium");
        ReactantsRegistry.remove("cyanite");
        ReactionsRegistry.remove("yellorium");


        ReactionsRegistry.register("coal", "diamond", 0.5f, 0.5f);
        ReactantsRegistry.register("coal", ReactantType.Fuel, 0x000000, "reactant.supertubtweaks.coal");
        ReactantsRegistry.register("diamond", ReactantType.Waste, 0x0000ff, "reactant.supertubtweaks.diamond");
        ReactantMappingsRegistry.registerSolid("coal", 1000, new ResourceLocation("minecraft:coals"));
        ReactantMappingsRegistry.registerSolid("diamond", 1000, new ResourceLocation("forge:gems/emerald"));

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        /*
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
         */
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        /*
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
         */
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    }
}
