package com.scientia.mercatus.config.seed;

import com.scientia.mercatus.entity.Category;
import com.scientia.mercatus.entity.InventoryItem;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.entity.ProductImage;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.repository.CategoryRepository;
import com.scientia.mercatus.repository.InventoryItemRepository;
import com.scientia.mercatus.repository.ProductImageRepository;
import com.scientia.mercatus.repository.ProductRepository;
import com.scientia.mercatus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Seeds a curated vintage camera demo catalog for frontend development.
 * <p>
 * Runs ONLY under the "dev" Spring profile. Controlled by the
 * {@code mercatus.seed.demo-catalog} property (defaults to true in dev).
 * <p>
 * Idempotent: skips seeding if categories already exist in the database.
 * <p>
 * The backend remains domain agnostic, this class is purely for dev convenience
 * and has no effect on schema, services, or entities.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${mercatus.seed.demo-catalog:true}")
    private boolean seedDemoCatalog;

    //  Unsplash camera image pool (demo/hotlink only)
    private static final String[] IMG = {
        "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&q=80",
        "https://images.unsplash.com/photo-1452780212940-6f5c0d14d848?w=800&q=80",
        "https://images.unsplash.com/photo-1495707902641-75cac588d2e9?w=800&q=80",
        "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=800&q=80",
        "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=800&q=80",
        "https://images.unsplash.com/photo-1510127034890-ba27508e9f1c?w=800&q=80",
        "https://images.unsplash.com/photo-1500634245200-e5245c7574ef?w=800&q=80",
        "https://images.unsplash.com/photo-1554136920-a1bdb1a0f27e?w=800&q=80",
        "https://images.unsplash.com/photo-1519183071298-a2962fee2c8a?w=800&q=80",
        "https://images.unsplash.com/photo-1551721434-8b94ddff0e6d?w=800&q=80",
        "https://images.unsplash.com/photo-1590004953392-5141f9172541?w=800&q=80",
        "https://images.unsplash.com/photo-1542567455-cd733f23fbb1?w=800&q=80",
        "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&q=80",
        "https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=800&q=80",
        "https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=800&q=80",
    };

    @Override
    @Transactional
    public void run(String... args) {
        seedTestUser();

        if (!seedDemoCatalog) {
            log.info("[DevSeeder] Demo catalog seeding disabled (mercatus.seed.demo-catalog=false)");
            return;
        }

        if (categoryRepository.count() > 0) {
            log.info("[DevSeeder] Categories already exist — skipping demo catalog seed");
            return;
        }

        log.info("[DevSeeder] Seeding demo vintage camera catalog...");

        Map<String, Category> cats = seedCategories();
        seedProducts(cats);

        log.info("[DevSeeder] Demo catalog seeded: {} categories, {} products, {} images, {} inventory items",
                categoryRepository.count(),
                productRepository.count(),
                productImageRepository.count(),
                inventoryItemRepository.count());
    }

    // Categories

    private Map<String, Category> seedCategories() {
        Category c1 = makeCat("35mm Film Cameras", "35mm-film-cameras");
        Category c2 = makeCat("Medium Format Cameras", "medium-format-cameras");
        Category c3 = makeCat("Instant Cameras", "instant-cameras");
        Category c4 = makeCat("Rangefinder Cameras", "rangefinder-cameras");
        Category c5 = makeCat("TLR Cameras", "tlr-cameras");
        Category c6 = makeCat("Camera Lenses", "camera-lenses");
        Category c7 = makeCat("Camera Accessories", "camera-accessories");

        categoryRepository.saveAll(List.of(c1, c2, c3, c4, c5, c6, c7));
        log.debug("[DevSeeder] Seeded 7 categories");

        return Map.of(
            "35mm", c1, "medium", c2, "instant", c3,
            "rangefinder", c4, "tlr", c5, "lens", c6, "accessory", c7
        );
    }

    // Products curated catalog of 31 real  world vintage cameras

    private void seedProducts(Map<String, Category> cats) {


        seed("Nikon FM2",
             "Professional mechanical 35mm SLR. Titanium shutter with 1/4000s top speed. Match-needle metering. Produced 1982-2001. Excellent condition — shutter accurate, meter working, clean finder.",
             "18500.00", "VC-NIK-FM2-EXC-001", "nikon-fm2-exc",
             cats.get("35mm"), IMG[0], gallery(IMG[0], IMG[1], IMG[2], IMG[3], IMG[4]));

        seed("Nikon FM2",
             "Professional mechanical 35mm SLR. Titanium shutter, 1/4000s. Fair condition — meter functional, light brassing on edges, viewfinder slightly dusty. A solid user-grade body.",
             "9250.00", "VC-NIK-FM2-FR-001", "nikon-fm2-fr",
             cats.get("35mm"), IMG[1], null);

        seed("Canon AE-1 Program",
             "Iconic 80s SLR with programmed auto-exposure. FD lens mount. Good condition — clean cosmetics, working light seals, accurate meter. Body only.",
             "7350.00", "VC-CAN-AE1P-GD-001", "canon-ae-1-program-gd",
             cats.get("35mm"), IMG[2], null);

        seed("Pentax K1000",
             "The quintessential student SLR. Full mechanical, K-mount. CLC metering. Excellent condition — fresh light seals, bright viewfinder. Body only.",
             "7500.00", "VC-PEN-K1K-EXC-001", "pentax-k1000-exc",
             cats.get("35mm"), IMG[3], null);

        seed("Olympus OM-1",
             "Compact mechanical SLR that redefined camera size in 1972. Mercury-free meter conversion done. Zuiko mount. Mint condition — clean prism, no haze.",
             "18460.00", "VC-OLY-OM1-MNT-001", "olympus-om-1-mnt",
             cats.get("35mm"), IMG[4], gallery(IMG[4], IMG[5], IMG[6]));

        seed("Minolta X-700",
             "Advanced 35mm SLR with programmed auto and aperture priority. Bright Acute Matte screen. MD mount. Good condition — fully functional, light brass wear on edges.",
             "6675.00", "VC-MIN-X700-GD-001", "minolta-x-700-gd",
             cats.get("35mm"), IMG[5], null);

        seed("Nikon F3HP",
             "Nikon's flagship pro SLR (1980-2001). High-eyepoint viewfinder, titanium shutter, 1/2000s. Used by photojournalists worldwide. Excellent condition.",
             "24000.00", "VC-NIK-F3HP-EXC-001", "nikon-f3hp-exc",
             cats.get("35mm"), IMG[6], null);

        seed("Canon New F-1",
             "Canon's professional system SLR (1981-1996). Modular design with interchangeable finders and screens. Rugged build. Fair condition — functional but cosmetically worn.",
             "14000.00", "VC-CAN-F1N-FR-001", "canon-new-f1-fr",
             cats.get("35mm"), IMG[7], null);

        // ── Rangefinder Cameras (5 products) ────────────────────────────

        seed("Leica M3",
             "The gold standard of 35mm rangefinders since 1954. Double-stroke advance. 0.91x viewfinder with 50/90/135 framelines. Freshly CLA'd. Mint condition.",
             "240500.00", "VC-LEI-M3-MNT-001", "leica-m3-mnt",
             cats.get("rangefinder"), IMG[8], gallery(IMG[8], IMG[9], IMG[10], IMG[11]));

        seed("Leica M6",
             "Classic Leica M-mount rangefinder with built-in TTL meter (1984-1998). 0.72x viewfinder. Brass body, vulcanite covering. Excellent condition.",
             "245000.00", "VC-LEI-M6-EXC-001", "leica-m6-exc",
             cats.get("rangefinder"), IMG[9], null);

        seed("Yashica Electro 35 GSN",
             "Electronic rangefinder with Yashinon 45mm f/1.7 lens. Aperture priority auto exposure. Pad of death replaced. Good condition — clean optics, accurate meter.",
             "4875.00", "VC-YAS-E35-GD-001", "yashica-electro-35-gsn-gd",
             cats.get("rangefinder"), IMG[10], null);

        seed("Canonet QL17 GIII",
             "Premium compact rangefinder with 40mm f/1.7 lens. Shutter priority and full manual. Known as the poor man's Leica. Excellent condition.",
             "12500.00", "VC-CAN-QL17-EXC-001", "canonet-ql17-giii-exc",
             cats.get("rangefinder"), IMG[11], null);

        seed("Yashica Electro 35 GSN",
             "Electronic rangefinder with Yashinon 45mm f/1.7. Aperture priority. Fair condition — meter works, some paint loss on top plate. Lens clean. Great starter rangefinder.",
             "3250.00", "VC-YAS-E35-FR-001", "yashica-electro-35-gsn-fr",
             cats.get("rangefinder"), IMG[12], null);

        // ── Medium Format Cameras (4 products) ─────────────────────────

        seed("Hasselblad 500C/M",
             "Modular medium format system camera. Waist-level finder, A12 back. Accepts C/CF/CFi lenses. Excellent condition — matching serial insert.",
             "95000.00", "VC-HAS-500CM-EXC-001", "hasselblad-500cm-exc",
             cats.get("medium"), IMG[13], gallery(IMG[13], IMG[14], IMG[0], IMG[1]));

        seed("Mamiya RB67 Pro S",
             "Medium format workhorse producing 6x7 negatives. Rotating back, bellows focusing. Includes WLF and 120 Pro-S back. Good condition — fully functional.",
             "24000.00", "VC-MAM-RB67-GD-001", "mamiya-rb67-pro-s-gd",
             cats.get("medium"), IMG[14], null);

        seed("Mamiya 645 1000S",
             "Portable 6x4.5 medium format SLR. Focal plane shutter to 1/1000s. Interchangeable finders and film backs. Mint condition — recently serviced.",
             "28600.00", "VC-MAM-645-MNT-001", "mamiya-645-1000s-mnt",
             cats.get("medium"), IMG[0], null);

        seed("Hasselblad 500C/M",
             "Modular medium format system camera. Waist-level finder, A12 back. Fair condition — functional with cosmetic wear, minor brassing. Prism sold separately.",
             "47500.00", "VC-HAS-500CM-FR-001", "hasselblad-500cm-fr",
             cats.get("medium"), IMG[1], null);

        // ── Instant Cameras (4 products) ────────────────────────────────

        seed("Polaroid SX-70",
             "Folding SLR instant camera. Uses SX-70/600 film (with ND filter). Iconic design by Henry Dreyfuss (1972). Good condition — tested and shooting. Tan leather model.",
             "11625.00", "VC-POL-SX70-GD-001", "polaroid-sx-70-gd",
             cats.get("instant"), IMG[2], null);

        seed("Polaroid SX-70",
             "Folding SLR instant camera. Uses SX-70/600 film. Mint condition — recently refurbished with new skin. Shutter tested across all speeds. Chrome edition.",
             "20150.00", "VC-POL-SX70-MNT-001", "polaroid-sx-70-mnt",
             cats.get("instant"), IMG[3], null);

        seed("Polaroid 680",
             "Premium folding instant SLR with built-in flash and sonar autofocus. Uses 600 film. Successor to the SX-70. Excellent condition — flash tested and working.",
             "18000.00", "VC-POL-680-EXC-001", "polaroid-680-exc",
             cats.get("instant"), IMG[4], null);

        seed("Fujifilm FP-1 Professional",
             "Professional pack-film instant camera. Uses FP-100C peel-apart film. Sharp Fujinon lens with manual controls. Mint condition — pristine optics.",
             "11050.00", "VC-FUJ-FP1-MNT-001", "fujifilm-fp1-professional-mnt",
             cats.get("instant"), IMG[5], null);

        // ── TLR Cameras (3 products) ────────────────────────────────────

        seed("Rolleiflex 2.8F",
             "Top-tier TLR with Planar 80mm f/2.8 taking lens. Built-in meter. Gorgeous Xenotar optics. Recently serviced. Mint condition — collector-grade.",
             "162500.00", "VC-ROL-28F-MNT-001", "rolleiflex-2-8f-mnt",
             cats.get("tlr"), IMG[6], null);

        seed("Yashica-Mat 124G",
             "Affordable 6x6 TLR with Yashinon 80mm f/3.5 lens. CdS meter. Great entry into medium format. Excellent condition — clean optics, smooth focus.",
             "18000.00", "VC-YAS-124G-EXC-001", "yashica-mat-124g-exc",
             cats.get("tlr"), IMG[7], null);

        seed("Yashica-Mat 124G",
             "Affordable 6x6 TLR with Yashinon 80mm f/3.5. Good condition — meter accurate, minor cleaning marks on taking lens. Focus smooth. With original case.",
             "13500.00", "VC-YAS-124G-GD-001", "yashica-mat-124g-gd",
             cats.get("tlr"), IMG[8], null);

        // ── Camera Lenses (4 products) ──────────────────────────────────

        seed("Nikkor 50mm f/1.4 AI-S",
             "Classic Nikon manual focus standard lens. Multi-coated optics, 7-element design. Nikon F-mount. Excellent condition — smooth focus, clean glass.",
             "9500.00", "VC-NIK-50F14-EXC-001", "nikkor-50mm-f14-ais-exc",
             cats.get("lens"), IMG[9], null);

        seed("Canon FD 50mm f/1.4 S.S.C.",
             "Premium Canon FD-mount standard lens with Super Spectra Coating. 8 elements in 7 groups. Breech-lock mount. Good condition — minor dust, no haze.",
             "5400.00", "VC-CAN-50F14-GD-001", "canon-fd-50mm-f14-ssc-gd",
             cats.get("lens"), IMG[10], null);

        seed("Leica Summicron 50mm f/2",
             "Legendary Leica M-mount standard lens. 6 elements in 4 groups. Renowned sharpness and contrast. Mint condition — glass clean, focus smooth.",
             "110500.00", "VC-LEI-50SUM-MNT-001", "leica-summicron-50mm-f2-mnt",
             cats.get("lens"), IMG[11], null);

        seed("Pentax SMC Takumar 55mm f/1.8",
             "M42 screw-mount standard lens. Super-Multi-Coated optics. Radioactive thorium element gives warm rendering. Excellent condition.",
             "4500.00", "VC-PEN-55F18-EXC-001", "pentax-smc-takumar-55mm-f18-exc",
             cats.get("lens"), IMG[12], null);

        // ── Camera Accessories (3 products) ─────────────────────────────

        seed("Sekonic L-308X Flashmate",
             "Compact ambient/flash light meter. Incident and reflected readings. Essential for film photography. Mint condition — accurate readings, fresh battery.",
             "18850.00", "VC-SEK-L308-MNT-001", "sekonic-l308x-flashmate-mnt",
             cats.get("accessory"), IMG[13], null);

        seed("Nikon MD-12 Motor Drive",
             "Motor drive for Nikon FM2/FE2/FA series. 3.2 fps continuous shooting. Uses 8x AA batteries. Good condition — contacts clean, runs strong.",
             "2850.00", "VC-NIK-MD12-GD-001", "nikon-md12-motor-drive-gd",
             cats.get("accessory"), IMG[14], null);

        seed("Hasselblad A12 Film Back",
             "120 roll film back for Hasselblad V-system cameras. 12 exposures per roll on 6x6. Excellent condition — light trap intact, counter working.",
             "12000.00", "VC-HAS-A12BK-EXC-001", "hasselblad-a12-film-back-exc",
             cats.get("accessory"), IMG[0], null);

        log.debug("[DevSeeder] Seeded 31 products with images and inventory");
    }

    // HELPERS
    private Category makeCat(String name, String slug) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setSlug(slug);
        return c;
    }

    /**
     * Seeds a single product with its primary image, optional gallery images,
     * and a matching inventory item (total_stock=1).
     */
    private void seed(String name, String description, String price,
                      String sku, String slug, Category category,
                      String primaryImageUrl, String[] galleryUrls) {

        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(new BigDecimal(price));
        p.setSku(sku);
        p.setSlug(slug);
        p.setIsActive(true);
        p.setCategory(category);
        p.setPrimaryImageUrl(primaryImageUrl);
        productRepository.save(p);

        // Primary image
        saveImage(p, primaryImageUrl, true, 0);

        // Additional gallery images (if provided)
        if (galleryUrls != null) {
            for (int i = 1; i < galleryUrls.length; i++) {
                saveImage(p, galleryUrls[i], false, i);
            }
        }

        // Inventory  1 unit per vintage item
        InventoryItem inv = new InventoryItem();
        inv.setSku(sku);
        inv.setTotalStock(1);
        inv.setReservedStock(0);
        inventoryItemRepository.save(inv);
    }

    private void saveImage(Product product, String url, boolean isPrimary, int sortOrder) {
        ProductImage img = new ProductImage();
        img.setProduct(product);
        img.setUrl(url);
        img.setIsPrimary(isPrimary);
        img.setSortOrder(sortOrder);
        productImageRepository.save(img);
    }

    /** Creates a gallery URL array for featured products. */
    private String[] gallery(String... urls) {
        return urls;
    }

    // Test user

    private void seedTestUser() {
        if (userRepository.existsByEmail("testuser@test.com")) {
            return;
        }

        User user = new User();
        user.setUserName("Test User");
        user.setEmail("testuser@test.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setOpaqueIdentifier(UUID.randomUUID().toString());
        userRepository.save(user);
        log.debug("[DevSeeder] Seeded test user: testuser@test.com");
    }
}
