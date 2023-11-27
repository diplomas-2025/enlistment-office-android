package ru.enlistment.office.common

import android.content.ClipData
import android.content.Context
import android.widget.Toast

fun setClipboard(context: Context, text: String) {
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)

    Toast.makeText(context, "Текст скопирован !", Toast.LENGTH_SHORT).show()
}