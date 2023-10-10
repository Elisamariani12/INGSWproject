package it.polimi.ingsw.common.util;

import com.google.gson.annotations.SerializedName;

/**
 * The enum for Card Banner color.
 */
public enum BannerColor {
    /**
     * Green banner color.
     */
    @SerializedName("green")
    GREEN,
    /**
     * Blue banner color.
     */
    @SerializedName("blue")
    BLUE,
    /**
     * Yellow banner color.
     */
    @SerializedName("yellow")
    YELLOW,
    /**
     * Purple banner color.
     */
    @SerializedName("purple")
    PURPLE;

    @Override
    public String toString() {
        char[] buffer = this.name().toLowerCase().toCharArray();
        if(buffer.length == 0) return null;

        buffer[0] = String.valueOf(buffer[0]).toUpperCase().charAt(0);
        return String.copyValueOf(buffer);
    }
}
