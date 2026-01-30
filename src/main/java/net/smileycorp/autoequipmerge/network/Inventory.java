package net.smileycorp.autoequipmerge.network;

public enum Inventory {
    
    HOTBAR((byte) 0),
    MAIN((byte) 9),
    ARMOUR((byte) 36),
    OFFHAND((byte) 40);
    
    private final byte start;
    
    Inventory(byte start) {
        this.start = start;
    }
    
    public byte getSlot(byte i) {
        return (byte) (start + i);
    }
    
}
