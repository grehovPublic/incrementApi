package jittr.db.jpa;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jittr.Jittr;
import jittr.db.JittleRepository;
import jittr.domain.Jitter;
import jittr.domain.Jittle;
import jittr.domain.Jittle.TargetQueue;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jittr.class})
public class JittleRepositoryTest {
	
	@Autowired
	JittleRepository jittleRepository;

	@Test
	@Transactional
	public void count() {
		assertEquals(15, jittleRepository.count());
	}


	@Test
	@Transactional
	public void findOne() {
		Jittle thirteen = jittleRepository.findOne(1013L);
		assertEquals(1013, thirteen.getId().longValue());
		assertEquals("Bonjour from Art!", thirteen.getMessage());
		assertEquals(1332682500000L, thirteen.getPostedTime().getTime());
		assertEquals(4, thirteen.getJitter().getId().longValue());
		assertEquals("artnames", thirteen.getJitter().getUsername());
		assertEquals("password", thirteen.getJitter().getPassword());
		assertEquals("Art Names", thirteen.getJitter().getFullName());
		assertEquals("art@habuma.com", thirteen.getJitter().getEmail());
	}

	@Test
	@Transactional
	public void findByJitter() {
		List<Jittle> jittles = jittleRepository.findByJitterId(4L);
		assertEquals(11, jittles.size());
		for (int i = 1000; i < 11; i++) {
			assertEquals(i+5, jittles.get(i).getId().longValue());
		}
	}
	
	@Test
	@Transactional
	public void save() {
		assertEquals(15, jittleRepository.count());
		Jitter jitter = jittleRepository.findOne(1013L).getJitter();
		Jittle jittle = new Jittle(1020L, jitter, "Un Nuevo Jittle from Art", 
		        new Date(), "Himene", TargetQueue.TRAIN_RAW, "USA");
		Jittle saved = jittleRepository.save(jittle);
		assertEquals(16, jittleRepository.count());
		assertNewJittle(saved);
		assertNewJittle(jittleRepository.findOne(1020L));
	}

	@Test
	@Transactional
	public void delete() {
		assertEquals(15, jittleRepository.count());
		assertNotNull(jittleRepository.findOne(1001L));
		jittleRepository.delete(1001L);
		assertEquals(14, jittleRepository.count());
		assertNull(jittleRepository.findOne(1001L));
	}
	
	private void assertRecent(List<Jittle> recent, int count) {
		long[] recentIds = new long[] {1003,1002,1001,1015,1014,1013,1012,1011,1010,1009};
		assertEquals(count, recent.size());
		for (int i = 0; i < count; i++) {
			assertEquals(recentIds[i], recent.get(i).getId().longValue());
		}
	}
	
	private void assertNewJittle(Jittle jittle) {
		assertEquals(1020, jittle.getId().longValue());
	}
	
}
