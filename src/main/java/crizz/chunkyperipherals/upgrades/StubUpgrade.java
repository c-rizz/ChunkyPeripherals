package crizz.chunkyperipherals.upgrades;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import crizz.chunkyperipherals.ChunkyPeripherals;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyUpgrade;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public class StubUpgrade implements ITurtleUpgrade
{
		@Override
		public int getUpgradeID() {
			// TODO Auto-generated method stub
			return 260;
		}

		@Override
		public String getUnlocalisedAdjective() {
			// TODO Auto-generated method stub
			return "stub";
		}

		@Override
		public TurtleUpgradeType getType() {
			// TODO Auto-generated method stub
			return TurtleUpgradeType.Peripheral;
		}

		@Override
		public ItemStack getCraftingItem() {
			// TODO Auto-generated method stub
			return new ItemStack(ChunkyPeripherals.chunkyModuleItem);
		}

		@Override
		public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side,
				TurtleVerb verb, int direction) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
			// TODO Auto-generated method stub
			return ChunkyUpgrade.icon;
		}

		@Override
		public void update(ITurtleAccess turtle, TurtleSide side) {
			// TODO Auto-generated method stub
			
		}
}
