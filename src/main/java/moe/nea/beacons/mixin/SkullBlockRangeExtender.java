package moe.nea.beacons.mixin;

import moe.nea.beacons.BeaconRangeExtender;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SkullBlock.class)
public class SkullBlockRangeExtender implements BeaconRangeExtender {
	@Override
	public int extraBeaconRange_nea() {
		return ((SkullBlock) (Object) this) instanceof WitherSkullBlock ? 32 : 5;
	}
}
