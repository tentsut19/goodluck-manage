package th.co.infinitait.goodluck.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import th.co.infinitait.goodluck.exception.InvalidParameterException;

@Component
@Slf4j
public class ChecksumUtil {
    public String getFileChecksum(byte[] fileBytes) {
        String checksum = DigestUtils.md5DigestAsHex(fileBytes);
        if (checksum.length() < 10) {
            log.error("generate checksum failed");
            throw new InvalidParameterException("Invalid generated file checksum");
        }
        return checksum;
    }
}
