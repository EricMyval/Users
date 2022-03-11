package com.ericmyval.users.screens.users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.widget.CustomPopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ericmyval.users.R
import com.ericmyval.users.databinding.ItemUserBinding
import com.ericmyval.users.model.User

class UsersAdapter(
    private val actionListener: UserActionListener
): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), View.OnClickListener {

    class UsersViewHolder(val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
        private const val ID_FIRE= 4
    }

    var users: List<UserListItem> = emptyList()
        set(value) {
            val diffCallback = UsersDiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.moreImageViewButton.setOnClickListener(this)

        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val userListItem = users[position]
        val user = userListItem.user
        val context = holder.itemView.context

        with(holder.binding) {
            holder.itemView.tag = user
            moreImageViewButton.tag = user

            if (userListItem.isInProgress) {
                moreImageViewButton.visibility = View.INVISIBLE
                itemProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                moreImageViewButton.visibility = View.VISIBLE
                itemProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@UsersAdapter)
            }

            userNameTextView.text = user.name
            userCompanyTextView.text = user.company.ifBlank { context.getString(R.string.unemployed) }
            Glide.with(photoImageView.context)
                .load(user.photo.ifBlank { R.drawable.ic_user_avatar })
                .circleCrop()
                .placeholder(R.drawable.ic_user_avatar)
                .error(R.drawable.ic_user_avatar)
                .into(photoImageView)
        }
    }

    override fun getItemCount(): Int = users.size

    override fun onClick(view: View) {
        val user = view.tag as User
        when (view.id) {
            R.id.moreImageViewButton -> {
                showPopupMenu(view)
            }
            else -> {
                actionListener.onUserDetails(user)
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = CustomPopupMenu(view.context, view)
        val context = view.context
        val user = view.tag as User
        val position = users.indexOfFirst { it.user.id == user.id }
        // Доступность кнопок
        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
            isEnabled = position > 0
            setIcon(R.drawable.ic_up)
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
            isEnabled = position < users.size - 1
            setIcon(R.drawable.ic_down)
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove)).apply {
            setIcon(R.drawable.ic_delete)
        }
        if (user.company.isNotBlank())
            popupMenu.menu.add(0, ID_FIRE, Menu.NONE, context.getString(R.string.fire)).apply {
                setIcon(R.drawable.ic_fire)
            }
        // Обработка кликов
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> actionListener.onUserMove(user, -1)
                ID_MOVE_DOWN -> actionListener.onUserMove(user, 1)
                ID_REMOVE -> actionListener.onUserDelete(user)
                ID_FIRE -> actionListener.onUserFire(user)
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }
}