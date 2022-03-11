package com.ericmyval.users.screens.users

import androidx.recyclerview.widget.DiffUtil

class UsersDiffCallback(
    private val oldList: List<UserListItem>,
    private val newList: List<UserListItem>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].user.id == newList[newItemPosition].user.id
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
    // работает только для дата класса (сравнивает каждое поле сам)
}