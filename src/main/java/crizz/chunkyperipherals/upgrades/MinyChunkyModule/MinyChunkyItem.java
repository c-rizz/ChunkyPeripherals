package crizz.chunkyperipherals.upgrades.MinyChunkyModule;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crizz.chunkyperipherals.ChunkyPeripherals;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class MinyChunkyItem extends Item
{

	public MinyChunkyItem()
	{
		super();
		
		
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("MinyChunkyModuleItem");
	//	LanguageRegistry.addName(this, "Turtle Miny Chunky Module");
		setTextureName("chunkyperipherals:MinyChunkyModuleUpgrade");
		GameRegistry.registerItem(this, "MinyChunkyModuleItem");
		
	}
	
	public void loadRecipe()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(ChunkyPeripherals.minyChunkyModuleItem), new Object[] { new ItemStack(ChunkyPeripherals.chunkyModuleItem), new ItemStack(Items.diamond_pickaxe)});
	}
	

	public IIcon getIcon()
	{
		return itemIcon;
	}
	
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
	{
		par2List.add("A pickaxe for turtles that loads chunks");
	}

}
