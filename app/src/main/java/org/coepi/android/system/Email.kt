package org.coepi.android.system

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.content.Intent.createChooser
import android.net.Uri
import androidx.core.content.FileProvider
import org.coepi.android.system.log.log

interface Email {
    fun open(activity: Activity, recipient: String, subject: String, message: String = "", uri: Uri?)
}

class EmailImpl: Email {

    /**
     * String filename="contacts_sid.vcf";
    File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
    Uri path = Uri.fromFile(filelocation);
    Intent emailIntent = new Intent(Intent.ACTION_SEND);
    // set the type to 'email'
    emailIntent .setType("vnd.android.cursor.dir/email");
    String to[] = {"asd@gmail.com"};
    emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
    // the attachment
    emailIntent .putExtra(Intent.EXTRA_STREAM, path);
    // the mail subject
    emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
    startActivity(Intent.createChooser(emailIntent , "Send email..."));
     */

    override fun open(activity: Activity, recipient: String, subject: String, message: String, uri: Uri?) {
        val intent = Intent(ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "message/rfc822"
            putExtra(EXTRA_EMAIL, arrayOf(recipient))
            putExtra(EXTRA_SUBJECT, subject)
            putExtra(EXTRA_TEXT, message)
            putExtra(EXTRA_STREAM, uri)
        }
        try {
            activity.startActivity(createChooser(intent, "Choose Email Client..."))
        } catch (e: Exception) {
            log.e("Couldn't open email client: $e")
        }
    }
}
