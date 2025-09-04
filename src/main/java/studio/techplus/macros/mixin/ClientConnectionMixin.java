package studio.techplus.macros.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.techplus.macros.ModuleManager;
import studio.techplus.macros.modules.Module;
import studio.techplus.macros.modules.TridentDuper;

/**
 * Mixin to intercept and cancel packets for the TridentDuper module
 */
@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        // Check if TridentDuper is enabled
        Module duper = ModuleManager.getInstance().getModule("TridentDuper");
        if (duper instanceof TridentDuper && duper.isEnabled()) {
            TridentDuper tridentDuper = (TridentDuper) duper;
            
            // Skip non-relevant packets
            if (packet instanceof ClientTickEndC2SPacket ||
                packet instanceof PlayerMoveC2SPacket ||
                packet instanceof CloseHandledScreenC2SPacket) {
                return;
            }
            
            // Check if we should cancel this packet
            if (tridentDuper.shouldCancelPacket() && 
                (packet instanceof ClickSlotC2SPacket || 
                 packet instanceof PlayerActionC2SPacket)) {
                System.out.println("[TridentDuper] Cancelling packet: " + packet.getClass().getSimpleName());
                ci.cancel();
            }
        }
    }
}