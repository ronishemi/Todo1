package il.ac.shenkar.todos.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MyResultReceiver extends ResultReceiver {
	private Receiver mReceiver;

	public MyResultReceiver(Handler handler) {
		super(handler);
	}

	public void setReceiver(Receiver receiver) {
		mReceiver = receiver;
	}

	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultBundle);
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultBundle) {
		if (mReceiver != null) {
			mReceiver.onReceiveResult(resultCode, resultBundle);
		}
	}
}
