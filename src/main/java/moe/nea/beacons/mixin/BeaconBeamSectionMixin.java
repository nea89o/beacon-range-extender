package moe.nea.beacons.mixin;

import moe.nea.beacons.BeaconBeamSectionExtra;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BeaconBlockEntity.BeaconBeamSection.class)
public class BeaconBeamSectionMixin implements BeaconBeamSectionExtra {
	@Unique
	private int rangeExtenders;

	@Override
	public void setRangeExtenders_nea(int newRangeExtenders) {
		rangeExtenders = newRangeExtenders;
	}

	@Override
	public int getRangeExtenders_nea() {
		return rangeExtenders;
	}
}
