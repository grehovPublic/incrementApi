package jittr.dto;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import jittr.domain.Jitter;
import jittr.domain.Jittle;
import jittr.domain.Jitter.Role;
import jittr.domain.Jittle.TargetQueue;

/**
 * Unit tests 
 * @author Grehov
 *
 */
public class JittleDtoTest {
 
    @Autowired
    private ModelMapper modelMapper = new ModelMapper();
 
    @Test
    public void whenConvertPostEntityToPostDto_thenCorrect() {
        Jitter jitter = new Jitter(Long.MAX_VALUE, "alex", "alex", "alex", "alex", 
                Role.ROLE_JITTER);
        Jittle jittle = new Jittle(Long.MAX_VALUE, jitter, "message", Date.from(Instant.now()),
                "author", TargetQueue.BUILD_MAP, "USA");
 
        JittleDto jittleDto = modelMapper.map(jittle, JittleDto.class);
        assertEquals(jittle.getId(), jittleDto.getId());
        assertEquals(jittle.getMessage(), jittleDto.getMessage());
        assertEquals(jittle.getJitter(), jittleDto.getJitter());             
    }
 
    @Test
    public void whenConvertPostDtoToPostEntity_thenCorrect() {
        JitterDto jitterDto = new JitterDto(Long.MAX_VALUE, "username", "password", "fullName", 
                "email", Role.ROLE_JITTER);
        JittleDto jittleDto = new JittleDto(Long.MAX_VALUE, jitterDto, "message", Date.from(Instant.now()),
                "author", TargetQueue.BUILD_MAP, "USA");
 
        Jittle jittle = modelMapper.map(jittleDto, Jittle.class);
        assertEquals(jittleDto.getId(), jittle.getId());
        assertEquals(jittle.getMessage(), jittleDto.getMessage());
        assertEquals(jittle.getJitter(), jittleDto.getJitter());    
    }
}
