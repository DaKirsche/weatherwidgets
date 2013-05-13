package eu.dakirsche.weatherwidgets;

/**
 * Interfaceklasse für den Responsehandler des API-SyncTasks
 */
public interface CallbackInterface
{
    /**
     * Callbackmethode des Responsehandlers
     * @param result Von der Api erhaltener Response
     */
	public void callback(String result);
}
