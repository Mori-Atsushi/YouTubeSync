package com.cyder.android.syncpod.view.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.app.ShareCompat
import com.cyder.android.syncpod.R
import com.cyder.android.syncpod.viewmodel.ButtonInterface
import com.cyder.android.syncpod.viewmodel.DialogCallback
import com.cyder.android.syncpod.viewmodel.ShareCompatCallback
import com.cyder.android.syncpod.viewmodel.SnackbarCallback
import com.google.android.material.snackbar.Snackbar

/**
 * Created by chigichan24 on 2018/02/23.
 */

fun View.hideSoftwareKeyBoard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Activity.setUpSnackbar(): SnackbarCallback {
    return object : SnackbarCallback {
        override fun onFailed(resId: Int) {
            currentFocus?.hideSoftwareKeyBoard()
            val view = findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(view,
                    resId,
                    Snackbar.LENGTH_LONG).apply {
                setAction(R.string.ok) {
                    dismiss()
                }
            }
            val snackbarView = snackbar.view
            val tv = snackbarView.findViewById<TextView>(R.id.snackbar_text)
            tv.maxLines = 3
            snackbar.show()
        }
    }
}

fun Activity.setUpShareCompat(): ShareCompatCallback {
    return object : ShareCompatCallback {
        override fun onStart(message: String) {
            ShareCompat.IntentBuilder.from(this@setUpShareCompat)
                    .setText(message)
                    .setType("text/plain")
                    .startChooser()
        }
    }
}

fun Activity.setUpMenuDialog(items: List<ButtonInterface>): DialogCallback {
    val names = items.map { it.name }.toTypedArray()

    val builder = AlertDialog.Builder(this)
            .setItems(names) { _, index ->
                items[index].onClick()
            }
            .create()

    return object : DialogCallback {
        override fun onAction() {
            builder.show()
        }
    }
}

fun Activity.setUpConfirmationDialog(title: String, description: String, positiveButton: ButtonInterface): DialogCallback {
    val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton(positiveButton.name) { _, _ ->
                positiveButton.onClick()
            }
            .setNegativeButton(R.string.cancel_button) { _, _ ->
            }

    return object : DialogCallback {
        override fun onAction() {
            builder.show()
        }
    }
}

fun Resources.dpToPx(dp: Int): Int {
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}
