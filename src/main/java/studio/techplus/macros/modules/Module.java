package studio.techplus.macros.modules;

import org.lwjgl.glfw.GLFW;

public abstract class Module {
    private String name;
    private String description;
    private boolean enabled;
    private int keyCode;
    
    public Module(String name, String description) {
        this.name = name;
        this.description = description;
        this.enabled = false;
        this.keyCode = GLFW.GLFW_KEY_UNKNOWN;
    }
    
    public Module(String name, String description, int keyCode) {
        this.name = name;
        this.description = description;
        this.enabled = false;
        this.keyCode = keyCode;
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
    
    public int getKeyCode() {
        return keyCode;
    }
    
    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
}