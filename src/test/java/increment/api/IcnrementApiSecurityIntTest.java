/*
 * Copyright 2013-2104 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package increment.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import increment.Increment;
import increment.security.WebSecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Web security tests.
 *  
 * @author Grehov
 *
 */
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Increment.class, WebSecurityConfig.class}, 
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IcnrementApiSecurityIntTest {
    
    @Value("${increment.client-username}")
    private String clientUsername;
    
    @Value("${increment.client-password}")
    private String clientPassword;
    
    private static String SEC_CONTEXT_ATTR = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    
    private MockMvc mockMvc;

    @Before
    public void before() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(this.springSecurityFilterChain).build();
    }
	
	
    @Test
    public void userAuthenticates() throws Exception {
        mockMvc.perform(post("/login").param("username", clientUsername)
                .param("password", clientPassword))
            .andExpect(r -> 
                Assert.assertEquals(((SecurityContext) r.getRequest()
                                                        .getSession()
                                                        .getAttribute(SEC_CONTEXT_ATTR))
                                                        .getAuthentication()
                                                        .getName(), clientUsername));
    }
    
    @Test
    public void userAuthenticationFails() throws Exception {
        final String username = clientUsername;
        mockMvc.perform(post("/login").param("username", username).param("password", "invalid"))
                .andExpect(r -> 
                Assert.assertNull(r.getRequest().getSession().getAttribute(SEC_CONTEXT_ATTR)));
    }	
}
