package net.bettercombat.logic;

import com.google.gson.Gson;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.Objects;

public class CustomIdentifier {
    @Nullable
    private String identifier;
    @Nullable
    public int priority = 0;
    @Nullable
    private NBTMatcher[] matcher;
    CustomIdentifier(String identifier,NBTMatcher[] matcher){
        this.identifier=identifier;
        this.matcher=matcher;
    }
    CustomIdentifier(String identifier){
        this.identifier=identifier;
        this.matcher=null;
    }
    public boolean matches(ItemStack itemStack){
        if(identifier!=null){
            Identifier id = Registry.ITEM.getId(itemStack.getItem());
            if(!id.toString().equals(identifier)){
                return false;
            }
        }
        if(matcher!=null){
            if(!itemStack.hasNbt()){
                return false;
            }
            for(NBTMatcher nbtMatcher : matcher){
                if(!nbtMatcher.matches(itemStack.getNbt())){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean matches(CustomIdentifier identifier){
        if(!this.identifier.equals(identifier.identifier)) return false;
        if(this.priority != identifier.priority) return false;
        if(this.matcher==null) return true;
        if(this.matcher.length!=identifier.matcher.length) return false;
        for(NBTMatcher m :matcher){
            //find a way to make sure matchers are the same
            //not required as this checks should only happen for stringids, but its counterintuitive that its not checked
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, priority,matcher);
    }

    @Override
    public String toString() {
        var gson = new Gson();
        return gson.toJson(this);
        /*
        return "CustomIdentifier[" +
                "identifier=" + identifier + ", " +
                "priority=" + priority + ", " +
                "matcher=" + matcher + ']';
         */
    }
}
