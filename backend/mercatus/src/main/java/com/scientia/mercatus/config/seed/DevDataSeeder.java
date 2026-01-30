package com.scientia.mercatus.config.seed;

import com.scientia.mercatus.entity.Category;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.repository.CategoryRepository;
import com.scientia.mercatus.repository.ProductRepository;
import com.scientia.mercatus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Category cameras = seedCategory();
        seedProducts(cameras);
        seedUser();
    }

    private Category seedCategory() {
        return categoryRepository.findByCategoryName("Cameras")
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setCategoryName("Cameras");
                    return categoryRepository.save(category);
                });
    }

    private void seedProducts(Category cameras) {
        if (productRepository.count() > 0) {
            return;
        }

        Product p1 = new Product();
        p1.setCategory(cameras);
        p1.setName("Canon EOS 1500D DSLR");
        p1.setDescription("24.1MP DSLR camera with 18-55mm lens");
        p1.setPrice(new BigDecimal("37999.00"));
        p1.setSku("SKU-1");

        Product p2 = new Product();
        p2.setCategory(cameras);
        p2.setName("Sony Alpha a6400 Mirrorless");
        p2.setDescription("24.2MP mirrorless camera with 4K video");
        p2.setPrice(new BigDecimal("68999.00"));
        p2.setSku("SKU-2");


        productRepository.saveAll(List.of(p1, p2));
    }

    private void seedUser() {
        if (userRepository.existsByEmail("testuser@test.com")) {
            return;
        }

        User user = new User();
        user.setUserName("Test User");
        user.setEmail("testuser@test.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));

        userRepository.save(user);
    }
}
