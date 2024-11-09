package moe.nea.beacons.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moe.nea.beacons.BeaconBeamSectionExtra;
import moe.nea.beacons.BeaconBlockEntityExtra;
import moe.nea.beacons.BeaconRangeExtender;
import moe.nea.beacons.BeaconRangeIncreaser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class BeaconMixin implements BeaconBlockEntityExtra {

	@Shadow
	private List<BeaconBlockEntity.BeaconBeamSection> checkingBeamSections;
	@Unique
	private int rangeExtenders;

	@Override
	public int getRangeExtender_nea() {
		return rangeExtenders;
	}

	@Override
	public void setRangeExtender_nea(int rangeExtender) {
		this.rangeExtenders = rangeExtender;
	}

	@Override
	public List<BeaconBlockEntity.BeaconBeamSection> getCheckingBeamSections_nea() {
		return this.checkingBeamSections;
	}

	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;beamSections:Ljava/util/List;", opcode = Opcodes.PUTFIELD))
	private static void saveRangeExtender(Level level, BlockPos blockPos, BlockState blockState, BeaconBlockEntity beaconBlockEntity, CallbackInfo ci) {
		if (level.isClientSide) return;
		var extra = BeaconBlockEntityExtra.cast(beaconBlockEntity);
		var accExtraRange = 0;
		for (var beaconBeamSection : extra.getCheckingBeamSections_nea()) {
			var extraSection = BeaconBeamSectionExtra.cast(beaconBeamSection);
			accExtraRange += extraSection.getRangeExtenders_nea();
		}
		extra.setRangeExtender_nea(accExtraRange);
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;"))
	private static BlockPos checkForRangeExtender(
			BlockPos oldBlockPos, Operation<BlockPos> original, @Local BeaconBlockEntity.BeaconBeamSection lastSection,
			@Local(argsOnly = true) Level level) {
		var blockState = level.getBlockState(oldBlockPos);
		if (!level.isClientSide && blockState.getBlock() instanceof BeaconRangeExtender extender && lastSection != null) {
			var lastSectionExtra = BeaconBeamSectionExtra.cast(lastSection);
			lastSectionExtra.setRangeExtenders_nea(
					extender.extraBeaconRange_nea() + lastSectionExtra.getRangeExtenders_nea()
			);
		}
		return original.call(oldBlockPos);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)V"))
	private static void applyExtraEffects(Level level, BlockPos blockPos, BlockState blockState, BeaconBlockEntity beaconBlockEntity, CallbackInfo ci) {
		var extra = BeaconBlockEntityExtra.cast(beaconBlockEntity);
		lastRangeExtend = extra.getRangeExtender_nea();
	}

	@Unique
	private static int lastRangeExtend = -1;

	@ModifyArg(method = "applyEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"))
	private static double increaseRange(double d) {
		if (lastRangeExtend < 0) {
			BeaconRangeIncreaser.LOGGER.error("Could not get extended range from beacon range increase mod (which is awesome, btw)");
			return d;
		}
		d += lastRangeExtend;
		lastRangeExtend = -1;
		return d;
	}

}
