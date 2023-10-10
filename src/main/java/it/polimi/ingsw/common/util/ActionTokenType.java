package it.polimi.ingsw.common.util;

import com.google.gson.annotations.SerializedName;

/** Enumerates the types of action token in the game */
public enum ActionTokenType
{
    //The action token requires two green development cards to be discarded
    @SerializedName("discard_green")
    DEV_CARD_DISCARD_GREEN,
    //The action token requires two yellow development cards to be discarded
    @SerializedName("discard_yellow")
    DEV_CARD_DISCARD_YELLOW,
    //The action token requires two blue development cards to be discarded
    @SerializedName("discard_blue")
    DEV_CARD_DISCARD_BLUE,
    //The action token requires two purple development cards to be discarded
    @SerializedName("discard_purple")
    DEV_CARD_DISCARD_PURPLE,
    //The action token makes the black cross advance on its faith track by 2 spaces
    @SerializedName("cross_advance")
    ADVANCE_BLACK_CROSS,
    //The action token makes the black cross advance on its faith track by 1 space and prescribes a shuffle of the action tokens
    @SerializedName("cross_advance_shuffle")
    SHUFFLE_ADVANCE_BLACK_CROSS
}
