package it.polimi.ingsw.common.util;

import com.google.gson.annotations.SerializedName;

/**
 * There are no differences between Colours and Resource Types in our implementation
 */
public enum Resource {
    //White color.
    @SerializedName("white")
    WHITE,
    //Faith resource.
    @SerializedName("faith")
    FAITH,
    //Coin resource.
    @SerializedName("coin")
    COIN,
    //Stone resource.
    @SerializedName("stone")
    STONE,
     //Servant resource.
    @SerializedName("servant")
    SERVANT,
    //Shield resource.
    @SerializedName("shield")
    SHIELD,
    //Generic resource used for the default production power.
    @SerializedName("generic")
    GENERIC;

    @Override
    public String toString() {
        char[] buffer = this.name().toLowerCase().toCharArray();
        if(buffer.length == 0) return null;

        buffer[0] = String.valueOf(buffer[0]).toUpperCase().charAt(0);
        return String.copyValueOf(buffer);
    }
}
