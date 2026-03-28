package com.scientia.mercatus.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String passwordField;
    private String confirmPasswordField;

    @Override
    public void initialize(PasswordMatch annotation) {
        this.passwordField = annotation.passwordField();
        this.confirmPasswordField = annotation.confirmPasswordField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Field passwordFieldObj = value.getClass().getDeclaredField(passwordField);
            Field confirmPasswordFieldObj = value.getClass().getDeclaredField(confirmPasswordField);

            passwordFieldObj.setAccessible(true);
            confirmPasswordFieldObj.setAccessible(true);

            String password = (String) passwordFieldObj.get(value);
            String confirmPassword = (String) confirmPasswordFieldObj.get(value);

            if (password == null || confirmPassword == null) {
                return true; // @NotBlank will validate these
            }

            boolean isValid = password.equals(confirmPassword);
            
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(confirmPasswordField)
                        .addConstraintViolation();
            }

            return isValid;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}

