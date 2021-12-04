package net.marscraft.xmasevent.shared.logmanager;

public interface ILogmanager {
    void Debug(String msg);
    void Info(String msg);
    void Warn(String msg);
    void Error(String msg);
    void Error(String msg, Exception e);
    void Error(Exception e);
}
