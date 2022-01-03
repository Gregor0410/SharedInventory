package com.gregor0410.sharedinventory.mixin;

import com.gregor0410.sharedinventory.IMinecraftServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.collection.DefaultedList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Mutable
    @Shadow @Final public DefaultedList<ItemStack> main;

    @Shadow @Final public PlayerEntity player;

    @Mutable
    @Shadow @Final public DefaultedList<ItemStack> armor;

    @Mutable
    @Shadow @Final public DefaultedList<ItemStack> offHand;


    @Redirect(method="<init>",at=@At(value="FIELD",target ="Lnet/minecraft/entity/player/PlayerInventory;main:Lnet/minecraft/util/collection/DefaultedList;",opcode = Opcodes.PUTFIELD))
    private void setMain(PlayerInventory instance, DefaultedList<ItemStack> value,PlayerEntity player){
        MinecraftServer server = player.getServer();
        if(server!=null){
            this.main = ((IMinecraftServer)server).getMain();
        }else{
            this.main=value;
        }
    }
    @Redirect(method="<init>",at=@At(value="FIELD",target ="Lnet/minecraft/entity/player/PlayerInventory;armor:Lnet/minecraft/util/collection/DefaultedList;",opcode = Opcodes.PUTFIELD))
    private void setArmor(PlayerInventory instance, DefaultedList<ItemStack> value,PlayerEntity player){
        MinecraftServer server = player.getServer();
        if(server!=null){
            this.armor = ((IMinecraftServer)server).getArmor();
        }else{
            this.armor=value;
        }
    }
    @Redirect(method="<init>",at=@At(value="FIELD",target ="Lnet/minecraft/entity/player/PlayerInventory;offHand:Lnet/minecraft/util/collection/DefaultedList;",opcode = Opcodes.PUTFIELD))
    private void setOffHand(PlayerInventory instance, DefaultedList<ItemStack> value,PlayerEntity player){
        MinecraftServer server = player.getServer();
        if(server!=null){
            this.offHand = ((IMinecraftServer)server).getOffHand();
        }else{
            this.offHand=value;
        }
    }
    @Inject(method="deserialize",at=@At("HEAD"),cancellable = true)
    private void cancelDeserialize(ListTag tag, CallbackInfo ci){
        ci.cancel();
    }
}
