package com.example.rastreioonibus.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ItemLineBinding
import com.example.rastreioonibus.databinding.ItemLineSearchBinding
import com.example.rastreioonibus.domain.model.Lines

class SearchLinesAdapter() :
    ListAdapter<Lines, SearchLinesAdapter.SearchLinesViewHolder>(SearchLinesAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SearchLinesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_line_search, parent, false)
        )

    override fun onBindViewHolder(holder: SearchLinesViewHolder, position: Int) {
        holder.apply {
            val line = getItem(position)
            bind(line)
        }
    }

    inner class SearchLinesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemLineSearchBinding.bind(itemView)

        fun bind(lines: Lines){

            binding.apply {
                txtCode.text = lines.codeOfLine.toString()
                txtIsCircular.text = lines.worksInCircleMode.toString()
                txtIniSign.text =lines.firstPartOfSignLine
                txtFinSign.text = lines.secondPartOfSignLine.toString()
                txtTpTs.text = lines.signOfLineDirectionPrincipal
                txtTsTp.text = lines.signOfLineDirectionSecondary
            }
        }
    }

    private companion object : DiffUtil.ItemCallback<Lines>() {

        override fun areItemsTheSame(
            oldItem: Lines,
            newItem: Lines
        ): Boolean {
            return oldItem.codeOfLine == newItem.codeOfLine
        }

        override fun areContentsTheSame(
            oldItem: Lines,
            newItem: Lines
        ): Boolean {
            return oldItem == newItem
        }
    }
}