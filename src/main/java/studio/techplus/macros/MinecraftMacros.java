package studio.techplus.macros;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class MinecraftMacros implements ClientModInitializer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && mc.interactionManager != null) {
                checkAndMoveTotem();
            }
        });
    }
    
    private void checkAndMoveTotem() {
        ItemStack offhand = mc.player.getOffHandStack();
        
        if (offhand.isEmpty() || offhand.getItem() != Items.TOTEM_OF_UNDYING) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    mc.interactionManager.clickSlot(0, i + 36, 40, SlotActionType.SWAP, mc.player);
                    break;
                }
            }
        }
    }
}