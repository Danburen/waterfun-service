package org.waterwood.waterfunservice.service.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.api.BaseResponseCode;
import org.waterwood.waterfunservicecore.entity.user.User;
import org.waterwood.waterfunservicecore.entity.post.Category;
import org.waterwood.common.exceptions.BusinessException;
import org.waterwood.utils.generator.SlugGenerator;
import org.waterwood.waterfunservicecore.infrastructure.persistence.user.UserRepository;
import org.waterwood.waterfunservice.infrastructure.mapper.CategoryMapper;
import org.waterwood.waterfunservicecore.infrastructure.persistence.CategoryRepository;
import org.waterwood.waterfunservicecore.infrastructure.security.AuthContextHelper;
import org.waterwood.waterfunservice.service.post.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;
    private final SlugGenerator slugGenerator;

    @Transactional
    @Override
    public void createCategory(Category category) {
        categoryRepository.findByName(category.getName()).ifPresent(_->{
            throw new BusinessException(BaseResponseCode.POST_CATEGORY_EXISTS);
        });
        User u = userRepository.findUserById(AuthContextHelper.getCurrentUserId()).orElseThrow(
                ()-> new BusinessException(BaseResponseCode.USER_NOT_FOUND)
        );
        category.setCreator(u);
        category.setSortOrder(category.getSortOrder());
        category.setName(category.getName());
        category.setSlug(slugGenerator.generateSlug(category.getName(), categoryRepository));
        category.setDescription(category.getDescription());
        category.setIsActive(category.getIsActive() == null || category.getIsActive());
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getCategories() {
        Long userId = AuthContextHelper.getCurrentUserId();
        return categoryRepository.findAllByCreatorId(userId);
    }

    @Override
    public Category getCategory(Integer id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new BusinessException(BaseResponseCode.NOT_FOUND, "Category ID: " + id)
        );
    }

    @Override
    public void updateCategory(Category category) {
        Category c = categoryRepository.findById(category.getId()).orElseThrow(
                () -> new BusinessException(BaseResponseCode.NOT_FOUND)
        );
        if (category.getName() != null) c.setName(category.getName());
        if (category.getSlug() != null) c.setSlug(category.getSlug());
        if (category.getDescription() != null) c.setDescription(category.getDescription());
        if (category.getParentId() != null) {
            Category parent = categoryRepository.findById(category.getParentId()).orElseThrow(
                    () -> new BusinessException(BaseResponseCode.PARENT_NOT_FOUND, "ID: " + category.getParentId())
            );
            c.setParentId(parent.getParentId());
        }
        if (category.getSortOrder() != null) c.setSortOrder(category.getSortOrder());
        if (category.getIsActive() != null) c.setIsActive(category.getIsActive());
        categoryRepository.save(c);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        Category c = categoryRepository.findById(id).orElseThrow(
                () -> new BusinessException(BaseResponseCode.NOT_FOUND)
        );

        if(! c.getCreator().getId().equals(AuthContextHelper.getCurrentUserId())) {
            throw new BusinessException(BaseResponseCode.FORBIDDEN);
        }

        categoryRepository.removeCategoryById(id);
    }
}
