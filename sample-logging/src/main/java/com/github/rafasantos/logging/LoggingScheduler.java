package com.github.rafasantos.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoggingScheduler {
	Logger logger = LoggerFactory.getLogger(LoggingScheduler.class);

	@Scheduled(fixedRate = 2000)
	public void logScheduledSummary() {
		logger.info("Logging message at INFO level");
		logger.debug("Logging message at DEBUG level");
	}
}
