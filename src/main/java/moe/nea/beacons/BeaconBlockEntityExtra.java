package moe.nea.beacons;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;

import java.util.List;

public interface BeaconBlockEntityExtra {
	static BeaconBlockEntityExtra cast(BeaconBlockEntity beaconBlockEntity) {
		return (BeaconBlockEntityExtra) beaconBlockEntity;
	}

	void setRangeExtender_nea(int rangeExtender);

	int getRangeExtender_nea();

	List<BeaconBlockEntity.BeaconBeamSection> getCheckingBeamSections_nea();
}
