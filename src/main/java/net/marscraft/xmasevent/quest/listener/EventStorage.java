package net.marscraft.xmasevent.quest.listener;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EventStorage {
    private BlockPlaceEvent _blockPlaceEvent;
    private EntityDeathEvent _entityDeathEvent;
    private InventoryCloseEvent _inventoryCloseEvent;

    public void SetBlockPlaceEvent(BlockPlaceEvent event) { _blockPlaceEvent = event; }
    public void SetEntityDeathEvent(EntityDeathEvent event) { _entityDeathEvent = event; }
    public void SetInventoryCloseEvent(InventoryCloseEvent inventoryCloseEvent) { _inventoryCloseEvent = inventoryCloseEvent; }

    public BlockPlaceEvent GetBlockPlaceEvent() { return _blockPlaceEvent; }
    public EntityDeathEvent GetEntityDeathEvent() { return _entityDeathEvent; }
    public InventoryCloseEvent GetInventoryCloseEvent() { return _inventoryCloseEvent; }


}
