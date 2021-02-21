package com.cmat.supertubtweaks.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Random;

public class ItemEeep extends Item {
    Random r = new Random();

    public ItemEeep(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote || entityIn == null) {
            return;
        }
        if (entityIn instanceof PlayerEntity) {
            PlayerEntity p = (PlayerEntity) entityIn;
            if(p.inventory.getCurrentItem() == stack) {
                worldIn.createExplosion(entityIn, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), stack.getCount() * stack.getCount(), false, Explosion.Mode.BREAK);
            }
        }
    }
}
