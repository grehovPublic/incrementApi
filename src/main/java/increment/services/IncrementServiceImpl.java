package increment.services;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IncrementServiceImpl implements IncrementService {
    
    private static final Logger LOG = LoggerFactory.getLogger(IncrementServiceImpl.class);

    @Override
    public BigInteger increment(int toIncrement) {
        final BigInteger incremented = BigInteger.valueOf(toIncrement).add(BigInteger.ONE);
        LOG.info("Value after incrementing: {}", incremented);
        return incremented;
    }
}
