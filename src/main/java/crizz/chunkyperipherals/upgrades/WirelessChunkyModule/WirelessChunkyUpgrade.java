package crizz.chunkyperipherals.upgrades.WirelessChunkyModule;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crizz.chunkyperipherals.CCReflectionHelper;
import crizz.chunkyperipherals.ChunkyPeripherals;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyPeripheral;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyUpgrade;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public class WirelessChunkyUpgrade extends ChunkyUpgrade
{

	public static IIcon icon;
	@Override
	public int getUpgradeID()
	{
		return ChunkyPeripherals.wirelessChunkyModuleUpgradeID;
	}

	@Override
	public String getUnlocalisedAdjective()
	{
		return "Wireless Chunky";
	}

	@Override
	public TurtleUpgradeType getType()
	{
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem()
	{
		return new ItemStack(ChunkyPeripherals.wirelessChunkyModuleItem);
	}


	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side)
	{
		return new WirelessChunkyPeripheral(turtle, side);
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction)
	{
		return TurtleCommandResult.failure();
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side)
	{
		return icon;
	}
	
	@Override
	public void update(ITurtleAccess turtle, TurtleSide side)
	{
		//update the chunky part of the peripheral
		super.update(turtle,side);
		
		
		//do what dan does
		if(turtle.getWorld().isRemote)
			return;
		IPeripheral p = turtle.getPeripheral(side);
		if( p!=null && p instanceof WirelessChunkyPeripheral)
		{
			Object subPeripheral = ((WirelessChunkyPeripheral)p).getSubPeripheral();
			if((Boolean) CCReflectionHelper.invokeMethod(subPeripheral, true, "pollChanged"))
			{
				turtle.getUpgradeNBTData(side).setBoolean("active", (Boolean) CCReflectionHelper.invokeMethod(subPeripheral, true, "isActive"));
		        turtle.updateUpgradeNBTData(side);
			}
		}
		else
		{
			if(p!=null)
				ChunkyPeripherals.logger.error("update called on a turtle without wireless chunky module ( p is "+p.getClass().getName()+")");
			else
				ChunkyPeripherals.logger.error("update called on a turtle without wireless chunky module ( p is null )");
		}
	}
	
	
	
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerUpgradeIcon(TextureStitchEvent event)
	{
		//Side side = FMLCommonHandler.instance().getEffectiveSide();
		int mapType = 0;
		mapType = event.map.getTextureType();
		
		
		if(mapType==0)
		{
			icon=event.map.registerIcon("chunkyperipherals:WirelessChunkyModule");
		}
	}
}
