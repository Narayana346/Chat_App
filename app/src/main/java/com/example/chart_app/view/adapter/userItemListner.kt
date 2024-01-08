package com.example.chart_app.view.adapter

import androidx.cardview.widget.CardView
import com.example.chart_app.model.User

interface UsersItemClickListener {
    fun onItemClick(user: User)

    fun onItemLongClicked(user: User,cardView: CardView)
}