package pt.it.esoares.android.util;

public abstract class GenericExecutionCallback {

	public abstract void onSuccessfulExecution();

	public abstract void onUnsuccessfulExecution();

	public static GenericExecutionCallback getEmptyCallback() {
		return new GenericExecutionCallback() {
			@Override
			public void onSuccessfulExecution() {
			}

			@Override
			public void onUnsuccessfulExecution() {
			}
		};
	}
}
