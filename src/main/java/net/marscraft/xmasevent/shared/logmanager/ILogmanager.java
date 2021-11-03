package net.marscraft.xmasevent.shared.logmanager;

public interface ILogmanager {

    public void Debug(String msg);

    public void Info(String msg);

    public void Warn(String msg);

    public void Error(String msg);

    public void Error(String msg, Exception e);

    public void Error(Exception e);

}
