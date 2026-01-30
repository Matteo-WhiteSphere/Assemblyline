package org.RisingSMP.factory.conveyor;

import org.RisingSMP.factory.items.FactoryItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class ConveyorSystem {
    
    private static final NamespacedKey CONVEYOR_KEY = new NamespacedKey("factory", "conveyor_type");
    private static final NamespacedKey DIRECTION_KEY = new NamespacedKey("factory", "conveyor_direction");
    private static final NamespacedKey SPEED_KEY = new NamespacedKey("factory", "conveyor_speed");
    
    public enum ConveyorType {
        BASIC(Material.IRON_BLOCK, 1.0),
        FAST(Material.GOLD_BLOCK, 2.0),
        EXPRESS(Material.DIAMOND_BLOCK, 3.0);
        
        private final Material material;
        private final double speed;
        
        ConveyorType(Material material, double speed) {
            this.material = material;
            this.speed = speed;
        }
        
        public Material getMaterial() { return material; }
        public double getSpeed() { return speed; }
    }
    
    public enum Direction {
        NORTH(new Vector(0, 0, -1)),
        SOUTH(new Vector(0, 0, 1)),
        EAST(new Vector(1, 0, 0)),
        WEST(new Vector(-1, 0, 0));
        
        private final Vector vector;
        
        Direction(Vector vector) {
            this.vector = vector;
        }
        
        public Vector getVector() { return vector; }
    }
    
    public static void placeConveyor(Block block, ConveyorType type, Direction direction) {
        if (!(block.getState() instanceof TileState state)) return;
        
        state.getPersistentDataContainer()
                .set(CONVEYOR_KEY, PersistentDataType.STRING, type.name());
        state.getPersistentDataContainer()
                .set(DIRECTION_KEY, PersistentDataType.STRING, direction.name());
        state.getPersistentDataContainer()
                .set(SPEED_KEY, PersistentDataType.DOUBLE, type.getSpeed());
        
        state.update();
    }
    
    public static ConveyorType getType(Block block) {
        if (!(block.getState() instanceof TileState state)) return null;
        
        String typeName = state.getPersistentDataContainer()
                .get(CONVEYOR_KEY, PersistentDataType.STRING);
        
        return typeName != null ? ConveyorType.valueOf(typeName) : null;
    }
    
    public static Direction getDirection(Block block) {
        if (!(block.getState() instanceof TileState state)) return null;
        
        String dirName = state.getPersistentDataContainer()
                .get(DIRECTION_KEY, PersistentDataType.STRING);
        
        return dirName != null ? Direction.valueOf(dirName) : null;
    }
    
    public static double getSpeed(Block block) {
        if (!(block.getState() instanceof TileState state)) return 1.0;
        
        return state.getPersistentDataContainer()
                .getOrDefault(SPEED_KEY, PersistentDataType.DOUBLE, 1.0);
    }
    
    public static void transportItem(Item item, Block conveyor) {
        Direction direction = getDirection(conveyor);
        double speed = getSpeed(conveyor);
        
        if (direction == null) return;
        
        Vector velocity = direction.getVector().multiply(speed * 0.5);
        item.setVelocity(velocity);
    }
    
    public static Location getNextLocation(Block conveyor) {
        Direction direction = getDirection(conveyor);
        if (direction == null) return null;
        
        return conveyor.getLocation().add(direction.getVector());
    }
}
