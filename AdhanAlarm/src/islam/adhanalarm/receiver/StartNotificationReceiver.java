package islam.adhanalarm.receiver;

import java.util.Calendar;

import islam.adhanalarm.Schedule;
import islam.adhanalarm.VARIABLE;
import islam.adhanalarm.WakeLock;
import islam.adhanalarm.service.SetNextAndPossiblyNotifyCurrentService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class StartNotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		WakeLock.acquire(context);
		if(VARIABLE.settings == null) { // We are booting up
			VARIABLE.settings = context.getSharedPreferences("settingsFile", Context.MODE_PRIVATE);
		}
		intent.setClass(context, SetNextAndPossiblyNotifyCurrentService.class);
		context.startService(intent);
	}

	public static void setNext(Context context) {
		Schedule today = Schedule.today();
		short nextTimeIndex = today.nextTimeIndex();
		set(context, nextTimeIndex, today.getTimes()[nextTimeIndex]);
	}

	private static void set(Context context, short timeIndex, Calendar actualTime) {
		if(Calendar.getInstance().after(actualTime)) return; // Somehow current time is greater than the prayer time

		Intent intent = new Intent(context, StartNotificationReceiver.class);
		intent.putExtra("timeIndex", timeIndex);
		intent.putExtra("actualTime", actualTime.getTimeInMillis());

		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, actualTime.getTimeInMillis(), PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
	}
}