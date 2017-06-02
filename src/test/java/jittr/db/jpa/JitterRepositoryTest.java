package jittr.db.jpa;

import static jittr.domain.Jitter.Role.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jittr.db.JitterRepository;
import jittr.domain.Jitter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=JpaConfig.class)
@ActiveProfiles("dev")
public class JitterRepositoryTest {

	@Autowired
	JitterRepository jitterRepository;
	
	@Test
	@Transactional
	public void count() {
		assertEquals(6, jitterRepository.count());
	}
	
	@Test
	@Transactional
	public void findAll() {
		List<Jitter> jitters = jitterRepository.findAll();
		assertEquals(6, jitters.size());
		assertJitter(0, jitters.get(0));
		assertJitter(1, jitters.get(1));
		assertJitter(2, jitters.get(2));
		assertJitter(3, jitters.get(3));
	}
	
	@Test
	@Transactional
	public void findByUsername() {
		assertJitter(0, jitterRepository.findByUsername("habuma").get());
		assertJitter(1, jitterRepository.findByUsername("mwalls").get());
		assertJitter(2, jitterRepository.findByUsername("chuck").get());
		assertJitter(3, jitterRepository.findByUsername("artnames").get());
	}
	
	@Test
	@Transactional
	public void findOne() {
		assertJitter(0, jitterRepository.findOne(1L));
		assertJitter(1, jitterRepository.findOne(2L));
		assertJitter(2, jitterRepository.findOne(3L));
		assertJitter(3, jitterRepository.findOne(4L));
	}
	
	@Test
	@Transactional
	public void save_newJitter() {
		assertEquals(6, jitterRepository.count());
		Jitter jitter = new Jitter(null, "newbee", "letmein", "New Bee", "newbee@habuma.com", ROLE_JITTER);
		Jitter saved = jitterRepository.save(jitter);
		assertEquals(7, jitterRepository.count());
		assertJitter(4, saved);
		assertJitter(4, jitterRepository.findOne(7L));
	}

	@Test
	@Transactional
	@Ignore
	public void save_existingJitter() {
		assertEquals(4, jitterRepository.count());
		Jitter jitter = new Jitter(4L, "arthur", "letmein", "Arthur Names", "arthur@habuma.com", ROLE_JITTER);
		Jitter saved = jitterRepository.save(jitter);
		assertJitter(5, saved);
		assertEquals(4, jitterRepository.count());
		Jitter updated = jitterRepository.findOne(4L);
		assertJitter(5, updated);
	}

	private static void assertJitter(int expectedJitterIndex, Jitter actual) {
		assertJitter(expectedJitterIndex, actual, "Newbie");
	}
	
	private static void assertJitter(int expectedJitterIndex, Jitter actual, String expectedStatus) {
		Jitter expected = JITTERS[expectedJitterIndex];
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getUsername(), actual.getUsername());
		assertEquals(expected.getPassword(), actual.getPassword());
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getEmail(), actual.getEmail());
	}
	
	private static Jitter[] JITTERS = new Jitter[6];
	
	@BeforeClass
	public static void before() {
		JITTERS[0] = new Jitter(1L, "habuma", "password", "Craig Walls", "craig@habuma.com", ROLE_JITTER);
		JITTERS[1] = new Jitter(2L, "mwalls", "password", "Michael Walls", "mwalls@habuma.com", ROLE_JITTER);
		JITTERS[2] = new Jitter(3L, "chuck", "password", "Chuck Wagon", "chuck@habuma.com", ROLE_JITTER);
		JITTERS[3] = new Jitter(4L, "artnames", "password", "Art Names", "art@habuma.com", ROLE_JITTER);
		JITTERS[4] = new Jitter(7L, "newbee", "letmein", "New Bee", "newbee@habuma.com", ROLE_JITTER);		
		JITTERS[5] = new Jitter(4L, "arthur", "letmein", "Arthur Names", "arthur@habuma.com", ROLE_JITTER);		
	}
	
}
