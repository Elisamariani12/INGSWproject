package it.polimi.ingsw.common.util;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("JavaDoc")
public enum SpecialAbility {
    //Production ability.
    @SerializedName("production_power")
    PRODUCTION,
    //Storage ability.
    @SerializedName("storage")
    STORAGE,
    //white substitution ability.
    @SerializedName("white_substitution")
    WHITE_MARBLE_SUBSTITUTION,
    //Discount ability.
    @SerializedName("discount")
    DISCOUNT;

    @Override
    public String toString() {
        switch(this) {
            case PRODUCTION: return "PRODUCTION";
            case STORAGE: return "STORAGE";
            case WHITE_MARBLE_SUBSTITUTION: return "WMSost";
            case DISCOUNT: return "DISCOUNT";
        }
        return null;
    }
}
