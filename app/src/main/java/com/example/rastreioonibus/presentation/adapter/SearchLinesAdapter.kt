package com.example.rastreioonibus.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ItemLineSearchBinding
import com.example.rastreioonibus.domain.model.Lines

class SearchLinesAdapter(private val context: Context) :
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

        fun bind(line: Lines) {

            binding.apply {
                txtCode.text = context.getString(R.string.txt_idLine, line.codeOfLine.toString())
                txtIsCircular.text = context.getString(
                    R.string.txt_isCircular,
                    if (line.worksInCircleMode) "Sim" else "Não"
                )
                txtSign.text = context.getString(
                    R.string.txt_sign,
                    line.firstPartOfSignLine,
                    line.secondPartOfSignLine.toString()
                )
                txtTpTs.text = context.getString(
                    R.string.txt_direction_principal,
                    line.signOfLineDirectionPrincipal
                )
                txtTsTp.text = context.getString(
                    R.string.txt_direction_secondary,
                    line.signOfLineDirectionSecondary
                )
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