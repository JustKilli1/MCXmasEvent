package net.marscraft.xmasevent.shared.logmanager;

import net.marscraft.xmasevent.Main;

import java.util.logging.Logger;

public class Logmanager implements ILogmanager{

    private final Main _plugin;
    private final Logger _logger;

    public Logmanager(Main plugin) {
        _plugin = plugin;
        _logger = plugin.getLogger();
    }

    @Override
    public void Debug(String msg) {
        _logger.finest(FormatMessage("DEBUG", msg));
    }

    @Override
    public void Info(String msg) {
        _logger.info(FormatMessage("INFO", msg));
    }

    @Override
    public void Warn(String msg) {
        _logger.warning(FormatMessage("WARNING", msg));
    }

    @Override
    public void Error(String msg) {
        _logger.warning(FormatMessage("ERROR", msg));
    }

    @Override
    public void Error(String msg, Exception e) {
        _logger.warning(FormatMessage("ERROR", msg));
        e.printStackTrace();
    }

    @Override
    public void Error(Exception e) {
        _logger.warning(FormatMessage("ERROR"));
        e.printStackTrace();
    }

    private String FormatMessage(String level){
        return FormatMessage(level, "No Content");
    }

    private String FormatMessage(String level, String msg){
        String message = "Level: " + level;
        message += " <|> Message: " + msg;
        return message;
    }
}
