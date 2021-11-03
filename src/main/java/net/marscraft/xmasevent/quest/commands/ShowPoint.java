package net.marscraft.xmasevent.quest.commands;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

public class ShowPoint implements CommandExecutor {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public ShowPoint(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        player.sendMessage("Aktueller Punktestand: " + _sql.GetPlayerQuestValueInt(player));

        return true;
    }
}
