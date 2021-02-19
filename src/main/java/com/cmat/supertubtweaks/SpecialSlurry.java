package com.cmat.supertubtweaks;

import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

class SpecialSlurryBuilder extends SlurryBuilder {


    protected SpecialSlurryBuilder(ResourceLocation texture, int color) {
        super(texture);
        ore(Tags.Items.ORES);
        color(color);
    }


}
