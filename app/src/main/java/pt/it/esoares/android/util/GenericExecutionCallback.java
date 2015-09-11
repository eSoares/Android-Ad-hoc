package pt.it.esoares.android.util;

public abstract class GenericExecutionCallback {

	public abstract void onSucessfullExecution();

	public abstract void onUnsucessfullExecution();

	public static GenericExecutionCallback getEmptyCallback() {
		return new GenericExecutionCallback() {
			@Override
			public void onSucessfullExecution() {
			}

			@Override
			public void onUnsucessfullExecution() {
			}
		};
	}
}
