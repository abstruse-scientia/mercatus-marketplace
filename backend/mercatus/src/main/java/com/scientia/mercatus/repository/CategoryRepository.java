package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    Optional<Category> findByCategoryName(String categoryName);

    Optional<Category> findBySlug(String slug);

    boolean existsByCategoryName(String categoryName);

    boolean existsBySlug(String slug);

    // Check if category name exists for different category id (different category) [for update]
    @Query("""
            select case when count(c) > 0 then true else false end from Category c  where c.categoryName = :categoryNamey and c.categoryId != :categoryId
    """)
    boolean existsByCategoryNameAndCategoryIdNot(@Param("categoryName") String categoryName, @Param("categoryId") Long categoryId);



    // Check if slug exists for different category [for updates]
    @Query("select case when count(c) > 0 then true else false end from Category c where c.slug = :slug and c.categoryId != :categoryId")
    boolean existsBySlugAndCategoryIdNot(@Param("slug") String slug, @Param("categoryId") Long categoryId);


    List<Category> findByCategoryNameContainingIgnoreCase(String categoryName);

    @Query("select count(c) from Category c")
    Long countAllCategories();


    // Get categories with product count by doing left join
    @Query("Select c, count(p) from Category c left join c.products p group by c")
    List<Object[]> findAllWithProductCount();


    //check if category has products by querying product and checking categoryId given in parameter
    @Query("select case when count(p) > 0 then true else false end from Product p where p.category.categoryId = :categoryId")
    boolean hasProducts(@Param("categoryId") Long categoryId);


    // Count products in a category
    @Query("select count(p) from Product p where p.category.categoryId = :categoryId")
    Long countProductsInCategory(@Param("categoryId") Long categoryId);
}
