package com.ruyicai.scorecenter;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.ruyicai.benchmark.BenchmarkTask;
import com.ruyicai.benchmark.ConcurrentBenchmark;
import com.ruyicai.scorecenter.service.ScoreService;
import com.ruyicai.scorecenter.util.SpringUtils;

public class AddScoreBatchTest extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 10;
	private static final long DEFAULT_LOOP_COUNT = 1000;

	private static AtomicLong idGenerator = new AtomicLong(0);
	private static AtomicLong expiredMills = new AtomicLong(System.currentTimeMillis() + (10 * 1000));

	private long expectTps = 5000L;

	SpringUtils springUtils;

	@SuppressWarnings("static-access")
	@Override
	protected void setUp() {
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
				"classpath:/META-INF/spring/applicationContext.xml",
				"classpath:/META-INF/spring/applicationContext-jms.xml",
				"classpath:/META-INF/spring/applicationContext-memcache.xml");
		springUtils.setApplicationContext(applicationContext);
	}

	public static void main(String[] args) throws Exception {
		AddScoreBatchTest benchmark = new AddScoreBatchTest();
		benchmark.execute();
	}

	public AddScoreBatchTest() {
		super(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT);
	}

	@Override
	protected BenchmarkTask createTask() {
		return new JobProducerTask();
	}

	public class JobProducerTask extends BenchmarkTask {
		@Override
		public void execute(final int requestSequence) {
			long jobId = idGenerator.getAndIncrement();
			System.out.println(jobId + "");
			ScoreService scoreService = SpringUtils.getBean(ScoreService.class);
			scoreService.addTuserinfoScore("00000033", "88batch_test" + jobId, 8, new BigDecimal(600), null, null,
					"88batch_test" + jobId);
			// 达到期望的每秒的TPS后，expireTime往后滚动一秒
			if ((jobId % (expectTps)) == 0) {
				expiredMills.addAndGet(1000);
			}
		}
	}
}
