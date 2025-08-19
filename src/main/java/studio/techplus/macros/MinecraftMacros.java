package studio.techplus.macros;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MinecraftMacros implements ClientModInitializer {
    private static ModuleManager moduleManager;
    
    @Override
    public void onInitializeClient() {
        moduleManager = ModuleManager.getInstance();
        
        // Initialize keybind system
        KeybindManager.init();
        
        // Handle module ticks
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            moduleManager.onTick();
        });
        
        // Inventory checking is handled by MinecraftClientMixin
    }
    
    public static ModuleManager getModuleManager() {
        return moduleManager;
    }
}