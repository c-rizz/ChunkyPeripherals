package crizz.chunkyperipherals;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CRModToggleInfoLogging implements ICommand
{

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName()
	{
		return "CRModToggleInfoLogging";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/CRModToggleInfoLogging";
	}

	@Override
	public List getCommandAliases()
	{
		List l= new ArrayList();
		l.add("CRModToggleInfoLogging");
		return l;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring)
	{
		ChunkyPeripherals.activateInfoLogging = !ChunkyPeripherals.activateInfoLogging;
		if(ChunkyPeripherals.activateInfoLogging)
			ChunkyPeripherals.logger.info("info logging enabled");
		else
			ChunkyPeripherals.logger.info("info logging disabled");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender)
	{
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,	String[] astring)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}

}
