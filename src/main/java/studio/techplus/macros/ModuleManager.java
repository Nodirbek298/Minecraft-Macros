package studio.techplus.macros;

import java.util.ArrayList;
import java.util.List;
import studio.techplus.macros.modules.Module;
import studio.techplus.macros.modules.AutoTotem;

public class ModuleManager {
    private static ModuleManager instance;
    private final List<Module> modules;
    
    private ModuleManager() {
        modules = new ArrayList<>();
        initModules();
    }
    
    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }
    
    private void initModules() {
        // Register all modules here
        modules.add(new AutoTotem());
        
        // Enable modules by default (Will be confugurable in the future)
        getModule("AutoTotem").onEnable();
    }
    
    public void onTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }
    
    public void onInventoryOpen() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onInventoryOpen();
            }
        }
    }
    
    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
    
    public List<Module> getModules() {
        return modules;
    }
}