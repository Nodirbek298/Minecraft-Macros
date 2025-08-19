package studio.techplus.macros;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class MinecraftMacros implements ClientModInitializer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && mc.interactionManager != null && mc.currentScreen == null) {
                checkHotbarTotem();
            }
        });
        
        // Check inventory when opened // This is to ensure auto totem is not too op
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof InventoryScreen && mc.player != null && mc.interactionManager != null) {
                checkInventoryTotem();
            }
        });
    }
    
    private void checkHotbarTotem() {
        ItemStack offhand = mc.player.getOffHandStack();
        
        if (offhand.isEmpty()) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    mc.interactionManager.clickSlot(0, i + 36, 40, SlotActionType.SWAP, mc.player);
                    break;
                }
            }
        }
    }
    
    private void checkInventoryTotem() {
        ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.isEmpty()) {
            // Check if totem is in the inventory and move it to the offhand
            for (int i = 9; i < 36; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        i,
                        40,
                        SlotActionType.SWAP,
                        mc.player
                    );
                    return;
                }
            }
        }
    }
}