package net.bettercombat.logic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtType;

import javax.annotation.Nullable;
import java.util.Objects;

public class NBTMatcher {
    private String key;
    @Nullable
    private String comparator;
    @Nullable
    private String value;
    public boolean matches(NbtCompound nbtTag){
        if(!nbtTag.contains(key)) return false;
        if(comparator==null || value == null ) return true;
        if(comparator==null) comparator = "=";
        NbtElement element = nbtTag.get(key);
        switch (comparator){
            case "=":
                return element.toString().equals(value);
            case "!=":
                return !element.toString().equals(value);
            default:
                return false;
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(key, comparator,value);
    }

    @Override
    public String toString() {
        return "NBTMatcher[" +
                    "key=" + key + ", " +
                    "comparator=" + comparator + ", " +
                    "value=" + value + ']';
    }
}
