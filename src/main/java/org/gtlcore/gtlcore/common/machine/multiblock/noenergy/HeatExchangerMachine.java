package org.gtlcore.gtlcore.common.machine.multiblock.noenergy;

import org.gtlcore.gtlcore.GTLCore;
import org.gtlcore.gtlcore.api.machine.multiblock.NoEnergyMultiblockMachine;
import org.gtlcore.gtlcore.common.data.GTLMaterials;
import org.gtlcore.gtlcore.utils.MachineUtil;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;

public class HeatExchangerMachine extends NoEnergyMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            HeatExchangerMachine.class, NoEnergyMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private static final Fluid SupercriticalSteam = GTLMaterials.SupercriticalSteam.getFluid();
    private static final Fluid DistilledWater = GTMaterials.DistilledWater.getFluid();

    public HeatExchangerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Persisted
    private long count = 0;

    public static GTRecipe recipeModifier(MetaMachine machine, @NotNull GTRecipe recipe) {
        if (machine instanceof HeatExchangerMachine hMachine) {
            if (FluidRecipeCapability.CAP.of(recipe.inputs.get(FluidRecipeCapability.CAP)
                    .get(1).getContent()).getStacks()[0].getFluid() == Fluids.WATER) {
                return GTRecipeModifiers.accurateParallel(machine, recipe, Integer.MAX_VALUE, false).getFirst();
            }
            final Pair<GTRecipe, Integer> result = GTRecipeModifiers.accurateParallel(machine, new GTRecipeBuilder(GTLCore.id("heat_exchanger"), GTRecipeTypes.DUMMY_RECIPES)
                    .inputFluids(FluidRecipeCapability.CAP.of(recipe.inputs
                            .get(FluidRecipeCapability.CAP).get(0).getContent()))
                    .outputFluids(FluidRecipeCapability.CAP.of(recipe.outputs
                            .get(FluidRecipeCapability.CAP).get(0).getContent()))
                    .duration(200)
                    .buildRawRecipe(), Integer.MAX_VALUE, false);
            long count = result.getSecond() * recipe.data.getLong("eu");
            if (MachineUtil.inputFluid(hMachine, FluidStack.create(DistilledWater, count / 160))) {
                hMachine.count = count / 16;
                return result.getFirst();
            }
        }
        return null;
    }

    @Override
    public void afterWorking() {
        if (count != 0) {
            MachineUtil.outputFluid(this, FluidStack.create(SupercriticalSteam, count));
        }
        count = 0;
        super.afterWorking();
    }
}
