package br.com.ufrj.coppetecpagamentos.domain.semaphore

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Semaphore

object ScheduleSemaphore {

    private val logger: Logger = LoggerFactory.getLogger(ScheduleSemaphore::class.java)
    private val semaphore = Semaphore(1)

    init {
        logger.info("Logger criado para a classe: ${this.javaClass.name}")
    }

    fun acquireStep1() {
        logger.info("Acquiring semaphore for Step 1")
        semaphore.acquire()
    }

    fun releaseStep1() {
        logger.info("Releasing semaphore for Step 1")
        semaphore.release()
    }

    fun acquireStep2() {
        logger.info("Acquiring semaphore for Step 2")
        semaphore.acquire()
    }

    fun releaseStep2() {
        logger.info("Releasing semaphore for Step 2")
        semaphore.release()
    }
}