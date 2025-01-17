package net.aspw.client.injection.forge.mixins.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The type Mixin c 17 packet custom payload.
 */
@Mixin(C17PacketCustomPayload.class)
public class MixinC17PacketCustomPayload {
    @Shadow
    public PacketBuffer data;

    @Inject(method = "processPacket(Lnet/minecraft/network/play/INetHandlerPlayServer;)V", at = @At("TAIL"))
    private void releaseData(INetHandlerPlayServer handler, CallbackInfo ci) {
        if (this.data != null) {
            this.data.release();
        }
    }
}
