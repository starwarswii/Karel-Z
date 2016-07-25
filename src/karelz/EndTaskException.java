package karelz;

/**
 * EndTaskExceptions are thrown during normal Karel robot execution to indicate that the robot task has finished executing.
 * This could be caused either by the robot crashing or the task finishing successfully.
 * 
 * @see Robot#launchThread()
 */
@SuppressWarnings("serial")
public class EndTaskException extends Exception {}
