package net.marscraft.xmasevent.quest.listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EventStorage {
    private BlockPlaceEvent _blockPlaceEvent;
    private BlockBreakEvent _blockBreakEvent;
    private EntityDeathEvent _entityDeathEvent;
    private InventoryClickEvent _invClickEvent;
    private String[] _commandArgs;

    public void SetBlockPlaceEvent(BlockPlaceEvent event) { _blockPlaceEvent = event; }
    public void SetEntityDeathEvent(EntityDeathEvent event) { _entityDeathEvent = event; }
    public void SetInventoryClickEvent(InventoryClickEvent event) { _invClickEvent = event; }
    public void SetBlockBreakEvent(BlockBreakEvent event) { _blockBreakEvent = event; }
    public void SetCommandArgs(String[] args) { _commandArgs = args; }

    public BlockPlaceEvent GetBlockPlaceEvent() { return _blockPlaceEvent; }
    public EntityDeathEvent GetEntityDeathEvent() { return _entityDeathEvent; }
    public InventoryClickEvent GetInventoryClickEvent() { return _invClickEvent; }
    public BlockBreakEvent GetBlockBreakEvent() { return _blockBreakEvent; }
    public String[] GetCommandArgs() { return _commandArgs; }
}
