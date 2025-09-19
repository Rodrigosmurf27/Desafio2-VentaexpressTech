package com.udb.ventaexpress.ext

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

fun Spinner.setOnItemSelectedListenerCompat(onSelected: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>,
            view: View?,
            position: Int,
            id: Long
        ) { onSelected(position) }

        override fun onNothingSelected(parent: AdapterView<*>) { /* no-op */ }
    }
}
