package jittr.rest;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestBody;

import jittr.db.JitterRepository;
import jittr.db.JittleRepository;
import jittr.domain.Jitter;
import jittr.domain.Jittle;
import jittr.domain.Jitter.Role;
import jittr.domain.Jittle.TargetQueue;
import jittr.dto.JitterDto;
import jittr.dto.JittleDto;
import jittr.rest.JittleFacadeRest;

@RunWith(SpringRunner.class)
public class JittleFacadeRestUnitTest {
    
    JittleFacadeRest controller;
    
    public JittleFacadeRestUnitTest() {
        controller = new JittleFacadeRest();
    }

    
    /*
     * Testing strategy for List<JittleDto> convertToDtos(Collection<Jittle> jittleList):
     * 
     * Partitions:

     */
    
    @Test
    public void whenConvertJittleEntityToJittleDto_thenCorrect() {
        Jitter jitter = new Jitter(Long.MAX_VALUE, "alex", "alex", "alex", "alex", 
                Role.ROLE_JITTER);
        Jittle jittle = new Jittle(Long.MAX_VALUE, jitter, "message", Date.from(Instant.now()),
                "author", TargetQueue.BUILD_MAP, "USA");
        ModelMapper modelMapper = new ModelMapper();
 
        JittleDto jittleDto = controller.convertToDto(jittle);
        assertEquals(jittle.getId(), jittleDto.getId());
        assertEquals(jittle.getMessage(), jittleDto.getMessage());
        assertEquals(jittle.getJitter(), modelMapper.map(jittleDto.getJitter(), Jitter.class));             
    }
    
    /*
     * Testing strategy for List<Jittle> convertToEntities(Collection<JittleDto> jittleDtoList):
     * 
     * Partitions:
     */
 
    @Test
    public void whenConvertJittleDtoToJittleEntity_thenCorrect() {
        JitterDto jitterDto = new JitterDto(Long.MAX_VALUE, "username", "password", "fullName", 
                "email", Role.ROLE_JITTER);
        JittleDto jittleDto = new JittleDto(Long.MAX_VALUE, jitterDto, "message", Date.from(Instant.now()),
                "author", TargetQueue.BUILD_MAP, "USA");       
        ModelMapper modelMapper = new ModelMapper();
 
        Jittle jittle = controller.convertToEntity(jittleDto);
        assertEquals(jittleDto.getId(), jittle.getId());
        assertEquals(jittle.getMessage(), jittleDto.getMessage());
        assertEquals(modelMapper.map(jittle.getJitter(), JitterDto.class), jittleDto.getJitter());    
    }
    
}
