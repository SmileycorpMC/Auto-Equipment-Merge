package net.smileycorp.autoequipmerge.network;

public enum EnumInventoryType {
    
    HOTBAR((byte) 0),
    MAIN((byte) 9),
    ARMOUR((byte) 36),
    OFFHAND((byte) 40);
    
    private final int start;
    
    EnumInventoryType(byte start) {
        this.start = start;
    }
    
    public byte getSlot(byte i) {
        return (byte) (start + i);
    }
    
}
