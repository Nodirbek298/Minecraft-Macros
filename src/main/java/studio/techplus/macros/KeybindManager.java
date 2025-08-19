package studio.techplus.macros;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import studio.techplus.macros.modules.Module;

import java.util.HashMap;
import java.util.Map;

public class KeybindManager {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Map<Integer, Boolean> keyStates = new HashMap<>();
    
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.currentScreen != null) return;
            
            for (Module module : ModuleManager.getInstance().getModules()) {
                int keyCode = module.getKeyCode();
                if (keyCode == GLFW.GLFW_KEY_UNKNOWN) continue;
                
                boolean isPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), keyCode);
                boolean wasPressed = keyStates.getOrDefault(keyCode, false);
                
                // Detect key press
                if (isPressed && !wasPressed) {
                    module.toggle();
                }
                
                keyStates.put(keyCode, isPressed);
            }
        });
    }
}