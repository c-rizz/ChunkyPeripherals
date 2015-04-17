package crizz.chunkyperipherals.upgrades.WirelessChunkyModule;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.registry.GameRegistry;
import crizz.chunkyperipherals.CCReflectionHelper;
import crizz.chunkyperipherals.ChunkyPeripherals;

public class WirelessChunkyItem extends Item
{

	public WirelessChunkyItem()
	{
		super();
		
		
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("WirelessChunkyModuleItem");
	//	LanguageRegistry.addName(this, "Turtle Miny Chunky Module");
		setTextureName("chunkyperipherals:WirelessChunkyModule");
		GameRegistry.registerItem(this, "WirelessChunkyModuleItem");
		
	}
	
	public void loadRecipe()
	{
		
		Object wirelessUpgrade = CCReflectionHelper.runMainCCClassMethod("getTurtleUpgrade", 1);
		GameRegistry.addShapelessRecipe(new ItemStack(ChunkyPeripherals.wirelessChunkyModuleItem),
										new Object[]
										{ 
											new ItemStack(ChunkyPeripherals.chunkyModuleItem),
											CCReflectionHelper.invokeMethod(wirelessUpgrade,true, "getCraftingItem")
										});
	}
	

	public IIcon getIcon()
	{
		return itemIcon;
	}
	
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
	{
		par2List.add("A wireless modem for turtles\nthat keeps the turtle loaded");
	}

}
