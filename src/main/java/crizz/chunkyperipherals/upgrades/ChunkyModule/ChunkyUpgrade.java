package crizz.chunkyperipherals.upgrades.ChunkyModule;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crizz.chunkyperipherals.ChunkyPeripherals;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

/** Class to load the Chunk Loader Peripheral
 */
public class ChunkyUpgrade implements ITurtleUpgrade
{

	public static IIcon icon;
	@Override
	public int getUpgradeID()
	{
		return ChunkyPeripherals.chunkyModuleUpgradeID;
	}

	@Override
	public String getUnlocalisedAdjective()
	{
		return "Chunky";
	}

	@Override
	public TurtleUpgradeType getType()
	{
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem()
	{
		return new ItemStack(ChunkyPeripherals.chunkyModuleItem);
	}


	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side)
	{
		return new ChunkyPeripheral(turtle);
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
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerUpgradeIcon(TextureStitchEvent event)
	{
		//Side side = FMLCommonHandler.instance().getEffectiveSide();
		int mapType = 0;
		mapType = event.map.getTextureType();
		
		
		if(mapType==0)
		{
			icon=event.map.registerIcon("chunkyperipherals:TurtleChunkLoader");
		}
	}
	
	@Override
	public void update(ITurtleAccess turtle, TurtleSide side)
	{
		//CRMod.infoLog("chunky.update(): getUpgrade="+turtle.getUpgrade(side));
		if(turtle.getWorld().isRemote)//on Client getPeripheral doesn't work (CC1.63pr3)
			return;
		IPeripheral p = turtle.getPeripheral(side);
		if( p instanceof ChunkyPeripheral)
		{
			((ChunkyPeripheral) p).update();
		}
		else
		{
			if(p!=null)
				ChunkyPeripherals.logger.error("update called on a turtle without chunky module ( p is "+p.getClass().getName()+")");
			else
				ChunkyPeripherals.logger.error("update called on a turtle without chunky module ( p is null )");
		}
	}
}
