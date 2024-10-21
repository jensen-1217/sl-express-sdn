package com.sl.sdn;

import com.sl.sdn.dto.OrganDTO;
import com.sl.sdn.repository.OrganRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jensen
 * @date 2024-10-21 18:31
 */
@SpringBootTest
class OrganRepositoryTest {
    @Resource
    private OrganRepository organRepository;

    @Test
    void findByBid() {
        OrganDTO byBid = organRepository.findByBid(100280L);
        System.out.println("byBid = " + byBid);
    }

    @Test
    void findAllByLabel() {
        List<OrganDTO> agency = organRepository.findAllByLabel("AGENCY");
        int size = agency.size();
        System.out.println("size = " + size);
        System.out.println("agency = " + agency);
    }

    @Test
    void findAll() {
        List<OrganDTO> agency = organRepository.findALl("江苏省南京市玄武");
        int size = agency.size();
        System.out.println("size = " + size);
        System.out.println("agency = " + agency);
    }
}