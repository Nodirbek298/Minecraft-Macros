package studio.techplus.macros.modules;

public abstract class Module {
    private String name;
    private String description;
    private boolean enabled;
    
    public Module(String name, String description) {
        this.name = name;
        this.description = description;
        this.enabled = false;
    }
    
    public void onEnable() {
        enabled = true;
    }
    
    public void onDisable() {
        enabled = false;
    }
    
    public abstract void onTick();
    
    public void onInventoryOpen() {
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void toggle() {
        if (enabled) {
            onDisable();
        } else {
            onEnable();
        }
    }
}