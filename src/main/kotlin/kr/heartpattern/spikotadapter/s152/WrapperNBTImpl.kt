package kr.heartpattern.spikotadapter.s152

import kr.heartpattern.spikot.adapters.NBTAdapter
import kr.heartpattern.spikot.nbt.*
import net.minecraft.server.v1_5_R3.*
import java.lang.UnsupportedOperationException
import java.lang.reflect.Field
import java.util.*

class WrapperNBTEndImpl : WrapperNBTEnd {
    private companion object {
        val TAG: NBTTagEnd

        init {
            val constructor = NBTTagEnd::class.java.getDeclaredConstructor()
            constructor.isAccessible = true
            TAG = constructor.newInstance()
            constructor.isAccessible = false
        }
    }

    override val tag: Any
        get() = TAG

    override val value: Unit
        get() = Unit
}

class WrapperNBTByteImpl(override val tag: NBTTagByte) : WrapperNBTByte {
    constructor(value: Byte) : this(NBTTagByte("", value))

    override val value: Byte
        get() = tag.data
}

class WrapperNBTLongImpl(override val tag: NBTTagLong) : WrapperNBTLong {
    constructor(value: Long) : this(NBTTagLong("", value))

    override val value: Long
        get() = tag.data
}

class WrapperNBTIntImpl(override val tag: NBTTagInt) : WrapperNBTInt {
    constructor(value: Int) : this(NBTTagInt("", value))

    override val value: Int
        get() = tag.data
}

class WrapperNBTShortImpl(override val tag: NBTTagShort) : WrapperNBTShort {
    constructor(value: Short) : this(NBTTagShort("", value))

    override val value: Short
        get() = tag.data
}

class WrapperNBTDoubleImpl(override val tag: NBTTagDouble) : WrapperNBTDouble {
    constructor(value: Double) : this(NBTTagDouble("", value))

    override val value: Double
        get() = tag.data
}

class WrapperNBTFloatImpl(override val tag: NBTTagFloat) : WrapperNBTFloat {
    constructor(value: Float) : this(NBTTagFloat("", value))

    override val value: Float
        get() = tag.data
}

class WrapperNBTByteArrayImpl(override val tag: NBTTagByteArray) : WrapperNBTByteArray {
    constructor(value: ByteArray) : this(NBTTagByteArray("", value))

    override val value: ByteArray
        get() = tag.data
}

class WrapperNBTIntArrayImpl(override val tag: NBTTagIntArray) : WrapperNBTIntArray {
    constructor(value: IntArray) : this(NBTTagIntArray("", value))

    override val value: IntArray
        get() = tag.data
}


class WrapperNBTStringImpl(override val tag: NBTTagString) : WrapperNBTString {
    constructor(value: String) : this(NBTTagString("", value))

    override val value: String
        get() = tag.data
}

class WrapperNBTListImpl<W : WrapperNBTBase<*>>(override val tag: NBTTagList) : WrapperNBTList<W>,
    AbstractMutableList<W>() {
    private companion object {
        val listField = NBTTagList::class.java.getDeclaredField("list")!!
        val typeField = NBTTagList::class.java.getDeclaredField("type")!!

        init {
            listField.isAccessible = true
            typeField.isAccessible = true
        }
    }

    private val list: MutableList<NBTBase>
    private val typeID: Int

    constructor(value: List<W>) : this(NBTTagList()) {
        for (element in value) {
            tag.add(element.tag as NBTBase)
        }
    }

    init {
        @Suppress("UNCHECKED_CAST")
        list = listField.get(tag) as MutableList<NBTBase>
        typeID = (typeField.get(tag) as Byte).toInt()
    }

    override val enclosing: TagType<*>
        get() = TagType.ofId(typeID)

    override val value: MutableList<W>
        get() = this

    override val size: Int
        get() = tag.size()

    override fun add(index: Int, element: W) {
        list.add(index, element.tag as NBTBase)
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): W {
        return NBTAdapter.wrapNBT(tag.get(index)) as W
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAt(index: Int): W {
        return NBTAdapter.wrapNBT(list.removeAt(index)) as W
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(index: Int, element: W): W {
        val old = NBTAdapter.wrapNBT(tag.get(index)) as W
        list[index] = element.tag as NBTBase
        return old
    }
}

class WrapperNBTCompoundImpl(override val tag: NBTTagCompound) : WrapperNBTCompound,
    AbstractMutableMap<String, WrapperNBTBase<*>>() {
    private companion object {
        val mapField = NBTTagCompound::class.java.getDeclaredField("map")!!

        init {
            mapField.isAccessible = true
        }
    }

    constructor(map: Map<String, WrapperNBTBase<*>>) : this(NBTTagCompound()) {
        for ((key, value) in map) {
            tag.set(key, value.tag as NBTBase)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val map = mapField.get(tag) as MutableMap<String, NBTBase>

    override val entries: MutableSet<MutableMap.MutableEntry<String, WrapperNBTBase<*>>>
        get() = object : AbstractMutableSet<MutableMap.MutableEntry<String, WrapperNBTBase<*>>>() {
            override val size: Int
                get() = map.size

            override fun add(element: MutableMap.MutableEntry<String, WrapperNBTBase<*>>): Boolean {
                if (element.key in map && map[element.key] == element.value.tag) return false
                map[element.key] = element.value.tag as NBTBase
                return true
            }

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, WrapperNBTBase<*>>> {
                return object : MutableIterator<MutableMap.MutableEntry<String, WrapperNBTBase<*>>> {
                    private val original = map.iterator()
                    override fun hasNext(): Boolean {
                        return original.hasNext()
                    }

                    override fun next(): MutableMap.MutableEntry<String, WrapperNBTBase<*>> {
                        return original.next().let { SimpleEntry(it.key, NBTAdapter.wrapNBT(it.value)) }
                    }

                    override fun remove() {
                        original.remove()
                    }
                }
            }
        }
    override val keys: MutableSet<String>
        get() = super.keys
    override val size: Int
        get() = super.size
    override val values: MutableCollection<WrapperNBTBase<*>>
        get() = super.values

    override fun clear() {
        map.clear()
    }

    override fun containsKey(key: String): Boolean {
        return tag.hasKey(key)
    }

    override fun containsValue(value: WrapperNBTBase<*>): Boolean {
        return map.containsValue(value.tag as NBTBase)
    }

    override fun get(key: String): WrapperNBTBase<*>? {
        return NBTAdapter.wrapNBT(tag[key])
    }

    override fun isEmpty(): Boolean {
        return tag.isEmpty
    }

    override fun remove(key: String): WrapperNBTBase<*>? {
        return map.remove(key)?.let { NBTAdapter.wrapNBT(it) }
    }

    override fun remove(key: String, value: WrapperNBTBase<*>): Boolean {
        return map.remove(key, value.tag as NBTBase)
    }

    override val value: MutableMap<String, WrapperNBTBase<*>>
        get() = this

    override fun getBoolean(key: String): Boolean {
        return tag.getBoolean(key)
    }

    override fun getByte(key: String): Byte {
        return tag.getByte(key)
    }

    override fun getByteArray(key: String): ByteArray {
        return tag.getByteArray(key)
    }

    override fun getCompound(key: String): WrapperNBTCompound {
        return NBTAdapter.wrapNBTCompound(tag.getCompound(key))
    }

    override fun getDouble(key: String): Double {
        return tag.getDouble(key)
    }

    override fun getEnd(key: String) {
        // Nothing
    }

    override fun getFloat(key: String): Float {
        return tag.getFloat(key)
    }

    override fun getInt(key: String): Int {
        return tag.getInt(key)
    }

    override fun getIntArray(key: String): IntArray {
        return tag.getIntArray(key)
    }

    override fun <W : WrapperNBTBase<*>> getList(key: String, type: TagType<*>): WrapperNBTList<W> {
        return NBTAdapter.wrapNBTList(tag.getList(key))
    }

    override fun getLong(key: String): Long {
        return tag.getLong(key)
    }

    override fun getLongArray(key: String): LongArray {
        return NBTAdapter.wrapNBTLongArray(tag.get(key)).value
    }

    override fun getShort(key: String): Short {
        return tag.getShort(key)
    }

    override fun getString(key: String): String {
        return tag.getString(key)
    }

    override fun getUUID(key: String): UUID {
        throw UnsupportedOperationException("There is no NBTTagCompound#getUUID(String) in 1.5.2")
    }

    override fun hasKeyOfType(key: String, type: TagType<*>): Boolean {
        throw UnsupportedOperationException("There is no NBTTagCompound#hasKeyOfType(String, TagType<?>) in 1.5.2")
    }

    override fun hasUUID(key: String): Boolean {
        throw UnsupportedOperationException("There is no NBTTagCompound#hasUUID(String) in 1.5.2")
    }

    override fun put(key: String, value: WrapperNBTBase<*>): WrapperNBTBase<*>? {
        val old = tag.get(key)?.let { NBTAdapter.wrapNBT(it) }
        tag.set(key, value.tag as NBTBase)
        return old
    }

    override fun setBoolean(key: String, value: Boolean) {
        tag.setBoolean(key, value)
    }

    override fun setByte(key: String, value: Byte) {
        tag.setByte(key, value)
    }

    override fun setByteArray(key: String, value: ByteArray) {
        tag.setByteArray(key, value)
    }

    override fun setCompound(key: String, value: WrapperNBTCompound) {
        tag.set(key, value.tag as NBTBase)
    }

    override fun setDouble(key: String, value: Double) {
        tag.setDouble(key, value)
    }

    override fun setEnd(key: String, value: Unit) {
        tag.set(key, WrapperNBTEndImpl().tag as NBTBase)
    }

    override fun setFloat(key: String, value: Float) {
        tag.setFloat(key, value)
    }

    override fun setInt(key: String, value: Int) {
        tag.setInt(key, value)
    }

    override fun setIntArray(key: String, value: IntArray) {
        tag.setIntArray(key, value)
    }

    override fun <W : WrapperNBTBase<*>> setList(key: String, value: WrapperNBTList<W>) {
        tag.set(key, value.tag as NBTBase)
    }

    override fun setLong(key: String, value: Long) {
        tag.setLong(key, value)
    }

    override fun setLongArray(key: String, value: LongArray) {
        throw UnsupportedOperationException("There is no NBTTagCompound#setLongArray(String, LongArray) in 1.5.2")
    }

    override fun setShort(key: String, value: Short) {
        tag.setShort(key, value)
    }

    override fun setString(key: String, value: String) {
        tag.setString(key, value)
    }

    override fun setUUID(key: String, value: UUID) {
        throw UnsupportedOperationException("There is no NBTTagCompound#setUUID(String, UUID) in 1.5.2")
    }
}