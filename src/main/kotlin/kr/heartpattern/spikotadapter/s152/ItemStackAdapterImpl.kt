package kr.heartpattern.spikotadapter.s152

import kr.heartpattern.spikot.adapter.Adapter
import kr.heartpattern.spikot.adapter.SupportedVersion
import kr.heartpattern.spikot.adapters.ItemStackAdapter
import kr.heartpattern.spikot.adapters.NBTAdapter
import kr.heartpattern.spikot.module.AbstractModule
import kr.heartpattern.spikot.nbt.WrapperNBTCompound
import net.minecraft.server.v1_5_R3.ItemStack
import net.minecraft.server.v1_5_R3.NBTTagCompound
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack
import java.lang.reflect.Field

@Adapter
@SupportedVersion("all")
class ItemStackAdapterImpl : AbstractModule(), ItemStackAdapter {
    private companion object {
        val handleField: Field = CraftItemStack::class.java.getDeclaredField("handle")

        init {
            handleField.isAccessible = true
        }
    }

    private val org.bukkit.inventory.ItemStack.tag: NBTTagCompound?
        get() = if (this is CraftItemStack) (handleField.get(this) as ItemStack?)?.tag else null

    override fun getWrappedTag(itemStack: org.bukkit.inventory.ItemStack): WrapperNBTCompound? {
        return itemStack.tag?.let { NBTAdapter.wrapNBTCompound(it) }
    }

    override fun hasTag(itemStack: org.bukkit.inventory.ItemStack): Boolean {
        return itemStack.tag != null
    }

    override fun isCraftItemStack(itemStack: org.bukkit.inventory.ItemStack): Boolean {
        return itemStack is CraftItemStack
    }

    override fun toCraftItemStack(itemStack: org.bukkit.inventory.ItemStack): org.bukkit.inventory.ItemStack {
        return if (itemStack is CraftItemStack) itemStack
        else CraftItemStack.asCraftMirror(CraftItemStack.asNMSCopy(itemStack))
    }

    override fun fromNBTCompound(nbt: WrapperNBTCompound): org.bukkit.inventory.ItemStack {
        val itemStack = ItemStack.createStack(nbt.tag as NBTTagCompound)
        return CraftItemStack.asCraftMirror(itemStack)
    }

    override fun toNBTCompound(itemStack: org.bukkit.inventory.ItemStack): WrapperNBTCompound {
        return if (itemStack is CraftItemStack) {
            NBTAdapter.wrapNBTCompound((handleField.get(itemStack) as ItemStack).save(NBTTagCompound()))
        } else {
            val converted = CraftItemStack.asCraftCopy(itemStack)
            toNBTCompound(converted)
        }
    }
}