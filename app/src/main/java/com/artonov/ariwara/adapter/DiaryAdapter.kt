package com.artonov.ariwara.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.artonov.ariwara.R
import com.artonov.ariwara.database.Diary
import com.artonov.ariwara.databinding.ItemDiaryBinding
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DiaryAdapter(private val context: Context, private val diaries: ArrayList<Diary>, private val listener: OnAdapterListener) :
    RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(private val binding: ItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: Diary) {
            binding.apply {
                tvNote.text = diary.note
                tvDate.text = formatDate(diary.date)
                setMood(diary.mood, ivCapsule)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val rowBinding = ItemDiaryBinding.inflate(layoutInflater, parent, false)
        return DiaryViewHolder(rowBinding)
    }

    override fun getItemCount() = diaries.size

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        return holder.bind(diaries[position])
    }

    interface OnAdapterListener {
        fun onUpdate(diary: Diary)
    }

    fun setData(list: List<Diary>) {
        diaries.clear()
        diaries.addAll(list)
        notifyDataSetChanged()
    }

    fun setMood(mood: String, image: ImageView) {
        when (mood) {
            "Unhappy" -> image.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
            "Sad" -> image.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
            "Fine" -> image.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
            "Good" -> image.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lime))
            "Happy" -> image.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
        }
    }

    fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val inputDate = inputFormat.parse(date)
        return outputFormat.format(inputDate)
    }
}