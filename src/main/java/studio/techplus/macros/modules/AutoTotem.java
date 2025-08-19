package studio.techplus.macros.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private long lastMoveMs = 0L;
    private static final long COOLDOWN_MS = 500L;
    
    public AutoTotem() {
        super("AutoTotem", "Automatically moves totems to offhand");
    }
    
    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null) return;
        
        long now = System.currentTimeMillis();
        if (now - lastMoveMs < COOLDOWN_MS) {
            return;
        }
        
        ItemStack offhand = mc.player.getOffHandStack();
        if (!offhand.isEmpty()) {
            return;
        }
        
        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            ItemStack stack = mc.player.getInventory().getStack(hotbarSlot);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                System.out.println("[AutoTotem] Found totem in hotbar slot " + hotbarSlot);
                
                // Just swaps the hotbar slot with offhand (like pressing F)
                mc.interactionManager.clickSlot(
                    mc.player.playerScreenHandler.syncId,
                    36 + hotbarSlot,
                    40,
                    SlotActionType.SWAP,
                    mc.player
                );
                
                lastMoveMs = now;
                break;
            }
        }
    }
    
    @Override
    public void onInventoryOpen() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.interactionManager == null) {
            return;
        }
        
        System.out.println("[AutoTotem] Inventory opened!");
        
        long now = System.currentTimeMillis();
        if (now - lastMoveMs < COOLDOWN_MS) {
            return;
        }
        
        ItemStack offhand = mc.player.getOffHandStack();
        if (!offhand.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                System.out.println("[AutoTotem] Found totem in inventory slot " + i);
                int slotId = i < 9 ? i + 36 : i;
                
                // Pick up the totem first
                mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    slotId,
                    0,
                    SlotActionType.PICKUP,
                    mc.player
                );
                
                // Then place it in offhand
                mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    45,
                    0,
                    SlotActionType.PICKUP,
                    mc.player
                );
                
                lastMoveMs = now;
                break;
            }
        }
    }
}