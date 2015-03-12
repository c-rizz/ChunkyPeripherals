package crizz.chunkyperipherals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import crizz.chunkyperipherals.utils.TicketManager;

public class CRModListLoadedChunks implements ICommand
{

	@Override
	public int compareTo(Object arg0)
	{
		return -1;
	}

	@Override
	public String getCommandName()
	{
		return "CRModListLoadedChunks";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/CRModListLoadedChunks";
	}

	@Override
	public List getCommandAliases()
	{
		List l= new ArrayList();
		l.add("CRModListLoadedChunks");
		return l;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring)
	{
		ChunkyPeripherals.logger.info("CRMod's chunk list:");
		Collection<Ticket> c =TicketManager.myChunkList;
		Iterator<Ticket> i = c.iterator();
		int count=0;
		while (i.hasNext())
		{
			Ticket t = i.next();
			ChunkyPeripherals.logger.info("ticket #"+(count++)+" ="+t /*+ "world= " + t.world.getWorldInfo().getWorldName()*/);
			ImmutableSet<ChunkCoordIntPair> sc = t.getChunkList();
			ImmutableList<ChunkCoordIntPair> lc = sc.asList();
			Iterator<ChunkCoordIntPair> ic = lc.iterator();
			while(ic.hasNext())
			{
				ChunkCoordIntPair ccoord = ic.next();
				ChunkyPeripherals.logger.info("chunk " + ccoord.toString() + "("+ ccoord.chunkXPos*16+" ; "+ ccoord.chunkZPos*16 + ")");
			}
		}
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

