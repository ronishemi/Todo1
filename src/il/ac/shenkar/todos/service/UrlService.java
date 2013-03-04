package il.ac.shenkar.todos.service;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.StrictMode;

public class UrlService extends IntentService {

	// Status Constants
	public static final int STATUS_RUNNING = 0x1;
	public static final int STATUS_FINISHED = 0x2;
	public static final int STATUS_SUCCESS = 0x3;
	public static final int STATUS_ERROR = 0x4;
	// Command Constants
	public static final int PERFORM_SERVICE_ACTIVITY = 0x5;

	public static final String COMMAND_KEY = "service_command";
	public static final String RECEIVER_KEY = "serivce_receiver";
	public static final String SERVICE_WAS_SUCCESS_KEY = "service_was_success";

	private Bundle bundle;
	private ResultReceiver receiver;
	private GetUrlFromTomer getUrlFromTomer;

	public UrlService() {
		super("UrlService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String result;
		this.receiver = intent.getParcelableExtra(RECEIVER_KEY);
		int command = intent.getIntExtra(COMMAND_KEY, PERFORM_SERVICE_ACTIVITY);

		switch (command) {
		case PERFORM_SERVICE_ACTIVITY:

			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			try {
				getUrlFromTomer = new GetUrlFromTomer(new URL(
						intent.getStringExtra("url")));
				result = getUrlFromTomer.readTodo();
				if (result != null) {
					bundle = new Bundle();
					bundle.putString("ServiceResult", result);
					this.receiver.send(STATUS_FINISHED, bundle);
				} else
					this.stopSelf();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				this.stopSelf();
			}

			break;
		default:
			this.stopSelf();
		}

	}

}
