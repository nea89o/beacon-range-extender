package moe.nea.beacons;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public interface BeaconBeamSectionExtra {
	static BeaconBeamSectionExtra cast(BeaconBlockEntity.BeaconBeamSection beaconBeamSection) {
		return (BeaconBeamSectionExtra) beaconBeamSection;
	}

	void setRangeExtenders_nea(int newRangeExtenders);

	int getRangeExtenders_nea();
}
