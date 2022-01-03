package com.gregor0410.sharedinventory.mixin;

import com.gregor0410.sharedinventory.IMinecraftServer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.file.Path;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
    @Shadow @Final protected LevelStorage.Session session;
    private CompoundTag inventoryTag;
    private Path inventoryPath;
    public DefaultedList<ItemStack> main;
    public DefaultedList<ItemStack> armor;
    public DefaultedList<ItemStack> offHand;

    @Inject(method = "<init>",at=@At("TAIL"))
    private void init(CallbackInfo ci){
        this.main = DefaultedList.ofSize(36, ItemStack.EMPTY);
        this.armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.offHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
        inventoryPath = this.session.getDirectory(WorldSavePath.PLAYERDATA).resolve("inventory.dat");
        if(inventoryPath.toFile().exists()) {
            DataInputStream in;
            try {
                in = new DataInputStream(new FileInputStream(inventoryPath.toFile()));
                inventoryTag = NbtIo.read(in, PositionTracker.DEFAULT);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            inventoryTag = new CompoundTag();
            inventoryTag.put("inventory", new ListTag());
        }
        this.load();
    }
    @Inject(method="save",at=@At("HEAD"))
    private void saveInventory(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir){
        try {
            this.save();
            DataOutputStream out = new DataOutputStream(new FileOutputStream(inventoryPath.toFile()));
            NbtIo.write(inventoryTag,out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        ListTag tag = new ListTag();
        int i;
        CompoundTag compoundTag;
        for(i = 0; i < this.main.size(); ++i) {
            if (!((ItemStack)this.main.get(i)).isEmpty()) {
                compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte)i);
                ((ItemStack)this.main.get(i)).toTag(compoundTag);
                tag.add(compoundTag);
            }
        }

        for(i = 0; i < this.armor.size(); ++i) {
            if (!((ItemStack)this.armor.get(i)).isEmpty()) {
                compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte)(i + 100));
                ((ItemStack)this.armor.get(i)).toTag(compoundTag);
                tag.add(compoundTag);
            }
        }

        for(i = 0; i < this.offHand.size(); ++i) {
            if (!((ItemStack)this.offHand.get(i)).isEmpty()) {
                compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte)(i + 150));
                ((ItemStack)this.offHand.get(i)).toTag(compoundTag);
                tag.add(compoundTag);
            }
        }
        compoundTag = new CompoundTag();
        compoundTag.put("inventory",tag);
        inventoryTag = compoundTag;
    }

    public void load() {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();
        ListTag tag = (ListTag) inventoryTag.get("inventory");

        for(int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundTag = tag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromTag(compoundTag);
            if (!itemStack.isEmpty()) {
                if (j >= 0 && j < this.main.size()) {
                    this.main.set(j, itemStack);
                } else if (j >= 100 && j < this.armor.size() + 100) {
                    this.armor.set(j - 100, itemStack);
                } else if (j >= 150 && j < this.offHand.size() + 150) {
                    this.offHand.set(j - 150, itemStack);
                }
            }
        }

    }

    @Override
    public DefaultedList<ItemStack> getMain() {
        return this.main;
    }

    @Override
    public DefaultedList<ItemStack> getOffHand() {
        return this.offHand;
    }

    @Override
    public DefaultedList<ItemStack> getArmor() {
        return this.armor;
    }
}
