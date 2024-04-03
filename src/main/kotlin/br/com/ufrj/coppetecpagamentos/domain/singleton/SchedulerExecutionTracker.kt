package br.com.ufrj.coppetecpagamentos.domain.singleton

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

@Component
class SchedulerExecutionTracker private constructor() {

    private val MAX_RECORDS_PER_PROCESS = 200
    private val TRIM_THRESHOLD_PER_PROCESS = 10
    private val executionRecordsMap: MutableMap<ProcessType, MutableList<ExecutionRecord>> = mutableMapOf()

    init {
        for (processType in ProcessType.entries) {
            executionRecordsMap[processType] = mutableListOf()
        }
    }

    fun recordExecutionStart(processType: ProcessType) {
        val records = executionRecordsMap[processType] ?: mutableListOf()
        if (records.size >= MAX_RECORDS_PER_PROCESS) {
            trimExecutionRecords(records)
        }
        val record = ExecutionRecord(processType)
        record.startTime = LocalDateTime.now().format(ISO_DATE_TIME)
        records.add(record)
    }

    private fun trimExecutionRecords(records: MutableList<ExecutionRecord>) {
        if (records.size > TRIM_THRESHOLD_PER_PROCESS) {
            val toRemove = records.size - TRIM_THRESHOLD_PER_PROCESS
            records.subList(0, toRemove).clear()
        }
    }

    fun recordExecutionEnd(processType: ProcessType) {
        val records = executionRecordsMap[processType] ?: return
        val index = records.indexOfLast { it.processType == processType }
        if (index != -1) {
            records[index].endTime = LocalDateTime.now().format(ISO_DATE_TIME)
        }
    }

    fun getProcessStatus(): Map<ProcessType, ProcessStatus> {
        val processStatusMap = mutableMapOf<ProcessType, ProcessStatus>()
        for (processType in ProcessType.entries) {
            val records = executionRecordsMap[processType] ?: emptyList()
            val lastRecord = records.lastOrNull()
            val status = if (lastRecord != null && lastRecord.endTime == null) {
                ProcessStatus.IN_PROGRESS
            } else {
                ProcessStatus.STOPPED
            }
            processStatusMap[processType] = status
        }
        return processStatusMap
    }

    fun getLogProcess(valueOf: ProcessType): List<ExecutionRecord> {
        return executionRecordsMap[valueOf] ?: emptyList()
    }

    companion object {
        @Volatile
        private var instance: SchedulerExecutionTracker? = null

        fun getInstance(): SchedulerExecutionTracker {
            return instance ?: synchronized(this) {
                instance ?: SchedulerExecutionTracker().also { instance = it }
            }
        }
    }
}

data class ExecutionRecord(
        var processType: ProcessType,
        var startTime: String = LocalDateTime.now().format(ISO_DATE_TIME),
        var endTime: String? = null,
        val transferLogs: MutableList<TransferLog> = mutableListOf(),
)

data class TransferLog(
        val message: String,
        val timestamp: String = LocalDateTime.now().format(ISO_DATE_TIME),
        var processType: ProcessType? = null
)

enum class ProcessType {
    BANK_STATEMENT_INQUIRY_PROCESS,
    PAYMENT_SENDING_PROCESS,
    PRIORITY_PAYMENT_INQUIRY_PROCESS,
    NON_PRIORITY_PAYMENT_INQUIRY_PROCESS,
}

enum class ProcessStatus {
    IN_PROGRESS,
    STOPPED
}