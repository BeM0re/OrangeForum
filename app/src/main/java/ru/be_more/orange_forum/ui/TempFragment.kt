package ru.be_more.orange_forum.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.temp_layout.*
import ru.be_more.orange_forum.R

class TempFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.temp_layout, container, false)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        tvCommon.text = "Temp"
        super.onActivityCreated(savedInstanceState)
    }
}