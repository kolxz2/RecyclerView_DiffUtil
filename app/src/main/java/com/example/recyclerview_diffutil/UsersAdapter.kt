package com.example.recyclerview_diffutil

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recyclerview_diffutil.databinding.ItemUserBinding
import com.example.recyclerview_diffutil.model.User

interface UserActionListener{
    /* interface for itemView action */
    fun userMove(user: User, move: Int)

    fun onUserDelete(user: User)

    fun onUserDetails(user: User)

    fun onUserFire(user: User)
}

class UserDiffCallback(
    private val oldList: List<User>,
    private val newList: List<User>
) : DiffUtil.Callback(){
    /* listen what new in list */
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        /* comparison by object links */
        val  oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.id == newUser.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        /* comparison by content */
        val  oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser == newUser
    }

}

class UsersAdapter(
    private val actionListener: UserActionListener
): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), View.OnClickListener {

    var users: List<User> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(newValue) {
            val diffCallback = UserDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            // redraw list in Activity
            diffResult.dispatchUpdatesTo(this)

         //   notifyDataSetChanged()  // common object to update RecyclerView without animation
        }

    override fun onClick(v: View) {
        // get selected user
        val user = v.tag as User
        // where clicked
        when(v.id){
            R.id.moreImageView ->{
                showPopupMenu(v)
            }
            else -> {
                actionListener.onUserDetails(user)
            }
        }
    }

    private fun showPopupMenu(view: View) {
        /* create popupMenu for itemView RecyclerView */

        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val user = view.tag as User
        val position = users.indexOfFirst { it == user}

        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
            // turn off button if itemView first
            isVisible = position > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
            // turn off button if itemView last
            isVisible = position < users.size - 1
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove))
        popupMenu.menu.add(0, ID_FIRE, Menu.NONE, context.getString((R.string.fire)))

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                ID_MOVE_UP -> {
                    actionListener.userMove(user, -1)
                }
                ID_MOVE_DOWN ->{
                    actionListener.userMove(user, 1)
                }
                ID_REMOVE->{
                    actionListener.onUserDelete(user)
                }
                ID_FIRE ->{
                    actionListener.onUserFire(user)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()

    }


    class UsersViewHolder(
        val binding: ItemUserBinding
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        // setting listener for itemView and moreButton
        binding.root.setOnClickListener(this)
        binding.moreImageView.setOnClickListener(this)

        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        /* updated hidden itemViews */
        val user = users[position]
        val context = holder.itemView.context
        with(holder.binding){
            // put user into tag to understand that itemView is clicked
            holder.itemView.tag = user
            moreImageView.tag = user

            userCompanyTextView.text = if(user.company.isNotBlank())
                user.company else context.getString(R.string.unemployed)
            userNameTextView.text = user.name
            if (user.photo.isNotBlank()){
                Glide.with(photoImageView.context)
                    .load(user.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(photoImageView)
            } else {
                photoImageView.setImageResource(R.drawable.ic_person)
            }
        }
    }

    override fun getItemCount(): Int = users.size

    companion object{
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
        private const val ID_FIRE = 4
    }
}