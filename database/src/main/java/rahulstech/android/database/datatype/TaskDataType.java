package rahulstech.android.database.datatype;

@SuppressWarnings({"unused"})
public class TaskDataType {
    /**
     * text4 => "frequency" one of the value {@link TaskRepeat} always non-null
     * text5 => "unit" one of the value {@link TimeUnit} non-null for {@link TaskRepeat#REGULAR}
     *          null otherwise
     * long1 => "interval" any positive integer value non-null for {@link  TaskRepeat#REGULAR}
     *          null otherwise
     */
    public static final String TYPE_TASK_FREQUENCY = "task_frequency";
}
