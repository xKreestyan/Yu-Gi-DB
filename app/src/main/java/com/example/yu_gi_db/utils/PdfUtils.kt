package com.example.yu_gi_db.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo
import java.util.Locale

fun openRulebookPdf(context: Context) {
    val currentLang = Locale.getDefault().language
    val pdfFileNameInAssets = if (currentLang == "it") "pdf/rulebook_it.pdf" else "pdf/rulebook_en.pdf"
    val pdfTitle = if (currentLang == "it") "Regolamento Yu-Gi-Oh!" else "Yu-Gi-Oh! Rulebook"

    try {
        val pdfIntent: Intent = PdfViewerActivity.launchPdfFromPath(
            context = context,
            path = pdfFileNameInAssets,
            pdfTitle = pdfTitle,
            saveTo = saveTo.ASK_EVERYTIME,
            fromAssets = true
        )
        context.startActivity(pdfIntent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "PDF viewer not found.", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "PDF opening error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

