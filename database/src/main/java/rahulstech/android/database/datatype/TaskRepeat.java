package rahulstech.android.database.datatype;

@SuppressWarnings("unused")
public enum TaskRepeat {
    /**
     * A task with frequency ONCE does not repeat. This is the default frequency for
     * each new task
     */
    ONCE,
    /**
     * A task with frequency REGULAR repeats after certain intervals
     * like every day, every 5 days, every week, every month etc
     */
    REGULAR,
    /**
     * A task with frequency IRREGULAR repeats one or more but does not maintains
     * same intervals. For example: on 2022-08-15, 2022-08-21, 2022-08-23, 2022-08-24,
     * 2022-09-01. The task takes place after 6, 2, 1, 8 days respectively.
     */
    IRREGULAR
}
