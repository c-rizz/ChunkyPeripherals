package crizz.chunkyperipherals.upgrades.MinyChunkyModule;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crizz.chunkyperipherals.ChunkyPeripherals;
import crizz.chunkyperipherals.upgrades.ChunkyModule.*;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public class MinyChunkyUpgrade extends ChunkyUpgrade implements ITurtleUpgrade
{
	public static IIcon iconRight;
	public static IIcon iconLeft;
	@Override
	public int getUpgradeID()
	{
		return ChunkyPeripherals.minyChunkyModuleUpgradeID;
	}

	
	@Override
	public String getUnlocalisedAdjective()
	{
		return "MinyChunky";
	}

	@Override
	public TurtleUpgradeType getType()
	{
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem()
	{
		return new ItemStack(ChunkyPeripherals.minyChunkyModuleItem);
	}


	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side)
	{
		return new MinyChunkyPeripheral(turtle);
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction)
	{
		return TurtleCommandResult.failure();
		/*
		if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT)//on Client getPeripheral doesn't work (CC1.63pr3)
			return TurtleCommandResult.failure();
		
		if(verb==TurtleVerb.Dig)
		{
			IPeripheral p = turtle.getPeripheral(side);
			
			if(direction>1)//se la direzione non è su o giù
				direction = 2;
			ITurtleCommand dig = new MinyChunkyPeripheral.DigCommand(direction);
			
			return dig.execute(turtle);
		}
		else
		{
			return TurtleCommandResult.failure("Unsupported action");
		}
		*/
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side)
	{
		if(side==TurtleSide.Left)
			return iconLeft;
		else
			return iconRight;
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerUpgradeIcon(TextureStitchEvent event)
	{
		//Side side = FMLCommonHandler.instance().getEffectiveSide();
		if(event.map.getTextureType()==0)
		{
			iconRight	=	event.map.registerIcon("chunkyperipherals:MinyChunkyModuleUpgradeRight");
			iconLeft	=	event.map.registerIcon("chunkyperipherals:MinyChunkyModuleUpgradeLeft");
		}
	}

}
