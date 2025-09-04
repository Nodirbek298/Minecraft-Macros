package studio.techplus.macros.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Educational demonstration of the patched trident dupe exploit.
 * Credit goes to Killet Laztec & Ionar
 */
public class TridentDuper extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Settings
    private double delay = 0; // Delay between each dupe cycle
    private double chargeDelay = 5; // Delay between trident charge and throw
    private boolean dropTridents = true; // Drop tridents in last hotbar slot
    private boolean durabilityManagement = true; // Dupe highest durability trident
    
    // State
    private boolean cancel = true;
    private final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    private final List<ScheduledTask2> scheduledTasks2 = new ArrayList<>();
    
    private static class ScheduledTask {
        final long executeTime;
        final Runnable task;
        
        ScheduledTask(long executeTime, Runnable task) {
            this.executeTime = executeTime;
            this.task = task;
        }
    }
    
    private static class ScheduledTask2 {
        final double executeTime;
        final Runnable task;
        
        ScheduledTask2(double executeTime, Runnable task) {
            this.executeTime = executeTime;
            this.task = task;
        }
    }
    
    public TridentDuper() {
        super("TridentDuper", "To activate (Numpad 2)", GLFW.GLFW_KEY_KP_2);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("§e[TridentDuper] Enabled"), false);
            scheduledTasks.clear();
            scheduledTasks2.clear();
            dupe();
        }
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        cancel = false;
        scheduledTasks.clear();
        scheduledTasks2.clear();
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("§c[TridentDuper] Disabled"), false);
        }
    }
    
    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.interactionManager == null) return;
        
        long currentTime = System.currentTimeMillis();
        
        // Handle first task list
        List<ScheduledTask> tasksToRun1 = new ArrayList<>();
        {
            Iterator<ScheduledTask> iterator = scheduledTasks.iterator();
            while (iterator.hasNext()) {
                ScheduledTask entry = iterator.next();
                if (entry.executeTime <= currentTime) {
                    tasksToRun1.add(entry);
                    iterator.remove();
                }
            }
        }
        
        // Handle second task list
        List<ScheduledTask2> tasksToRun2 = new ArrayList<>();
        {
            Iterator<ScheduledTask2> iterator = scheduledTasks2.iterator();
            while (iterator.hasNext()) {
                ScheduledTask2 entry = iterator.next();
                if (entry.executeTime <= currentTime) {
                    tasksToRun2.add(entry);
                    iterator.remove();
                }
            }
        }
        
        // Execute tasks after iteration is complete
        for (ScheduledTask task : tasksToRun1) {
            task.task.run();
        }
        for (ScheduledTask2 task : tasksToRun2) {
            task.task.run();
        }
    }
    
    private void dupe() {
        if (mc.player == null || mc.interactionManager == null) return;
        
        int lowestHotbarSlot = 0;
        int lowestHotbarDamage = 1000;
        
        for (int i = 0; i < 9; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TRIDENT || stack.getItem() == Items.BOW) {
                int currentHotbarDamage = stack.getDamage();
                if (lowestHotbarDamage > currentHotbarDamage) {
                    lowestHotbarSlot = i;
                    lowestHotbarDamage = currentHotbarDamage;
                }
            }
        }
        
        // Start charging the trident/bow
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        cancel = true;
        
        int finalLowestHotbarSlot = lowestHotbarSlot;
        scheduleTask(() -> {
            cancel = false;
            
            if (durabilityManagement) {
                if (finalLowestHotbarSlot != 0) {
                    mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        44,
                        0,
                        SlotActionType.SWAP,
                        mc.player
                    );
                    
                    if (dropTridents) {
                        mc.interactionManager.clickSlot(
                            mc.player.currentScreenHandler.syncId,
                            44,
                            0,
                            SlotActionType.THROW,
                            mc.player
                        );
                    }
                    
                    // Swap best trident to slot 0
                    mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        36 + finalLowestHotbarSlot,
                        0,
                        SlotActionType.SWAP,
                        mc.player
                    );
                }
            }

            // Swap with inventory crafting slot
            mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                3,
                0,
                SlotActionType.SWAP,
                mc.player
            );
            
            // Send release packet
            PlayerActionC2SPacket packet2 = new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
                BlockPos.ORIGIN,
                Direction.DOWN,
                0
            );
            mc.getNetworkHandler().sendPacket(packet2);
            
            // Drop duped item if configured
            if (dropTridents) {
                mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    44,
                    0,
                    SlotActionType.THROW,
                    mc.player
                );
            }
            
            cancel = true;
            scheduleTask2(this::dupe, delay * 100);
        }, chargeDelay * 100);
    }
    
    public void scheduleTask(Runnable task, double tridentThrowTime) {
        // throw trident
        long executeTime = System.currentTimeMillis() + (int) tridentThrowTime;
        scheduledTasks.add(new ScheduledTask(executeTime, task));
    }
    
    public void scheduleTask2(Runnable task, double delayMillis) {
        // dupe loop
        double executeTime = System.currentTimeMillis() + delayMillis;
        scheduledTasks2.add(new ScheduledTask2(executeTime, task));
    }
    public boolean shouldCancelPacket() {
        return cancel;
    }
}