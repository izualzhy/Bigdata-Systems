package cn.izualzhy;

public enum CommandType {

    GET_APP_ID_REQUEST,
    GET_APP_ID_RESPONSE,

    REMOVE_TAK_LOG_REQUEST,

    REMOVE_TAK_LOG_RESPONSE,

    ROLL_VIEW_LOG_REQUEST,

    ROLL_VIEW_LOG_RESPONSE,

    VIEW_WHOLE_LOG_REQUEST,

    VIEW_WHOLE_LOG_RESPONSE,

    GET_LOG_BYTES_REQUEST,

    GET_LOG_BYTES_RESPONSE,


    WORKER_REQUEST,
    MASTER_RESPONSE,

    /**
     * task execute start, from api to master
     */
    TASK_EXECUTE_START,

    /**
     * dispatch task request
     */
    TASK_DISPATCH_REQUEST,

    /**
     * task execute running, from worker to master
     */
    TASK_EXECUTE_RUNNING,

    /**
     * task execute running ack, from master to worker
     */
    TASK_EXECUTE_RUNNING_ACK,

    /**
     * task execute response, from worker to master
     */
    TASK_EXECUTE_RESULT,

    /**
     * task execute response ack, from master to worker
     */
    TASK_EXECUTE_RESULT_ACK,

    TASK_KILL_REQUEST,

    TASK_KILL_RESPONSE,

    TASK_REJECT,

    TASK_REJECT_ACK,

    /**
     * task savepoint, for stream task
     */
    TASK_SAVEPOINT_REQUEST,

    /**
     * task savepoint ack, for stream task
     */
    TASK_SAVEPOINT_RESPONSE,

    HEART_BEAT,

    PING,

    PONG,

    ALERT_SEND_REQUEST,

    ALERT_SEND_RESPONSE,

    /**
     * process host update
     */
    PROCESS_HOST_UPDATE_REQUEST,

    /**
     * process host update response
     */
    PROCESS_HOST_UPDATE_RESPONSE,

    /**
     * state event request
     */
    STATE_EVENT_REQUEST,
    /**
     * cache expire
     */
    CACHE_EXPIRE,
    /**
     * task state event request
     */
    TASK_FORCE_STATE_EVENT_REQUEST,
    /**
     * task state event request
     */
    TASK_WAKEUP_EVENT_REQUEST,

    /**
     * workflow executing data request, from api to master
     */
    WORKFLOW_EXECUTING_DATA_REQUEST,

    /**
     * workflow executing data response, from master to api
     */
    WORKFLOW_EXECUTING_DATA_RESPONSE;
}
