package com.example.matgipv1.repository;

import com.example.matgipv1.domain.Place;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PlaceRepositoryTests {

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    public void testFindJagalchi() {
        List<Place> list = placeRepository.findAll();
        for (Place p : list) {
            if (p.getRestaurantNameKo() != null && (p.getRestaurantNameKo().contains("해변") || p.getRestaurantNameKo().contains("생선구이") || p.getRestaurantNameKo().contains("자갈치"))) {
                System.out.println("========== FOUND RESTAURANT ==========");
                System.out.println("PNO: " + p.getPno());
                System.out.println("NAME: " + p.getRestaurantNameKo());
                System.out.println("ADDRESS: [" + p.getAddress() + "]");
                System.out.println("GU: " + p.getGuName());
                System.out.println("======================================");
            }
        }
    }
}
