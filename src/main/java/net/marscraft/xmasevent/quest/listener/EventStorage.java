package net.marscraft.xmasevent.quest.listener;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EventStorage {

    private BlockPlaceEvent _blockPlaceEvent;

    private EntityDeathEvent _entityDeathEvent;

    public void SetBlockPlaceEvent(BlockPlaceEvent event) { _blockPlaceEvent = event; }
    public void SetEntityDeathEvent(EntityDeathEvent event) { _entityDeathEvent = event; }

    public BlockPlaceEvent GetBlockPlaceEvent() { return _blockPlaceEvent; }
    public EntityDeathEvent GetEntityDeathEvent() { return _entityDeathEvent; }

}
