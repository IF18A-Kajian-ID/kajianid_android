package com.kajianid.android.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kajianid.android.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class KajianAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_KAJIAN_TITLE = "extra_kajian_title"
        const val EXTRA_USTADZ_NAME = "extra_ustadz_name"

        private const val CHANNEL_ID = "Channel_1"
        private const val CHANNEL_NAME = "AlarmManager channel"
        
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        private const val NOTIFICATION_ID = 100
    }

    override fun onReceive(context: Context, intent: Intent) {
        // method yang akan dipanggil saat jam sudah menunjukkan
        // sesuai pada tanggal dan waktu yang ditentukan
        val title = intent.getStringExtra(EXTRA_KAJIAN_TITLE).toString()
        val ustadzName = intent.getStringExtra(EXTRA_USTADZ_NAME).toString()

        showReminderNotification(context, title, ustadzName)
    }

    fun setReminder(context: Context, id: Int, title: String, dateUnformatted: String, ustadzName: String) {
        if (isDateInvalid(dateUnformatted)) return

        // cek tanggal apakah sudah outdated atau belum
        val dateCheck = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(dateUnformatted)!!
        if (System.currentTimeMillis() > dateCheck.time) {
            Toast.makeText(context, "Jadwal Kajian ini sudah usang. Kami hanya akan menyimpan kajian ini ke dalam \"Kajian Tersimpan\".", Toast.LENGTH_SHORT).show()
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, KajianAlarmReceiver::class.java)

        intent.putExtra(EXTRA_KAJIAN_TITLE, title)
        intent.putExtra(EXTRA_USTADZ_NAME, ustadzName)

        Log.e("setReminder", "$dateUnformatted: $title by $ustadzName")

        val dateTimeArray = dateUnformatted.split(" ").toTypedArray()

        val dateArray = dateTimeArray[0].split("-").toTypedArray()
        val timeArray = dateTimeArray[1].split(":").toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0]))
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]))

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]) - 31)
        calendar.set(Calendar.SECOND, 0)

        val dateString = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(context, "Kajian $title akan kami ingatkan pada $dateString!", Toast.LENGTH_SHORT).show()
    }

    fun cancelReminder(context: Context, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, KajianAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)

        Toast.makeText(context, "Pengingat pada kajian ini berhasil dibatalkan!", Toast.LENGTH_SHORT).show()
    }

    private fun showReminderNotification(context: Context, title: String, ustadzName: String) {
        val contentTitle = context.getString(R.string.notification_half_title, title)
        val contentMessage = context.getString(R.string.notification_half_message, title, ustadzName)
        
        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(contentTitle)
                .setContentText(contentMessage)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(alarmSound)

        /*
        Untuk android Oreo ke atas perlu menambahkan notification channel
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            val channel = NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            builder.setChannelId(CHANNEL_ID)

            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(NOTIFICATION_ID, notification)
    }

    private fun isDateInvalid(date: String): Boolean {
        return try {
            val df = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e: ParseException) {
            true
        }
    }
}