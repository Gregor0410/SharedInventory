package com.gregor0410.sharedinventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface IMinecraftServer {
    DefaultedList<ItemStack> getMain();
    DefaultedList<ItemStack> getOffHand();
    DefaultedList<ItemStack> getArmor();
}
