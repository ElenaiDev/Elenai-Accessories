package com.elenai.elenaiaccessories.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elenai.elenaiaccessories.subscriber.CommonEventBusSubscriber;
import com.elenai.elenaidodge2.api.DodgeEvent;
import com.elenai.elenaidodge2.api.FeathersHelper;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class ItemArtfulDodger extends Item {

	private int force = 10;
	private int regen = 20;

	public ItemArtfulDodger() {
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC));
	}

	@Override
	public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(TextFormatting.GRAY + "When on Hand:"));
		list.add(new StringTextComponent(TextFormatting.BLUE + "+" + String.valueOf(regen) + " Regeneration Speed"));
		list.add(new StringTextComponent(TextFormatting.RED + "-" + String.valueOf(force) + "% Dodge Force"));
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, CompoundNBT unused) {
		ICurio curio = new ICurio() {
			@Override
			public boolean canRightClickEquip() {
				return true;
			}

			@Override
			public boolean canEquip(String identifier, LivingEntity entityLivingBase) {
				return !CuriosApi.getCuriosHelper()
						.findEquippedCurio(CommonEventBusSubscriber.artfulDodger, entityLivingBase).isPresent();
			}

			@Override
			public void onEquip(String identifier, int index, LivingEntity livingEntity) {
				if (livingEntity instanceof ServerPlayerEntity) {
					FeathersHelper helper = new FeathersHelper();
					helper.decreaseRegenModifier((ServerPlayerEntity) livingEntity, 20);
				}
			}

			@Override
			public void onUnequip(String identifier, int index, LivingEntity livingEntity) {
				if (livingEntity instanceof ServerPlayerEntity) {
					FeathersHelper helper = new FeathersHelper();
					helper.increaseRegenModifier((ServerPlayerEntity) livingEntity, 20);
				}
			}
		};

		return new ICapabilityProvider() {
			private final LazyOptional<ICurio> curioOpt = LazyOptional.of(() -> curio);

			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
				return CuriosCapability.ITEM.orEmpty(cap, curioOpt);
			}
		};
		
	}
	
	@SubscribeEvent
	public void onDodge(DodgeEvent.ServerDodgeEvent event) {
		
		if(CuriosApi.getCuriosHelper().findEquippedCurio(CommonEventBusSubscriber.artfulDodger, event.getPlayer())
		.isPresent()) {
			event.setForce((event.getForce()/100) * 90);
		}
		
	}
}