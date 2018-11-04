package cn.acewill.mobile.pos.interfices;


import cn.acewill.mobile.pos.model.dish.DishType;

/**
 * Created by DHH on 2016/12/23.
 */

public interface OnEmojiClickListener {
    void onEmojiDelete();

    void onEmojiClick(DishType dishType);
}
